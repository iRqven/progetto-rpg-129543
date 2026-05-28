package it.unicam.cs.mpgc.rpg129543;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.controller.BattleEngine;
import it.unicam.cs.mpgc.rpg129543.model.*;
import it.unicam.cs.mpgc.rpg129543.persistence.PersistenceManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
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

/**
 * Classe di ingresso principale dell'applicazione.
 * Coordina il ciclo di rendering visivo e gestisce la UI ad albero e a comparsa.
 */
public class Main extends Application {
    private Player player;
    private GameState gameState;
    private final PersistenceManager persistence = new PersistenceManager();
    private final Set<javafx.scene.input.KeyCode> pressedKeys = new HashSet<>();
    private boolean isInteracting = false;
    private boolean isMenuOpen = false;

    private static final int VALORE_PENALITA = 15;
    private static final double HP_BAR_WIDTH = 40;

    private BattleEngine currentBattle;
    private Enemy currentEnemy;
    private boolean isInSubMenuVirtu = false;

    private Pane gameArea;
    private Group playerGroup;
    private Group playerSpriteShape;
    private Rectangle hpBar;
    private VBox interactionOverlay;

    // Riferimenti HUD per aggiornamenti puliti ed allineati
    private Label labelKarma, labelLivello, labelPiano;

    @Override
    public void start(Stage primaryStage) {
        this.player = persistence.load().orElseGet(() -> {
            Player nuovoPlayer = new Player("Anima", "Ombra", "Viandante");
            nuovoPlayer.setHp(100);
            nuovoPlayer.setKarma(0);
            return nuovoPlayer;
        });

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

        if (challenge instanceof NarrativeChallenge) {
            Label name = new Label("Presenza Silenziosa");
            name.setStyle("-fx-text-fill: #3498db; -fx-font-size: 20px; -fx-font-weight: bold;");

            Label dialog = new Label(challenge.risolvi(player));
            dialog.setStyle("-fx-text-fill: white; -fx-font-style: italic; -fx-text-alignment: center; -fx-font-family: 'Georgia';");
            dialog.setWrapText(true);

            Button proceedBtn = new Button("Prosegui il Cammino");
            proceedBtn.setStyle("-fx-base: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold;");
            proceedBtn.setOnAction(e -> {
                currentRoom.setSfidaGestita(true);
                gameArea.getChildren().remove(interactionOverlay);
                player.setX(player.getX() - 60);
                isInteracting = false;
                refreshRoomGraphics();
            });

            interactionOverlay.getChildren().addAll(name, dialog, proceedBtn);
        } else {
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
        }
        gameArea.getChildren().add(interactionOverlay);
    }

    private void showBattleTutorial(Challenge challenge) {
        interactionOverlay.getChildren().clear();
        Label t = new Label("SCONTRO DIRETTO SULL'ANIMA");
        t.setStyle("-fx-text-fill: cyan; -fx-font-weight: bold; -fx-font-size: 18px;");

        Label desc = new Label("Il livello della stanza influisce sul potere dello Spettro.\n" +
                "Puoi navigare nel menu tattico aprendo il ramo 'VIRTÙ'.\n" +
                "Sconfiggere boss elargisce XP vitali per aumentare gli attributi.");
        desc.setStyle("-fx-text-fill: white; -fx-text-alignment: center; -fx-font-family: 'Georgia';");
        desc.setWrapText(true);

        Button startBtn = new Button("Inizia il Combattimento");
        startBtn.setStyle("-fx-base: #3498db; -fx-text-fill: white;");
        startBtn.setOnAction(e -> {
            BattleEngine.BossMood debolezza = BattleEngine.BossMood.values()[(int)(Math.random() * 3)];
            currentBattle = new BattleEngine(debolezza);
            isInSubMenuVirtu = false;

            String nomeBoss = (challenge instanceof CombatChallenge) ? ((CombatChallenge) challenge).getDescrizioneDettagliata() : "Rimorso Inquieto";
            currentEnemy = new Enemy(nomeBoss, 100, debolezza.name());

            updateBattleUI("La nebbia si focalizza. Lo spettro ringhia ferocemente.");
        });

        interactionOverlay.getChildren().addAll(t, desc, startBtn);
    }

    private void updateBattleUI(String logText) {
        interactionOverlay.getChildren().clear();
        interactionOverlay.setSpacing(12);
        interactionOverlay.setStyle("-fx-background-color: rgba(10, 10, 10, 0.98); -fx-border-color: #7f8c8d; -fx-padding: 20; -fx-border-width: 3;");

        Label auraLabel = new Label(" STATO EMOTIVO BOSS: " + currentBattle.getCurrentMood() + " ");
        auraLabel.setStyle("-fx-background-color: " + getMoodColor(currentBattle.getCurrentMood()) + "; -fx-text-fill: black; -fx-font-weight: bold;");

        Label stats = new Label("HP: " + player.getHp() + "/" + player.getHpMax() + " | VOLONTÀ: " + currentBattle.getVolonta() + "/8 | ATK: " + player.getDeterminazione());
        stats.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-font-size: 13px;");

        Label log = new Label(logText);
        log.setStyle("-fx-text-fill: white; -fx-font-style: italic; -fx-text-alignment: center; -fx-font-family: 'Georgia';");
        log.setWrapText(true); log.setMinHeight(70);

        GridPane menuLotta = new GridPane();
        menuLotta.setHgap(10); menuLotta.setVgap(10); menuLotta.setAlignment(Pos.CENTER);

        String styleBtn = "-fx-min-width: 135; -fx-min-height: 40; -fx-font-family: 'Courier New'; -fx-font-weight: bold;";

        if (currentBattle.getAnomalieEngine().isImprevistoAttivo()) {
            log.setText(currentBattle.getAnomalieEngine().getTestoBivio());
            log.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

            Button btnAccetta = new Button("ACCETTA PATTO");
            Button btnRifiuta = new Button("RIFIUTA PATTO");
            btnAccetta.setStyle(styleBtn + "-fx-base: #c0392b; -fx-text-fill: white;");
            btnRifiuta.setStyle(styleBtn + "-fx-base: #7f8c8d; -fx-text-fill: white;");

            btnAccetta.setOnAction(e -> processBattle("ACCETTA_PATTO"));
            btnRifiuta.setOnAction(e -> processBattle("RIFIUTA_PATTO"));

            menuLotta.add(btnAccetta, 0, 0); menuLotta.add(btnRifiuta, 1, 0);
        } else if (isInSubMenuVirtu) {
            Button btnPaz = new Button("PAZIENZA (-2V)");
            Button btnCor = new Button("CORAGGIO (-2V)");
            Button btnPer = new Button("PERDONO (-2V)");
            Button btnIndietro = new Button("INDIETRO");

            btnPaz.setStyle(styleBtn + "-fx-base: #2980b9; -fx-text-fill: white;");
            btnCor.setStyle(styleBtn + "-fx-base: #c0392b; -fx-text-fill: white;");
            btnPer.setStyle(styleBtn + "-fx-base: #f39c12; -fx-text-fill: white;");
            btnIndietro.setStyle(styleBtn + "-fx-base: #7f8c8d;");

            btnPaz.setOnAction(e -> processBattle("PAZIENZA"));
            btnCor.setOnAction(e -> processBattle("CORAGGIO"));
            btnPer.setOnAction(e -> processBattle("PERDONO"));
            btnIndietro.setOnAction(e -> { isInSubMenuVirtu = false; updateBattleUI(logText); });

            menuLotta.add(btnPaz, 0, 0); menuLotta.add(btnCor, 1, 0);
            menuLotta.add(btnPer, 0, 1); menuLotta.add(btnIndietro, 1, 1);
        } else {
            Button btnApriVirtu = new Button("VIRTÙ...");
            Button btnDef = new Button("DIFESA (+3V)");
            Button btnCur = new Button("CURA (-3V)");
            Button btnFug = new Button("FUGA");

            btnApriVirtu.setStyle(styleBtn + "-fx-base: #d35400; -fx-text-fill: white;");
            btnDef.setStyle(styleBtn + "-fx-base: #27ae60;");
            btnCur.setStyle(styleBtn + "-fx-base: #8e44ad;");
            btnFug.setStyle(styleBtn + "-fx-base: #7f8c8d;");

            btnApriVirtu.setOnAction(e -> { isInSubMenuVirtu = true; updateBattleUI(log.getText()); });
            btnDef.setOnAction(e -> processBattle("DIFESA"));
            btnCur.setOnAction(e -> processBattle("CURA"));
            btnFug.setOnAction(e -> {
                gameState.getCurrentRoom().setSfidaGestita(true);
                finishInteraction("Sei fuggito perdendo terreno.");
            });

            menuLotta.add(btnApriVirtu, 0, 0); menuLotta.add(btnDef, 1, 0);
            menuLotta.add(btnCur, 0, 1); menuLotta.add(btnFug, 1, 1);
        }

        interactionOverlay.getChildren().addAll(auraLabel, stats, log, menuLotta);

        if (currentEnemy.getHp() <= 0) {
            int premioXp = (gameState.getCurrentRoom().id() + 1) * 50;
            player.addKarma(30);

            // Intercettiamo il segnale booleano di livellamento per attivare l'overlay a comparsa
            boolean haLivellato = player.addXp(premioXp);
            gameState.getCurrentRoom().solveChallenge();

            if (haLivellato) {
                showLevelUpNotification(premioXp);
            } else {
                finishInteraction("PURIFICATO! Sconfiggi lo spettro ed accumuli +" + premioXp + " XP.");
            }
        } else if (player.getHp() <= 0) {
            finishInteraction("L'OSCURITÀ TI HA CONSUMATO...");
            resetGame();
        }
    }

    /**
     * Mostra la notifica centrale a comparsa di Trascendenza dell'Anima (Baldur's Style)
     */
    private void showLevelUpNotification(int xpGuadagnati) {
        interactionOverlay.getChildren().clear();
        interactionOverlay.setSpacing(20);
        interactionOverlay.setStyle("-fx-background-color: rgba(20, 20, 30, 0.98); -fx-border-color: #f1c40f; -fx-padding: 30; -fx-border-width: 3; -fx-border-radius: 10;");

        Label titolo = new Label("CONSEGUIMENTO DELLA CONSAPEVOLEZZA");
        titolo.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Courier New';");

        Label desc = new Label("Hai accumulato +" + xpGuadagnati + " XP. La tua Anima ascende al LIVELLO " + player.getLivello() + "!\n\n" +
                "Le tue proprietà spirituali si sono espanse permanentemente:\n" +
                "Determinazione (ATK): " + player.getDeterminazione() + " (+2)\n" +
                "Resilienza (DEF): " + player.getResilienza() + " (+2)\n" +
                "Sintonia (HP Max): " + player.getHpMax() + " (" + player.getHp() + " HP Correnti)");
        desc.setStyle("-fx-text-fill: white; -fx-text-alignment: center; -fx-font-family: 'Georgia'; -fx-font-size: 14px;");
        desc.setWrapText(true);

        Button btnChiudi = new Button("Prendi Consapevolezza");
        btnChiudi.setStyle("-fx-min-width: 180; -fx-min-height: 40; -fx-base: #f1c40f; -fx-text-fill: black; -fx-font-weight: bold;");
        btnChiudi.setOnAction(e -> {
            gameArea.getChildren().remove(interactionOverlay);
            player.setX(player.getX() - 60);
            isInteracting = false;
            refreshRoomGraphics();
        });

        interactionOverlay.getChildren().addAll(titolo, desc, btnChiudi);
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
        saveExitBtn.setOnAction(e -> {
            persistence.save(player);
            Platform.exit();
        });
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

    /**
     * Configura l'HUD di esplorazione superiore tramite una griglia bilanciata e priva di emoji.
     */
    private GridPane createTopHud() {
        GridPane hud = new GridPane();
        hud.setAlignment(Pos.CENTER);
        hud.setHgap(60);
        hud.setPrefHeight(50);
        hud.setStyle("-fx-background-color: #1a1a24; -fx-border-color: #2c3e50; -fx-border-width: 0 0 2 0;");

        labelKarma = new Label();
        labelLivello = new Label();
        labelPiano = new Label();

        String styleText = "-fx-text-fill: #bdc3c7; -fx-font-weight: bold; -fx-font-family: 'Courier New'; -fx-font-size: 13px;";
        labelKarma.setStyle(styleText);
        labelLivello.setStyle(styleText);
        labelPiano.setStyle(styleText);

        hud.add(labelKarma, 0, 0);
        hud.add(labelLivello, 1, 0);
        hud.add(labelPiano, 2, 0);

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

    private void render() { playerGroup.setTranslateX(player.getX()); playerGroup.setTranslateY(player.getY()); hpBar.setWidth(HP_BAR_WIDTH * (player.getHp() / (double)player.getHpMax())); }

    private void updateStatusBar() {
        labelKarma.setText("KARMA: " + player.getKarma());
        labelLivello.setText("LIVELLO ANIMA: " + player.getLivello() + " (" + player.getXp() + "/" + player.getXpNecessari() + " XP)");
        labelPiano.setText("PIANO CORRENTE: " + (gameState.getCurrentRoom().id() + 1));
    }

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