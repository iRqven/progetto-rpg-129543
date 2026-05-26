package it.unicam.cs.mpgc.rpg129543.api;

import it.unicam.cs.mpgc.rpg129543.model.Player;

public interface Challenge {
    String getDescription();
    boolean attempt(Player player, int diceRoll);
    int getKarmaReward();
    String getSuccessMessage();
    String getFailureMessage();
}