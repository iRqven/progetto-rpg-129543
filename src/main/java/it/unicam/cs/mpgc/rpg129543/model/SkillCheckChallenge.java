package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.Objects;
import java.util.Random;

/**
 * Gestisce i controlli di abilità stocastici condizionati dal piano logico[cite: 27].
 */
public class SkillCheckChallenge implements Challenge {
    private static final int SOGLIA_DIFFICOLTA = 12;
    private static final int BONUS_KARMA = 15;
    private static final int MAX_DICE = 20;

    private final Random random = new Random();
    private boolean completata = false;

    @Override
    public String risolvi(Player player, int pianoId) {
        Objects.requireNonNull(player, "Il giocatore non puo essere nullo.");
        if (completata) return "L'eco del passato si e spento tra i binari.";

        int tiro = random.nextInt(MAX_DICE) + 1;
        boolean successo = (tiro + player.getLivello()) >= SOGLIA_DIFFICOLTA;
        this.completata = true;

        String dialogo = (pianoId == 0) ?
                "IL CAPOSTAZIONE:\n\"Ti ricordi di me? Camminavi sulla linea gialla ignorando i miei richiami, convinto che le regole non valessero per uno splendido uomo d'affari come te. Quando mi sono opposto ai tuoi traffici, mi hai fatto licenziare con false accuse, distruggendo la mia dignità. Ora sono io a terra. Vuoi piegare il tuo orgoglio per aiutarmi o passerai oltre pensando di essere superiore a un vecchio barbone?\"" :
                "LA GENTILE ANZIANA:\n\"Ti fidavi di me, mi portavi i pasticcini a casa per convincermi a firmare la girata della mia pensione. Mi hai lasciato senza un soldo persino per fare la spesa. Ho passato gli ultimi mesi della mia vita a pane e acqua per colpa dei tuoi contratti. Ora c'è un'ultima razza di luce in questa stanza. La prenderai tutta per te o lancerai il dado per nutrirmi?\"";

        if (successo) {
            player.addKarma(BONUS_KARMA);
            return dialogo + "\n\n[SUCCESSO - Lancio: " + tiro + "]\nHai trovato la forza di superare il tuo egoismo. Ottieni +" + BONUS_KARMA + " Karma.";
        }
        return dialogo + "\n\n[FALLIMENTO - Lancio: " + tiro + "]\nL'esitazione cinica ti blocca. La figura svanisce lasciandoti solo con le tue colpe.";
    }

    @Override
    public boolean isCompletata() { return completata; }
}