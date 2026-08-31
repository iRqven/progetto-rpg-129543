package it.unicam.cs.mpgc.rpg129543.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Gestisce esclusivamente l'interfaccia grafica della barra di stato superiore (HUD).
 */
public class HudView {
    private final GridPane hudContainer;
    private final Label labelKarma;
    private final Label labelLivello;
    private final Label labelPiano;
    private final Button zainettoBtn;

    public HudView(Runnable onZainettoClick) {
        hudContainer = new GridPane();
        hudContainer.setAlignment(Pos.CENTER);
        hudContainer.setHgap(40);
        hudContainer.setPrefHeight(50);
        hudContainer.setStyle("-fx-background-color: #1a1a24; -fx-border-color: #2c3e50; -fx-border-width: 0 0 2 0;");

        labelKarma = new Label();
        labelLivello = new Label();
        labelPiano = new Label();

        String styleText = "-fx-text-fill: #bdc3c7; -fx-font-weight: bold; -fx-font-family: 'Courier New'; -fx-font-size: 13px;";
        labelKarma.setStyle(styleText);
        labelLivello.setStyle(styleText);
        labelPiano.setStyle(styleText);

        zainettoBtn = new Button("Zainetto (I)");
        zainettoBtn.setStyle("-fx-base: #2980b9; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
        // Quando viene cliccato, esegue l'azione passata dal Controller/Main
        zainettoBtn.setOnAction(e -> onZainettoClick.run());

        hudContainer.add(labelKarma, 0, 0);
        hudContainer.add(labelLivello, 1, 0);
        hudContainer.add(labelPiano, 2, 0);
        hudContainer.add(zainettoBtn, 3, 0);
    }

    // Restituisce il nodo grafico pronto per essere aggiunto alla scena
    public GridPane getHudNode() {
        return hudContainer;
    }

    // Metodo pulito per aggiornare i dati a schermo dal Model
    public void updateStatus(int karma, int livello, int xpCorrenti, int xpNecessari, int pianoCorrente) {
        labelKarma.setText("KARMA: " + karma);
        labelLivello.setText("LIVELLO ANIMA: " + livello + " (" + xpCorrenti + "/" + xpNecessari + " XP)");
        labelPiano.setText("PIANO CORRENTE: " + pianoCorrente);
    }
}