package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.Objects;

/**
 * Gestisce i dialoghi puramente statici e di interazione testuale con i personaggi.
 */
public class NarrativeChallenge implements Challenge {
    private final String nomeNPC;
    private final String dialogo;
    private final String oggettoRilasciato;
    private boolean completata;

    public NarrativeChallenge(String nomeNPC, String dialogo, String oggettoRilasciato) {
        this.nomeNPC = Objects.requireNonNull(nomeNPC, "Il nome NPC non può essere nullo.");
        this.dialogo = dialogo;
        this.oggettoRilasciato = oggettoRilasciato;
        this.completata = false;
    }

    @Override
    public String risolvi(Player player, int pianoCorrente) {
        Objects.requireNonNull(player, "Player nullo durante la risoluzione narrativa.");
        this.completata = true;

        return nomeNPC.toUpperCase() + ":\n" + dialogo + "\n\n[Sblocchi l'indizio: " + oggettoRilasciato + "]";
    }

    @Override
    public boolean isCompletata() {
        return this.completata;
    }
}