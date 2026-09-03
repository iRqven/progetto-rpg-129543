package it.unicam.cs.mpgc.rpg129543.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void testDistanzaMinimaBossFrammento() {
        // Creiamo la configurazione raggruppata
        RoomConfig config = new RoomConfig(730.0, 300.0, "Ricordo", true,
                "bg.jpg", "door.png", "frag.png");

        // Passiamo il DTO al costruttore snellito
        Room room = new Room(1, "Stanza Test", "Descrizione", null, config);

        // Calcoliamo la distanza euclidea generata
        double distanzaX = Math.pow(room.fragX() - room.npcX(), 2);
        double distanzaY = Math.pow(room.fragY() - room.npcY(), 2);
        double distanzaTotale = Math.sqrt(distanzaX + distanzaY);

        // Verifica che la distanza sia sempre >= 130 pixel
        assertTrue(distanzaTotale >= 130.0, "Errore: Il frammento e il boss sono spawnati troppo vicini! Distanza: " + distanzaTotale);
    }

    @Test
    void testRisoluzioneSfida() {
        RoomConfig config = new RoomConfig(730.0, 300.0, "Ric", false,
                "bg", "door", "frag");
        Room room = new Room(1, "Stanza Test", "Desc", null, config);

        room.solveChallenge();
        assertTrue(room.isSfidaGestita(), "La stanza deve risultare 'gestita' dopo aver risolto la sfida.");
    }
}