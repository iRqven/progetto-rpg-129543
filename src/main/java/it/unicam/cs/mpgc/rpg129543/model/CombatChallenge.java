package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.api.ChallengeResult;
import it.unicam.cs.mpgc.rpg129543.api.ChallengeType;
import java.util.Objects;

public class CombatChallenge implements Challenge {
    private final String nomeNemico;
    private final String descrizioneDettagliata;
    private final String fRabbia;
    private final String fPaura;
    private final String fColpa;
    private boolean completata;

    public CombatChallenge(String nomeNemico, String descrizioneDettagliata, String fRabbia, String fPaura, String fColpa) {
        this.nomeNemico = Objects.requireNonNull(nomeNemico);
        this.descrizioneDettagliata = descrizioneDettagliata;
        this.fRabbia = fRabbia;
        this.fPaura = fPaura;
        this.fColpa = fColpa;
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

    public void setCompletata(boolean completata) { this.completata = completata; }
    public String getNomeNemico() { return nomeNemico; }
    public String getDescrizioneDettagliata() { return descrizioneDettagliata; }
    public String getfRabbia() { return fRabbia; }
    public String getfPaura() { return fPaura; }
    public String getfColpa() { return fColpa; }
}