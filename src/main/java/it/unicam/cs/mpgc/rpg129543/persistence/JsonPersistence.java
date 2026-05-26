package it.unicam.cs.mpgc.rpg129543.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unicam.cs.mpgc.rpg129543.model.Player;
import java.io.File;
import java.io.IOException;

/**
 * Responsabilità: Gestire il salvataggio e caricamento dello stato su file JSON.
 * Rispetta il principio di separazione tra logica e infrastruttura.
 */
public class JsonPersistence {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String SAVE_FILE = "savegame.json";

    public void saveGame(Player player) throws IOException {
        mapper.writeValue(new File(SAVE_FILE), player);
        System.out.println("Progresso salvato nel Purgatorio.");
    }

    public Player loadGame() throws IOException {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return null;
        return mapper.readValue(file, Player.class);
    }
}