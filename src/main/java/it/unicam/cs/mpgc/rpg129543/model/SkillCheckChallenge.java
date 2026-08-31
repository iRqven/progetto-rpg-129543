package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.Objects;
import java.util.Random;

public class SkillCheckChallenge implements Challenge {
    private static final int SOGLIA_DIFFICOLTA = 12;
    private static final int BONUS_KARMA = 15;
    private static final int MAX_DICE = 20;

    private final String dialogo;
    private final Random random = new Random();
    private boolean completata = false;

    public SkillCheckChallenge(String dialogo) {
        this.dialogo = Objects.requireNonNull(dialogo, "Il dialogo iniziale non può essere nullo.");
    }

    public String getDialogo() { return dialogo; }

    @Override
    public boolean isCombat() { return false; }

    @Override
    public boolean isSkillCheck() { return true; }

    @Override
    public String risolvi(Player player, int pianoId) {
        Objects.requireNonNull(player, "Il giocatore non può essere nullo.");
        if (completata) return "L'eco del passato si è spento tra i binari.";

        int tiro = random.nextInt(MAX_DICE) + 1;
        boolean successo = (tiro + player.getLivello()) >= SOGLIA_DIFFICOLTA;
        this.completata = true;

        if (successo) {
            player.addKarma(BONUS_KARMA);
            return "[SUCCESSO - Lancio: " + tiro + " + Liv. " + player.getLivello() + " >= " + SOGLIA_DIFFICOLTA + "]\nHai trovato la forza di superare il tuo egoismo. Ottieni +" + BONUS_KARMA + " Karma.";
        }
        return "[FALLIMENTO - Lancio: " + tiro + " + Liv. " + player.getLivello() + " < " + SOGLIA_DIFFICOLTA + "]\nL'esitazione cinica ti blocca. La figura svanisce lasciandoti solo con le tue colpe.";
    }

    @Override
    public boolean isCompletata() { return completata; }
}