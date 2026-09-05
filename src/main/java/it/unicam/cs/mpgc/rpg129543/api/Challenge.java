package it.unicam.cs.mpgc.rpg129543.api;

import it.unicam.cs.mpgc.rpg129543.model.Player;

public interface Challenge {

    ChallengeResult risolvi(Player player, int pianoId);

    boolean isCompletata();

    /**
     * Tipo della sfida. Sostituisce isCombat()/isSkillCheck(): il chiamante
     * può fare un semplice switch invece di una catena di if/else su flag booleani,
     * ed è più facile aggiungere un nuovo tipo di sfida in futuro.
     */
    ChallengeType getTipo();

    /**
     * Segnala alla sfida che è stata risolta da un evento esterno
     * (es. vittoria in combattimento gestita da BattleEngine).
     * Sostituisce il precedente controllo "instanceof CombatChallenge" fatto da Room,
     * che rompeva l'incapsulamento facendo un cast esplicito sull'interfaccia.
     * Le sfide che si auto-completano già dentro risolvi() possono ignorare questo metodo.
     */
    default void markCompleted() {
        // no-op di default
    }
}