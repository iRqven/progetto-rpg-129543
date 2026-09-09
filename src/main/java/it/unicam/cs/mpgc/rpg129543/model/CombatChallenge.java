package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.api.ChallengeResult;
import it.unicam.cs.mpgc.rpg129543.api.ChallengeType;

import java.util.Objects;

public class CombatChallenge implements Challenge {
    private final String nomeNemico;
    private final String descrizioneDettagliata;
    private final String fraseRabbia;
    private final String frasePaura;
    private final String fraseColpa;
    private boolean completata;

    public CombatChallenge(String nomeNemico, String descrizioneDettagliata,
                           String fraseRabbia, String frasePaura, String fraseColpa) {
        this.nomeNemico = Objects.requireNonNull(nomeNemico);
        this.descrizioneDettagliata = descrizioneDettagliata;
        this.fraseRabbia = fraseRabbia;
        this.frasePaura = frasePaura;
        this.fraseColpa = fraseColpa;
        this.completata = false;
    }

    @Override
    public ChallengeResult risolvi(Player player, int pianoId) {
        return new ChallengeResult(true, "START_COMBAT", 0, nomeNemico);
    }

    @Override
    public boolean isCompletata() { return completata; }

    @Override
    public ChallengeType getTipo() { return ChallengeType.COMBAT; }

    @Override
    public void markCompleted() { this.completata = true; }

    public String getNomeNemico() { return nomeNemico; }
    public String getDescrizioneDettagliata() { return descrizioneDettagliata; }
    public String getFraseRabbia() { return fraseRabbia; }
    public String getFrasePaura() { return frasePaura; }
    public String getFraseColpa() { return fraseColpa; }
}