package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.Objects;

/**
 * Rappresenta una stanza del Purgatorio.
 * Contiene la logica spaziale (coordinate), la sfida (boss) e i frammenti di memoria.
 */
public class Room {
    private final int id;
    private final String nome;
    private final String descrizione;

    // Stato della sfida
    private Challenge sfida;
    private boolean sfidaGestita = false;
    private boolean karmaGiaTolto = false;

    // Coordinate NPC/Boss (fisse per stanza)
    private final double npcX;
    private final double npcY;

    // Coordinate Porta (fisse per stanza)
    private final double doorX;
    private final double doorY;

    // Frammenti di Memoria (Storytelling)
    private final String ricordoSbloccato;
    private final double fragX;
    private final double fragY;
    private final boolean hasFragment;

    public Room(int id, String nome, String descrizione, Challenge sfida,
                double npcX, double npcY, double doorX, double doorY,
                String ricordoSbloccato, double fragX, double fragY, boolean hasFragment) {

        // Clausole di guardia per la programmazione difensiva
        if (id < 0) {
            throw new IllegalArgumentException("L'ID della stanza non può essere negativo.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome della stanza non può essere nullo o vuoto.");
        }
        if (descrizione == null || descrizione.isBlank()) {
            throw new IllegalArgumentException("La descrizione della stanza non può essere nulla o vuota.");
        }

        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.sfida = sfida;

        // Assegniamo coordinate fisse calcolate in base all'id (così variano da stanza a stanza ma restano fisse nella stessa stanza)
        this.npcX = 520 + (id * 15) % 80;
        this.npcY = 250 + (id * 25) % 100;

        this.doorX = doorX;
        this.doorY = doorY;

        this.ricordoSbloccato = ricordoSbloccato;
        this.fragX = 300 + (id * 35) % 150;
        this.fragY = 400 + (id * 20) % 80;
        this.hasFragment = hasFragment;

        // Se non c'è una sfida, la stanza è considerata già "gestita" (es. Atrio)
        if (sfida == null) {
            this.sfidaGestita = true;
        }
    }

    // --- GETTER E SETTER LOGICI ---

    public int id() { return id; }
    public String nome() { return nome; }
    public String descrizione() { return descrizione; }

    public Challenge sfida() { return sfida; }
    public boolean hasChallenge() { return sfida != null; }

    public void solveChallenge() {
        if (this.sfidaGestita) return;

        if (sfida instanceof CombatChallenge) {
            ((CombatChallenge) sfida).setCompletata(true);
        }
        this.sfidaGestita = true;
    }

    public boolean isSfidaGestita() { return sfidaGestita; }
    public void setSfidaGestita(boolean stato) { this.sfidaGestita = stato; }

    public boolean isKarmaGiaTolto() { return karmaGiaTolto; }
    public void setKarmaGiaTolto(boolean stato) { this.karmaGiaTolto = stato; }

    // Restituiscono i valori fissi calcolati nel costruttore
    public double npcX() { return npcX; }
    public double npcY() { return npcY; }

    public double doorX() { return doorX; }
    public double doorY() { return doorY; }

    public boolean hasFragment() { return hasFragment; }
    public double fragX() { return fragX; }
    public double fragY() { return fragY; }
    public String ricordoSbloccato() { return ricordoSbloccato; }
}