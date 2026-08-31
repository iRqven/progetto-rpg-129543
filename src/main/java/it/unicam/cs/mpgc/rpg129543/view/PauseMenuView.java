package it.unicam.cs.mpgc.rpg129543.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Gestisce l'interfaccia del Menu di Pausa e la schermata di conferma del Reset.
 */
public class PauseMenuView {
    private final VBox layout;
    private final Runnable onResume;
    private final Runnable onSaveExit;
    private final Runnable onResetConfirm;

    public PauseMenuView(Runnable onResume, Runnable onSaveExit, Runnable onResetConfirm) {
        this.onResume = onResume;
        this.onSaveExit = onSaveExit;
        this.onResetConfirm = onResetConfirm;

        layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPrefSize(300, 400);
        layout.setStyle("-fx-background-color: rgba(15, 15, 15, 0.98); -fx-border-color: cyan; -fx-padding: 30; -fx-background-radius: 20; -fx-border-radius: 20;");

        buildMainMenu();
    }

    private void buildMainMenu() {
        layout.getChildren().clear();
        Label menuTitle = new Label("OBLIO");
        menuTitle.setStyle("-fx-text-fill: cyan; -fx-font-size: 24px; -fx-font-weight: bold;");

        Button resumeBtn = new Button("Riprendi");
        Button saveExitBtn = new Button("Esci e Salva");
        Button resetBtn = new Button("Reset Totale");

        String btnStyle = "-fx-min-width: 150px; -fx-base: #2c3e50; -fx-text-fill: white;";
        resumeBtn.setStyle(btnStyle);
        saveExitBtn.setStyle(btnStyle);
        resetBtn.setStyle(btnStyle + "-fx-base: #c0392b;");

        resumeBtn.setOnAction(e -> onResume.run());
        saveExitBtn.setOnAction(e -> onSaveExit.run());
        resetBtn.setOnAction(e -> showResetWarning());

        layout.getChildren().addAll(menuTitle, resumeBtn, saveExitBtn, resetBtn);
    }

    private void showResetWarning() {
        layout.getChildren().clear();
        Label warnLabel = new Label("ATTENZIONE!\nVuoi davvero tornare al nulla?\nOgni ricordo andrà perduto.");
        warnLabel.setStyle("-fx-text-fill: #e74c3c; -fx-text-alignment: center; -fx-font-weight: bold;");

        Button confirmBtn = new Button("SÌ, RESETTA");
        Button cancelBtn = new Button("NO, TORNA");
        confirmBtn.setStyle("-fx-base: #c0392b; -fx-text-fill: white;");
        cancelBtn.setStyle("-fx-base: #27ae60; -fx-text-fill: white;");

        confirmBtn.setOnAction(e -> onResetConfirm.run());
        cancelBtn.setOnAction(e -> buildMainMenu()); // Torna ai bottoni principali

        layout.getChildren().addAll(warnLabel, confirmBtn, cancelBtn);
    }

    public VBox getView() {
        return layout;
    }
}