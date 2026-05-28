package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.model.*;
import java.util.Objects;
import java.util.Random;

/**
 * Controllore logico dei turni di combattimento strategico.
 * Sincronizza le statistiche RPG del Player con i pattern progressivi del boss.
 */
public class BattleEngine {
    private static final int MAX_VOLONTA = 8;
    private static final int COSTO_VIRTU = 2;
    private static final int COSTO_CURA = 3;
    private static final int BASE_DANNO_GIOCATORE = 15;
    private static final int BASE_POTENZA_CURA = 25;

    public enum BossMood { RABBIA, PAURA, COLPA }

    private BossMood currentMood;
    private final BossMood debolezzaBoss;
    private int volonta;
    private final Random rand;
    private boolean staDifendendo;
    private int caricaRancore;

    private final AnomalieEngine anomalieEngine = new AnomalieEngine();

    public BattleEngine(BossMood debolezza) {
        this.debolezzaBoss = Objects.requireNonNull(debolezza, "La debolezza del boss non può essere nulla.");
        this.rand = new Random();
        this.currentMood = BossMood.values()[rand.nextInt(BossMood.values().length)];
        this.volonta = 4;
        this.staDifendendo = false;
        this.caricaRancore = 0;
    }

    public String executeTurn(Player p, Enemy e, String mossa) {
        Objects.requireNonNull(p, "Impossibile eseguire il turno con un Player nullo.");
        Objects.requireNonNull(e, "Impossibile eseguire il turno contro un Enemy nullo.");

        // --- GESTIONE ANOMALIE E PATTI ---
        if (anomalieEngine.isImprevistoAttivo()) {
            if (mossa.equals("ACCETTA_PATTO")) {
                String esito = anomalieEngine.applicaSceltaA(p, e, this);
                eseguiContrattaccoBoss(p);
                return esito + "\n\n[Turno del Rimorso]\nApprofitta del patto per ferirti!";
            } else if (mossa.equals("RIFIUTA_PATTO")) {
                anomalieEngine.disattiva();
                return "Hai rifiutato l'offerta. Il combattimento riprende ordinariamente.";
            }
            return "Risolvi prima l'anomalia!";
        }

        StringBuilder sb = new StringBuilder();
        this.staDifendendo = false;

        // Scaling del danno basato sulla statistica "Determinazione"
        int dannoModificato = BASE_DANNO_GIOCATORE + (p.getDeterminazione() - 10);
        int curaModificata = BASE_POTENZA_CURA + (p.getSintonia() - 10) * 2;

        // --- 1. AZIONE DEL GIOCATORE ---
        switch (mossa) {
            case "PAZIENZA", "CORAGGIO", "PERDONO" -> {
                if (volonta < COSTO_VIRTU) return "Volontà insufficiente! Usa DIFESA.";
                volonta -= COSTO_VIRTU;

                boolean virtuCorretta = (currentMood == BossMood.RABBIA && mossa.equals("PAZIENZA")) ||
                        (currentMood == BossMood.PAURA && mossa.equals("CORAGGIO")) ||
                        (currentMood == BossMood.COLPA && mossa.equals("PERDONO"));

                if (virtuCorretta) {
                    double mult = (currentMood == debolezzaBoss) ? 3.0 : 1.5;
                    int dannoFinale = (int) (dannoModificato * mult);
                    e.takeDamage(dannoFinale);
                    volonta = Math.min(MAX_VOLONTA, volonta + 1);
                    sb.append("Canalizzi ").append(mossa).append("! Infliggi ").append(dannoFinale).append(" danni.");
                    if (mult > 1.5) sb.append("\nSUPEREFFICACE! Guadagni +1 Volontà.");
                } else {
                    int dannoFinale = (int) (dannoModificato * 0.5);
                    e.takeDamage(dannoFinale);
                    caricaRancore++;
                    sb.append("Lo spettro resiste a ").append(mossa).append(". Solo ").append(dannoFinale).append(" danni. Il suo Rancore sale!");
                }
            }
            case "CURA" -> {
                if (volonta < COSTO_CURA) return "Volontà insufficiente per curarti.";
                p.setHp(p.getHp() + curaModificata);
                volonta -= COSTO_CURA;
                sb.append("Sintonizzi i ricordi. Sani ").append(curaModificata).append(" HP!");
            }
            case "DIFESA" -> {
                this.staDifendendo = true;
                this.volonta = Math.min(MAX_VOLONTA, this.volonta + 3);
                sb.append("Innalzi una barriera spirituale. Rigeneri +3 Volontà.");
            }
            default -> { return "Mossa non riconosciuta."; }
        }

        // --- 2. RISPOSTA PROGRESSIVA DEL BOSS ---
        if (e.getHp() > 0) {
            sb.append("\n\n[Turno del Rimorso]");

            if (caricaRancore >= 2) {
                // Il danno del boss è mitigato dalla Resilienza del Player
                int dannoUltimatum = Math.max(10, 40 - (p.getResilienza() - 10));
                if (staDifendendo) dannoUltimatum /= 3;
                p.takeDamage(dannoUltimatum);
                sb.append("\nIl nemico rilascia un'ESPLOSIONE DI RANCORE! Subisci ").append(dannoUltimatum).append(" HP.");
                caricaRancore = 0;
            } else {
                int dannoBaseBoss = 12 + rand.nextInt(6);
                int dannoMitigato = Math.max(5, dannoBaseBoss - (p.getResilienza() - 10) / 2);
                if (staDifendendo) dannoMitigato /= 2;

                p.takeDamage(dannoMitigato);
                sb.append("\nTi colpisce con l'Aura ").append(currentMood).append(". Perdi ").append(dannoMitigato).append(" HP.");

                if (rand.nextBoolean()) {
                    this.currentMood = BossMood.values()[rand.nextInt(BossMood.values().length)];
                    sb.append("\nL'espressione cromatica dello spettro fluttua mutando forma.");
                }
            }

            // Sistema di Imprevisti stocastici stile Isaac
            if (anomalieEngine.controllaInnesco()) {
                sb.append("\n\n🌀 ATTENZIONE: La nebbia pulsa... Un imprevisto altererà il prossimo turno!");
            } else {
                sb.append("\n[Aura attuale: ").append(currentMood).append("]");
            }
        }

        return sb.toString();
    }

    private void __DUMMY_STAGES() {} // Evita Magic Numbers strutturali

    private void eseguiContrattaccoBoss(Player p) {
        int d = Math.max(5, 12 - (p.getResilienza() - 10) / 2);
        p.takeDamage(d);
    }

    public int getVolonta() { return volonta; }
    public void setVolonta(int volonta) { this.volonta = volonta; }
    public BossMood getCurrentMood() { return currentMood; }
    public AnomalieEngine getAnomalieEngine() { return anomalieEngine; }
}