package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.model.Player;
import it.unicam.cs.mpgc.rpg129543.model.Enemy;
import java.util.Random;

public class BattleEngine {
    private int volonta = 5;
    private final Random rand = new Random();

    public String executeTurn(Player p, Enemy e, String azione) {
        StringBuilder sb = new StringBuilder();
        if (azione.equals("ATTACCO_VIRTU") && volonta >= 3) {
            int d = 15 + rand.nextInt(10);
            e.takeDamage(d);
            volonta -= 3;
            sb.append("Colpisci il ricordo con la tua virtù! (").append(d).append(" danni). ");
        } else if (azione.equals("ANALISI") && volonta >= 1) {
            volonta -= 1;
            sb.append("Il ricordo trema... la sua debolezza è ").append(e.getDebolezza()).append(". ");
        } else if (azione.equals("PREGHIERA")) {
            volonta += 2;
            p.setHp(Math.min(100, p.getHp() + 10));
            sb.append("Ti fermi a pregare. Recuperi energie e Volontà. ");
        } else if (volonta < 3) {
            sb.append("Sei troppo stanco per questa mossa! ");
        }

        if (e.getHp() > 0) {
            int ed = 5 + rand.nextInt(10);
            p.takeDamage(ed);
            sb.append("\nIl ricordo reagisce! Subisci ").append(ed).append(" danni.");
        }
        return sb.toString();
    }

    public int getVolonta() { return volonta; }
}