package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;

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

    // Coordinate NPC/Boss
    private final double npcX;
    private final double npcY;

    // Coordinate Porta
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
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.sfida = sfida;
        this.npcX = npcX;
        this.npcY = npcY;
        this.doorX = doorX;
        this.doorY = doorY;
        this.ricordoSbloccato = ricordoSbloccato;
        this.fragX = fragX;
        this.fragY = fragY;
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

    /** Rimuove il boss dalla stanza (chiamato dopo vittoria) */
    public void solveChallenge() {
        this.sfida = null;
        this.sfidaGestita = true;
    }

    public boolean isSfidaGestita() { return sfidaGestita; }
    public void setSfidaGestita(boolean stato) { this.sfidaGestita = stato; }

    public boolean isKarmaGiaTolto() { return karmaGiaTolto; }
    public void setKarmaGiaTolto(boolean stato) { this.karmaGiaTolto = stato; }

    // Coordinate Boss
    public double npcX() { return npcX; }
    public double npcY() { return npcY; }

    // Coordinate Porta
    public double doorX() { return doorX; }
    public double doorY() { return doorY; }

    // Logica Frammenti
    public boolean hasFragment() { return hasFragment; }
    public double fragX() { return fragX; }
    public double fragY() { return fragY; }
    public String ricordoSbloccato() { return ricordoSbloccato; }
}