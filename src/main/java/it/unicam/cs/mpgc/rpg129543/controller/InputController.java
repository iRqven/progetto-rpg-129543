package it.unicam.cs.mpgc.rpg129543.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

/**
 * Controller dedicato alla gestione degli input da tastiera.
 */
public class InputController {
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Runnable onEscapePressed;
    private final Runnable onInventoryPressed;

    public InputController(Scene scene, Runnable onEscapePressed, Runnable onInventoryPressed) {
        this.onEscapePressed = onEscapePressed;
        this.onInventoryPressed = onInventoryPressed;
        setupListeners(scene);
    }

    private void setupListeners(Scene scene) {
        scene.setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());
            if (e.getCode() == KeyCode.ESCAPE) {
                onEscapePressed.run();
            } else if (e.getCode() == KeyCode.I || e.getCode() == KeyCode.R) {
                onInventoryPressed.run();
            }
        });
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
    }

    public boolean isPressed(KeyCode key) {
        return pressedKeys.contains(key);
    }

    public void clearKeys() {
        pressedKeys.clear();
    }
}