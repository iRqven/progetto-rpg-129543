package it.unicam.cs.mpgc.rpg129543.controller;

import javafx.animation.AnimationTimer;

/**
 * Controller dedicato alla gestione del ciclo di gioco (Game Loop).
 */
public class GameLoopController {
    private AnimationTimer timer;
    private final Runnable updateLogic;
    private final Runnable renderGraphics;

    private long lastUpdate = 0;

    // --- IL FRENO DI PRECISIONE ---
    // 12_000_000 ns = ~83 FPS. È la via di mezzo perfetta!
    // Se lo vuoi "un pelo" più lento -> scrivi 14_000_000
    // Se lo vuoi "un pelo" più veloce -> scrivi 10_000_000
    private final long FRAME_DURATION = 12_000_000;

    public GameLoopController(Runnable updateLogic, Runnable renderGraphics) {
        this.updateLogic = updateLogic;
        this.renderGraphics = renderGraphics;
    }

    public void startLoop() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= FRAME_DURATION) {
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