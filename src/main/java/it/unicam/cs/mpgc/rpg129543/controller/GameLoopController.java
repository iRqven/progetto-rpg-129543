package it.unicam.cs.mpgc.rpg129543.controller;

import javafx.animation.AnimationTimer;

/** Gestisce il ciclo di gioco (Game Loop) a frequenza fissa. */
public class GameLoopController {
    /** ~83 FPS: frequenza di aggiornamento scelta per bilanciare fluidità e carico sulla CPU. */
    private static final long FRAME_DURATION_NS = 12_000_000L;

    private AnimationTimer timer;
    private final Runnable updateLogic;
    private final Runnable renderGraphics;
    private long lastUpdate = 0;

    public GameLoopController(Runnable updateLogic, Runnable renderGraphics) {
        this.updateLogic = updateLogic;
        this.renderGraphics = renderGraphics;
    }

    public void startLoop() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= FRAME_DURATION_NS) {
                    updateLogic.run();
                    renderGraphics.run();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    public void stopLoop() {
        if (timer != null) timer.stop();
    }
}