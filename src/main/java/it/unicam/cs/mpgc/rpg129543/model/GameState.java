package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final Player player;
    private final List<Room> rooms;
    private int currentRoomIndex = 0;

    public GameState(Player player) {
        this.player = player;
        this.rooms = new ArrayList<>();
        initRooms();
    }

    private void initRooms() {
        // STANZA 0:
        rooms.add(new Room(0,
                "Atrio delle Ombre",
                "Un luogo sfuocato. Senti il peso di mille vite.",
                null, 0, 0, 750, 300,
                "Un piccolo orologio da taschino fermo alle 14:02.",
                200, 400, true)); // Qui c'è un frammento vicino all'inizio

        // STANZA 1:
        rooms.add(new Room(1,
                "Incrocio delle Esitazioni",
                "Una figura minuta ti osserva. Sembra aspettare qualcosa che non è mai arrivato.",
                new OldLadyChallenge(), 400, 300, 750, 500,
                "Una mano tesa nel buio che non hai mai afferrato.",
                600, 100, true));

        // STANZA 2:
        rooms.add(new Room(2,
                "Vicolo dell'Abbandono",
                "L'aria è gelida. Frammenti di specchi rotti coprono il suolo.",
                new OldLadyChallenge(), 300, 450, 750, 100,
                "Il pianto di un cane rimasto solo sotto la pioggia.",
                100, 100, true));

        // GENERAZIONE PROCEDURALE PER I PIANI 3-9
        for (int i = 3; i < 10; i++) {
            double npcX = 200 + Math.random() * 400;
            double npcY = 150 + Math.random() * 300;
            double fragX = 100 + Math.random() * 600;
            double fragY = 100 + Math.random() * 400;

            rooms.add(new Room(i,
                    "Piano del Purgatorio n. " + i,
                    "La nebbia si infittisce. I ricordi diventano più nitidi e dolorosi.",
                    (i % 2 == 0) ? new OldLadyChallenge() : null,
                    npcX, npcY, 750, 300,
                    "Frammento di vita n. " + i + ": Una voce familiare che chiama il tuo nome.",
                    fragX, fragY, true));
        }
    }

    public Room getCurrentRoom() {
        return rooms.get(currentRoomIndex);
    }

    public boolean nextRoom() {
        if (currentRoomIndex < rooms.size() - 1) {
            currentRoomIndex++;
            return true;
        }
        return false; // Trigger per il Giudizio Finale
    }
}