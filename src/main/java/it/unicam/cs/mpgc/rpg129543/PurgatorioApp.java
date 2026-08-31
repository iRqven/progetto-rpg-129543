package it.unicam.cs.mpgc.rpg129543;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.controller.BattleEngine;
import it.unicam.cs.mpgc.rpg129543.model.*;
import it.unicam.cs.mpgc.rpg129543.persistence.PersistenceManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import it.unicam.cs.mpgc.rpg129543.view.UIManager;

/**
 * Classe principale dell'applicazione per Purgatorio RPG.
 * Ora funge unicamente da Coordinatore (Controller centrale) delegando la grafica alle View
 * e l'input e il loop ai Controller specifici (Architettura MVC).
 */
public class PurgatorioApp extends Application {

    // --- DIPENDENZE E STATO DEL MODELLO ---
    private Player player;
    private GameState gameState;
    private final PersistenceManager persistence = new PersistenceManager();

    private boolean isInteracting = false;
    private boolean isMenuOpen = false;
    private boolean isInSubMenuVirtu = false;

    private static final int VALORE_PENALITA = 15;

    // --- COMPONENTI DEL COMBATTIMENTO ---
    private BattleEngine currentBattle;
    private Enemy currentEnemy;

    // --- ARCHITETTURA MVC: VISTE E CONTROLLER ---
    private it.unicam.cs.mpgc.rpg129543.view.GameView gameView;
    private it.unicam.cs.mpgc.rpg129543.view.HudView hudView;
    private it.unicam.cs.mpgc.rpg129543.controller.InputController inputController;
    private it.unicam.cs.mpgc.rpg129543.controller.GameLoopController gameLoop;

    private VBox interactionOverlay;

    @Override
    public void start(Stage primaryStage) {
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
        // Inizializza le Viste
        gameView = new it.unicam.cs.mpgc.rpg129543.view.GameView(player);
        hudView = new it.unicam.cs.mpgc.rpg129543.view.HudView(() -> {
            if (!isInteracting) showMemoryArchive();
        });

        VBox root = new VBox(hudView.getHudNode(), gameView.getGameArea());
        root.setStyle("-fx-background-color: #000;");

        Scene scene = new Scene(root, 800, 650);

        // Inizializza i Controller periferici
        inputController = new it.unicam.cs.mpgc.rpg129543.controller.InputController(scene,
                () -> togglePauseMenu(),
                () -> { if (!isInteracting) showMemoryArchive(); }
        );

        gameLoop = new it.unicam.cs.mpgc.rpg129543.controller.GameLoopController(
                () -> { if (!isInteracting) { updatePhysics(); checkInteractions(); } },
                () -> gameView.renderPlayer(player)
        );
        gameLoop.startLoop();

        // Aggiorna lo stato iniziale a schermo
        aggiornaStanzaEHud();

        if (player.getKarma() == 0 && player.getRicordi().isEmpty() && gameState.getCurrentRoom().id() == 0) {
            showIntro();
        }

        primaryStage.setTitle("Purgatorio RPG - Metodologie 2025/26");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void centrareOverlay(VBox overlay) {
        overlay.layoutXProperty().bind(gameView.getGameArea().widthProperty().divide(2).subtract(overlay.prefWidthProperty().divide(2)));
        overlay.layoutYProperty().bind(gameView.getGameArea().heightProperty().divide(2).subtract(overlay.prefHeightProperty().divide(2)));
    }

    private void updatePhysics() {
        boolean isMoving = false;
        if (inputController.isPressed(javafx.scene.input.KeyCode.W)) { player.moveUp(); isMoving = true; }
        if (inputController.isPressed(javafx.scene.input.KeyCode.S)) { player.moveDown(); isMoving = true; }
        if (inputController.isPressed(javafx.scene.input.KeyCode.A)) { player.moveLeft(); isMoving = true; }
        if (inputController.isPressed(javafx.scene.input.KeyCode.D)) { player.moveRight(); isMoving = true; }

        // AGGIUNGI IL PLAYER COME SECONDO ARGOMENTO QUI:
        gameView.updateAnimation(isMoving, player);

        if (player.getX() < 20) player.setX(20);
        if (player.getX() > 780) player.setX(780);
        if (player.getY() < 20) player.setY(20);
        if (player.getY() > 580) player.setY(580);
    }

    private void checkInteractions() {
        if (isInteracting) return; // Se siamo già in un'interazione, blocca qualsiasi calcolo di collisione

        Room current = gameState.getCurrentRoom();
        if (!current.hasChallenge() && !current.hasFragment()) return;

        double distBoss = Math.sqrt(Math.pow(player.getX() - current.npcX(), 2) + Math.pow(player.getY() - current.npcY(), 2));
        double distFrag = Math.sqrt(Math.pow(player.getX() - current.fragX(), 2) + Math.pow(player.getY() - current.fragY(), 2));
        double distToDoor = Math.sqrt(Math.pow(player.getX() - current.doorX(), 2) + Math.pow(player.getY() - current.doorY(), 2));

        // 1. Controllo interazione con il Boss
        if (current.hasChallenge() && distBoss < 60) {
            isInteracting = true;
            if (current.isSfidaGestita()) {
                showLoreOnly(current);
            } else {
                startChoiceMenu(current.sfida());
            }
            return;
        }

        // 2. Controllo raccolta Stella / Frammento
        if (current.hasFragment() && !player.getRicordi().contains(current.ricordoSbloccato()) && distFrag < 40) {
            isInteracting = true;
            player.addRicordo(current.ricordoSbloccato());
            showMemoryPopup(current.ricordoSbloccato());
            return;
        }

        // 3. Controllo passaggio attraverso la Porta
        if (distToDoor < 50 && current.isSfidaGestita()) {
            isInteracting = true; // Blocca temporaneamente per evitare doppi passaggi
            if (gameState.nextRoom()) {
                player.setX(50);
                player.setY(300);
                aggiornaStanzaEHud();
                persistence.save(player);
                isInteracting = false; // Rilascia il blocco solo a stanza caricata
            } else {
                showFinalJudgment();
            }
        }
    }

    private void aggiornaStanzaEHud() {
        gameView.refreshRoomGraphics(gameState.getCurrentRoom(), player);
        updateStatusBar();
    }

    private void updateStatusBar() {
        if (hudView != null) {
            hudView.updateStatus(
                    player.getKarma(),
                    player.getLivello(),
                    player.getXp(),
                    player.getXpNecessari(),
                    gameState.getCurrentRoom().id() + 1
            );
        }
    }

    private void repelPlayer(Room room) {
        if (player.getX() > room.npcX()) player.setX(player.getX() + 50);
        else player.setX(player.getX() - 50);
    }

    // ==========================================
    // GESTIONE SCHERMATE ISOLATE (OVERLAYS)
    // ==========================================

    private void showIntro() {
        // Pulisci prima di aggiungere
        if (interactionOverlay != null) {
            gameView.getGameArea().getChildren().remove(interactionOverlay);
        }

        isInteracting = true;
        it.unicam.cs.mpgc.rpg129543.view.StartMenuView introView = new it.unicam.cs.mpgc.rpg129543.view.StartMenuView(() -> {
            showCharacterSelection();
        });
        interactionOverlay = introView.getView();
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    private void showCharacterSelection() {
        if (interactionOverlay != null) {
            gameView.getGameArea().getChildren().remove(interactionOverlay);
        }

        it.unicam.cs.mpgc.rpg129543.view.CharacterSelectionView selectionView = new it.unicam.cs.mpgc.rpg129543.view.CharacterSelectionView(gender -> {
            player.setGenereSprite(gender);
            gameView.updatePlayerSprite(player);
            persistence.save(player);

            if (interactionOverlay != null) {
                gameView.getGameArea().getChildren().remove(interactionOverlay);
            }

            isInteracting = false;
            gameView.getGameArea().requestFocus();
        });

        interactionOverlay = selectionView.getView();
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    private void showPauseMenu() {
        isInteracting = true;
        isMenuOpen = true;
        inputController.clearKeys();

        it.unicam.cs.mpgc.rpg129543.view.PauseMenuView pauseMenu = new it.unicam.cs.mpgc.rpg129543.view.PauseMenuView(
                () -> togglePauseMenu(),
                () -> { persistence.save(player); Platform.exit(); },
                () -> { resetGame(); isMenuOpen = false; showIntro(); }
        );

        interactionOverlay = pauseMenu.getView();
        interactionOverlay.setLayoutX(250);
        interactionOverlay.setLayoutY(100);
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    private void togglePauseMenu() {
        if (isMenuOpen) {
            gameView.getGameArea().getChildren().remove(interactionOverlay);
            isInteracting = false;
            isMenuOpen = false;
        } else {
            showPauseMenu();
        }
    }

    private void showMemoryArchive() {
        isInteracting = true;
        inputController.clearKeys();
        it.unicam.cs.mpgc.rpg129543.view.MemoryArchiveView archiveView = new it.unicam.cs.mpgc.rpg129543.view.MemoryArchiveView(player.getRicordi(), () -> {
            gameView.getGameArea().getChildren().remove(interactionOverlay);
            isInteracting = false;
            gameView.getGameArea().requestFocus();
        });
        interactionOverlay = archiveView.getView();
        centrareOverlay(interactionOverlay);
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    private void showMemoryPopup(String memory) {
        isInteracting = true;
        it.unicam.cs.mpgc.rpg129543.view.MessageView popup = new it.unicam.cs.mpgc.rpg129543.view.MessageView(
                "MEMORIA RITROVATA", memory, "Ricorda", "cyan", () -> {
            gameView.getGameArea().getChildren().remove(interactionOverlay);
            isInteracting = false;
            gameView.getGameArea().requestFocus();
        }
        );
        interactionOverlay = popup.getView();
        centrareOverlay(interactionOverlay);
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    private void showLoreOnly(Room room) {
        // Valori di default per l'indifferenza o la fuga (Karma negativo)
        String titolo = "PRESENZA IGNORATA";
        String esito = "\n\nLo spettro continua a contorcersi nel suo eterno tormento. Hai scelto di voltare lo sguardo, confermando la tua natura.";
        String colore = "#e74c3c"; // Rosso per la colpa

        // Se la sfida è stata effettivamente vinta/completata (Karma positivo)
        if (room.hasChallenge() && room.sfida().isCompletata()) {
            titolo = "PURIFICAZIONE";
            esito = "\n\nLa vittima ha trovato pace. Il rimorso è stato espiato.";
            colore = "#f1c40f"; // Oro per la redenzione
        }

        it.unicam.cs.mpgc.rpg129543.view.MessageView loreView = new it.unicam.cs.mpgc.rpg129543.view.MessageView(
                titolo, room.descrizione() + esito, "Chiudi", colore, () -> {
            gameView.getGameArea().getChildren().remove(interactionOverlay);
            isInteracting = false;
            player.setX(player.getX() + 60);
            gameView.getGameArea().requestFocus();
        });

        interactionOverlay = loreView.getView();
        centrareOverlay(interactionOverlay);
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    private void finishInteraction(String text) {
        gameView.getGameArea().getChildren().remove(interactionOverlay);
        it.unicam.cs.mpgc.rpg129543.view.MessageView finishView = new it.unicam.cs.mpgc.rpg129543.view.MessageView(
                "ESITO SCONTRO", text, "Prosegui", "#f1c40f", () -> {
            gameView.getGameArea().getChildren().remove(interactionOverlay);
            isInteracting = false;
            player.setX(player.getX() + 60);
            aggiornaStanzaEHud();
        }
        );
        interactionOverlay = finishView.getView();
        centrareOverlay(interactionOverlay);
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    // ==========================================
    // BATTAGLIA E FINE GIOCO
    // ==========================================

    private void startChoiceMenu(Challenge challenge) {
        inputController.clearKeys();
        Room currentRoom = gameState.getCurrentRoom();

        if (challenge.isCombat()) {
            // [Il blocco del combattimento rimane invariato]
            interactionOverlay = UIManager.createCombatChoiceMenu(
                    "L'Ombra del Passato",
                    "\"Viandante... la fuga ha un prezzo, ma il pentimento restituisce forza al cuore.\"",
                    VALORE_PENALITA,
                    () -> {
                        if (currentRoom.isKarmaGiaTolto()) {
                            player.addKarma(VALORE_PENALITA);
                            currentRoom.setKarmaGiaTolto(false);
                            updateStatusBar();
                        }
                        showBattleTutorial(challenge);
                    },
                    () -> {
                        if (!currentRoom.isKarmaGiaTolto()) {
                            player.addKarma(-VALORE_PENALITA);
                            currentRoom.setKarmaGiaTolto(true);
                            updateStatusBar();
                        }
                        currentRoom.setSfidaGestita(true);
                        finishInteraction("Hai scelto l'indifferenza. La porta è aperta.");
                    }
            );
        } else if (challenge.isSkillCheck()) {
            SkillCheckChallenge skc = (SkillCheckChallenge) challenge;
            interactionOverlay = UIManager.createSkillCheckMenu(
                    "Prova di Consapevolezza",
                    skc.getDialogo(),
                    () -> {
                        // Risolve la sfida (tira il dado) solo dopo il click
                        String esitoLancio = challenge.risolvi(player, currentRoom.id());

                        gameView.getGameArea().getChildren().remove(interactionOverlay);
                        interactionOverlay = UIManager.createNarrativeMenu(
                                "Esito della Prova",
                                esitoLancio,
                                () -> {
                                    currentRoom.setSfidaGestita(true);
                                    gameView.getGameArea().getChildren().remove(interactionOverlay);
                                    repelPlayer(currentRoom);
                                    isInteracting = false;
                                    aggiornaStanzaEHud();
                                    gameView.getGameArea().requestFocus();
                                }
                        );
                        centrareOverlay(interactionOverlay);
                        gameView.getGameArea().getChildren().add(interactionOverlay);
                    }
            );
        } else {
            // [Il blocco del NarrativeMenu rimane invariato]
            interactionOverlay = UIManager.createNarrativeMenu(
                    "Presenza Silenziosa",
                    challenge.risolvi(player, currentRoom.id()),
                    () -> {
                        currentRoom.setSfidaGestita(true);
                        gameView.getGameArea().getChildren().remove(interactionOverlay);
                        repelPlayer(currentRoom);
                        isInteracting = false;
                        aggiornaStanzaEHud();
                        gameView.getGameArea().requestFocus();
                    }
            );
        }

        centrareOverlay(interactionOverlay);
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    private void showBattleTutorial(Challenge challenge) {
        interactionOverlay.getChildren().clear();

        // Sappiamo che è un combat perché è stato filtrato a monte da startChoiceMenu
        CombatChallenge combatChallenge = (CombatChallenge) challenge;

        VBox tutorialMenu = UIManager.createBattleTutorialMenu(
                combatChallenge.getNomeNemico(),
                combatChallenge.getDescrizioneDettagliata(),
                player.getLivello(),
                () -> {
                    BattleEngine.BossMood mood = BattleEngine.BossMood.values()[(int)(Math.random() * 3)];
                    currentBattle = new BattleEngine(mood);
                    isInSubMenuVirtu = false;

                    // I dialoghi sono presi direttamente dall'oggetto, zero hardcoding!
                    currentEnemy = new Enemy(
                            combatChallenge.getNomeNemico(),
                            100,
                            mood.name(),
                            combatChallenge.getfRabbia(),
                            combatChallenge.getfPaura(),
                            combatChallenge.getfColpa()
                    );

                    updateBattleUI("La nebbia si focalizza. Lo scontro ha inizio.");
                }
        );

        interactionOverlay.getChildren().add(tutorialMenu);
    }

    private void updateBattleUI(String logText) {
        gameView.getGameArea().getChildren().remove(interactionOverlay);

        if (currentEnemy.getHp() <= 0) {
            int premioXp = (gameState.getCurrentRoom().id() + 1) * 50;
            player.addKarma(30);
            boolean haLivellato = player.addXp(premioXp);
            gameState.getCurrentRoom().solveChallenge();

            if (haLivellato) showLevelUpNotification(premioXp);
            else finishInteraction("VITTORIA! Sconfiggi lo spettro ed accumuli +" + premioXp + " XP.");
            return;
        } else if (player.getHp() <= 0) {
            finishInteraction("L'OSCURITÀ TI HA CONSUMATO...");
            resetGame();
            return;
        }

        it.unicam.cs.mpgc.rpg129543.view.BattleView battleView = new it.unicam.cs.mpgc.rpg129543.view.BattleView(
                player, currentEnemy, currentBattle, logText, isInSubMenuVirtu,
                mossa -> {
                    if (mossa.equals("INDIETRO")) {
                        isInSubMenuVirtu = false;
                        updateBattleUI(logText);
                    } else if (mossa.equals("APRI_VIRTU")) {
                        isInSubMenuVirtu = true;
                        updateBattleUI(logText);
                    } else {
                        processBattle(mossa);
                    }
                },
                () -> {
                    gameState.getCurrentRoom().setSfidaGestita(true);
                    finishInteraction("Sei fuggito perdendo terreno.");
                }
        );

        interactionOverlay = battleView.getView();
        centrareOverlay(interactionOverlay);
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    private void processBattle(String mossaInput) {
        String res = "";

        // 1. Intercetta le risposte all'Anomalia
        if (mossaInput.equals("ACCETTA_PATTO")) {
            res = currentBattle.getAnomalieEngine().applicaSceltaA(player, currentEnemy, currentBattle);
        } else if (mossaInput.equals("RIFIUTA_PATTO")) {
            currentBattle.getAnomalieEngine().disattiva();
            res = "Hai rifiutato il patto con l'ignoto. L'illusione svanisce e lo scontro riprende.";
        }
        // 2. Flusso di combattimento standard
        else {
            String mossaPulita = mossaInput.split(" ")[0].trim();
            res = currentBattle.executeTurn(player, currentEnemy, mossaPulita, gameState);

            // 3. Calcola se il prossimo turno avrà un'anomalia (solo se nessuno è morto)
            if (currentEnemy.getHp() > 0 && player.getHp() > 0) {
                currentBattle.getAnomalieEngine().controllaInnesco();
            }
        }

        if (res.contains("violentemente")) {
            gameView.applyDamageEffect();
        }
        updateBattleUI(res);
    }

    private void showLevelUpNotification(int xpGuadagnati) {
        String desc = "Hai accumulato +" + xpGuadagnati + " XP. La tua Anima ascende al LIVELLO " + player.getLivello() + "!\n\n" +
                "Le tue proprietà spirituali si sono espanse permanentemente:\n" +
                "Determinazione (ATK): " + player.getDeterminazione() + " (+2)\n" +
                "Resilienza (DEF): " + player.getResilienza() + " (+2)\n" +
                "Sintonia (HP Max): " + player.getHpMax() + " (" + player.getHp() + " HP Correnti)";

        it.unicam.cs.mpgc.rpg129543.view.MessageView lvlView = new it.unicam.cs.mpgc.rpg129543.view.MessageView(
                "CONSEGUIMENTO DELLA CONSAPEVOLEZZA", desc, "Prendi Consapevolezza", "#f1c40f", () -> {
            gameView.getGameArea().getChildren().remove(interactionOverlay);
            repelPlayer(gameState.getCurrentRoom());
            isInteracting = false;
            aggiornaStanzaEHud();
        });
        interactionOverlay = lvlView.getView();
        centrareOverlay(interactionOverlay);
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    private void showFinalJudgment() {
        isInteracting = true;
        gameView.getGameArea().getChildren().clear();
        inputController.clearKeys();

        // Deleghiamo interamente la creazione dell'interfaccia a UIManager
        ScrollPane finalScreen = UIManager.createFinalJudgmentMenu(player.getKarma(), this::resetGame);
        gameView.getGameArea().getChildren().add(finalScreen);
    }

    private void resetGame() {
        // 1. Rimuovi forzatamente l'overlay se esiste
        if (interactionOverlay != null) {
            gameView.getGameArea().getChildren().remove(interactionOverlay);
            interactionOverlay = null;
        }

        // 2. Resetta il giocatore e lo stato
        this.player = new Player("Anima", "Ombra", "Viandante");
        this.player.setHp(100);
        this.player.setKarma(0);
        this.gameState = new GameState(player);

        // 3. Resetta i flag di interazione
        this.isInteracting = false;

        persistence.save(player);
        aggiornaStanzaEHud();

        // 4. Riavvia il flusso
        showIntro();
    }

    public static void main(String[] args) {
        launch(args);
    }
}