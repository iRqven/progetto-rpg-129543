package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.view.GameView;
import javafx.scene.layout.VBox;

import java.util.Objects;

/** Gestisce l'overlay centrato mostrato sopra l'area di gioco (menu, popup, dialoghi). */
public class OverlayManager {
    private final GameView gameView;
    private VBox currentOverlay;

    public OverlayManager(GameView gameView) {
        this.gameView = Objects.requireNonNull(gameView);
    }

    public void showCentered(VBox overlay) {
        remove();
        currentOverlay = overlay;
        currentOverlay.layoutXProperty().bind(
                gameView.getGameArea().widthProperty().divide(2).subtract(currentOverlay.prefWidthProperty().divide(2)));
        currentOverlay.layoutYProperty().bind(
                gameView.getGameArea().heightProperty().divide(2).subtract(currentOverlay.prefHeightProperty().divide(2)));
        gameView.getGameArea().getChildren().add(currentOverlay);
    }

    public void remove() {
        if (currentOverlay != null) {
            gameView.getGameArea().getChildren().remove(currentOverlay);
            currentOverlay = null;
        }
    }

    public boolean isShowing() {
        return currentOverlay != null;
    }
}