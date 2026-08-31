package it.unicam.cs.mpgc.rpg129543.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import java.util.List;

/**
 * Gestisce l'interfaccia dello Zainetto Spirituale (Archivio Ricordi).
 */
public class MemoryArchiveView {
    private final VBox layout;

    public MemoryArchiveView(List<String> ricordi, Runnable onClose) {
        layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: rgba(15, 15, 25, 0.98); -fx-border-color: #3498db; -fx-border-width: 2; -fx-padding: 25; -fx-border-radius: 10;");
        layout.setPrefSize(500, 450);

        Label title = new Label("ZAINETTO SPIRITUALE: ARCHIVIO DEI RICORDI");
        title.setStyle("-fx-text-fill: #3498db; -fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: 'Courier New';");

        VBox ricordiBox = new VBox(10);
        ricordiBox.setAlignment(Pos.CENTER_LEFT);
        ricordiBox.setStyle("-fx-padding: 10;");

        if (ricordi.isEmpty()) {
            Label noMemories = new Label("Nessun frammento di memoria recuperato...\nEsplora i piani e cerca le luci dorate.");
            noMemories.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-font-family: 'Georgia'; -fx-text-alignment: center;");
            ricordiBox.getChildren().add(noMemories);
        } else {
            int index = 1;
            for (String ricordo : ricordi) {
                Label memLabel = new Label(index + ". " + ricordo);
                memLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 13px; -fx-font-family: 'Georgia';");
                memLabel.setWrapText(true);
                memLabel.setMaxWidth(440);
                ricordiBox.getChildren().add(memLabel);
                index++;
            }
        }

        ScrollPane scrollPane = new ScrollPane(ricordiBox);
        scrollPane.setPrefSize(460, 280);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Button closeBtn = new Button("Chiudi Zainetto");
        closeBtn.setStyle("-fx-base: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 150; -fx-min-height: 35;");
        closeBtn.setOnAction(e -> onClose.run());

        layout.getChildren().addAll(title, scrollPane, closeBtn);
    }

    public VBox getView() {
        return layout;
    }
}