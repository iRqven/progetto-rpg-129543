package it.unicam.cs.mpgc.rpg129543.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Componente grafico riutilizzabile per i popup testuali (Ricordi, Storia, Avvisi).
 */
public class MessageView {
    private final VBox layout;

    public MessageView(String titleText, String contentText, String buttonText, String colorHex, Runnable onClose) {
        layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPrefSize(450, 300);
        layout.setStyle("-fx-background-color: rgba(0,0,0,0.95); -fx-padding: 25; -fx-border-color: " + colorHex + "; -fx-border-width: 2; -fx-border-radius: 15;");

        Label title = new Label(titleText);
        title.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label content = new Label(contentText);
        content.setStyle("-fx-text-fill: white; -fx-text-alignment: center; -fx-font-size: 14px; -fx-font-family: 'Georgia';");
        content.setWrapText(true);
        content.setMaxWidth(400);

        Button closeBtn = new Button(buttonText);
        closeBtn.setStyle("-fx-base: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold;");
        closeBtn.setOnAction(e -> onClose.run());

        layout.getChildren().addAll(title, content, closeBtn);
    }

    public VBox getView() {
        return layout;
    }
}