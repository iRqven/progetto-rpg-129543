package it.unicam.cs.mpgc.rpg129543;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.controller.BattleEngine;
import it.unicam.cs.mpgc.rpg129543.model.*;
import it.unicam.cs.mpgc.rpg129543.persistence.PersistenceManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.Group;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.Set;

public class Main extends Application {
    // Logica e Stato
    private Player player;
    private GameState gameState;
    private final PersistenceManager persistence = new PersistenceManager();
    private final Set<javafx.scene.input.KeyCode> pressedKeys = new HashSet<>();
    private boolean isInteracting = false;
    private boolean isMenuOpen = false;
    private final int VALORE_PENALITA = 15;

    // Motore di Combattimento
    private BattleEngine currentBattle;
    private Enemy currentEnemy;

    // Componenti Grafici
    private Pane gameArea;
    private Group playerGroup;
    private Group playerSpriteShape;
    private Rectangle hpBar;
    private VBox interactionOverlay;
    private final double HP_BAR_WIDTH = 40;
    private Label karmaLabel, levelLabel;

    @Override
    public void start(Stage primaryStage) {
        Player savedPlayer = persistence.load();
        this.player = (savedPlayer != null) ? savedPlayer : new Player("Anima", "Ombra", "Viandante");
        if (savedPlayer == null) { this.player.setHp(100); this.player.setKarma(0); }
        this.gameState = new GameState(player);

        gameArea = new Pane();
        gameArea.setPrefSize(800, 600);
        createPlayerGraphics();

        VBox root = new VBox(createTopHud(), gameArea);
        root.setStyle("-fx-background-color: #000;");
        Scene scene = new Scene(root, 800, 650);

        scene.setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());
            if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) togglePauseMenu();
        });
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));

        refreshRoomGraphics();
        if (player.getKarma() == 0 && player.getRicordi().isEmpty() && gameState.getCurrentRoom().id() == 0) {
            showIntro();
        }

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isInteracting) { updatePhysics(); checkInteractions(); }
                render();
            }
        }.start();

        primaryStage.setTitle("Purgatorio RPG - Metodologie 2025/26");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void showIntro() {
        isInteracting = true;
        VBox box = new VBox(25);
        box.setStyle("-fx-background-color: black; -fx-padding: 50;");
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(800, 600);

        Label l1 = new Label("Ti risvegli nel grigio.");
        Label l2 = new Label("Non ricordi la tua morte, ma senti il peso dei tuoi errori passati.");
        Label l3 = new Label("Esplora i piani, affronta i tuoi rimorsi spettrali... o ignorali.");

        String s = "-fx-text-fill: #ecf0f1; -fx-font-size: 20px; -fx-font-family: 'Georgia';";
        l1.setStyle(s); l2.setStyle(s); l3.setStyle(s);

        Button b = new Button("Apri gli occhi e inizia il cammino");
        b.setStyle("-fx-base: #2c3e50; -fx-text-fill: cyan; -fx-font-size: 16px; -fx-padding: 10 20;");
        b.setOnAction(e -> { gameArea.getChildren().remove(box); isInteracting = false; });

        box.getChildren().addAll(l1, l2, l3, b);
        gameArea.getChildren().add(box);
    }

    private void startChoiceMenu(Challenge challenge) {
        pressedKeys.clear();
        Room currentRoom = gameState.getCurrentRoom();

        interactionOverlay = new VBox(20);
        interactionOverlay.setAlignment(Pos.CENTER);
        interactionOverlay.setPrefSize(450, 350);
        interactionOverlay.setLayoutX(175); interactionOverlay.setLayoutY(125);
        interactionOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.95); -fx-border-color: #f1c40f; -fx-padding: 25; -fx-border-radius: 15;");

        Label name = new Label("L'Ombra del Passato");
        name.setStyle("-fx-text-fill: #9b59b6; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label dialog = new Label("\"Viandante... la fuga ha un prezzo, ma il pentimento restituisce forza al cuore.\"");
        dialog.setStyle("-fx-text-fill: white; -fx-font-style: italic; -fx-text-alignment: center; -fx-font-family: 'Georgia';");
        dialog.setWrapText(true);

        Button fightBtn = new Button("LOTTA (Pentimento)");
        Button ignoreBtn = new Button("IGNORA (-" + VALORE_PENALITA + " Karma)");

        fightBtn.setStyle("-fx-base: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        ignoreBtn.setStyle("-fx-base: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        ignoreBtn.setOnAction(e -> {
            if (!currentRoom.isKarmaGiaTolto()) {
                player.addKarma(-VALORE_PENALITA);
                currentRoom.setKarmaGiaTolto(true);
                updateStatusBar();
            }
            currentRoom.setSfidaGestita(true);
            finishInteraction("Hai scelto l'indifferenza. La porta è aperta.");
        });

        fightBtn.setOnAction(e -> {
            if (currentRoom.isKarmaGiaTolto()) {
                player.addKarma(VALORE_PENALITA);
                currentRoom.setKarmaGiaTolto(false);
                updateStatusBar();
            }
            showBattleTutorial(challenge);
        });

        interactionOverlay.getChildren().addAll(name, dialog, fightBtn, ignoreBtn);
        gameArea.getChildren().add(interactionOverlay);
    }

    private void showBattleTutorial(Challenge challenge) {
        interactionOverlay.getChildren().clear();
        Label t = new Label("TUTORIAL DI COMBATTIMENTO");
        t.setStyle("-fx-text-fill: cyan; -fx-font-weight: bold; -fx-font-size: 18px;");

        Label desc = new Label("Ogni Rimorso ha un'Aura colorata.\n\n" +
                "🔵 PAZIENZA batte Aura RABBIA\n" +
                "🔴 CORAGGIO batte Aura PAURA\n" +
                "🟡 PERDONO batte Aura COLPA\n\n" +
                "DIFESA riduce i danni e rigenera Volontà.\n" +
                "CURA ripristina la tua salute.");
        desc.setStyle("-fx-text-fill: white; -fx-text-alignment: center; -fx-font-family: 'Georgia';");
        desc.setWrapText(true);

        Button startBtn = new Button("Inizia lo scontro");
        startBtn.setStyle("-fx-base: #3498db; -fx-text-fill: white;");
        startBtn.setOnAction(e -> {
            BattleEngine.BossMood debolezza = BattleEngine.BossMood.values()[(int)(Math.random()*3)];
            currentBattle = new BattleEngine(debolezza);
            currentEnemy = new Enemy("Rimorso Inquieto", 100, "PERDONO");
            updateBattleUI("Lo scontro ha inizio!");
        });

        interactionOverlay.getChildren().addAll(t, desc, startBtn);
    }

    private void updateBattleUI(String logText) {
        interactionOverlay.getChildren().clear();
        interactionOverlay.setSpacing(15);
        interactionOverlay.setStyle("-fx-background-color: rgba(10, 10, 10, 0.98); -fx-border-color: #7f8c8d; -fx-padding: 20; -fx-border-width: 3;");

        Label auraLabel = new Label(" AURA NEMICA: " + currentBattle.getCurrentMood() + " ");
        auraLabel.setStyle("-fx-background-color: " + getMoodColor(currentBattle.getCurrentMood()) + "; -fx-text-fill: black; -fx-font-weight: bold;");

        Label stats = new Label("HP: " + player.getHp() + "/100  |  VOLONTÀ: " + currentBattle.getVolonta());
        stats.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label log = new Label(logText);
        log.setStyle("-fx-text-fill: white; -fx-font-style: italic; -fx-text-alignment: center; -fx-font-family: 'Georgia';");
        log.setWrapText(true); log.setMinHeight(60);

        GridPane menuLotta = new GridPane();
        menuLotta.setHgap(15); menuLotta.setVgap(15); menuLotta.setAlignment(Pos.CENTER);

        Button btnAtk = new Button("LOTTA (-1V)");
        Button btnDef = new Button("DIFESA (+1V)");
        Button btnCur = new Button("CURA (-2V)");
        Button btnFug = new Button("FUGA");

        String styleBtn = "-fx-min-width: 130; -fx-min-height: 40; -fx-font-family: 'Courier New'; -fx-font-weight: bold;";
        btnAtk.setStyle(styleBtn + "-fx-base: #c0392b;");
        btnDef.setStyle(styleBtn + "-fx-base: #27ae60;");
        btnCur.setStyle(styleBtn + "-fx-base: #f1c40f;");
        btnFug.setStyle(styleBtn + "-fx-base: #7f8c8d;");

        btnAtk.setOnAction(e -> processBattle("ATTACCO"));
        btnDef.setOnAction(e -> processBattle("DIFESA"));
        btnCur.setOnAction(e -> processBattle("CURA"));
        btnFug.setOnAction(e -> {
            if (!gameState.getCurrentRoom().isKarmaGiaTolto()) {
                player.addKarma(-VALORE_PENALITA);
                gameState.getCurrentRoom().setKarmaGiaTolto(true);
            }
            gameState.getCurrentRoom().setSfidaGestita(true);
            finishInteraction("Sei fuggito dal combattimento.");
        });

        menuLotta.add(btnAtk, 0, 0); menuLotta.add(btnDef, 1, 0);
        menuLotta.add(btnCur, 0, 1); menuLotta.add(btnFug, 1, 1);

        interactionOverlay.getChildren().addAll(auraLabel, stats, log, menuLotta);

        if (currentEnemy.getHp() <= 0) {
            player.addKarma(30); player.setHp(Math.min(100, player.getHp() + 25));
            gameState.getCurrentRoom().solveChallenge();
            finishInteraction("VITTORIA! Il Rimorso svanisce. Recuperi energie.");
        } else if (player.getHp() <= 0) {
            finishInteraction("L'OSCURITÀ TI HA CONSUMATO...");
            resetGame();
        }
    }

    private void togglePauseMenu() {
        if (isMenuOpen) { gameArea.getChildren().remove(interactionOverlay); isInteracting = false; isMenuOpen = false; }
        else showPauseMenu();
    }

    private void showPauseMenu() {
        isInteracting = true; isMenuOpen = true; pressedKeys.clear();
        interactionOverlay = new VBox(20);
        interactionOverlay.setAlignment(Pos.CENTER);
        interactionOverlay.setPrefSize(300, 400);
        interactionOverlay.setLayoutX(250); interactionOverlay.setLayoutY(100);
        interactionOverlay.setStyle("-fx-background-color: rgba(15, 15, 15, 0.98); -fx-border-color: cyan; -fx-padding: 30; -fx-background-radius: 20; -fx-border-radius: 20;");

        Label menuTitle = new Label("OBLIO");
        menuTitle.setStyle("-fx-text-fill: cyan; -fx-font-size: 24px; -fx-font-weight: bold;");

        Button resumeBtn = new Button("Riprendi");
        Button saveExitBtn = new Button("Esci e Salva");
        Button resetBtn = new Button("Reset Totale");

        String btnStyle = "-fx-min-width: 150px; -fx-base: #2c3e50; -fx-text-fill: white;";
        resumeBtn.setStyle(btnStyle); saveExitBtn.setStyle(btnStyle); resetBtn.setStyle(btnStyle + "-fx-base: #c0392b;");

        resumeBtn.setOnAction(e -> togglePauseMenu());
        saveExitBtn.setOnAction(e -> { persistence.save(player); System.exit(0); });
        resetBtn.setOnAction(e -> showResetWarning());

        interactionOverlay.getChildren().addAll(menuTitle, resumeBtn, saveExitBtn, resetBtn);
        gameArea.getChildren().add(interactionOverlay);
    }

    private void showResetWarning() {
        interactionOverlay.getChildren().clear();
        Label warnLabel = new Label("ATTENZIONE!\nVuoi davvero tornare al nulla?\nOgni ricordo andrà perduto.");
        warnLabel.setStyle("-fx-text-fill: #e74c3c; -fx-text-alignment: center; -fx-font-weight: bold;");
        Button confirmBtn = new Button("SÌ, RESETTA");
        Button cancelBtn = new Button("NO, TORNA");
        confirmBtn.setStyle("-fx-base: #c0392b; -fx-text-fill: white;");
        cancelBtn.setStyle("-fx-base: #27ae60; -fx-text-fill: white;");
        confirmBtn.setOnAction(e -> { resetGame(); isMenuOpen = false; showIntro(); });
        cancelBtn.setOnAction(e -> { isMenuOpen = false; togglePauseMenu(); });
        interactionOverlay.getChildren().addAll(warnLabel, confirmBtn, cancelBtn);
    }

    private void createPlayerGraphics() {
        Polygon mantello = new Polygon(0, 0, -10, 25, 10, 25);
        mantello.setFill(Color.DARKSLATEGRAY); mantello.setStroke(Color.CYAN);
        Circle testa = new Circle(0, -5, 7, Color.GHOSTWHITE);
        playerSpriteShape = new Group(mantello, testa);
        Rectangle hpBg = new Rectangle(HP_BAR_WIDTH, 5, Color.web("#330000"));
        hpBg.setX(-20); hpBg.setY(-35);
        hpBar = new Rectangle(HP_BAR_WIDTH, 5, Color.LIME);
        hpBar.setX(-20); hpBar.setY(-35);
        playerGroup = new Group(playerSpriteShape, hpBg, hpBar);
    }

    private HBox createTopHud() {
        karmaLabel = new Label(); levelLabel = new Label();
        String style = "-fx-text-fill: white; -fx-font-weight: bold;";
        karmaLabel.setStyle(style); levelLabel.setStyle(style);
        HBox hud = new HBox(50, karmaLabel, levelLabel); hud.setAlignment(Pos.CENTER);
        hud.setPrefHeight(50); hud.setStyle("-fx-background-color: #2c3e50;");
        return hud;
    }

    private void updatePhysics() {
        if (pressedKeys.contains(javafx.scene.input.KeyCode.W)) player.moveUp();
        if (pressedKeys.contains(javafx.scene.input.KeyCode.S)) player.moveDown();
        if (pressedKeys.contains(javafx.scene.input.KeyCode.A)) player.moveLeft();
        if (pressedKeys.contains(javafx.scene.input.KeyCode.D)) player.moveRight();
        if (player.getX() < 20) player.setX(20); if (player.getX() > 780) player.setX(780);
        if (player.getY() < 20) player.setY(20); if (player.getY() > 580) player.setY(580);
    }

    private void checkInteractions() {
        Room current = gameState.getCurrentRoom();
        if (current.hasChallenge()) {
            double dist = Math.sqrt(Math.pow(player.getX() - current.npcX(), 2) + Math.pow(player.getY() - current.npcY(), 2));
            if (dist < 45) { isInteracting = true; startChoiceMenu(current.sfida()); }
        }
        if (current.hasFragment() && !player.getRicordi().contains(current.ricordoSbloccato())) {
            double dist = Math.sqrt(Math.pow(player.getX() - current.fragX(), 2) + Math.pow(player.getY() - current.fragY(), 2));
            if (dist < 30) { player.addRicordo(current.ricordoSbloccato()); showMemoryPopup(current.ricordoSbloccato()); }
        }
        double distToDoor = Math.sqrt(Math.pow(player.getX() - current.doorX(), 2) + Math.pow(player.getY() - current.doorY(), 2));
        if (distToDoor < 45 && current.isSfidaGestita()) {
            if (gameState.nextRoom()) { player.setX(50); player.setY(300); refreshRoomGraphics(); persistence.save(player); }
            else showFinalJudgment();
        }
    }

    private void processBattle(String mossa) {
        String res = currentBattle.executeTurn(player, currentEnemy, mossa);
        if (res.contains("violentemente")) applyDamageEffect();
        updateBattleUI(res);
    }

    private void finishInteraction(String text) {
        interactionOverlay.getChildren().clear();
        Label l = new Label(text); l.setStyle("-fx-text-fill: #f1c40f; -fx-text-alignment: center; -fx-font-family: 'Georgia';"); l.setWrapText(true);
        Button b = new Button("Prosegui"); b.setStyle("-fx-base: #2c3e50; -fx-text-fill: white;");
        b.setOnAction(e -> { gameArea.getChildren().remove(interactionOverlay); player.setX(player.getX() - 60); isInteracting = false; refreshRoomGraphics(); });
        interactionOverlay.getChildren().addAll(l, b);
    }

    private void refreshRoomGraphics() {
        gameArea.getChildren().clear();
        updateBackground();
        Room current = gameState.getCurrentRoom();
        if (current.hasChallenge()) {
            Circle boss = new Circle(30, Color.rgb(155, 89, 182, 0.7)); boss.setEffect(new javafx.scene.effect.Glow(0.8));
            boss.setCenterX(current.npcX()); boss.setCenterY(current.npcY()); gameArea.getChildren().add(boss);
        }
        if (current.hasFragment() && !player.getRicordi().contains(current.ricordoSbloccato())) {
            Polygon star = new Polygon(0, -10, 2, -2, 10, 0, 2, 2, 0, 10, -2, 2, -10, 0, -2, -2);
            star.setFill(Color.GOLD); star.setEffect(new javafx.scene.effect.Glow(1.0));
            star.setTranslateX(current.fragX()); star.setTranslateY(current.fragY()); gameArea.getChildren().add(star);
        }
        Rectangle door = new Rectangle(50, 80, Color.rgb(241, 196, 15, 0.8));
        door.setX(current.doorX() - 25); door.setY(current.doorY() - 40); door.setStroke(Color.ORANGE);
        gameArea.getChildren().addAll(door, playerGroup);
        updateStatusBar();
    }

    private void render() { playerGroup.setTranslateX(player.getX()); playerGroup.setTranslateY(player.getY()); hpBar.setWidth(HP_BAR_WIDTH * (player.getHp() / 100.0)); }

    private void updateStatusBar() { karmaLabel.setText("⚖️ Karma: " + player.getKarma()); levelLabel.setText("🚪 Piano: " + (gameState.getCurrentRoom().id() + 1)); }

    private void updateBackground() {
        int liv = gameState.getCurrentRoom().id();
        gameArea.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 100%, rgba(80,80,80,"+(1-liv/10.0)+") 0%, rgba("+(liv*20)+",0,"+(liv*40)+",1) 100%);");
    }

    private void showMemoryPopup(String memory) {
        isInteracting = true;
        VBox p = new VBox(15); p.setStyle("-fx-background-color: rgba(44, 62, 80, 0.95); -fx-padding: 20; -fx-border-color: cyan; -fx-border-radius: 10;");
        p.setAlignment(Pos.CENTER); p.setLayoutX(200); p.setLayoutY(200); p.setPrefWidth(400);
        Label t = new Label("MEMORIA RITROVATA"); t.setStyle("-fx-text-fill: cyan; -fx-font-weight: bold;");
        Label c = new Label(memory); c.setStyle("-fx-text-fill: white; -fx-font-style: italic; -fx-font-family: 'Georgia';"); c.setWrapText(true);
        Button b = new Button("Ricorda"); b.setStyle("-fx-base: #2c3e50; -fx-text-fill: white;");
        b.setOnAction(e -> { gameArea.getChildren().remove(p); isInteracting = false; });
        p.getChildren().addAll(t, c, b); gameArea.getChildren().add(p);
    }

    private void applyDamageEffect() { playerSpriteShape.setOpacity(0.4); javafx.animation.PauseTransition p = new javafx.animation.PauseTransition(javafx.util.Duration.millis(250)); p.setOnFinished(e -> playerSpriteShape.setOpacity(1.0)); p.play(); }

    private void showFinalJudgment() {
        isInteracting = true; gameArea.getChildren().clear();
        String v = (player.getKarma() >= 40) ? "PARADISO - Sei libero." : "INFERNO - Il peso ti ha vinto.";
        VBox end = new VBox(30); end.setAlignment(Pos.CENTER); end.setPrefSize(800, 600); end.setStyle("-fx-background-color: black;");
        Label r = new Label(v); r.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-family: 'Georgia';");
        Button restartBtn = new Button("Ritorna nell'Oblio (Nuova Partita)"); restartBtn.setStyle("-fx-base: #2c3e50; -fx-text-fill: white;");
        restartBtn.setOnAction(e -> resetGame());
        end.getChildren().addAll(r, restartBtn); gameArea.getChildren().add(end);
    }

    private void resetGame() { this.player = new Player("Anima", "Ombra", "Viandante"); this.player.setHp(100); this.player.setKarma(0); this.gameState = new GameState(player); persistence.save(player); refreshRoomGraphics(); updateStatusBar(); isInteracting = false; showIntro(); }

    private String getMoodColor(BattleEngine.BossMood m) { return switch(m){case RABBIA->"#e74c3c";case PAURA->"#3498db";case COLPA->"#f1c40f";};}

    public static void main(String[] args) { launch(args); }
}