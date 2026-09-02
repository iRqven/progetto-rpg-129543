package it.unicam.cs.mpgc.rpg129543.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void testDistanzaMinimaBossFrammento() {
        // Creiamo una stanza generica (il costruttore calcolerà le coordinate random)
        Room room = new Room(1, "Stanza Test", "Descrizione", null,
                730.0, 300.0, "Ricordo", true,
                "bg.jpg", "door.png", "frag.png");

        // Calcoliamo la distanza euclidea generata
        double distanzaX = Math.pow(room.fragX() - room.npcX(), 2);
        double distanzaY = Math.pow(room.fragY() - room.npcY(), 2);
        double distanzaTotale = Math.sqrt(distanzaX + distanzaY);

        // Verifica che la distanza sia sempre >= 130 pixel
        assertTrue(distanzaTotale >= 130.0, "Errore: Il frammento e il boss sono spawnati troppo vicini! Distanza: " + distanzaTotale);
    }

    @Test
    void testRisoluzioneSfida() {
        Room room = new Room(1, "Stanza Test", "Desc", null,
                730, 300, "Ric", false, "bg", "door", "frag");

        room.solveChallenge();
        assertTrue(room.isSfidaGestita(), "La stanza deve risultare 'gestita' dopo aver risolto la sfida.");
    }
}