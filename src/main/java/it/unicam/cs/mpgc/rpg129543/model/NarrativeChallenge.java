package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.Objects;

/**
 * Incontro puramente narrativo che sblocca frammenti di Lore e rivela il passato del protagonista.
 */
public class NarrativeChallenge implements Challenge {
    private final String nomeSpirito;
    private final String rivelazioneStoria;
    private final String frammentoMemoria;
    private boolean completata;

    public NarrativeChallenge(String nomeSpirito, String rivelazioneStoria, String frammentoMemoria) {
        if (nomeSpirito == null || rivelazioneStoria == null || frammentoMemoria == null) {
            throw new IllegalArgumentException("I parametri della sfida narrativa non possono essere nulli.");
        }
        this.nomeSpirito = nomeSpirito;
        this.rivelazioneStoria = rivelazioneStoria;
        this.frammentoMemoria = frammentoMemoria;
        this.completata = false;
    }

    @Override
    public String risolvi(Player player) {
        Objects.requireNonNull(player, "Il giocatore non può essere nullo.");

        if (!completata) {
            player.addRicordo(frammentoMemoria);
            this.completata = true;
        }

        return "💡 " + nomeSpirito + " ti parla:\n\"" + rivelazioneStoria + "\"\n\n[Nuovo indizio aggiunto ai tuoi Ricordi!]";
    }

    @Override
    public boolean isCompletata() { return completata; }
}