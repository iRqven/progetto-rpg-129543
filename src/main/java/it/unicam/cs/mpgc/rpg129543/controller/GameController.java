package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.model.*;
import it.unicam.cs.mpgc.rpg129543.view.GameView;
import it.unicam.cs.mpgc.rpg129543.view.HudView;
import javafx.scene.input.KeyCode;
import java.util.function.Consumer;

/**
 * Controller dedicato alla fisica, al movimento e al rilevamento delle collisioni.
 */
public class GameController {

    // Limiti fisici della mappa (Clean Code: rimozione Magic Numbers)
    private static final double MIN_X = 20.0;
    private static final double MAX_X = 780.0;
    private static final double MIN_Y = 20.0;
    private static final double MAX_Y = 580.0;

    private final Player player;
    private final GameState gameState;
    private final GameView gameView;
    private final HudView hudView;
    private final InputController inputController;

    private boolean isInteracting = false;

    // Callbacks di instradamento per delegare le schermate grafiche all'App principale
    private final Consumer<Challenge> onStartCombat;
    private final Consumer<Challenge> onStartSkillCheck;
    private final Consumer<Challenge> onStartNarrative;
    private final Consumer<Room> onLoreOnly;
    private final Consumer<String> onMemoryCollected;
    private final Runnable onNextRoom;
    private final Runnable onFinalJudgment;

    public GameController(Player player, GameState gameState, GameView gameView, HudView hudView,
                          InputController inputController,
                          Consumer<Challenge> onStartCombat, Consumer<Challenge> onStartSkillCheck,
                          Consumer<Challenge> onStartNarrative, Consumer<Room> onLoreOnly,
                          Consumer<String> onMemoryCollected, Runnable onNextRoom, Runnable onFinalJudgment) {
        this.player = player;
        this.gameState = gameState;
        this.gameView = gameView;
        this.hudView = hudView;
        this.inputController = inputController;

        this.onStartCombat = onStartCombat;
        this.onStartSkillCheck = onStartSkillCheck;
        this.onStartNarrative = onStartNarrative;
        this.onLoreOnly = onLoreOnly;
        this.onMemoryCollected = onMemoryCollected;
        this.onNextRoom = onNextRoom;
        this.onFinalJudgment = onFinalJudgment;
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

        // Applica i limiti della mappa usando le costanti
        if (player.getX() < MIN_X) player.setX(MIN_X);
        if (player.getX() > MAX_X) player.setX(MAX_X);
        if (player.getY() < MIN_Y) player.setY(MIN_Y);
        if (player.getY() > MAX_Y) player.setY(MAX_Y);
    }

    private void checkInteractions() {
        Room current = gameState.getCurrentRoom();
        if (!current.hasChallenge() && !current.hasFragment()) return;

        double distBoss = calcolaDistanza(player.getX(), player.getY(), current.npcX(), current.npcY());
        double distFrag = calcolaDistanza(player.getX(), player.getY(), current.fragX(), current.fragY());
        double distToDoor = calcolaDistanza(player.getX(), player.getY(), current.doorX(), current.doorY());

        // 1. Controllo Boss
        if (current.hasChallenge() && distBoss < Room.INTERACTION_RADIUS_BOSS) {
            isInteracting = true;
            if (current.isSfidaGestita()) {
                onLoreOnly.accept(current);
            } else {
                Challenge c = current.sfida();
                if (c.isCombat()) onStartCombat.accept(c);
                else if (c.isSkillCheck()) onStartSkillCheck.accept(c);
                else onStartNarrative.accept(c);
            }
            return;
        }

        // 2. Controllo Frammento
        if (current.hasFragment() && !player.getRicordi().contains(current.ricordoSbloccato()) && distFrag < Room.INTERACTION_RADIUS_FRAG) {
            isInteracting = true;
            player.addRicordo(current.ricordoSbloccato());

            gameView.avviaAnimazioneRaccolta(current, 750.0, 50.0);
            if (hudView != null) hudView.evidenziaZainetto();

            onMemoryCollected.accept(current.ricordoSbloccato());
            return;
        }

        // 3. Controllo Porta
        if (distToDoor < Room.INTERACTION_RADIUS_DOOR && current.isSfidaGestita()) {
            isInteracting = true;
            if (gameState.nextRoom()) {
                player.setX(50);
                player.setY(300);
                onNextRoom.run();
            } else {
                onFinalJudgment.run();
            }
        }
    }

    private double calcolaDistanza(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public void setInteracting(boolean interacting) {
        this.isInteracting = interacting;
    }
}