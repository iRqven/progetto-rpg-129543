package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.model.Player;
import java.util.Random;

public class EncounterController {
    private final Random random = new Random();

    public String handleChoice(Player player, Challenge challenge, boolean wantsToHelp) {
        if (!wantsToHelp) {
            player.addKarma(-10); // Punizione morale per l'indifferenza
            return "Hai deciso di ignorare la richiesta. Il Purgatorio si fa più freddo.";
        }

        int roll = random.nextInt(20) + 1; // Lancio del dado D20
        if (challenge.attempt(player, roll)) {
            player.addKarma(challenge.getKarmaReward());
            return "Risultato Dado: " + roll + " -> SUCCESSO! " + challenge.getSuccessMessage();
        } else {
            player.takeDamage(10); // Fallire una sfida morale ferisce l'anima (HP)
            return "Risultato Dado: " + roll + " -> FALLIMENTO. " + challenge.getFailureMessage();
        }
    }
}