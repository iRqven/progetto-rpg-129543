package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.model.GameState;
import it.unicam.cs.mpgc.rpg129543.model.Player;
import it.unicam.cs.mpgc.rpg129543.model.Room;
import it.unicam.cs.mpgc.rpg129543.util.GameplayConstants;
import it.unicam.cs.mpgc.rpg129543.util.GeometryUtils;
import it.unicam.cs.mpgc.rpg129543.view.GameView;
import it.unicam.cs.mpgc.rpg129543.view.HudView;
import javafx.scene.input.KeyCode;

import java.util.Objects;

/** Controller dedicato alla fisica, al movimento e al rilevamento delle collisioni. */
public class GameController {
    private static final double MIN_X = 20.0;
    private static final double MAX_X = 780.0;
    private static final double MIN_Y = 20.0;
    private static final double MAX_Y = 580.0;

    private final Player player;
    private final GameState gameState;
    private final GameView gameView;
    private final HudView hudView;
    private final InputController inputController;
    private final GameNavigationCallbacks callbacks;

    private boolean isInteracting = false;

    public GameController(Player player, GameState gameState, GameView gameView, HudView hudView,
                          InputController inputController, GameNavigationCallbacks callbacks) {
        this.player = Objects.requireNonNull(player);
        this.gameState = Objects.requireNonNull(gameState);
        this.gameView = Objects.requireNonNull(gameView);
        this.hudView = hudView;
        this.inputController = Objects.requireNonNull(inputController);
        this.callbacks = Objects.requireNonNull(callbacks);
    }

    public void update() {
        if (isInteracting) return;
        updatePhysics();
        checkInteractions();
    }

    private void updatePhysics() {
        boolean isMoving = false;

        if (inputController.isPressed(KeyCode.W) || inputController.isPressed(KeyCode.UP)) { player.moveUp(); isMoving = true; }
        if (inputController.isPressed(KeyCode.S) || inputController.isPressed(KeyCode.DOWN)) { player.moveDown(); isMoving = true; }
        if (inputController.isPressed(KeyCode.A) || inputController.isPressed(KeyCode.LEFT)) { player.moveLeft(); isMoving = true; }
        if (inputController.isPressed(KeyCode.D) || inputController.isPressed(KeyCode.RIGHT)) { player.moveRight(); isMoving = true; }

        gameView.updateAnimation(isMoving, player);

        if (player.getX() < MIN_X) player.setX(MIN_X);
        if (player.getX() > MAX_X) player.setX(MAX_X);
        if (player.getY() < MIN_Y) player.setY(MIN_Y);
        if (player.getY() > MAX_Y) player.setY(MAX_Y);
    }

    private void checkInteractions() {
        Room current = gameState.getCurrentRoom();
        if (!current.hasChallenge() && !current.hasFragment()) return;

        double distBoss = GeometryUtils.distanza(player.getX(), player.getY(), current.npcX(), current.npcY());
        double distFrag = GeometryUtils.distanza(player.getX(), player.getY(), current.fragX(), current.fragY());
        double distToDoor = GeometryUtils.distanza(player.getX(), player.getY(), current.doorX(), current.doorY());

        if (current.hasChallenge() && distBoss < Room.INTERACTION_RADIUS_BOSS) {
            handleBossInteraction(current);
            return;
        }

        if (current.hasFragment() && !current.isFrammentoRaccolto() && distFrag < Room.INTERACTION_RADIUS_FRAG) {
            handleFragmentInteraction(current);
            return;
        }

        if (distToDoor < Room.INTERACTION_RADIUS_DOOR && current.isSfidaGestita()) {
            handleDoorInteraction();
        }
    }

    private void handleBossInteraction(Room current) {
        isInteracting = true;
        if (current.isSfidaGestita()) {
            callbacks.onLoreOnly(current);
            return;
        }
        Challenge c = current.sfida();
        switch (c.getTipo()) {
            case COMBAT -> callbacks.onStartCombat(c);
            case SKILL_CHECK -> callbacks.onStartSkillCheck(c);
            case NARRATIVE -> callbacks.onStartNarrative(c);
        }
    }

    private void handleFragmentInteraction(Room current) {
        isInteracting = true;
        current.setFrammentoRaccolto(true);

        if (player.getRicordi().contains(current.ricordoSbloccato())) {
            player.setHp(player.getHp() + GameplayConstants.MEMORY_RESONANCE_HP_BONUS);
            player.addKarma(GameplayConstants.MEMORY_RESONANCE_KARMA_BONUS);
            gameView.avviaAnimazioneRaccolta(current, 750.0, 50.0);
            callbacks.onMemoryCollected("RISONANZA SPIRITUALE\n\nHai già vissuto questo dolore. " +
                    "La consapevolezza ti rigenera " + GameplayConstants.MEMORY_RESONANCE_HP_BONUS +
                    " HP e consolida il tuo Karma (+" + GameplayConstants.MEMORY_RESONANCE_KARMA_BONUS + ").");
        } else {
            player.addRicordo(current.ricordoSbloccato());
            gameView.avviaAnimazioneRaccolta(current, 750.0, 50.0);
            if (hudView != null) hudView.evidenziaZainetto();
            callbacks.onMemoryCollected(current.ricordoSbloccato());
        }
    }

    private void handleDoorInteraction() {
        isInteracting = true;
        if (gameState.nextRoom()) {
            player.setPianoCorrente(gameState.getCurrentRoom().id());
            player.setX(50);
            player.setY(300);
            callbacks.onNextRoom();
        } else {
            callbacks.onFinalJudgment();
        }
    }

    public void setInteracting(boolean interacting) {
        this.isInteracting = interacting;
    }
}