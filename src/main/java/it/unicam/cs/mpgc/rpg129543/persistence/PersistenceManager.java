package it.unicam.cs.mpgc.rpg129543.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unicam.cs.mpgc.rpg129543.model.Player;

import java.io.File;
import java.io.IOException;

public class PersistenceManager {
    private final ObjectMapper mapper;
    private final String FILE_PATH = "savegame.json";

    public PersistenceManager() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Salva lo stato del player su file.
     */
    public void save(Player player) {
        try {
            mapper.writeValue(new File(FILE_PATH), player);
            System.out.println("Sistema: Progresso salvato nel Purgatorio.");
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    /**
     * Carica il player dal file. Se il file non esiste, restituisce null.
     */
    public Player load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return null;

        try {
            return mapper.readValue(file, Player.class);
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento: " + e.getMessage());
            return null;
        }
    }
}