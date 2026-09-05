package it.unicam.cs.mpgc.rpg129543.view;

import it.unicam.cs.mpgc.rpg129543.api.BattleAction;
import it.unicam.cs.mpgc.rpg129543.controller.BattleEngine;
import it.unicam.cs.mpgc.rpg129543.model.Enemy;
import it.unicam.cs.mpgc.rpg129543.model.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

/**
 * Gestisce esclusivamente l'interfaccia grafica durante il combattimento.
 */
public class BattleView {
    private final VBox layout;

    // CORREZIONE: Consumer<BattleAction> invece di Consumer<String>
    public BattleView(Player player, Enemy currentEnemy, BattleEngine currentBattle, String logText, boolean isInSubMenuVirtu, Consumer<BattleAction> onAction, Runnable onFlee) {
        layout = new VBox(12);
        layout.setAlignment(Pos.CENTER);
        layout.setPrefWidth(450);
        layout.setStyle("-fx-background-color: rgba(10, 10, 10, 0.98); -fx-border-color: #7f8c8d; -fx-padding: 20; -fx-border-width: 3; -fx-border-radius: 10;");

        // 1. STATO EMOTIVO E AURA DEL BOSS
        Label auraLabel = new Label(" STATO EMOTIVO BOSS: " + currentBattle.getCurrentMood() + " ");
        String colorHex = switch (currentBattle.getCurrentMood()) {
            case RABBIA -> "#e74c3c";
            case PAURA -> "#3498db";
            case COLPA -> "#f1c40f";
        };
        auraLabel.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-family: 'Courier New';");

        // 2. STATISTICHE
        HBox statusBox = new HBox(20);
        statusBox.setAlignment(Pos.CENTER);

        Label playerStats = new Label("TUOI HP: " + player.getHp() + "/" + player.getHpMax() + "\nVOLONTÀ: " + currentBattle.getVolonta() + "/8");
        playerStats.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-font-size: 12px; -fx-font-family: 'Courier New'; -fx-text-alignment: center;");

        Label enemyStats = new Label("NEMICO HP: " + currentEnemy.getHp() + "/" + currentEnemy.getHpMax() + "\nSCALATO: LIV " + player.getRunCorrente());        enemyStats.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 12px; -fx-font-family: 'Courier New'; -fx-text-alignment: center;");

        statusBox.getChildren().addAll(playerStats, enemyStats);

        // 3. LOG DI BATTAGLIA
        Label log = new Label(logText);
        log.setStyle("-fx-text-fill: white; -fx-font-style: italic; -fx-text-alignment: center; -fx-font-family: 'Georgia'; -fx-font-size: 13px;");
        log.setWrapText(true);
        log.setMinHeight(80);
        log.setMaxWidth(420);

        // 4. MENU DEI COMANDI
        GridPane menuLotta = new GridPane();
        menuLotta.setHgap(10);
        menuLotta.setVgap(10);
        menuLotta.setAlignment(Pos.CENTER);
        String styleBtn = "-fx-min-width: 135; -fx-min-height: 40; -fx-font-family: 'Courier New'; -fx-font-weight: bold;";

        // GESTIONE ANOMALIA STOCASTICA
        if (currentBattle.getAnomalieEngine().isImprevistoAttivo()) {
            log.setText("⚠ ANOMALIA NELLA NEBBIA ⚠\n" + currentBattle.getAnomalieEngine().getTestoBivio());
            log.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

            Button btnAccetta = new Button("ACCETTA PATTO");
            Button btnRifiuta = new Button("RIFIUTA PATTO");
            btnAccetta.setStyle(styleBtn + "-fx-base: #c0392b; -fx-text-fill: white;");
            btnRifiuta.setStyle(styleBtn + "-fx-base: #7f8c8d; -fx-text-fill: white;");

            // CORREZIONE: Uso dell'Enum
            btnAccetta.setOnAction(e -> onAction.accept(BattleAction.ACCETTA_PATTO));
            btnRifiuta.setOnAction(e -> onAction.accept(BattleAction.RIFIUTA_PATTO));
            menuLotta.add(btnAccetta, 0, 0);
            menuLotta.add(btnRifiuta, 1, 0);
        }
        // MENU SECONDARIO VIRTÙ
        else if (isInSubMenuVirtu) {
            Button btnPaz = new Button("PAZIENZA (-2V)");
            Button btnCor = new Button("CORAGGIO (-2V)");
            Button btnPer = new Button("PERDONO (-2V)");
            Button btnIndietro = new Button("INDIETRO");

            btnPaz.setStyle(styleBtn + "-fx-base: #2980b9; -fx-text-fill: white;");
            btnCor.setStyle(styleBtn + "-fx-base: #c0392b; -fx-text-fill: white;");
            btnPer.setStyle(styleBtn + "-fx-base: #f39c12; -fx-text-fill: white;");
            btnIndietro.setStyle(styleBtn + "-fx-base: #7f8c8d; -fx-text-fill: white;");

            // CORREZIONE: Uso dell'Enum
            btnPaz.setOnAction(e -> onAction.accept(BattleAction.PAZIENZA));
            btnCor.setOnAction(e -> onAction.accept(BattleAction.CORAGGIO));
            btnPer.setOnAction(e -> onAction.accept(BattleAction.PERDONO));
            btnIndietro.setOnAction(e -> onAction.accept(BattleAction.INDIETRO));

            menuLotta.add(btnPaz, 0, 0); menuLotta.add(btnCor, 1, 0);
            menuLotta.add(btnPer, 0, 1); menuLotta.add(btnIndietro, 1, 1);
        }
        // MENU PRINCIPALE
        else {
            Button btnApriVirtu = new Button("VIRTÙ...");
            Button btnDef = new Button("DIFESA (+3V)");
            Button btnCur = new Button("CURA (-3V)");
            Button btnFug = new Button("FUGA");

            btnApriVirtu.setStyle(styleBtn + "-fx-base: #d35400; -fx-text-fill: white;");
            btnDef.setStyle(styleBtn + "-fx-base: #27ae60; -fx-text-fill: white;");
            btnCur.setStyle(styleBtn + "-fx-base: #8e44ad; -fx-text-fill: white;");
            btnFug.setStyle(styleBtn + "-fx-base: #7f8c8d; -fx-text-fill: white;");

            // CORREZIONE: Uso dell'Enum
            btnApriVirtu.setOnAction(e -> onAction.accept(BattleAction.APRI_VIRTU));
            btnDef.setOnAction(e -> onAction.accept(BattleAction.DIFESA));
            btnCur.setOnAction(e -> onAction.accept(BattleAction.CURA));
            btnFug.setOnAction(e -> onFlee.run());

            menuLotta.add(btnApriVirtu, 0, 0); menuLotta.add(btnDef, 1, 0);
            menuLotta.add(btnCur, 0, 1); menuLotta.add(btnFug, 1, 1);
        }

        layout.getChildren().addAll(auraLabel, statusBox, log, menuLotta);
    }

    public VBox getView() {
        return layout;
    }
}