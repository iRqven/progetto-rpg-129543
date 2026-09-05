package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.api.BattleAction;
import it.unicam.cs.mpgc.rpg129543.model.*;
import java.util.Objects;
import java.util.Random;

public class BattleEngine {
    private static final int MAX_WILL = 8;
    private static final int COST_VIRTU = 2;
    private static final int COST_CURA = 3;
    private static final int DAMAGE_BASE = 15;
    private static final int HEAL_BASE = 25;

    public enum BossMood { RABBIA, PAURA, COLPA }

    private BossMood currentMood;
    private final BossMood debolezzaBoss;
    private int volonta = 4;
    private boolean staDifendendo = false;
    private int caricaRancore = 0;

    private final AnomalieEngine anomalieEngine = new AnomalieEngine();
    private final Random rand = new Random();

    public BattleEngine(BossMood debolezza) {
        this.debolezzaBoss = Objects.requireNonNull(debolezza);
        this.currentMood = BossMood.values()[rand.nextInt(BossMood.values().length)];
    }

    public String executeTurn(Player p, Enemy e, BattleAction mossa, GameState gameState) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(e);
        Objects.requireNonNull(mossa);

        if (anomalieEngine.isImprevistoAttivo()) {
            return gestisciAnomalia(p, e, mossa);
        }

        StringBuilder sb = new StringBuilder();
        this.staDifendendo = false;

        int attacco = DAMAGE_BASE + (p.getDeterminazione() - 10);
        int cura = HEAL_BASE + (p.getSintonia() - 10) * 2;

        switch (mossa) {
            case PAZIENZA:
            case CORAGGIO:
            case PERDONO:
                if (volonta < COST_VIRTU) return "Volontà insufficiente! Usa DIFESA.";
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
                break;

            case CURA:
                if (volonta < COST_CURA) return "Volontà insufficiente.";
                p.setHp(p.getHp() + cura); volonta -= COST_CURA;
                sb.append("Curato per ").append(cura).append(" HP.");
                break;

            case DIFESA:
                this.staDifendendo = true;
                this.volonta = Math.min(MAX_WILL, this.volonta + 3);
                sb.append("Difesa attiva. +3 Volontà.");
                break;

            default:
                sb.append("Mossa non valida per il turno corrente.");
                break;
        }

        if (e.getHp() <= 0) {
            gameState.getCurrentRoom().solveChallenge();
            return "VITTORIA! " + e.getNome() + " è stato purificato.";
        }

        sb.append("\n\n[").append(e.getNome().toUpperCase()).append("]\n");
        sb.append(e.getFraseTipica(currentMood.name())).append("\n");

        // SCALING DANNI: I boss infliggono +8 danni base per ogni run
        int scalingDanni = (p.getRunCorrente() - 1) * 8;
        int bossDmg = (12 + scalingDanni + rand.nextInt(6)) / (staDifendendo ? 2 : 1);

        p.takeDamage(bossDmg);
        sb.append("Subisci ").append(bossDmg).append(" HP.");

        if (anomalieEngine.controllaInnesco()) sb.append("\n\nLa nebbia pulsa...");

        return sb.toString();
    }

    private String gestisciAnomalia(Player p, Enemy e, BattleAction mossa) {
        if (mossa == BattleAction.ACCETTA_PATTO) {
            String res = anomalieEngine.applicaSceltaA(p, e, this);
            p.takeDamage(10);
            return res;
        }
        anomalieEngine.disattiva();
        return "Patto rifiutato.";
    }

    public int getVolonta() { return volonta; }
    public void setVolonta(int v) { this.volonta = v; }
    public BossMood getCurrentMood() { return currentMood; }
    public AnomalieEngine getAnomalieEngine() { return anomalieEngine; }
}