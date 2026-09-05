package it.unicam.cs.mpgc.rpg129543.util;

/**
 * Costanti di bilanciamento condivise tra più classi (controller/model),
 * per evitare valori "magici" duplicati in punti diversi del codice
 * (es. il valore 60 usato in almeno 5 punti di GameRouter/BattleController
 * per spostare il giocatore dopo un'interazione).
 */
public final class GameplayConstants {

    private GameplayConstants() {
    }

    /** Passo con cui il giocatore viene spostato dopo aver concluso un'interazione. */
    public static final double POST_INTERACTION_STEP = 60.0;

    /** Bonus HP quando si ritrova un ricordo già sbloccato in una run precedente. */
    public static final int MEMORY_RESONANCE_HP_BONUS = 30;

    /** Bonus Karma quando si ritrova un ricordo già sbloccato in una run precedente. */
    public static final int MEMORY_RESONANCE_KARMA_BONUS = 10;

    /** Moltiplicatore usato per calcolare l'XP guadagnata sconfiggendo il boss di un piano. */
    public static final int XP_PER_ROOM_MULTIPLIER = 50;

    /** Karma guadagnato/perso scegliendo di affrontare o ignorare uno scontro. */
    public static final int KARMA_PENALTY_IGNORE_CHALLENGE = 15;

    /** Karma guadagnato sconfiggendo un boss in combattimento. */
    public static final int KARMA_REWARD_VICTORY = 30;

    /** Soglia di Karma sopra la quale si ottiene il finale "buono" (Oblio). */
    public static final int KARMA_THRESHOLD_GOOD_ENDING = 40;

    /** HP massimi base di un boss, prima dello scaling per run successive. */
    public static final int BOSS_HP_BASE = 100;

    /** HP aggiuntivi per ogni Run successiva alla prima. */
    public static final int BOSS_HP_GROWTH_PER_RUN = 50;
}