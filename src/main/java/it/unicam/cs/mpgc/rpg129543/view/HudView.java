package it.unicam.cs.mpgc.rpg129543.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.animation.ScaleTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Gestisce esclusivamente l'interfaccia grafica della barra di stato superiore (HUD).
 */
public class HudView {
    private final HBox hudContainer;
    private final Label labelKarma;
    private final Label labelLivello;
    private final Label labelPiano;
    private final Button zainettoBtn;

    public HudView(Runnable onZainettoClick) {
        hudContainer = new HBox(12);
        hudContainer.setAlignment(Pos.CENTER);
        hudContainer.setPadding(new Insets(10, 15, 10, 15));
        hudContainer.setPrefHeight(60);

        hudContainer.setStyle(
                "-fx-background-color: #21222c; " +
                        "-fx-border-color: #111216; " +
                        "-fx-border-width: 0 0 5 0; " +
                        "-fx-opacity: 1.0;"
        );

        String styleText = "-fx-text-fill: #ecf0f1; -fx-font-weight: bold; -fx-font-family: 'Courier New'; -fx-font-size: 11px;";

        labelKarma = new Label();
        labelLivello = new Label();
        labelPiano = new Label();

        labelKarma.setStyle(styleText);
        labelLivello.setStyle(styleText);
        labelPiano.setStyle(styleText);

        StackPane boxKarma = createHudBox(labelKarma);
        StackPane boxLivello = createHudBox(labelLivello);
        StackPane boxPiano = createHudBox(labelPiano);

        zainettoBtn = new Button("Zaino (I)");
        zainettoBtn.setStyle("-fx-base: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        zainettoBtn.setOnAction(e -> onZainettoClick.run());

        hudContainer.getChildren().addAll(boxKarma, boxLivello, boxPiano, zainettoBtn);
    }

    private StackPane createHudBox(Label label) {
        StackPane box = new StackPane(label);
        box.setPadding(new Insets(5, 12, 5, 12));
        box.setStyle(
                "-fx-background-color: #2c3e50; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #34495e; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 2;"
        );
        return box;
    }

    public HBox getHudNode() {
        return hudContainer;
    }

    public void updateStatus(int karma, int livello, int xpCorrenti, int xpNecessari, int pianoCorrente) {
        labelKarma.setText("KARMA: " + karma);
        labelLivello.setText("LIV: " + livello + " (" + xpCorrenti + "/" + xpNecessari + "XP)");
        labelPiano.setText("PIANO: " + pianoCorrente);
    }

    public void evidenziaZainetto() {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.GOLD);
        glow.setRadius(20);
        glow.setSpread(0.5);
        zainettoBtn.setEffect(glow);

        ScaleTransition battito = new ScaleTransition(Duration.millis(200), zainettoBtn);
        battito.setByX(0.2);
        battito.setByY(0.2);
        battito.setCycleCount(2);
        battito.setAutoReverse(true);

        battito.setOnFinished(e -> zainettoBtn.setEffect(null));
        battito.play();
    }
}