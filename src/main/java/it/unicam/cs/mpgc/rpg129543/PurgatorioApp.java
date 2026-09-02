package it.unicam.cs.mpgc.rpg129543;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.api.ChallengeResult;
import it.unicam.cs.mpgc.rpg129543.controller.BattleController;
import it.unicam.cs.mpgc.rpg129543.controller.GameController;
import it.unicam.cs.mpgc.rpg129543.controller.GameLoopController;
import it.unicam.cs.mpgc.rpg129543.controller.InputController;
import it.unicam.cs.mpgc.rpg129543.model.*;
import it.unicam.cs.mpgc.rpg129543.persistence.PersistenceManager;
import it.unicam.cs.mpgc.rpg129543.view.GameView;
import it.unicam.cs.mpgc.rpg129543.view.HudView;
import it.unicam.cs.mpgc.rpg129543.view.MessageView;
import it.unicam.cs.mpgc.rpg129543.view.UIManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Classe principale dell'applicazione per Purgatorio RPG.
 * Grazie al refactoring finale, ora funge unicamente da Coordinatore (Router) delegando:
 * - La Grafica alle View
 * - Il Combattimento al BattleController
 * - La Fisica e le Collisioni al GameController
 */
public class PurgatorioApp extends Application {

    // --- DIPENDENZE E STATO DEL MODELLO ---
    private Player player;
    private GameState gameState;
    private final PersistenceManager persistence = new PersistenceManager();

    private boolean isMenuOpen = false;
    private static final int VALORE_PENALITA = 15;

    // --- ARCHITETTURA MVC: VISTE E CONTROLLER ---
    private GameView gameView;
    private HudView hudView;
    private InputController inputController;
    private GameLoopController gameLoop;

    private GameController gameController;
    private BattleController battleController;

    private VBox interactionOverlay;
    private Stage mainStage;

    @Override
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage; // Salva il riferimento
        initializeGameData();
        setupUI(primaryStage);
    }

    private void initializeGameData() {
        this.player = persistence.load().orElseGet(() -> {
            Player nuovoPlayer = new Player("Anima", "Ombra", "Viandante");
            nuovoPlayer.setHp(100);
            nuovoPlayer.setKarma(0);
            return nuovoPlayer;
        });
        this.gameState = new GameState(player);
    }

    private void setupUI(Stage primaryStage) {
        // 1. Inizializza le Viste
        gameView = new GameView(player);
        hudView = new HudView(() -> {
            if (interactionOverlay == null) showMemoryArchive();
        });

        VBox root = new VBox(hudView.getHudNode(), gameView.getGameArea());
        root.setStyle("-fx-background-color: #000;");
        Scene scene = new Scene(root, 800, 650);

        // 2. Inizializza il Controller degli Input
        inputController = new InputController(scene,
                this::togglePauseMenu,
                () -> { if (interactionOverlay == null) showMemoryArchive(); }
        );

        // 3. Inizializza il BattleController passando le azioni di routing (Callbacks)
        battleController = new BattleController(
                player, gameState,
                this::mostraOverlayCentrato,
                this::rimuoviOverlay,
                this::aggiornaStanzaEHud,
                this::showLevelUpNotification,
                this::resetGame,
                () -> { gameController.setInteracting(false); gameView.getGameArea().requestFocus(); },
                () -> gameView.applyDamageEffect()
        );

        // 4. Inizializza il GameController delegandogli fisica e collisioni
        gameController = new GameController(
                player, gameState, gameView, hudView, inputController,
                this::startCombatChoice,
                this::startSkillCheckChoice,
                this::startNarrativeChoice,
                this::showLoreOnly,
                this::showMemoryPopup,
                () -> {
                    aggiornaStanzaEHud();
                    persistence.save(player);
                    gameController.setInteracting(false);
                },
                this::showFinalJudgment
        );

        // 5. Avvia il metronomo di gioco
        gameLoop = new GameLoopController(
                gameController::update,
                () -> gameView.renderPlayer(player)
        );
        gameLoop.startLoop();

        aggiornaStanzaEHud();

        if (player.getKarma() == 0 && player.getRicordi().isEmpty() && gameState.getCurrentRoom().id() == 0) {
            showIntro();
        }

        primaryStage.setTitle("Purgatorio RPG - Metodologie 2025/26");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // ==========================================
    // UTILITY DI GESTIONE OVERLAY (CLEAN CODE)
    // ==========================================

    private void mostraOverlayCentrato(VBox overlay) {
        rimuoviOverlay();
        interactionOverlay = overlay;
        interactionOverlay.layoutXProperty().bind(gameView.getGameArea().widthProperty().divide(2).subtract(interactionOverlay.prefWidthProperty().divide(2)));
        interactionOverlay.layoutYProperty().bind(gameView.getGameArea().heightProperty().divide(2).subtract(interactionOverlay.prefHeightProperty().divide(2)));
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    private void rimuoviOverlay() {
        if (interactionOverlay != null) {
            gameView.getGameArea().getChildren().remove(interactionOverlay);
            interactionOverlay = null;
        }
    }

    private void aggiornaStanzaEHud() {
        gameView.refreshRoomGraphics(gameState.getCurrentRoom(), player);
        if (hudView != null) {
            hudView.updateStatus(player.getKarma(), player.getLivello(), player.getXp(), player.getXpNecessari(), gameState.getCurrentRoom().id() + 1);
        }
    }

    private void repelPlayer(Room room) {
        if (player.getX() > room.npcX()) player.setX(player.getX() + 50);
        else player.setX(player.getX() - 50);
    }

    // ==========================================
    // ROUTING DELLE SCHERMATE E DEI MENU
    // ==========================================

    private void startCombatChoice(Challenge challenge) {
        inputController.clearKeys();
        Room currentRoom = gameState.getCurrentRoom();

        VBox menu = UIManager.createCombatChoiceMenu(
                "L'Ombra del Passato",
                "\"Viandante... la fuga ha un prezzo, ma il pentimento restituisce forza al cuore.\"",
                VALORE_PENALITA,
                () -> {
                    if (currentRoom.isKarmaGiaTolto()) {
                        player.addKarma(VALORE_PENALITA);
                        currentRoom.setKarmaGiaTolto(false);
                        aggiornaStanzaEHud();
                    }
                    battleController.startBattleTutorial((CombatChallenge) challenge);
                },
                () -> {
                    if (!currentRoom.isKarmaGiaTolto()) {
                        player.addKarma(-VALORE_PENALITA);
                        currentRoom.setKarmaGiaTolto(true);
                        aggiornaStanzaEHud();
                    }
                    currentRoom.setSfidaGestita(true);
                    mostraMessaggioUscita("ESITO SCONTRO", "Hai scelto l'indifferenza. La porta è aperta.");
                }
        );
        mostraOverlayCentrato(menu);
    }

    private void startSkillCheckChoice(Challenge challenge) {
        inputController.clearKeys();
        Room currentRoom = gameState.getCurrentRoom();
        SkillCheckChallenge skc = (SkillCheckChallenge) challenge;

        VBox menu = UIManager.createSkillCheckMenu(
                "Prova di Consapevolezza",
                skc.getDialogo(),
                () -> {
                    // DTO Pulito
                    ChallengeResult risultato = challenge.risolvi(player, currentRoom.id());
                    String etichetta = risultato.isSuccesso() ? "SUCCESSO" : "FALLIMENTO";
                    String testoFormattato = "[" + etichetta + " - " + risultato.getEntitaCoinvolta() + "]\n" + risultato.getMessaggioLogico();

                    VBox narrativeMenu = UIManager.createNarrativeMenu(
                            "Esito della Prova",
                            testoFormattato,
                            () -> {
                                currentRoom.setSfidaGestita(true);
                                rimuoviOverlay();
                                repelPlayer(currentRoom);
                                gameController.setInteracting(false);
                                aggiornaStanzaEHud();
                                gameView.getGameArea().requestFocus();
                            }
                    );
                    mostraOverlayCentrato(narrativeMenu);
                }
        );
        mostraOverlayCentrato(menu);
    }

    private void startNarrativeChoice(Challenge challenge) {
        inputController.clearKeys();
        Room currentRoom = gameState.getCurrentRoom();
        NarrativeChallenge nc = (NarrativeChallenge) challenge;

        // DTO Pulito
        ChallengeResult risultato = challenge.risolvi(player, currentRoom.id());
        String testoFormattato = nc.getNomeNPC().toUpperCase() + ":\n" + risultato.getMessaggioLogico() + "\n\n[Sblocchi l'indizio: " + risultato.getEntitaCoinvolta() + "]";

        VBox menu = UIManager.createNarrativeMenu(
                "Presenza Silenziosa",
                testoFormattato,
                () -> {
                    currentRoom.setSfidaGestita(true);
                    rimuoviOverlay();
                    repelPlayer(currentRoom);
                    gameController.setInteracting(false);
                    aggiornaStanzaEHud();
                    gameView.getGameArea().requestFocus();
                }
        );
        mostraOverlayCentrato(menu);
    }

    private void showIntro() {
        gameController.setInteracting(true);
        it.unicam.cs.mpgc.rpg129543.view.StartMenuView introView = new it.unicam.cs.mpgc.rpg129543.view.StartMenuView(this::showCharacterSelection);
        mostraOverlayCentrato(introView.getView());
    }

    private void showCharacterSelection() {
        it.unicam.cs.mpgc.rpg129543.view.CharacterSelectionView selectionView = new it.unicam.cs.mpgc.rpg129543.view.CharacterSelectionView(gender -> {
            player.setGenereSprite(gender);
            gameView.updatePlayerSprite(player);
            persistence.save(player);
            rimuoviOverlay();
            gameController.setInteracting(false);
            gameView.getGameArea().requestFocus();
        });
        mostraOverlayCentrato(selectionView.getView());
    }

    private void showPauseMenu() {
        gameController.setInteracting(true);
        isMenuOpen = true;
        inputController.clearKeys();

        it.unicam.cs.mpgc.rpg129543.view.PauseMenuView pauseMenu = new it.unicam.cs.mpgc.rpg129543.view.PauseMenuView(
                this::togglePauseMenu,
                () -> { persistence.save(player); Platform.exit(); },
                () -> { resetGame(); isMenuOpen = false; showIntro(); }
        );

        rimuoviOverlay();
        interactionOverlay = pauseMenu.getView();
        interactionOverlay.setLayoutX(250);
        interactionOverlay.setLayoutY(100);
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    private void togglePauseMenu() {
        if (isMenuOpen) {
            rimuoviOverlay();
            gameController.setInteracting(false);
            isMenuOpen = false;
        } else {
            showPauseMenu();
        }
    }

    private void showMemoryArchive() {
        gameController.setInteracting(true);
        inputController.clearKeys();
        it.unicam.cs.mpgc.rpg129543.view.MemoryArchiveView archiveView = new it.unicam.cs.mpgc.rpg129543.view.MemoryArchiveView(player.getRicordi(), () -> {
            rimuoviOverlay();
            gameController.setInteracting(false);
            gameView.getGameArea().requestFocus();
        });
        mostraOverlayCentrato(archiveView.getView());
    }

    private void showMemoryPopup(String memory) {
        MessageView popup = new MessageView("MEMORIA RITROVATA", memory, "Ricorda", "cyan", () -> {
            rimuoviOverlay();
            gameController.setInteracting(false);
            gameView.getGameArea().requestFocus();
        });
        mostraOverlayCentrato(popup.getView());
    }

    private void showLoreOnly(Room room) {
        String titolo = (room.hasChallenge() && room.sfida().isCompletata()) ? "PURIFICAZIONE" : "PRESENZA IGNORATA";
        String esito = (room.hasChallenge() && room.sfida().isCompletata()) ? "\n\nLa vittima ha trovato pace. Il rimorso è stato espiato." : "\n\nLo spettro continua a contorcersi nel suo eterno tormento. Hai scelto di voltare lo sguardo, confermando la tua natura.";
        String colore = (room.hasChallenge() && room.sfida().isCompletata()) ? "#f1c40f" : "#e74c3c";

        MessageView loreView = new MessageView(titolo, room.descrizione() + esito, "Chiudi", colore, () -> {
            rimuoviOverlay();
            player.setX(player.getX() + 60);
            gameController.setInteracting(false);
            gameView.getGameArea().requestFocus();
        });
        mostraOverlayCentrato(loreView.getView());
    }

    private void mostraMessaggioUscita(String titolo, String testo) {
        MessageView msgView = new MessageView(titolo, testo, "Prosegui", "#f1c40f", () -> {
            rimuoviOverlay();
            player.setX(player.getX() + 60);
            gameController.setInteracting(false);
            aggiornaStanzaEHud();
            gameView.getGameArea().requestFocus();
        });
        mostraOverlayCentrato(msgView.getView());
    }

    private void showLevelUpNotification(int xpGuadagnati) {
        String desc = "Hai accumulato +" + xpGuadagnati + " XP. La tua Anima ascende al LIVELLO " + player.getLivello() + "!\n\n" +
                "Le tue proprietà spirituali si sono espanse permanentemente:\n" +
                "Determinazione (ATK): " + player.getDeterminazione() + " (+2)\n" +
                "Resilienza (DEF): " + player.getResilienza() + " (+2)\n" +
                "Sintonia (HP Max): " + player.getHpMax() + " (" + player.getHp() + " HP Correnti)";

        MessageView lvlView = new MessageView("CONSEGUIMENTO DELLA CONSAPEVOLEZZA", desc, "Prendi Consapevolezza", "#f1c40f", () -> {
            rimuoviOverlay();
            repelPlayer(gameState.getCurrentRoom());
            gameController.setInteracting(false);
            aggiornaStanzaEHud();
        });
        mostraOverlayCentrato(lvlView.getView());
    }

    private void showFinalJudgment() {
        gameController.setInteracting(true);
        gameView.getGameArea().getChildren().clear();
        inputController.clearKeys();
        ScrollPane finalScreen = UIManager.createFinalJudgmentMenu(player.getKarma(), this::resetGame);
        gameView.getGameArea().getChildren().add(finalScreen);
    }

    private void resetGame() {
        rimuoviOverlay();

        // 1. Ferma il vecchio loop di gioco per evitare "zombie" in background
        if (gameLoop != null) {
            gameLoop.stopLoop();
        }

        // 2. Crea i nuovi oggetti sani
        this.player = new Player("Anima", "Ombra", "Viandante");
        this.player.setHp(100);
        this.player.setKarma(0);
        this.gameState = new GameState(player);

        persistence.save(player);

        // 3. Ricostruisce completamente l'architettura MVC collegandola ai nuovi oggetti!
        setupUI(mainStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}