package it.unicam.cs.mpgc.rpg129543.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

public class UIManager {
    public static VBox createNarrativeMenu(String title, String message, Runnable onProceed) {
        VBox overlay = createBaseOverlay();

        Label name = createStyledLabel(title, "#3498db", 20);
        Label dialog = createDialogLabel(message);

        Button proceedBtn = new Button("Prosegui il Cammino");
        proceedBtn.setStyle("-fx-base: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold;");
        proceedBtn.setOnAction(e -> onProceed.run());

        overlay.getChildren().addAll(name, dialog, proceedBtn);
        return overlay;
    }

    public static VBox createCombatChoiceMenu(String title, String message, int penaltyValue, Runnable onFight, Runnable onIgnore) {
        VBox overlay = createBaseOverlay();

        Label name = createStyledLabel(title, "#9b59b6", 20);
        Label dialog = createDialogLabel(message);

        Button fightBtn = new Button("LOTTA (Pentimento)");
        fightBtn.setStyle("-fx-base: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        fightBtn.setOnAction(e -> onFight.run());

        Button ignoreBtn = new Button("IGNORA (-" + penaltyValue + " Karma)");
        ignoreBtn.setStyle("-fx-base: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        ignoreBtn.setOnAction(e -> onIgnore.run());

        overlay.getChildren().addAll(name, dialog, fightBtn, ignoreBtn);
        return overlay;
    }

    private static VBox createBaseOverlay() {
        VBox overlay = new VBox(20);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPrefSize(450, 350);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.95); -fx-border-color: #f1c40f; -fx-padding: 25; -fx-border-radius: 15; -fx-border-width: 2;");
        return overlay;
    }

    private static Label createStyledLabel(String text, String color, int size) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: " + size + "px; -fx-font-weight: bold;");
        return label;
    }

    private static Label createDialogLabel(String text) {
        Label dialog = new Label(text);
        dialog.setStyle("-fx-text-fill: white; -fx-font-style: italic; -fx-text-alignment: center; -fx-font-family: 'Georgia';");
        dialog.setWrapText(true);
        dialog.setMaxWidth(400);
        return dialog;
    }

    public static VBox createBattleTutorialMenu(String nomeBoss, String descBoss, int playerLevel, Runnable onStart) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        Label t = new Label("SVELAMENTO DELLA VITTIMA: " + nomeBoss.toUpperCase());
        t.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 15px; -fx-font-family: 'Courier New';");

        Label desc = createDialogLabel(descBoss);
        desc.setMaxWidth(420);

        VBox tutorialBox = new VBox(5);
        tutorialBox.setAlignment(Pos.CENTER);

        if (playerLevel == 1) {
            Label tutTitolo = new Label("[REGISTRO DELLE DEBOLEZZE EMOTIVE]");
            tutTitolo.setStyle("-fx-text-fill: #f1c40f; -fx-font-weight: bold; -fx-font-size: 12px; -fx-font-family: 'Courier New';");

            Label tutDesc = new Label(
                    "Durante lo scontro, seleziona la Virtù che contrasta l'Aura cromatico-emotiva della vittima:\n" +
                            "• Se lo stato è RABBIA -> Sferra PAZIENZA\n" +
                            "• Se lo stato è PAURA  -> Sferra CORAGGIO\n" +
                            "• Se lo stato è COLPA  -> Sferra PERDONO\n" +
                            "Colpire l'Aura corretta raddoppia l'efficacia d'attacco e rigenera Volontà."
            );
            tutDesc.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 11px; -fx-text-alignment: center; -fx-font-family: 'Georgia';");
            tutDesc.setWrapText(true);
            tutDesc.setMaxWidth(420);
            tutorialBox.getChildren().addAll(tutTitolo, tutDesc);
        }

        Button startBtn = new Button("Inizia il Combattimento");
        startBtn.setStyle("-fx-base: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 200; -fx-min-height: 40;");
        startBtn.setOnAction(e -> onStart.run());

        layout.getChildren().addAll(t, desc, tutorialBox, startBtn);
        return layout;
    }

    public static VBox createSkillCheckMenu(String title, String message, Runnable onRoll) {
        VBox overlay = createBaseOverlay();

        Label name = createStyledLabel(title, "#f39c12", 20);
        Label dialog = createDialogLabel(message);

        Label explanation = new Label("La redenzione richiede uno sforzo: lancia il dado del destino (D20 + Livello).");
        explanation.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 12px; -fx-font-family: 'Courier New'; -fx-font-weight: bold;");

        Button rollBtn = new Button("LANCIA IL DADO");
        rollBtn.setStyle("-fx-base: #8e44ad; -fx-text-fill: white; -fx-font-weight: bold;");
        rollBtn.setOnAction(e -> onRoll.run());

        overlay.getChildren().addAll(name, dialog, explanation, rollBtn);
        return overlay;
    }

    public static ScrollPane createFinalJudgmentMenu(int karma, Runnable onRestart) {
        VBox endLayout = new VBox(15);
        endLayout.setAlignment(Pos.CENTER);
        endLayout.setStyle("-fx-background-color: black; -fx-padding: 20;");

        Label titoloOrologio = createStyledLabel("L'OROLOGIO DELLA STAZIONE SCATTA: 14:03", "#e74c3c", 18);

        Label rivelazioneGiudice = createDialogLabel(
                "[ LA VERITÀ SVELATA ]\n" +
                        "Il Giudice emerge dall'ombra, osservando il tuo percorso:\n\n" +
                        "\"Hai scalato i sette piani credendo di compiere un cammino di purificazione, di espiare le tue colpe per guadagnare la pace. " +
                        "Ma guarda bene le vittime che hai incontrato: quel socio tradito, il padre disperato, il giovane operaio... non erano entità astratte, " +
                        "ma le persone che hai calpestato e distrutto in vita con il tuo cinismo prima di salire su quel treno.\n\n" +
                        "Questo non è un Purgatorio che porta alla salvezza. Questo è un dispositivo psicologico eterno: " +
                        "la tua coscienza è condannata a rivivere la sua stessa colpa all'infinito, intrappolata in un crudele loop temporale.\""
        );
        rivelazioneGiudice.setMaxWidth(720);

        Label esitoCondanna = new Label();
        String styleCondanna = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Courier New'; -fx-text-alignment: center;";
        Button azioneBtn = new Button();
        String styleBtn = "-fx-min-width: 280; -fx-min-height: 40; -fx-font-weight: bold; -fx-font-size: 12px;";

        if (karma >= 40) {
            esitoCondanna.setText("• EPILOGO: IL LOOP DELL'ILLUSIONE (Karma Alto) •\nHai cercato la via della bontà, ma la redenzione è un miraggio. La tua memoria viene completamente azzerata: verrai rispedito al Piano 1 per ricominciare da capo, alimentando l'eterna illusione di poterti salvare la prossima volta.");
            esitoCondanna.setStyle(styleCondanna + "-fx-text-fill: #3498db;");
            azioneBtn.setText("Ricomincia l'Illusione (Torna al Piano 1)");
            azioneBtn.setStyle(styleBtn + "-fx-base: #2980b9; -fx-text-fill: white;");
        } else {
            esitoCondanna.setText("• EPILOGO: IL CARNEFICE CONSAPEVOLE (Karma Basso) •\nHai scelto l'indifferenza e la fuga. La maschera cade definitivamente: accetti la tua natura di carnefice. Resterai confinato in questo inferno circolare, condannato a tormentare e a essere tormentato dai tuoi stessi fantasmi per l'eternità.");
            esitoCondanna.setStyle(styleCondanna + "-fx-text-fill: #c0392b;");
            azioneBtn.setText("Accetta il Ruolo di Carnefice (Ricomincia il Ciclo)");
            azioneBtn.setStyle(styleBtn + "-fx-base: #c0392b; -fx-text-fill: white;");
        }
        esitoCondanna.setWrapText(true);
        esitoCondanna.setMaxWidth(720);
        azioneBtn.setOnAction(e -> onRestart.run());

        endLayout.getChildren().addAll(titoloOrologio, rivelazioneGiudice, esitoCondanna, azioneBtn);

        ScrollPane scrollPane = new ScrollPane(endLayout);
        scrollPane.setPrefSize(800, 600);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: black; -fx-background-color: black;");

        return scrollPane;
    }
}