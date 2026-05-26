package it.unicam.cs.mpgc.rpg129543.model;
import it.unicam.cs.mpgc.rpg129543.api.Challenge;

public class AnimalChallenge implements Challenge {
    @Override
    public String getDescription() {
        return "Un animale sacro sbarra la strada. Ucciderlo ti darà potere (Livello+1) ma perderai Karma.";
    }
    @Override
    public boolean attempt(Player player, int diceRoll) {
        return diceRoll > 15; // Difficilissimo da superare senza violenza
    }
    @Override public int getKarmaReward() { return -30; } // Scelta malvagia
    @Override public String getSuccessMessage() { return "Hai risparmiato la creatura. La tua anima brilla."; }
    @Override public String getFailureMessage() { return "Hai ceduto all'istinto primordiale."; }
}