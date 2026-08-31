package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.model.Enemy;
import it.unicam.cs.mpgc.rpg129543.model.Player;
import java.util.Objects;
import java.util.Random;

/**
 * Gestisce la logica stocastica e casuale degli imprevisti interattivi nel Purgatorio.
 */
public class AnomalieEngine {
    private static final int RANGE_CASUALE = 15;
    private static final int TRIGGER_EVENTO = 0;

    private static final int PATTO_HP_COST = 20;
    private static final int PATTO_WILL_BONUS = 4;
    private static final int AZZARDO_DAMAGE = 50;
    private static final int AZZARDO_BACKLASH = 20;

    private final Random rand = new Random();
    private boolean imprevistoAttivo = false;
    private int eventoId = 0;

    /**
     * Calcola stocasticamente ad ogni turno se l'imprevisto deve manifestarsi o meno.
     */
    public boolean controllaInnesco() {
        // Genera un numero da 0 a 3. L'anomalia si attiva SOLO se esce esattamente 0
        this.imprevistoAttivo = (rand.nextInt(RANGE_CASUALE) == TRIGGER_EVENTO);
        if (imprevistoAttivo) {
            this.eventoId = rand.nextInt(3); // Pesca un imprevisto casuale tra i 3 disponibili
        }
        return imprevistoAttivo;
    }

    public String getTestoBivio() {
        if (!imprevistoAttivo) return "";
        return switch (eventoId) {
            case 0 -> "[PATTO CON L'IGNOTO] Sacrifica la tua stabilita terrena per accumulare energia spirituale.\n" +
                    "[ACCETTA: Perdi " + PATTO_HP_COST + " HP per ottenere +" + PATTO_WILL_BONUS + " Volonta] | [RIFIUTA: Consolida l'Anima]";
            case 1 -> "[AZZARDO DEL PASSATO] Un'eco instabile sfida la tua sorte.\n" +
                    "[ACCETTA: 50% di infliggere " + AZZARDO_DAMAGE + " danni, 50% di subire " + AZZARDO_BACKLASH + " contraccolpo] | [RIFIUTA: Evita il rischio]";
            default -> "[REVERSIONE SPIRITUALE] La nebbia stringe un baratto estremo.\n" +
                    "[ACCETTA: Consuma tutta la Volonta per rigenerare la salute al valore massimo] | [RIFIUTA: Conserva le risorse]";
        };
    }

    public String applicaSceltaA(Player p, Enemy e, BattleEngine engine) {
        Objects.requireNonNull(p); Objects.requireNonNull(e); Objects.requireNonNull(engine);
        this.imprevistoAttivo = false; // Disattiva l'anomalia subito dopo l'esecuzione della scelta

        return switch (eventoId) {
            case 0 -> {
                p.takeDamage(PATTO_HP_COST);
                engine.setVolonta(Math.min(8, engine.getVolonta() + PATTO_WILL_BONUS));
                yield "Patto siglato. Sacrificati " + PATTO_HP_COST + " HP per estendere la riserva di Volonta.";
            }
            case 1 -> {
                if (rand.nextBoolean()) {
                    e.takeDamage(AZZARDO_DAMAGE);
                    yield "Sorte favorevole! Lo squarcio della nebbia infligge " + AZZARDO_DAMAGE + " danni alla tua vittima.";
                } else {
                    p.takeDamage(AZZARDO_BACKLASH);
                    yield "Contraccolpo violento dei binari! Subisci " + AZZARDO_BACKLASH + " danni spirituali sulla coscienza.";
                }
            }
            default -> {
                p.setHp(p.getHpMax());
                engine.setVolonta(0);
                yield "Salute ripristinata al massimo valore dell'Anima. Tutta la tua Volonta e evaporata.";
            }
        };
    }

    public boolean isImprevistoAttivo() { return imprevistoAttivo; }
    public void disattiva() { this.imprevistoAttivo = false; }
}