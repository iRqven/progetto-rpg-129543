package it.unicam.cs.mpgc.rpg129543.model;

import java.util.List;
import java.util.Objects;

public class GameState {
    private final List<Room> rooms;
    private int currentRoomIndex;

    public GameState(Player player) {
        this(player, RoomFactory.createRooms());
    }

    public GameState(Player player, List<Room> rooms) {
        Objects.requireNonNull(player, "Il giocatore non può essere nullo.");
        this.rooms = Objects.requireNonNull(rooms, "L'elenco delle stanze non può essere nullo.");
        if (rooms.isEmpty()) {
            throw new IllegalArgumentException("L'elenco delle stanze non può essere vuoto.");
        }
        this.currentRoomIndex = Math.clamp(player.getPianoCorrente(), 0, rooms.size() - 1);
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