package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.model.*;
import java.util.Random;

public class BattleEngine {
    public enum BossMood { RABBIA, PAURA, COLPA }
    private BossMood currentMood;
    private BossMood debolezzaBoss;
    private int volonta = 5;
    private final Random rand = new Random();
    private boolean staDifendendo = false;

    public BattleEngine(BossMood debolezza) {
        this.debolezzaBoss = debolezza;
        this.currentMood = BossMood.values()[rand.nextInt(3)];
    }

    public String executeTurn(Player p, Enemy e, String mossa) {
        StringBuilder sb = new StringBuilder();
        double moltiplicatore = 1.0;
        staDifendendo = false;

        // --- TURNO DEL GIOCATORE ---
        switch (mossa) {
            case "ATTACCO" -> {
                if (volonta <= 0) return "Non hai abbastanza Volontà!";
                if ((currentMood == BossMood.RABBIA && debolezzaBoss == BossMood.RABBIA)) moltiplicatore = 2.0;

                int danno = (int)(15 * moltiplicatore);
                e.takeDamage(danno);
                volonta--;
                sb.append("Usi una Virtù! Infliggi ").append(danno).append(" danni.");
                if (moltiplicatore > 1) sb.append("\nÈ SUPEREFFICACE!");
            }
            case "CURA" -> {
                if (volonta < 2) return "Ti servono 2 punti Volontà per curarti!";
                int cura = 30;
                p.setHp(Math.min(100, p.getHp() + cura));
                volonta -= 2;
                sb.append("Usi un Frammento di Luce. Recuperi ").append(cura).append(" HP!");
            }
            case "DIFESA" -> {
                staDifendendo = true;
                volonta += 1; // Difendersi rigenera un po' di Volontà
                sb.append("Ti metti in guardia. Il prossimo attacco sarà dimezzato.");
            }
        }

        // --- TURNO DEL BOSS ---
        if (e.getHp() > 0) {
            int dannoBossBase = 12 + rand.nextInt(8);
            if (staDifendendo) dannoBossBase /= 2;

            p.takeDamage(dannoBossBase);
            sb.append("\nIl Rimorso usa ").append(currentMood).append("! Perdi ").append(dannoBossBase).append(" HP.");

            currentMood = BossMood.values()[rand.nextInt(3)];
        } else {
            // Premio vittoria (Drop di vita)
            p.setHp(Math.min(100, p.getHp() + 20));
            sb.append("\nBoss sconfitto! Recuperi 20 HP dalla purificazione.");
        }

        return sb.toString();
    }

    public int getVolonta() { return volonta; }
    public BossMood getCurrentMood() { return currentMood; }
}