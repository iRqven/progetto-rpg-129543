package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.Objects;
import java.util.Random;

/**
 * Rappresenta la sfida di interazione morale con l'Ombra di una Vecchia Signora.
 * Gestisce la logica di superamento della prova basandosi sulle statistiche del giocatore.
 */
public class OldLadyChallenge implements Challenge {
    // Sostituzione dei Magic Numbers con costanti esplicite per il Clean Code
    private static final int SOGLIA_DIFFICOLTA = 12;
    private static final int PREMIO_KARMA = 15;
    private static final int MASSIMO_LANCIO_DADO = 20;

    private final Random random;
    private boolean completata;

    /**
     * Costruttore della sfida. Inizializza lo stato e il generatore di numeri casuali.
     */
    public OldLadyChallenge() {
        this.random = new Random();
        this.completata = false;
    }

    /**
     * Risolve l'interazione con l'anziana calcolando l'esito tramite un lancio di dadi
     * influenzato dal livello attuale del protagonista.
     *
     * @param player Il giocatore che affronta la sfida.
     * @return Il testo descrittivo dell'esito da mostrare nell'interfaccia grafica.
     */
    @Override
    public String risolvi(Player player) {
        // Clausola di guardia per la programmazione difensiva
        Objects.requireNonNull(player, "Impossibile avviare la sfida per un giocatore nullo.");

        if (completata) {
            return "L'ombra della vecchia signora ha già trovato la sua strada oltre la nebbia.";
        }

        // Simulazione del lancio del dado (da 1 a 20) in linea con i sistemi RPG classici
        int tiroDado = random.nextInt(MASSIMO_LANCIO_DADO) + 1;

        // Applichiamo la tua formula: (Tiro + Livello) >= Difficoltà
        boolean successo = (tiroDado + player.getLivello()) >= SOGLIA_DIFFICOLTA;

        this.completata = true;

        if (successo) {
            player.addKarma(PREMIO_KARMA);
            return "Risultato Dado: " + tiroDado + " (Livello +" + player.getLivello() + ") -> SUCCESSO!\n"
                    + "L'hai aiutata. Un calore familiare ti avvolge. Sblocchi un ricordo d'infanzia.";
        } else {
            // Il fallimento non altera il karma negativamente, ma pesa sulla coscienza narrativa
            return "Risultato Dado: " + tiroDado + " (Livello +" + player.getLivello() + ") -> FALLIMENTO.\n"
                    + "Hai esitato o hai fallito. La vecchia svanisce nella nebbia. Il senso di colpa ti pesa.";
        }
    }

    @Override
    public boolean isCompletata() {
        return this.completata;
    }
}