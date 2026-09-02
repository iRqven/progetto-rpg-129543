package it.unicam.cs.mpgc.rpg129543.view;

import it.unicam.cs.mpgc.rpg129543.model.Player;
import it.unicam.cs.mpgc.rpg129543.model.Room;
import it.unicam.cs.mpgc.rpg129543.util.AssetManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameView {
    private final Pane gameArea;
    private Group playerGroup;
    private ImageView playerImageView;
    private Rectangle hpBar;
    private static final double HP_BAR_WIDTH = 40;

    private int frameIndex = 0;
    private long lastFrameTime = 0;

    private final EntityManager entityManager = new EntityManager();

    private int getTotalFrames(Player player) {
        return "female".equalsIgnoreCase(player.getGenereSprite()) ? 12 : 8;
    }

    public void updateAnimation(boolean isMoving, Player player) {
        if (playerImageView == null || playerImageView.getImage() == null) return;

        Image img = playerImageView.getImage();
        double totalWidth = img.getWidth();
        double totalHeight = img.getHeight();

        int maxFrames = getTotalFrames(player);
        double frameW = totalWidth / (double) maxFrames;

        if (isMoving) {
            if (System.currentTimeMillis() - lastFrameTime > 90) {
                frameIndex = (frameIndex + 1) % maxFrames;
                playerImageView.setViewport(new Rectangle2D(frameIndex * frameW, 0, frameW, totalHeight));
                lastFrameTime = System.currentTimeMillis();
            }
        } else {
            frameIndex = 0;
            playerImageView.setViewport(new Rectangle2D(0, 0, frameW, totalHeight));
        }
    }

    public void updatePlayerSprite(Player player) {
        playerGroup.getChildren().removeIf(node -> !(node instanceof Rectangle));

        boolean isFemale = "female".equalsIgnoreCase(player.getGenereSprite());

        String assetName = isFemale ? "players/player_female.png" : "players/player_male.png";

        Image playerImg = AssetManager.getImage(assetName);

        if (playerImg != null && !playerImg.isError()) {
            playerImageView = new ImageView(playerImg);

            int maxFrames = getTotalFrames(player);
            double initialFrameW = playerImg.getWidth() / (double) maxFrames;
            playerImageView.setViewport(new Rectangle2D(0, 0, initialFrameW, playerImg.getHeight()));

            double displaySize = 95.0;
            playerImageView.setFitWidth(displaySize);
            playerImageView.setFitHeight(displaySize);
            playerImageView.setX(-displaySize / 2.0);
            playerImageView.setY(-displaySize / 2.0);

            playerGroup.getChildren().add(0, playerImageView);
        } else {
            playerImageView = null;
            Circle testa = new Circle(0, -5, 7, Color.GHOSTWHITE);
            playerGroup.getChildren().add(0, testa);
        }
    }

    public GameView(Player player) {
        gameArea = new Pane();
        gameArea.setPrefSize(800, 600);
        createPlayerGraphics(player);
    }

    public Pane getGameArea() {
        return gameArea;
    }

    private void createPlayerGraphics(Player player) {
        Rectangle hpBg = new Rectangle(HP_BAR_WIDTH, 5, Color.web("#330000"));
        hpBg.setX(-20); hpBg.setY(-35);

        hpBar = new Rectangle(HP_BAR_WIDTH, 5, Color.LIME);
        hpBar.setX(-20); hpBar.setY(-35);

        playerGroup = new Group();
        playerGroup.getChildren().addAll(hpBg, hpBar);

        updatePlayerSprite(player);
    }

    public void refreshRoomGraphics(Room currentRoom, Player player) {
        gameArea.getChildren().clear();

        updateBackground(currentRoom.backgroundAssetName());

        gameArea.getChildren().addAll(entityManager.createRoomEntities(currentRoom, player));

        gameArea.getChildren().add(playerGroup);
    }

    private void updateBackground(String backgroundAssetName) {
        Image bgImg = AssetManager.getImage(backgroundAssetName);
        if (bgImg != null && !bgImg.isError()) {
            BackgroundImage bgImage = new BackgroundImage(
                    bgImg,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );
            gameArea.setBackground(new Background(bgImage));
        } else {
            gameArea.setStyle("-fx-background-color: #7f7f7f;");
        }
    }

    public void renderPlayer(Player player) {
        playerGroup.setTranslateX(player.getX());
        playerGroup.setTranslateY(player.getY());
        hpBar.setWidth(HP_BAR_WIDTH * (player.getHp() / (double)player.getHpMax()));
    }

    public void applyDamageEffect() {
        playerGroup.setOpacity(0.4);
        PauseTransition p = new PauseTransition(Duration.millis(250));
        p.setOnFinished(e -> playerGroup.setOpacity(1.0));
        p.play();
    }

    /**
     * Anima il frammento facendolo volare verso l'icona dello zaino.
     * @param frammento Il nodo grafico del frammento da animare.
     * @param zainoX La coordinata X a cui si trova l'icona dello zaino a schermo.
     * @param zainoY La coordinata Y a cui si trova l'icona dello zaino a schermo.
     */
    public void animaRaccoltaFrammento(Node frammento, double zainoX, double zainoY) {
        frammento.toFront();

        double startX = frammento.getBoundsInParent().getMinX();
        double startY = frammento.getBoundsInParent().getMinY();

        TranslateTransition volo = new TranslateTransition(Duration.millis(500), frammento);
        volo.setByX(zainoX - startX);
        volo.setByY(zainoY - startY);

        ScaleTransition scala = new ScaleTransition(Duration.millis(500), frammento);
        scala.setToX(0.2);
        scala.setToY(0.2);

        FadeTransition dissolvenza = new FadeTransition(Duration.millis(500), frammento);
        dissolvenza.setToValue(0.0);

        ParallelTransition animazioneCompleta = new ParallelTransition(volo, scala, dissolvenza);

        animazioneCompleta.setOnFinished(e -> gameArea.getChildren().remove(frammento));

        animazioneCompleta.play();
    }
    public void avviaAnimazioneRaccolta(Room currentRoom, double zainoX, double zainoY) {
        // Cerca il nodo grafico del frammento basandosi sulle sue coordinate
        Node fragmentNode = null;
        for (Node n : gameArea.getChildren()) {
            // Se il nodo si trova esattamente dove dovrebbe esserci il frammento
            if (Math.abs(n.getBoundsInParent().getMinX() - (currentRoom.fragX() - n.getBoundsInParent().getWidth()/2)) < 5 &&
                    Math.abs(n.getBoundsInParent().getMinY() - (currentRoom.fragY() - n.getBoundsInParent().getHeight()/2)) < 5) {
                fragmentNode = n;
                break;
            }
        }

        if (fragmentNode != null) {
            animaRaccoltaFrammento(fragmentNode, zainoX, zainoY);
        }
    }
}