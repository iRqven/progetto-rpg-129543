package it.unicam.cs.mpgc.rpg129543.api;

import it.unicam.cs.mpgc.rpg129543.model.Player;

/**
 * Contratto fondamentale per la gestione delle interazioni e delle sfide nel gioco.
 * Permette l'estendibilità del dominio senza modificare la logica del ciclo di gioco principale.
 */
public interface Challenge {

    /**
     * Avvia la risoluzione della sfida applicando le regole specifiche.
     *
     * @param player Il giocatore che affronta la sfida (clausola di guardia obbligatoria).
     * @return String contenente l'esito narrativo dell'interazione.
     */
    String risolvi(Player player);

    /**
     * Verifica se la sfida è già stata affrontata e gestita.
     *
     * @return vero se la sfida è conclusa, falso altrimenti.
     */
    boolean isCompletata();
}