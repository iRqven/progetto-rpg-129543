package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.model.*;
import java.util.Objects;
import java.util.Random;

/**
 * Controllore logico che governa i turni di combattimento tattico.
 * Integra la gestione delle anomalie interattive e i calcoli delle debolezze.
 */
public class BattleEngine {
    private static final int MAX_VOLONTA = 8;
    private static final int COSTO_VIRTU = 2;
    private static final int COSTO_CURA = 3;
    private static final int DANNO_BASE_GIOCATORE = 15;
    private static final int POTENZA_CURA = 35;
    private static final double MOLTIPLICATORE_SUPEREFFICACE = 3.0;
    private static final double MOLTIPLICATORE_RESISTITO = 0.5;

    public enum BossMood { RABBIA, PAURA, COLPA }

    private BossMood currentMood;
    private final BossMood debolezzaBoss;
    private int volonta;
    private final Random rand;
    private boolean staDifendendo;
    private int caricaRancore;

    // Iniezione del motore delle anomalie (Clean Code)
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
        if (mossa == null || mossa.isBlank()) {
            return "Azione non valida.";
        }

        // --- GESTIONE DEI PATTI E DELLE SCELTE DELL'ANOMALIA ---
        if (anomalieEngine.isImprevistoAttivo()) {
            if (mossa.equals("ACCETTA_PATTO")) {
                String esito = anomalieEngine.applicaSceltaA(p, e, this);
                eseguiTurnoBossAvanzato(p);
                return esito + "\n\n[Turno del Rimorso]\nApprofitta della tua distrazione per contrattaccare!";
            } else if (mossa.equals("RIFIUTA_PATTO")) {
                anomalieEngine.disattiva();
                return "Hai rifiutato il patto della nebbia. Il combattimento riprende regolarmente.";
            }
            return "Risolvi prima l'anomalia spirituale!";
        }

        StringBuilder sb = new StringBuilder();
        double moltiplicatore = 1.0;
        this.staDifendendo = false;

        // --- 1. TURNO DEL GIOCATORE STANDARD ---
        switch (mossa) {
            case "PAZIENZA", "CORAGGIO", "PERDONO" -> {
                if (volonta < COSTO_VIRTU) {
                    return "Non hai abbastanza Volontà! [Usa DIFESA per rigenerarla]";
                }
                volonta -= COSTO_VIRTU;

                boolean virtuCorretta = (currentMood == BossMood.RABBIA && mossa.equals("PAZIENZA")) ||
                        (currentMood == BossMood.PAURA && mossa.equals("CORAGGIO")) ||
                        (currentMood == BossMood.COLPA && mossa.equals("PERDONO"));

                if (virtuCorretta) {
                    if (currentMood == debolezzaBoss) {
                        moltiplicatore = MOLTIPLICATORE_SUPEREFFICACE;
                    }
                    int dannoCalculato = (int) (DANNO_BASE_GIOCATORE * moltiplicatore);
                    e.takeDamage(dannoCalculato);
                    volonta = Math.min(MAX_VOLONTA, volonta + 1);

                    sb.append("Usi ").append(mossa).append("! Infliggi ").append(dannoCalculato).append(" danni.");
                    if (moltiplicatore > 1.0) {
                        sb.append("\nÈ SUPEREFFICACE! Spezzi l'aura e recuperi +1 Volontà.");
                    }
                } else {
                    moltiplicatore = MOLTIPLICATORE_RESISTITO;
                    int dannoCalculato = (int) (DANNO_BASE_GIOCATORE * moltiplicatore);
                    e.takeDamage(dannoCalculato);
                    caricaRancore++;
                    sb.append("Usi ").append(mossa).append(", ma lo spettro resiste. Infliggi ").append(dannoCalculato).append(" danni. Il suo Rancore sale!");
                }
            }
            case "CURA" -> {
                if (volonta < COSTO_CURA) {
                    return "Ti servono " + COSTO_CURA + " punti Volontà per curarti!";
                }
                p.setHp(p.getHp() + POTENZA_CURA);
                volonta -= COSTO_CURA;
                sb.append("Usi un Frammento di Luce purificatrice. Recuperi ").append(POTENZA_CURA).append(" HP!");
            }
            case "DIFESA" -> {
                this.staDifendendo = true;
                this.volonta = Math.min(MAX_VOLONTA, this.volonta + 3);
                sb.append("Ti erigi in guardia spirituale. Rigeneri +3 Volontà.");
            }
            default -> { return "Mossa sconosciuta."; }
        }

        // --- 2. TURNO SINFONICO DEL BOSS ---
        if (e.getHp() > 0) {
            sb.append("\n\n[Turno del Rimorso]");
            eseguiTurnoBossAvanzato(p);

            if (caricaRancore >= 2) {
                int dannoUltimatum = staDifendendo ? 12 : 36;
                sb.append("\nIl nemico sprigiona un'ESPLOSIONE DI RANCORE! Perdi ").append(dannoUltimatum).append(" HP.");
                caricaRancore = 0;
            } else {
                int dannoBossBase = (10 + rand.nextInt(6)) / (staDifendendo ? 2 : 1);
                p.takeDamage(dannoBossBase);
                sb.append("\nIl Rimorso ti lacera usando ").append(currentMood).append(". Perdi ").append(dannoBossBase).append(" HP.");

                if (rand.nextBoolean()) {
                    this.currentMood = BossMood.values()[rand.nextInt(BossMood.values().length)];
                    sb.append("\nL'espressione dello spettro muta barriera cromatica.");
                }
            }

            // Lancio stocastico del dado per il turno successivo (Isaac Dynamic)
            if (anomalieEngine.controllaInnesco()) {
                sb.append("\n\n🌀 ATTENZIONE: La nebbia pulsa... Un imprevisto altererà il prossimo turno!");
            } else {
                String prossimoSuggerimento = switch (this.currentMood) {
                    case RABBIA -> "\n[Ora emana RABBIA: contrastalo con la PAZIENZA!]";
                    case PAURA -> "\n[Ora emana PAURA: contrastalo con il CORAGGIO!]";
                    case COLPA -> "\n[Ora emana COLPA: contrastalo con il PERDONO!]";
                };
                sb.append(prossimoSuggerimento);
            }
        }

        return sb.toString();
    }

    private void eseguiTurnoBossAvanzato(Player p) {
        if (caricaRancore >= 2) {
            int d = staDifendendo ? 12 : 36;
            p.takeDamage(d);
        }
    }

    public int getVolonta() { return volonta; }
    public void setVolonta(int volonta) { this.volonta = volonta; }
    public BossMood getCurrentMood() { return currentMood; }
    public AnomalieEngine getAnomalieEngine() { return anomalieEngine; }
}