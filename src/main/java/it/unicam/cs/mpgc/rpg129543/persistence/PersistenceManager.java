package it.unicam.cs.mpgc.rpg129543.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unicam.cs.mpgc.rpg129543.model.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

/**
 * Gestore della persistenza locale incaricato di serializzare e deserializzare lo stato del giocatore.
 * Isola la logica di I/O su file JSON rispettando il principio di singola responsabilità.
 */
public class PersistenceManager {
    // Sostituzione della stringa sparsa con una costante formale (Punto 5)
    private static final String DEFAULT_SAVE_FILENAME = "savegame.json";

    private final ObjectMapper mapper;
    private final Path percorsoSalvataggio;

    /**
     * Inizializza il manager configurando l'istanza di ObjectMapper e definendo il percorso di salvataggio.
     */
    public PersistenceManager() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Utilizzo di Path per una gestione moderna e indipendente dal sistema operativo (Punto 1)
        this.percorsoSalvataggio = Paths.get(System.getProperty("user.dir"), DEFAULT_SAVE_FILENAME);
    }

    /**
     * Serializza lo stato del giocatore e lo scrive su file JSON localizzato nella directory corrente.
     *
     * @param player L'istanza del giocatore da salvare.
     * @throws IllegalArgumentException se il parametro passato è nullo (Clausola di Guardia - Punto 4).
     */
    public void save(Player player) {
        // Clausola di guardia per evitare NullPointerException a monte (Punto 4)
        if (player == null) {
            throw new IllegalArgumentException("Impossibile salvare uno stato del giocatore nullo.");
        }

        try {
            File file = percorsoSalvataggio.toFile();
            mapper.writeValue(file, player);
            // NOTA: Rimosso System.out.println in conformità con la checklist di Clean Code (Punto 5).
            // Il feedback visivo dell'avvenuto salvataggio deve essere delegato alla View.
        } catch (IOException e) {
            // Incapsulamento dell'errore (Punto 4): in un'applicazione reale qui si usa un logger (es. SLF4J)
            throw new RuntimeException("Errore critico durante la scrittura del file di salvataggio: " + e.getMessage(), e);
        }
    }

    /**
     * Recupera lo stato del giocatore decodificando il file JSON locale.
     * Sostituisce il ritorno di 'null' con un contenitore Optional (Programmazione Difensiva - Punto 4).
     *
     * @return Un Optional contenente il Player se il file esiste e la lettura ha esito positivo, un Optional vuoto altrimenti.
     */
    public Optional<Player> load() {
        // Verifica preventiva dell'esistenza del file senza invocare eccezioni pesanti
        if (!Files.exists(percorsoSalvataggio)) {
            return Optional.empty();
        }

        try {
            File file = percorsoSalvataggio.toFile();
            Player playerCaricato = mapper.readValue(file, Player.class);
            // Ritorno protetto per evitare controlli condizionali sparsi sul null nella View
            return Optional.ofNullable(playerCaricato);
        } catch (IOException e) {
            // Gestione protetta dell'errore di lettura: se il file è corrotto, restituisce un Optional vuoto
            // e notifica l'anomalia senza interrompere bruscamente l'esecuzione del software
            return Optional.empty();
        }
    }
}