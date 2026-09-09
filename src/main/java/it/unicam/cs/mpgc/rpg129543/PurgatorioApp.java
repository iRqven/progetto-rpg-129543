package it.unicam.cs.mpgc.rpg129543;

import it.unicam.cs.mpgc.rpg129543.controller.*;
import it.unicam.cs.mpgc.rpg129543.model.*;
import it.unicam.cs.mpgc.rpg129543.persistence.PersistenceManager;
import it.unicam.cs.mpgc.rpg129543.view.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PurgatorioApp extends Application {
    private Player player;
    private GameState gameState;
    private final PersistenceManager persistence = new PersistenceManager();
    private GameLoopController gameLoop;
    private GameRouter router;
    private Stage mainStage;

    @Override
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;
        initializeGameData();
        setupUI();
    }

    private void initializeGameData() {
        this.player = persistence.load().orElseGet(() -> {
            Player nuovoPlayer = new Player("Anima", "Ombra", "Viandante");
            nuovoPlayer.setHp(100);
            nuovoPlayer.setKarma(0);
            return nuovoPlayer;
        });
        this.gameState = new GameState(player);
    }

    private void setupUI() {
        HudView hudView = new HudView(() -> {
            if (router != null) router.showMemoryArchive();
        });

        GameView gameView = new GameView(player, hudView);
        VBox root = new VBox(hudView.getHudNode(), gameView.getGameArea());
        root.setStyle("-fx-background-color: #000;");
        Scene scene = new Scene(root, 800, 650);

        // 1. Inizializzazione Router (Implementa le nuove interfacce di Callback)
        router = new GameRouter(player, gameState, gameView, hudView, persistence, this::resetGame, this::loopGame);

        // 2. Inizializzazione Controller
        InputController inputController = new InputController(scene,
                router::togglePauseMenu,
                router::showMemoryArchive
        );

        BattleController battleController = new BattleController(player, gameState, router);
        GameController gameController = new GameController(player, gameState, gameView, hudView, inputController, router);

        // Connessione bidirezionale
        router.setControllers(gameController, battleController, inputController);

        // 3. Avvio del Game Loop
        gameLoop = new GameLoopController(
                gameController::update,
                () -> gameView.renderPlayer(player)
        );
        gameLoop.startLoop();
        router.aggiornaStanzaEHud();

        if (player.getKarma() == 0 && player.getRicordi().isEmpty() && gameState.getCurrentRoom().id() == 0) {
            router.showIntro();
        }

        mainStage.setTitle("Purgatorio RPG - Metodologie");
        mainStage.setScene(scene);
        mainStage.setResizable(false);
        mainStage.show();
    }

    @Override
    public void stop() {
        // Metodo nativo chiamato da Platform.exit() o chiusura finestra
        if (gameLoop != null) gameLoop.stopLoop();
        persistence.save(player);
    }

    private void resetGame() {
        if (router != null) router.chiudiOverlayCorrente();
        if (gameLoop != null) gameLoop.stopLoop();

        this.player = new Player("Anima", "Ombra", "Viandante");
        this.player.setHp(100);
        this.player.setKarma(0);
        this.gameState = new GameState(player);

        persistence.save(player);
        setupUI();
    }

    private void loopGame() {
        if (router != null) router.chiudiOverlayCorrente();
        if (gameLoop != null) gameLoop.stopLoop();

        player.preparaPerNuovaRun();
        this.gameState = new GameState(player);

        persistence.save(player);
        setupUI();
    }

    public static void main(String[] args) {
        launch(args);
    }
}