package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.api.ChallengeResult;
import it.unicam.cs.mpgc.rpg129543.model.*;
import it.unicam.cs.mpgc.rpg129543.persistence.PersistenceManager;
import it.unicam.cs.mpgc.rpg129543.view.*;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class GameRouter {
    private final Player player;
    private final GameState gameState;
    private final GameView gameView;
    private final HudView hudView;
    private final PersistenceManager persistence;
    private final Runnable onReset;
    private final Runnable onLoop;

    private VBox interactionOverlay;
    private boolean isMenuOpen = false;
    private static final int VALORE_PENALITA = 15;

    // Riferimenti ai controller instradati
    private GameController gameController;
    private BattleController battleController;
    private InputController inputController;

    public GameRouter(Player player, GameState gameState, GameView gameView, HudView hudView, PersistenceManager persistence, Runnable onReset, Runnable onLoop) {
        this.player = player;
        this.gameState = gameState;
        this.gameView = gameView;
        this.hudView = hudView;
        this.persistence = persistence;
        this.onReset = onReset;
        this.onLoop = onLoop;
    }

    public void setControllers(GameController gc, BattleController bc, InputController ic) {
        this.gameController = gc;
        this.battleController = bc;
        this.inputController = ic;
    }

    public void endInteraction() {
        gameController.setInteracting(false);
        gameView.getGameArea().requestFocus();
    }

    public void mostraOverlayCentrato(VBox overlay) {
        rimuoviOverlay();
        interactionOverlay = overlay;
        interactionOverlay.layoutXProperty().bind(gameView.getGameArea().widthProperty().divide(2).subtract(interactionOverlay.prefWidthProperty().divide(2)));
        interactionOverlay.layoutYProperty().bind(gameView.getGameArea().heightProperty().divide(2).subtract(interactionOverlay.prefHeightProperty().divide(2)));
        gameView.getGameArea().getChildren().add(interactionOverlay);
    }

    public void rimuoviOverlay() {
        if (interactionOverlay != null) {
            gameView.getGameArea().getChildren().remove(interactionOverlay);
            interactionOverlay = null;
        }
    }

    public void aggiornaStanzaEHud() {
        gameView.refreshRoomGraphics(gameState.getCurrentRoom(), player);
        if (hudView != null) {
            hudView.updateStatus(player.getKarma(), player.getLivello(), player.getXp(), player.getXpNecessari(), gameState.getCurrentRoom().id() + 1);
        }
    }

    private void repelPlayer(Room room) {
        if (player.getX() > room.npcX()) player.setX(player.getX() + 50);
        else player.setX(player.getX() - 50);
    }

    public void startCombatChoice(Challenge challenge) {
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

    public void startSkillCheckChoice(Challenge challenge) {
        inputController.clearKeys();
        Room currentRoom = gameState.getCurrentRoom();
        SkillCheckChallenge skc = (SkillCheckChallenge) challenge;
        VBox menu = UIManager.createSkillCheckMenu(
                "Prova di Consapevolezza",
                skc.getDialogo(),
                () -> {
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
                                endInteraction();
                                aggiornaStanzaEHud();
                            }
                    );
                    mostraOverlayCentrato(narrativeMenu);
                }
        );
        mostraOverlayCentrato(menu);
    }

    public void startNarrativeChoice(Challenge challenge) {
        inputController.clearKeys();
        Room currentRoom = gameState.getCurrentRoom();
        NarrativeChallenge nc = (NarrativeChallenge) challenge;
        ChallengeResult risultato = challenge.risolvi(player, currentRoom.id());
        String testoFormattato = nc.getNomeNPC().toUpperCase() + ":\n" + risultato.getMessaggioLogico() + "\n\n[Sblocchi l'indizio: " + risultato.getEntitaCoinvolta() + "]";
        VBox menu = UIManager.createNarrativeMenu(
                "Presenza Silenziosa",
                testoFormattato,
                () -> {
                    currentRoom.setSfidaGestita(true);
                    rimuoviOverlay();
                    repelPlayer(currentRoom);
                    endInteraction();
                    aggiornaStanzaEHud();
                }
        );
        mostraOverlayCentrato(menu);
    }

    public void showIntro() {
        gameController.setInteracting(true);
        CharacterSelectionView selectionView = new CharacterSelectionView(gender -> {
            player.setGenereSprite(gender);
            gameView.updatePlayerSprite(player);
            persistence.save(player);
            rimuoviOverlay();
            endInteraction();
        });
        StartMenuView introView = new StartMenuView(() -> mostraOverlayCentrato(selectionView.getView()));
        mostraOverlayCentrato(introView.getView());
    }

    public void showPauseMenu() {
        gameController.setInteracting(true);
        isMenuOpen = true;
        inputController.clearKeys();
        PauseMenuView pauseMenu = new PauseMenuView(
                this::togglePauseMenu,
                () -> { persistence.save(player); Platform.exit(); },
                () -> { onReset.run(); isMenuOpen = false; showIntro(); }
        );
        mostraOverlayCentrato(pauseMenu.getView());
    }

    public void togglePauseMenu() {
        if (isMenuOpen) {
            rimuoviOverlay();
            endInteraction();
            isMenuOpen = false;
        } else {
            showPauseMenu();
        }
    }

    public void showMemoryArchive() {
        if (interactionOverlay != null) return;
        gameController.setInteracting(true);
        inputController.clearKeys();
        MemoryArchiveView archiveView = new MemoryArchiveView(player.getRicordi(), () -> {
            rimuoviOverlay();
            endInteraction();
        });
        mostraOverlayCentrato(archiveView.getView());
    }

    public void showMemoryPopup(String memory) {
        MessageView popup = new MessageView("MEMORIA RITROVATA", memory, "Ricorda", "cyan", () -> {
            rimuoviOverlay();
            endInteraction();
        });
        mostraOverlayCentrato(popup.getView());
    }

    public void showLoreOnly(Room room) {
        String titolo = (room.hasChallenge() && room.sfida().isCompletata()) ? "PURIFICAZIONE" : "PRESENZA IGNORATA";
        String esito = (room.hasChallenge() && room.sfida().isCompletata()) ? "\n\nLa vittima ha trovato pace. Il rimorso è stato espiato." : "\n\nLo spettro continua a contorcersi nel suo eterno tormento. Hai scelto di voltare lo sguardo, confermando la tua natura.";
        String colore = (room.hasChallenge() && room.sfida().isCompletata()) ? "#f1c40f" : "#e74c3c";
        MessageView loreView = new MessageView(titolo, room.descrizione() + esito, "Chiudi", colore, () -> {
            rimuoviOverlay();
            player.setX(player.getX() + 60);
            endInteraction();
        });
        mostraOverlayCentrato(loreView.getView());
    }

    public void mostraMessaggioUscita(String titolo, String testo) {
        MessageView msgView = new MessageView(titolo, testo, "Prosegui", "#f1c40f", () -> {
            rimuoviOverlay();
            player.setX(player.getX() + 60);
            endInteraction();
            aggiornaStanzaEHud();
        });
        mostraOverlayCentrato(msgView.getView());
    }

    public void showLevelUpNotification(int xpGuadagnati) {
        String desc = "Hai accumulato +" + xpGuadagnati + " XP. La tua Anima ascende al LIVELLO " + player.getLivello() + "!\n\n" +
                "Le tue proprietà spirituali si sono espanse permanentemente:\n" +
                "Determinazione (ATK): " + player.getDeterminazione() + " (+2)\n" +
                "Resilienza (DEF): " + player.getResilienza() + " (+2)\n" +
                "Sintonia (HP Max): " + player.getHpMax() + " (" + player.getHp() + " HP Correnti)";
        if (!player.isTutorialLivelloVisto()) {
            desc += "\n\n[SISTEMA]: Crescere di livello è vitale. Questo luogo è un loop. " +
                    "Se raggiungerai la fine, il ciclo ricomincerà e gli Spettri torneranno più forti. " +
                    "La tua unica speranza di sopravvivere ai futuri cicli è accumulare potere ora.";
            player.setTutorialLivelloVisto(true);
        }
        MessageView lvlView = new MessageView("CONSEGUIMENTO DELLA CONSAPEVOLEZZA", desc, "Prendi Consapevolezza", "#f1c40f", () -> {
            rimuoviOverlay();
            repelPlayer(gameState.getCurrentRoom());
            endInteraction();
            aggiornaStanzaEHud();
        });
        mostraOverlayCentrato(lvlView.getView());
    }

    public void showFinalJudgment() {
        gameController.setInteracting(true);
        gameView.getGameArea().getChildren().clear();
        inputController.clearKeys();
        ScrollPane finalScreen = UIManager.createFinalJudgmentMenu(player.getKarma(), onReset, onLoop);
        gameView.getGameArea().getChildren().add(finalScreen);
    }
}