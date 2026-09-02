package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.Objects;
import java.util.Random;

/**
 * Rappresenta una stanza del Purgatorio.
 * Contiene la logica spaziale (con spawn casuale dinamico controllato), la sfida e i frammenti.
 */
public class Room {
    private final int id;
    private final String nome;
    private final String descrizione;

    private Challenge sfida;
    private boolean sfidaGestita = false;
    private boolean karmaGiaTolto = false;

    private final double npcX;
    private final double npcY;

    private final double doorX;
    private final double doorY;

    private final String ricordoSbloccato;
    private final double fragX;
    private final double fragY;
    private final boolean hasFragment;

    private final String backgroundAssetName;
    private final String doorAssetName;
    private final String fragmentAssetName;

    // Costanti per l'area di spawn sicura (Schermo 800x600)
    private static final double MIN_SPAWN_X = 200.0;
    private static final double MAX_SPAWN_X = 600.0;
    private static final double MIN_SPAWN_Y = 100.0;
    private static final double MAX_SPAWN_Y = 480.0;

    // Distanza minima per impedire la sovrapposizione tra Boss e Frammento
    private static final double MIN_DISTANZA_ENTITA = 130.0;

    private static final Random RANDOM = new Random();

    public static final double INTERACTION_RADIUS_BOSS = 60.0;
    public static final double INTERACTION_RADIUS_FRAG = 40.0;
    public static final double INTERACTION_RADIUS_DOOR = 50.0;

    public Room(int id, String nome, String descrizione, Challenge sfida,
                double doorX, double doorY,
                String ricordoSbloccato, boolean hasFragment,
                String backgroundAssetName, String doorAssetName, String fragmentAssetName) {

        if (id < 0) throw new IllegalArgumentException("L'ID della stanza non può essere negativo.");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Il nome non può essere vuoto.");
        if (descrizione == null || descrizione.isBlank()) throw new IllegalArgumentException("La descrizione non può essere vuota.");

        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.sfida = sfida;

        this.doorX = doorX;
        this.doorY = doorY;

        this.ricordoSbloccato = ricordoSbloccato;
        this.hasFragment = hasFragment;

        this.backgroundAssetName = backgroundAssetName;
        this.doorAssetName = doorAssetName;
        this.fragmentAssetName = fragmentAssetName;

        // 1. Genera la posizione del Boss
        this.npcX = calcolaInRange(MIN_SPAWN_X, MAX_SPAWN_X);
        this.npcY = calcolaInRange(MIN_SPAWN_Y, MAX_SPAWN_Y);

        // 2. Genera la posizione del Frammento ed evita che collida col Boss
        double tempFragX;
        double tempFragY;
        do {
            tempFragX = calcolaInRange(MIN_SPAWN_X, MAX_SPAWN_X);
            tempFragY = calcolaInRange(MIN_SPAWN_Y, MAX_SPAWN_Y);
        } while (calcolaDistanza(this.npcX, this.npcY, tempFragX, tempFragY) < MIN_DISTANZA_ENTITA);

        this.fragX = tempFragX;
        this.fragY = tempFragY;

        if (sfida == null) {
            this.sfidaGestita = true;
        }
    }

    private double calcolaInRange(double min, double max) {
        return min + (RANDOM.nextDouble() * (max - min));
    }

    // Metodo di utility per calcolare lo spazio tra due punti
    private double calcolaDistanza(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

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

    public double npcX() { return npcX; }
    public double npcY() { return npcY; }

    public double doorX() { return doorX; }
    public double doorY() { return doorY; }

    public boolean hasFragment() { return hasFragment; }
    public double fragX() { return fragX; }
    public double fragY() { return fragY; }
    public String ricordoSbloccato() { return ricordoSbloccato; }

    public String backgroundAssetName() { return backgroundAssetName; }
    public String doorAssetName() { return doorAssetName; }
    public String fragmentAssetName() { return fragmentAssetName; }
}