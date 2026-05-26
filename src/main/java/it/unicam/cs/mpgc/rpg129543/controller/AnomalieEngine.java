package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.model.Enemy;
import it.unicam.cs.mpgc.rpg129543.model.Player;
import java.util.Objects;
import java.util.Random;

/**
 * Gestisce la logica stocastica e decisionale degli imprevisti interattivi.
 * Isola gli effetti collaterali e i patti dal ciclo standard di combattimento.
 */
public class AnomalieEngine {
    private static final int SOGLIA_PROBABILITA = 4; // 25% di probabilità
    private static final int PENALITA_VITA_PATTO = 20;
    private static final int BONUS_VOLONTA_PATTO = 4;
    private static final int DANNO_AZZARDO = 50;
    private static final int CONTRACCOLPO_AZZARDO = 20;
    private static final int MASSIMO_HP = 100;

    private final Random rand = new Random();
    private boolean imprevistoAttivo = false;
    private int tipoEventoCorrente = 0;

    /**
     * Determina se un'anomalia interattiva si manifesta nel turno corrente.
     *
     * @return true se il bivio si è attivato, false altrimenti.
     */
    public boolean controllaInnesco() {
        this.imprevistoAttivo = (rand.nextInt(SOGLIA_PROBABILITA) == 0);
        if (imprevistoAttivo) {
            this.tipoEventoCorrente = rand.nextInt(3);
        }
        return imprevistoAttivo;
    }

    /**
     * Restituisce la formulazione testuale del bivio per il log dell'interfaccia.
     */
    public String getTestoBivio() {
        if (!imprevistoAttivo) {
            return "";
        }
        return switch (tipoEventoCorrente) {
            case 0 -> "🔴 DIALOGO CON L'IGNOTO: \"Sacrifica la tua stabilità terrea per ottenere l'energia per combattere...\"\n" +
                    "[ACCETTA: Perdi " + PENALITA_VITA_PATTO + " HP per ottenere +" + BONUS_VOLONTA_PATTO + " Volontà] | [RIFIUTA: Mantieni intatta l'Anima]";
            case 1 -> "🟡 AZZARDO PURGATORIALE: Un'eco instabile del passato sfida la tua sorte.\n" +
                    "[ACCETTA: 50% di infliggere " + DANNO_AZZARDO + " danni, 50% di subire " + CONTRACCOLPO_AZZARDO + " danni] | [RIFIUTA: Non rischiare]";
            default -> "🔵 REVERSIONE ETALICA: La nebbia si stringe per un baratto estremo.\n" +
                    "[ACCETTA: Consuma TUTTA la Volontà attuale per rigenerarti a " + MASSIMO_HP + " HP] | [RIFIUTA: Conserva le risorse]";
        };
    }

    /**
     * Applica gli effetti collaterali positivi o negativi della Scelta A.
     */
    public String applicaSceltaA(Player p, Enemy e, BattleEngine engine) {
        Objects.requireNonNull(p, "Impossibile applicare anomalie su un Player nullo.");
        Objects.requireNonNull(e, "Impossibile applicare anomalie su un Enemy nullo.");
        Objects.requireNonNull(engine, "Impossibile modificare un BattleEngine nullo.");

        this.imprevistoAttivo = false;

        return switch (tipoEventoCorrente) {
            case 0 -> {
                p.takeDamage(PENALITA_VITA_PATTO);
                engine.setVolonta(Math.min(8, engine.getVolonta() + BONUS_VOLONTA_PATTO));
                yield "Patto siglato. Perdi " + PENALITA_VITA_PATTO + " HP ma la tua Volontà si espande.";
            }
            case 1 -> {
                if (rand.nextBoolean()) {
                    e.takeDamage(DANNO_AZZARDO);
                    yield "Sorte favorevole! Lo squarcio infligge " + DANNO_AZZARDO + " danni al Rimorso.";
                } else {
                    p.takeDamage(CONTRACCOLPO_AZZARDO);
                    yield "La fortuna ti volge le spalle! Subisci " + CONTRACCOLPO_AZZARDO + " danni da contraccolpo.";
                }
            }
            default -> {
                p.setHp(MASSIMO_HP);
                engine.setVolonta(0);
                yield "Forma spirituale ripristinata a " + MASSIMO_HP + " HP. La tua Volontà è azzerata.";
            }
        };
    }

    public boolean isImprevistoAttivo() { return imprevistoAttivo; }
    public void disattiva() { this.imprevistoAttivo = false; }
}