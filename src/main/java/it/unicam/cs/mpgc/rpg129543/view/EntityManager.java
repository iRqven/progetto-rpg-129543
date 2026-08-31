package it.unicam.cs.mpgc.rpg129543.view;

import it.unicam.cs.mpgc.rpg129543.model.Player;
import it.unicam.cs.mpgc.rpg129543.model.Room;
import it.unicam.cs.mpgc.rpg129543.util.AssetManager;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {

    public List<Node> createRoomEntities(Room currentRoom, Player player) {
        List<Node> entities = new ArrayList<>();

        Node boss = createBossEntity(currentRoom);
        if (boss != null) entities.add(boss); // Aggiunge il boss solo se esiste

        Node fragment = createFragmentEntity(currentRoom, player);
        if (fragment != null) entities.add(fragment);

        entities.add(createDoorEntity(currentRoom));

        return entities;
    }

    private Node createBossEntity(Room currentRoom) {
        if (!currentRoom.hasChallenge()) return null; // Restituisce null invece di un Group vuoto

        int pianoId = currentRoom.id();
        Image bossImg = AssetManager.getImage("boss_" + pianoId + ".png");
        if (bossImg == null || bossImg.isError()) {
            bossImg = AssetManager.getImage("boss.png");
        }

        if (bossImg != null && !bossImg.isError()) {
            ImageView bossView = new ImageView(bossImg);
            double imgW = bossImg.getWidth();
            double imgH = bossImg.getHeight();
            double displayH = 110.0;
            double displayW = displayH * (imgW / imgH);

            bossView.setFitWidth(displayW);
            bossView.setFitHeight(displayH);
            bossView.setX(currentRoom.npcX() - (displayW / 2.0));
            bossView.setY(currentRoom.npcY() - (displayH / 2.0));
            return bossView;
        } else {
            Circle boss = new Circle(50, Color.rgb(155, 89, 182, 0.7));
            boss.setEffect(new Glow(0.8));
            boss.setCenterX(currentRoom.npcX());
            boss.setCenterY(currentRoom.npcY());
            return boss;
        }
    }

    private Node createFragmentEntity(Room currentRoom, Player player) {
        if (currentRoom.hasFragment() && !player.getRicordi().contains(currentRoom.ricordoSbloccato())) {
            Polygon star = new Polygon(0, -10, 2, -2, 10, 0, 2, 2, 0, 10, -2, 2, -10, 0, -2, -2);
            star.setFill(Color.GOLD);
            star.setEffect(new Glow(1.0));
            star.setTranslateX(currentRoom.fragX());
            star.setTranslateY(currentRoom.fragY());
            return star;
        }
        return null;
    }

    private Node createDoorEntity(Room currentRoom) {
        Image doorImg = AssetManager.getImage("door.png");
        if (doorImg != null && !doorImg.isError()) {
            ImageView doorView = new ImageView(doorImg);
            doorView.setFitWidth(50);
            doorView.setFitHeight(80);
            doorView.setX(currentRoom.doorX() - 25);
            doorView.setY(currentRoom.doorY() - 40);
            return doorView;
        } else {
            Rectangle door = new Rectangle(50, 80, Color.rgb(241, 196, 15, 0.8));
            door.setX(currentRoom.doorX() - 25);
            door.setY(currentRoom.doorY() - 40);
            door.setStroke(Color.ORANGE);
            return door;
        }
    }
}