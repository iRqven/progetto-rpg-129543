package it.unicam.cs.mpgc.rpg129543.api;

import it.unicam.cs.mpgc.rpg129543.model.Player;

public interface Challenge {

    ChallengeResult risolvi(Player player, int pianoId);

    boolean isCompletata();

    /** Tipo della sfida, usato per lo switch di dispatch nel controller. */
    ChallengeType getTipo();

    /** Segnala il completamento da un evento esterno (es. vittoria in combattimento). */
    default void markCompleted() {
    }
}