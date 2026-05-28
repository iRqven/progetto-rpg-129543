package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.Objects;

/**
 * Rappresenta una sfida di interazione con una creatura bestiale del Purgatorio.
 * Implementa il contratto Challenge isolando la propria logica di risoluzione.
 */
public class AnimalChallenge implements Challenge {
    private static final int MALUS_KARMA_CRUCCIO = -30;
    private static final int BONUS_KARMA_PIETA = 15;
    private static final String DESCR_COMPLETATA = "La creatura ti osserva con gratitudine prima di svanire nel nulla.";

    private final String descrizioneAnimale;
    private boolean completata;

    public AnimalChallenge(String descrizioneAnimale) {
        if (descrizioneAnimale == null || descrizioneAnimale.isBlank()) {
            throw new IllegalArgumentException("La descrizione della creatura non può essere nulla o vuota.");
        }
        this.descrizioneAnimale = descrizioneAnimale;
        this.completata = false;
    }

    @Override
    public String risolvi(Player player, int pianoCorrente) {
        Objects.requireNonNull(player, "Impossibile risolvere la sfida per un giocatore nullo.");

        if (completata) {
            return DESCR_COMPLETATA;
        }

        if (player.getKarma() >= 0) {
            player.addKarma(BONUS_KARMA_PIETA);
            this.completata = true;
            return "Incontri: " + descrizioneAnimale + " -> SUCCESSO! Hai risparmiato la creatura. La tua anima brilla.";
        } else {
            player.addKarma(MALUS_KARMA_CRUCCIO);
            this.completata = true;
            return "Incontri: " + descrizioneAnimale + " -> FALLIMENTO. Hai ceduto all'istinto primordiale e scacciato la fiera.";
        }
    }

    @Override
    public boolean isCompletata() {
        return this.completata;
    }
}