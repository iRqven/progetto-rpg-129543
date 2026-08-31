package it.unicam.cs.mpgc.rpg129543.api;

import it.unicam.cs.mpgc.rpg129543.model.Player;

public interface Challenge {
    String risolvi(Player player, int pianoCorrente);
    boolean isCompletata();
    boolean isCombat(); // Nuovo metodo per rimuovere l'instanceof
    boolean isSkillCheck();
}