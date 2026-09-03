package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.Objects;
import java.util.Random;

public class Room {
    private final int id;
    private final String nome;
    private final String descrizione;

    private Challenge sfida;
    private boolean sfidaGestita = false;
    private boolean karmaGiaTolto = false;

    private final double npcX;
    private final double npcY;

    // L'oggetto che raggruppa tutti i dati accessori (Data Clumps risolto)
    private final RoomConfig config;

    private final double fragX;
    private final double fragY;

    private static final double MIN_SPAWN_X = 200.0;
    private static final double MAX_SPAWN_X = 600.0;
    private static final double MIN_SPAWN_Y = 100.0;
    private static final double MAX_SPAWN_Y = 480.0;
    private static final double MIN_DISTANZA_ENTITA = 130.0;

    private static final Random RANDOM = new Random();

    public static final double INTERACTION_RADIUS_BOSS = 60.0;
    public static final double INTERACTION_RADIUS_FRAG = 40.0;
    public static final double INTERACTION_RADIUS_DOOR = 50.0;

    public Room(int id, String nome, String descrizione, Challenge sfida, RoomConfig config) {
        if (id < 0) throw new IllegalArgumentException("L'ID della stanza non può essere negativo.");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Il nome non può essere vuoto.");
        if (descrizione == null || descrizione.isBlank()) throw new IllegalArgumentException("La descrizione non può essere vuota.");

        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.sfida = sfida;
        this.config = Objects.requireNonNull(config, "La configurazione della stanza non può essere nulla.");

        this.npcX = calcolaInRange(MIN_SPAWN_X, MAX_SPAWN_X);
        this.npcY = calcolaInRange(MIN_SPAWN_Y, MAX_SPAWN_Y);

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

    // Delegazione dei getter all'oggetto di configurazione
    public double doorX() { return config.doorX(); }
    public double doorY() { return config.doorY(); }
    public boolean hasFragment() { return config.hasFragment(); }
    public double fragX() { return fragX; }
    public double fragY() { return fragY; }
    public String ricordoSbloccato() { return config.ricordoSbloccato(); }
    public String backgroundAssetName() { return config.backgroundAssetName(); }
    public String doorAssetName() { return config.doorAssetName(); }
    public String fragmentAssetName() { return config.fragmentAssetName(); }
}