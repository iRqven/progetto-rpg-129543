package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.util.DefaultRandomSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void testRoomCreation() {
        RoomConfig config = new RoomConfig(730.0, 300.0, "Ricordo", true, "bg.jpg", "door.png", "frag.png");
        Room room = new Room(1, "Stanza Test", "Descrizione", null, config, new DefaultRandomSource());

        assertEquals(1, room.id());
        assertEquals("Stanza Test", room.nome());
        assertNull(room.sfida());
        assertTrue(room.isSfidaGestita()); // Senza sfida, è autogestita
    }

    @Test
    void testFragmentCollection() {
        RoomConfig config = new RoomConfig(730.0, 300.0, "Ricordo", true, "bg.jpg", "door.png", "frag.png");
        Room room = new Room(1, "Stanza Test", "Desc", null, config, new DefaultRandomSource());

        assertFalse(room.isFrammentoRaccolto());
        room.setFrammentoRaccolto(true);
        assertTrue(room.isFrammentoRaccolto());
    }
}