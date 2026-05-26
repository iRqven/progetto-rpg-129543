package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.Objects;

/**
 * Sfida di combattimento aperto contro un'entità ostile.
 * Richiede l'intervento del BattleEngine per essere risolta.
 */
public class CombatChallenge implements Challenge {
    private final String nomeNemico;
    private final String descrizioneDettagliata;
    private boolean completata;

    public CombatChallenge(String nomeNemico, String descrizioneDettagliata) {
        if (nomeNemico == null || nomeNemico.isBlank()) {
            throw new IllegalArgumentException("Il nome del nemico non può essere vuoto.");
        }
        this.nomeNemico = nomeNemico;
        this.descrizioneDettagliata = descrizioneDettagliata;
        this.completata = false;
    }

    @Override
    public String risolvi(Player player) {
        Objects.requireNonNull(player, "Il giocatore non può essere nullo.");
        if (completata) {
            return "L'eco di " + nomeNemico + " è stato dissipato. La stanza è silenziosa.";
        }
        // Restituisce un flag testuale che il Main intercetterà per deviare sul BattleTutorial
        return "START_COMBAT:" + nomeNemico;
    }

    @Override
    public boolean isCompletata() { return completata; }
    public void setCompletata(boolean completata) { this.completata = completata; }
    public String getDescrizioneDettagliata() { return descrizioneDettagliata; }
}