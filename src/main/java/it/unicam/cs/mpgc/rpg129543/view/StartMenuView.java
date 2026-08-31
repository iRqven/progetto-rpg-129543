package it.unicam.cs.mpgc.rpg129543.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Gestisce la schermata iniziale di benvenuto (Intro).
 */
public class StartMenuView {
    private final VBox layout;

    // Passiamo un'azione (Runnable) che verrà eseguita quando si clicca "Inizia"
    public StartMenuView(Runnable onStartClick) {
        layout = new VBox(25);
        layout.setStyle("-fx-background-color: black; -fx-padding: 50;");
        layout.setAlignment(Pos.CENTER);
        layout.setPrefSize(800, 600);

        Label l1 = new Label("Ti risvegli nel grigio.");
        Label l2 = new Label("Non ricordi la tua morte, ma senti il peso dei tuoi errori passati.");
        Label l3 = new Label("Esplora i piani, affronta i tuoi rimorsi spettrali... o ignorali.");

        String s = "-fx-text-fill: #ecf0f1; -fx-font-size: 20px; -fx-font-family: 'Georgia';";
        l1.setStyle(s); l2.setStyle(s); l3.setStyle(s);

        Button b = new Button("Apri gli occhi e inizia il cammino");
        b.setStyle("-fx-base: #2c3e50; -fx-text-fill: cyan; -fx-font-size: 16px; -fx-padding: 10 20;");
        b.setOnAction(e -> onStartClick.run());

        layout.getChildren().addAll(l1, l2, l3, b);
    }

    public VBox getView() {
        return layout;
    }
}