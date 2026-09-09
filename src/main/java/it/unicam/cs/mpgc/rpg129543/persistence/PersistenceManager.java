package it.unicam.cs.mpgc.rpg129543.persistence;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unicam.cs.mpgc.rpg129543.model.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/** Serializza e deserializza lo stato del giocatore su file JSON. */
public class PersistenceManager {
    private static final String DEFAULT_SAVE_FILENAME = "savegame.json";
    private final ObjectMapper mapper;
    private final Path percorsoSalvataggio;

    public PersistenceManager() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Serializza/deserializza direttamente sui campi, senza richiedere getter/setter per ognuno.
        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        this.percorsoSalvataggio = Paths.get(System.getProperty("user.dir"), DEFAULT_SAVE_FILENAME);
    }

    public void save(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Impossibile salvare uno stato del giocatore nullo.");
        }
        try {
            mapper.writeValue(percorsoSalvataggio.toFile(), player);
        } catch (IOException e) {
            throw new RuntimeException("Errore durante la scrittura del salvataggio: " + e.getMessage(), e);
        }
    }

    public Optional<Player> load() {
        if (!Files.exists(percorsoSalvataggio)) {
            return Optional.empty();
        }
        try {
            File file = percorsoSalvataggio.toFile();
            return Optional.ofNullable(mapper.readValue(file, Player.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}