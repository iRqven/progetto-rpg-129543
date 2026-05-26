package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.model.Player;
import java.util.Objects;

/**
 * Controller di mediazione delegato alla gestione degli incontri e delle sfide ambientali.
 * Raccoglie l'input della View ed esegue i contratti astratti del modello.
 */
public class EncounterController {

    /**
     * Gestisce l'interazione tra il giocatore e una sfida generica sfruttando il polimorfismo.
     *
     * @param player    Il protagonista dell'azione.
     * @param challenge La sfida astratta da avviare.
     * @return Il testo narrativo dell'esito da mostrare nella View.
     */
    public String gestisciIncontro(Player player, Challenge challenge) {
        // Clausole di guardia per evitare NullPointerException a run-time
        Objects.requireNonNull(player, "Il giocatore non può essere nullo durante un incontro.");
        Objects.requireNonNull(challenge, "La sfida da gestire non può essere nulla.");

        // Il controller non fa calcoli di dadi o modifiche dirette, delega tutto alla sfida astratta
        return challenge.risolvi(player);
    }
}