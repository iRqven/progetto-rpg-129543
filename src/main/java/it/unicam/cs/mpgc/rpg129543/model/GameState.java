package it.unicam.cs.mpgc.rpg129543.model;

import java.util.List;
import java.util.Objects;

public class GameState {
    private static final int START_INDEX = 0;
    private final List<Room> rooms;
    private int currentRoomIndex = START_INDEX;

    public GameState(Player player) {
        Objects.requireNonNull(player, "Il giocatore non puo essere nullo.");
        this.rooms = RoomFactory.createRooms();
    }

    public Room getCurrentRoom() { return rooms.get(currentRoomIndex); }

    public boolean nextRoom() {
        if (currentRoomIndex < rooms.size() - 1) {
            currentRoomIndex++;
            return true;
        }
        return false;
    }
}