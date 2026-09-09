package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.model.Enemy;
import it.unicam.cs.mpgc.rpg129543.model.Player;
import it.unicam.cs.mpgc.rpg129543.util.DefaultRandomSource;
import it.unicam.cs.mpgc.rpg129543.util.RandomSource;

import java.util.Objects;

/** Gestisce la logica stocastica degli imprevisti interattivi durante un combattimento. */
public class AnomalieEngine {
    private static final int RANGE_CASUALE = 15;
    private static final int TRIGGER_EVENTO = 0;

    private static final int PATTO_HP_COST = 20;
    private static final int PATTO_WILL_BONUS = 4;
    private static final int AZZARDO_DAMAGE = 50;
    private static final int AZZARDO_BACKLASH = 20;

    private static final AnomalyType[] TIPI = AnomalyType.values();

    private final RandomSource random;
    private boolean imprevistoAttivo = false;
    private AnomalyType tipoCorrente;

    public AnomalieEngine() {
        this(new DefaultRandomSource());
    }

    public AnomalieEngine(RandomSource random) {
        this.random = Objects.requireNonNull(random, "La sorgente casuale non può essere nulla.");
    }

    public boolean controllaInnesco() {
        this.imprevistoAttivo = (random.nextInt(RANGE_CASUALE) == TRIGGER_EVENTO);
        if (imprevistoAttivo) {
            this.tipoCorrente = TIPI[random.nextInt(TIPI.length)];
        }
        return imprevistoAttivo;
    }

    public String getTestoBivio() {
        if (!imprevistoAttivo) return "";
        return switch (tipoCorrente) {
            case PATTO_CON_IGNOTO -> "[PATTO CON L'IGNOTO] Sacrifica la tua stabilita terrena per accumulare energia spirituale.\n" +
                    "[ACCETTA: Perdi " + PATTO_HP_COST + " HP per ottenere +" + PATTO_WILL_BONUS + " Volonta] | [RIFIUTA: Consolida l'Anima]";
            case AZZARDO_DEL_PASSATO -> "[AZZARDO DEL PASSATO] Un'eco instabile sfida la tua sorte.\n" +
                    "[ACCETTA: 50% di infliggere " + AZZARDO_DAMAGE + " danni, 50% di subire " + AZZARDO_BACKLASH + " contraccolpo] | [RIFIUTA: Evita il rischio]";
            case REVERSIONE_SPIRITUALE -> "[REVERSIONE SPIRITUALE] La nebbia stringe un baratto estremo.\n" +
                    "[ACCETTA: Consuma tutta la Volonta per rigenerare la salute al valore massimo] | [RIFIUTA: Conserva le risorse]";
        };
    }

    public AnomalyOutcome applicaSceltaA(Player p, Enemy e, BattleEngine engine) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(e);
        Objects.requireNonNull(engine);
        this.imprevistoAttivo = false;

        return switch (tipoCorrente) {
            case PATTO_CON_IGNOTO -> {
                p.takeDamage(PATTO_HP_COST);
                engine.setVolonta(Math.min(BattleEngine.MAX_WILL, engine.getVolonta() + PATTO_WILL_BONUS));
                yield new AnomalyOutcome("Patto siglato. Sacrificati " + PATTO_HP_COST + " HP per estendere la riserva di Volonta.", true);
            }
            case AZZARDO_DEL_PASSATO -> {
                if (random.nextBoolean()) {
                    e.takeDamage(AZZARDO_DAMAGE);
                    yield new AnomalyOutcome("Sorte favorevole! Lo squarcio della nebbia infligge " + AZZARDO_DAMAGE + " danni alla tua vittima.", false);
                } else {
                    p.takeDamage(AZZARDO_BACKLASH);
                    yield new AnomalyOutcome("Contraccolpo violento dei binari! Subisci " + AZZARDO_BACKLASH + " danni spirituali sulla coscienza.", true);
                }
            }
            case REVERSIONE_SPIRITUALE -> {
                p.setHp(p.getHpMax());
                engine.setVolonta(0);
                yield new AnomalyOutcome("Salute ripristinata al massimo valore dell'Anima. Tutta la tua Volonta e evaporata.", false);
            }
        };
    }

    public boolean isImprevistoAttivo() { return imprevistoAttivo; }
    public void disattiva() { this.imprevistoAttivo = false; }
}