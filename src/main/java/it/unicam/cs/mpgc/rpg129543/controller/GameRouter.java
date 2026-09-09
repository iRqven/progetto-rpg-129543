package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.api.ChallengeResult;
import it.unicam.cs.mpgc.rpg129543.model.*;
import it.unicam.cs.mpgc.rpg129543.persistence.PersistenceManager;
import it.unicam.cs.mpgc.rpg129543.util.GameplayConstants;
import it.unicam.cs.mpgc.rpg129543.view.*;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * Coordina la navigazione tra le schermate del gioco, delegando la gestione
 * dell'overlay a OverlayManager. Implementa le callback richieste da
 * GameController e BattleController.
 */
public class GameRouter implements GameNavigationCallbacks, BattleCallbacks {
    private final Player player;
    private final GameState gameState;
    private final GameView gameView;
    private final HudView hudView;
    private final PersistenceManager persistence;
    private final Runnable onReset;
    private final Runnable onLoop;
    private final OverlayManager overlayManager;

    private boolean isMenuOpen = false;

    private GameController gameController;
    private BattleController battleController;
    private InputController inputController;

    public GameRouter(Player player, GameState gameState, GameView gameView, HudView hudView,
                      PersistenceManager persistence, Runnable onReset, Runnable onLoop) {
        this.player = player;
        this.gameState = gameState;
        this.gameView = gameView;
        this.hudView = hudView;
        this.persistence = persistence;
        this.onReset = onReset;
        this.onLoop = onLoop;
        this.overlayManager = new OverlayManager(gameView);
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

    public void chiudiOverlayCorrente() {
        overlayManager.remove();
    }

    private void repelPlayer(Room room) {
        if (player.getX() > room.npcX()) player.setX(player.getX() + 50);
        else player.setX(player.getX() - 50);
    }

    public void aggiornaStanzaEHud() {
        gameView.refreshRoomGraphics(gameState.getCurrentRoom(), player);
        if (hudView != null) {
            hudView.updateStatus(player.getKarma(), player.getLivello(), player.getXp(),
                    player.getXpNecessari(), gameState.getCurrentRoom().id() + 1);
        }
    }

    // ---- GameNavigationCallbacks ----

    @Override
    public void onStartCombat(Challenge challenge) {
        inputController.clearKeys();
        Room currentRoom = gameState.getCurrentRoom();
        VBox menu = UIManager.createCombatChoiceMenu(
                "L'Ombra del Passato",
                "\"Viandante... la fuga ha un prezzo, ma il pentimento restituisce forza al cuore.\"",
                GameplayConstants.KARMA_PENALTY_IGNORE_CHALLENGE,
                () -> {
                    if (currentRoom.isKarmaGiaTolto()) {
                        player.addKarma(GameplayConstants.KARMA_PENALTY_IGNORE_CHALLENGE);
                        currentRoom.setKarmaGiaTolto(false);
                        aggiornaStanzaEHud();
                    }
                    battleController.startBattleTutorial((CombatChallenge) challenge);
                },
                () -> {
                    if (!currentRoom.isKarmaGiaTolto()) {
                        player.addKarma(-GameplayConstants.KARMA_PENALTY_IGNORE_CHALLENGE);
                        currentRoom.setKarmaGiaTolto(true);
                        aggiornaStanzaEHud();
                    }
                    currentRoom.setSfidaGestita(true);
                    mostraMessaggioUscita("ESITO SCONTRO", "Hai scelto l'indifferenza. La porta è aperta.");
                }
        );
        overlayManager.showCentered(menu);
    }

    @Override
    public void onStartSkillCheck(Challenge challenge) {
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
                                overlayManager.remove();
                                repelPlayer(currentRoom);
                                endInteraction();
                                aggiornaStanzaEHud();
                            }
                    );
                    overlayManager.showCentered(narrativeMenu);
                }
        );
        overlayManager.showCentered(menu);
    }

    @Override
    public void onStartNarrative(Challenge challenge) {
        inputController.clearKeys();
        Room currentRoom = gameState.getCurrentRoom();
        NarrativeChallenge nc = (NarrativeChallenge) challenge;
        ChallengeResult risultato = challenge.risolvi(player, currentRoom.id());
        String testoFormattato = nc.getNomeNPC().toUpperCase() + ":\n" + risultato.getMessaggioLogico() +
                "\n\n[Sblocchi l'indizio: " + risultato.getEntitaCoinvolta() + "]";
        VBox menu = UIManager.createNarrativeMenu(
                "Presenza Silenziosa",
                testoFormattato,
                () -> {
                    currentRoom.setSfidaGestita(true);
                    overlayManager.remove();
                    repelPlayer(currentRoom);
                    endInteraction();
                    aggiornaStanzaEHud();
                }
        );
        overlayManager.showCentered(menu);
    }

    @Override
    public void onLoreOnly(Room room) {
        boolean risolta = room.hasChallenge() && room.sfida().isCompletata();
        String titolo = risolta ? "PURIFICAZIONE" : "PRESENZA IGNORATA";
        String esito = risolta
                ? "\n\nLa vittima ha trovato pace. Il rimorso è stato espiato."
                : "\n\nLo spettro continua a contorcersi nel suo eterno tormento. Hai scelto di voltare lo sguardo, confermando la tua natura.";
        String colore = risolta ? "#f1c40f" : "#e74c3c";
        MessageView loreView = new MessageView(titolo, room.descrizione() + esito, "Chiudi", colore, () -> {
            overlayManager.remove();
            player.setX(player.getX() + GameplayConstants.POST_INTERACTION_STEP);
            endInteraction();
        });
        overlayManager.showCentered(loreView.getView());
    }

    @Override
    public void onMemoryCollected(String memory) {
        MessageView popup = new MessageView("MEMORIA RITROVATA", memory, "Ricorda", "cyan", () -> {
            overlayManager.remove();
            endInteraction();
        });
        overlayManager.showCentered(popup.getView());
    }

    @Override
    public void onNextRoom() {
        aggiornaStanzaEHud();
        persistence.save(player);
        endInteraction();
    }

    @Override
    public void onFinalJudgment() {
        gameController.setInteracting(true);
        gameView.getGameArea().getChildren().clear();
        inputController.clearKeys();
        ScrollPane finalScreen = UIManager.createFinalJudgmentMenu(player.getKarma(), onReset, onLoop);
        gameView.getGameArea().getChildren().add(finalScreen);
    }

    // ---- BattleCallbacks ----

    @Override
    public void showOverlay(VBox overlay) { overlayManager.showCentered(overlay); }

    @Override
    public void clearOverlay() { overlayManager.remove(); }

    @Override
    public void updateHud() { aggiornaStanzaEHud(); }

    @Override
    public void onLevelUp(int xpGuadagnati) {
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
            overlayManager.remove();
            repelPlayer(gameState.getCurrentRoom());
            endInteraction();
            aggiornaStanzaEHud();
        });
        overlayManager.showCentered(lvlView.getView());
    }

    @Override
    public void onGameOver() {
        onReset.run();
    }

    @Override
    public void onBattleEnd() {
        endInteraction();
    }

    @Override
    public void onDamageEffect() {
        gameView.applyDamageEffect();
    }

    // ---- Altre schermate ----

    public void showIntro() {
        gameController.setInteracting(true);
        gameView.setHudVisible(false);

        CharacterSelectionView selectionView = new CharacterSelectionView(gender -> {
            player.setGenereSprite(gender);
            gameView.updatePlayerSprite(player);
            persistence.save(player);
            overlayManager.remove();
            gameView.setHudVisible(true);
            endInteraction();
        });
        StartMenuView introView = new StartMenuView(() -> overlayManager.showCentered(selectionView.getView()));
        overlayManager.showCentered(introView.getView());
    }

    public void showPauseMenu() {
        gameController.setInteracting(true);
        isMenuOpen = true;
        inputController.clearKeys();
        PauseMenuView pauseMenu = new PauseMenuView(
                this::togglePauseMenu,
                () -> {
                    Platform.exit();
                },
                () -> { onReset.run(); isMenuOpen = false; showIntro(); }
        );
        overlayManager.showCentered(pauseMenu.getView());
    }

    public void togglePauseMenu() {
        if (isMenuOpen) {
            overlayManager.remove();
            endInteraction();
            isMenuOpen = false;
        } else {
            showPauseMenu();
        }
    }

    public void showMemoryArchive() {
        if (overlayManager.isShowing()) return;
        gameController.setInteracting(true);
        inputController.clearKeys();
        MemoryArchiveView archiveView = new MemoryArchiveView(player.getRicordi(), () -> {
            overlayManager.remove();
            endInteraction();
        });
        overlayManager.showCentered(archiveView.getView());
    }

    public void mostraMessaggioUscita(String titolo, String testo) {
        MessageView msgView = new MessageView(titolo, testo, "Prosegui", "#f1c40f", () -> {
            overlayManager.remove();
            player.setX(player.getX() + GameplayConstants.POST_INTERACTION_STEP);
            endInteraction();
            aggiornaStanzaEHud();
        });
        overlayManager.showCentered(msgView.getView());
    }
}