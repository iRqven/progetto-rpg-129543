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

/** Interfaccia grafica del combattimento: aura del boss, statistiche, log e menu dei comandi. */
public class BattleView {
    private final VBox layout;
    private final Consumer<BattleAction> onAction;
    private final Runnable onFlee;

    public BattleView(Player player, Enemy currentEnemy, BattleEngine currentBattle, String logText,
                      boolean isInSubMenuVirtu, Consumer<BattleAction> onAction, Runnable onFlee) {
        this.onAction = onAction;
        this.onFlee = onFlee;

        layout = new VBox(12);
        layout.setAlignment(Pos.CENTER);
        layout.setPrefWidth(480);
        layout.setStyle("-fx-background-color: rgba(10, 10, 10, 0.98); -fx-border-color: #7f8c8d; -fx-padding: 20; -fx-border-width: 3; -fx-border-radius: 10;");

        Label auraLabel = buildAuraLabel(currentBattle);
        HBox statusBox = buildStatusBox(player, currentEnemy, currentBattle);
        Label log = buildLogLabel(logText);

        GridPane menu;
        if (currentBattle.getAnomalieEngine().isImprevistoAttivo()) {
            log.setText("⚠ ANOMALIA NELLA NEBBIA ⚠\n" + currentBattle.getAnomalieEngine().getTestoBivio());
            log.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-text-alignment: center;");
            menu = buildAnomalyMenu();
        } else if (isInSubMenuVirtu) {
            menu = buildVirtueMenu();
        } else {
            menu = buildMainMenu();
        }

        layout.getChildren().addAll(auraLabel, statusBox, log, menu);
    }

    private Label buildAuraLabel(BattleEngine currentBattle) {
        Label auraLabel = new Label(" STATO EMOTIVO BOSS: " + currentBattle.getCurrentMood() + " ");
        String colorHex = switch (currentBattle.getCurrentMood()) {
            case RABBIA -> "#e74c3c";
            case PAURA -> "#3498db";
            case COLPA -> "#f1c40f";
        };
        auraLabel.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-family: 'Courier New';");
        return auraLabel;
    }

    private HBox buildStatusBox(Player player, Enemy currentEnemy, BattleEngine currentBattle) {
        HBox statusBox = new HBox(20);
        statusBox.setAlignment(Pos.CENTER);

        Label playerStats = new Label("TUOI HP: " + player.getHp() + "/" + player.getHpMax() +
                "\nVOLONTÀ: " + currentBattle.getVolonta() + "/" + BattleEngine.MAX_WILL);
        playerStats.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-font-size: 11px; -fx-font-family: 'Courier New'; -fx-text-alignment: center;");

        Label enemyStats = new Label("NEMICO HP: " + currentEnemy.getHp() + "/" + currentEnemy.getHpMax() +
                "\nSCALATO: LIV " + player.getRunCorrente());
        enemyStats.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px; -fx-font-family: 'Courier New'; -fx-text-alignment: center;");

        statusBox.getChildren().addAll(playerStats, enemyStats);
        return statusBox;
    }

    private Label buildLogLabel(String logText) {
        Label log = new Label(logText);
        log.setStyle("-fx-text-fill: white; -fx-font-style: italic; -fx-text-alignment: center; -fx-font-family: 'Georgia'; -fx-font-size: 12px;");
        log.setWrapText(true);
        log.setMinHeight(85);
        log.setMaxWidth(440);
        return log;
    }

    private GridPane newMenuGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private GridPane buildAnomalyMenu() {
        GridPane grid = newMenuGrid();
        Button btnAccetta = new Button("ACCETTA PATTO");
        Button btnRifiuta = new Button("RIFIUTA PATTO");
        btnAccetta.setStyle(UIStyles.battleButton("#c0392b"));
        btnRifiuta.setStyle(UIStyles.battleButton("#7f8c8d"));

        btnAccetta.setOnAction(e -> onAction.accept(BattleAction.ACCETTA_PATTO));
        btnRifiuta.setOnAction(e -> onAction.accept(BattleAction.RIFIUTA_PATTO));

        grid.add(btnAccetta, 0, 0);
        grid.add(btnRifiuta, 1, 0);
        return grid;
    }

    private GridPane buildVirtueMenu() {
        GridPane grid = newMenuGrid();
        Button btnPaz = new Button("PAZIENZA (-2V)");
        Button btnCor = new Button("CORAGGIO (-2V)");
        Button btnPer = new Button("PERDONO (-2V)");
        Button btnIndietro = new Button("INDIETRO");

        btnPaz.setStyle(UIStyles.battleButton("#2980b9"));
        btnCor.setStyle(UIStyles.battleButton("#c0392b"));
        btnPer.setStyle(UIStyles.battleButton("#f39c12"));
        btnIndietro.setStyle(UIStyles.battleButton("#7f8c8d"));

        btnPaz.setOnAction(e -> onAction.accept(BattleAction.PAZIENZA));
        btnCor.setOnAction(e -> onAction.accept(BattleAction.CORAGGIO));
        btnPer.setOnAction(e -> onAction.accept(BattleAction.PERDONO));
        btnIndietro.setOnAction(e -> onAction.accept(BattleAction.INDIETRO));

        grid.add(btnPaz, 0, 0);
        grid.add(btnCor, 1, 0);
        grid.add(btnPer, 0, 1);
        grid.add(btnIndietro, 1, 1);
        return grid;
    }

    private GridPane buildMainMenu() {
        GridPane grid = newMenuGrid();
        Button btnApriVirtu = new Button("VIRTÙ...");
        Button btnDef = new Button("DIFESA (+3V)");
        Button btnCur = new Button("CURA (-3V)");
        Button btnFug = new Button("FUGA");

        btnApriVirtu.setStyle(UIStyles.battleButton("#d35400"));
        btnDef.setStyle(UIStyles.battleButton("#27ae60"));
        btnCur.setStyle(UIStyles.battleButton("#8e44ad"));
        btnFug.setStyle(UIStyles.battleButton("#7f8c8d"));

        btnApriVirtu.setOnAction(e -> onAction.accept(BattleAction.APRI_VIRTU));
        btnDef.setOnAction(e -> onAction.accept(BattleAction.DIFESA));
        btnCur.setOnAction(e -> onAction.accept(BattleAction.CURA));
        btnFug.setOnAction(e -> onFlee.run());

        grid.add(btnApriVirtu, 0, 0);
        grid.add(btnDef, 1, 0);
        grid.add(btnCur, 0, 1);
        grid.add(btnFug, 1, 1);
        return grid;
    }

    public VBox getView() {
        return layout;
    }
}