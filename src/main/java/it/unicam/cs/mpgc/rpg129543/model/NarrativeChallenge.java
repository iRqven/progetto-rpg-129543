package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.api.ChallengeResult;
import it.unicam.cs.mpgc.rpg129543.api.ChallengeType;

import java.util.Objects;

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
    public ChallengeResult risolvi(Player player, int pianoCorrente) {
        Objects.requireNonNull(player, "Player nullo durante la risoluzione narrativa.");
        this.completata = true;
        return new ChallengeResult(true, dialogo, 0, oggettoRilasciato);
    }

    @Override
    public boolean isCompletata() { return completata; }

    @Override
    public ChallengeType getTipo() { return ChallengeType.NARRATIVE; }

    public String getNomeNPC() { return nomeNPC; }
}