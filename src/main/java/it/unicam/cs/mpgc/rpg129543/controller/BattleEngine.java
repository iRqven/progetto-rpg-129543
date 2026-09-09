package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.api.BattleAction;
import it.unicam.cs.mpgc.rpg129543.model.Enemy;
import it.unicam.cs.mpgc.rpg129543.model.GameState;
import it.unicam.cs.mpgc.rpg129543.model.Player;
import it.unicam.cs.mpgc.rpg129543.util.DefaultRandomSource;
import it.unicam.cs.mpgc.rpg129543.util.RandomSource;

import java.util.Objects;

public class BattleEngine {
    public static final int MAX_WILL = 8;
    private static final int COST_VIRTU = 2;
    private static final int COST_CURA = 3;
    private static final int DAMAGE_BASE = 15;
    private static final int HEAL_BASE = 25;
    private static final int BOSS_DAMAGE_BASE = 12;
    private static final int BOSS_DAMAGE_RANDOM_RANGE = 6;
    private static final int BOSS_DAMAGE_SCALING_PER_RUN = 8;

    public enum BossMood { RABBIA, PAURA, COLPA }

    private BossMood currentMood;
    private final BossMood debolezzaBoss;
    private int volonta = 4;
    private boolean staDifendendo = false;
    private int caricaRancore = 0;

    private final AnomalieEngine anomalieEngine;
    private final RandomSource random;

    public BattleEngine(BossMood debolezza) {
        this(debolezza, new AnomalieEngine(), new DefaultRandomSource());
    }

    public BattleEngine(BossMood debolezza, AnomalieEngine anomalieEngine, RandomSource random) {
        this.debolezzaBoss = Objects.requireNonNull(debolezza);
        this.anomalieEngine = Objects.requireNonNull(anomalieEngine, "Il motore delle anomalie non può essere nullo.");
        this.random = Objects.requireNonNull(random, "La sorgente casuale non può essere nulla.");
        this.currentMood = BossMood.values()[this.random.nextInt(BossMood.values().length)];
    }

    /**
     * Esegue un turno di combattimento standard. Le scelte ACCETTA_PATTO/RIFIUTA_PATTO
     * sono gestite direttamente da BattleController tramite AnomalieEngine.applicaSceltaA,
     * quindi questo metodo assume che nessuna anomalia sia attiva.
     */
    public BattleTurnOutcome executeTurn(Player p, Enemy e, BattleAction mossa, GameState gameState) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(e);
        Objects.requireNonNull(mossa);

        StringBuilder sb = new StringBuilder();
        this.staDifendendo = false;

        int attacco = DAMAGE_BASE + (p.getDeterminazione() - 10);
        int cura = HEAL_BASE + (p.getSintonia() - 10) * 2;

        switch (mossa) {
            case PAZIENZA, CORAGGIO, PERDONO -> {
                if (volonta < COST_VIRTU) {
                    return new BattleTurnOutcome("Volontà insufficiente! Usa DIFESA.", false);
                }
                volonta -= COST_VIRTU;

                boolean match = (currentMood == BossMood.RABBIA && mossa == BattleAction.PAZIENZA) ||
                        (currentMood == BossMood.PAURA && mossa == BattleAction.CORAGGIO) ||
                        (currentMood == BossMood.COLPA && mossa == BattleAction.PERDONO);

                double mult = match ? (currentMood == debolezzaBoss ? 3.0 : 1.5) : 0.5;
                int finalDmg = (int) (attacco * mult);
                e.takeDamage(finalDmg);

                if (match) volonta = Math.min(MAX_WILL, volonta + 1);
                else caricaRancore++;

                sb.append("Usato ").append(mossa.name()).append(". Danni: ").append(finalDmg);
            }
            case CURA -> {
                if (volonta < COST_CURA) {
                    return new BattleTurnOutcome("Volontà insufficiente.", false);
                }
                p.setHp(p.getHp() + cura);
                volonta -= COST_CURA;
                sb.append("Curato per ").append(cura).append(" HP.");
            }
            case DIFESA -> {
                this.staDifendendo = true;
                this.volonta = Math.min(MAX_WILL, this.volonta + 3);
                sb.append("Difesa attiva. +3 Volontà.");
            }
            default -> sb.append("Mossa non valida per il turno corrente.");
        }

        if (e.getHp() <= 0) {
            gameState.getCurrentRoom().solveChallenge();
            return new BattleTurnOutcome("VITTORIA! " + e.getNome() + " è stato purificato.", false);
        }

        sb.append("\n\n[").append(e.getNome().toUpperCase()).append("]\n");
        sb.append(e.getFraseTipica(currentMood.name())).append("\n");

        int scalingDanni = (p.getRunCorrente() - 1) * BOSS_DAMAGE_SCALING_PER_RUN;
        int bossDmg = (BOSS_DAMAGE_BASE + scalingDanni + random.nextInt(BOSS_DAMAGE_RANDOM_RANGE)) / (staDifendendo ? 2 : 1);

        p.takeDamage(bossDmg);
        sb.append("Subisci ").append(bossDmg).append(" HP.");

        if (anomalieEngine.controllaInnesco()) {
            sb.append("\n\nLa nebbia pulsa...");
        }

        return new BattleTurnOutcome(sb.toString(), true);
    }

    public int getVolonta() { return volonta; }
    public void setVolonta(int v) { this.volonta = v; }
    public BossMood getCurrentMood() { return currentMood; }
    public AnomalieEngine getAnomalieEngine() { return anomalieEngine; }
}