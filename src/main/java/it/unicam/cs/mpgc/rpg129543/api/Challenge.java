package it.unicam.cs.mpgc.rpg129543.api;

import it.unicam.cs.mpgc.rpg129543.model.Player;

public interface Challenge {
    ChallengeResult risolvi(Player player, int pianoId);

    boolean isCompletata();
    boolean isCombat();
    boolean isSkillCheck();
}