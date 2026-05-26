package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;

public class OldLadyChallenge implements Challenge {
    private final int difficulty = 12; // Soglia da superare con i dadi

    @Override
    public String getDescription() {
        return "Una vecchia signora è bloccata sul ciglio di una strada di nebbia. Ti guarda con occhi imploranti.";
    }

    @Override
    public boolean attempt(Player player, int diceRoll) {
        // Ora .getLivello() è riconosciuto!
        return (diceRoll + player.getLivello()) >= difficulty;
    }

    @Override
    public int getKarmaReward() { return 15; } // Aiutare aumenta il karma verso il Paradiso

    @Override
    public String getSuccessMessage() { return "L'hai aiutata. Un calore familiare ti avvolge. Sblocchi un ricordo d'infanzia."; }

    @Override
    public String getFailureMessage() { return "Hai esitato o hai fallito. La vecchia svanisce nella nebbia. Il senso di colpa ti pesa."; }
}