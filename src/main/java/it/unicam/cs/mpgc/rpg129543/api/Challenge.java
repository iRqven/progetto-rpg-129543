package it.unicam.cs.mpgc.rpg129543.api;

import it.unicam.cs.mpgc.rpg129543.model.Player;

/**
 * Contratto astratto per la risoluzione polimorfa delle sfide di gioco[cite: 401, 557].
 */
public interface Challenge {
    /**
     * Risolve la logica interna della sfida e restituisce il testo per la View[cite: 408].
     */
    String risolvi(Player player, int pianoId);

    boolean isCompletata();
}