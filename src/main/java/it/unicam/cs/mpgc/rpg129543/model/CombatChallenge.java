package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.Objects;

public class CombatChallenge implements Challenge {
    private final String nomeNemico;
    private final String descrizioneDettagliata;
    private boolean completata;

    public CombatChallenge(String nomeNemico, String descrizioneDettagliata) {
        this.nomeNemico = Objects.requireNonNull(nomeNemico);
        this.descrizioneDettagliata = descrizioneDettagliata;
        this.completata = false;
    }

    @Override
    public String risolvi(Player player, int pianoId) {
        return "START_COMBAT:" + nomeNemico;
    }

    @Override
    public boolean isCompletata() { return completata; }

    // METODO CRUCIALE PER IL MAIN E ROOM
    public void setCompletata(boolean completata) { this.completata = completata; }

    public String getNomeNemico() { return nomeNemico; }
    public String getDescrizioneDettagliata() { return descrizioneDettagliata; }
}