package it.unicam.cs.mpgc.rpg129543.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Gestisce la schermata di selezione del genere del personaggio dopo l'intro.
 */
public class CharacterSelectionView {
    private final VBox layout;

    public CharacterSelectionView(java.util.function.Consumer<String> onGenderSelected) {
        layout = new VBox(25);
        layout.setStyle("-fx-background-color: black; -fx-padding: 50;");
        layout.setAlignment(Pos.CENTER);
        layout.setPrefSize(800, 600);

        Label title = new Label("Scegli la forma della tua Anima");
        title.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 22px; -fx-font-family: 'Georgia'; -fx-font-weight: bold;");

        Label subtitle = new Label("La nebbia riflette il riflesso di cio che eri...");
        subtitle.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 14px; -fx-font-family: 'Georgia'; -fx-font-style: italic;");

        Button maleBtn = new Button("Viandante (Maschile)");
        maleBtn.setStyle("-fx-base: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20;");
        maleBtn.setOnAction(e -> onGenderSelected.accept("male"));

        Button femaleBtn = new Button("Viandante (Femminile)");
        femaleBtn.setStyle("-fx-base: #8e44ad; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 20;");
        femaleBtn.setOnAction(e -> onGenderSelected.accept("female"));

        HBox btnBox = new HBox(20, maleBtn, femaleBtn);
        btnBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(title, subtitle, btnBox);
    }

    public VBox getView() {
        return layout;
    }
}