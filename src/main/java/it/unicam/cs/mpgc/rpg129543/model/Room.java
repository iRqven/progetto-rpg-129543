package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.util.RandomSource;
import java.util.Objects;

public class Room {
    public static final double INTERACTION_RADIUS_BOSS = 60.0;
    public static final double INTERACTION_RADIUS_FRAG = 40.0;
    public static final double INTERACTION_RADIUS_DOOR = 50.0;

    private final int id;
    private final String nome;
    private final String descrizione;
    private Challenge sfida;
    private boolean sfidaGestita = false;
    private boolean karmaGiaTolto = false;
    private boolean frammentoRaccolto = false;

    private final double npcX;
    private final double npcY;
    private final double fragX;
    private final double fragY;
    private final RoomConfig config;

    public Room(int id, String nome, String descrizione, Challenge sfida, RoomConfig config, RandomSource randomSource) {
        if (id < 0) throw new IllegalArgumentException("L'ID non può essere negativo.");
        this.id = id;
        this.nome = Objects.requireNonNull(nome);
        this.descrizione = Objects.requireNonNull(descrizione);
        this.sfida = sfida;
        this.config = Objects.requireNonNull(config);

        SpawnPositionGenerator spawner = new SpawnPositionGenerator(randomSource);
        double[] primary = spawner.generatePrimaryPosition();
        this.npcX = primary[0];
        this.npcY = primary[1];

        double[] secondary = spawner.generateSecondaryPosition(this.npcX, this.npcY);
        this.fragX = secondary[0];
        this.fragY = secondary[1];

        if (sfida == null) this.sfidaGestita = true;
    }

    public void solveChallenge() {
        if (this.sfidaGestita) return;
        if (sfida != null) sfida.markCompleted();
        this.sfidaGestita = true;
    }

    public int id() { return id; }
    public String nome() { return nome; }
    public String descrizione() { return descrizione; }
    public Challenge sfida() { return sfida; }
    public boolean hasChallenge() { return sfida != null; }
    public boolean isSfidaGestita() { return sfidaGestita; }
    public void setSfidaGestita(boolean stato) { this.sfidaGestita = stato; }
    public boolean isKarmaGiaTolto() { return karmaGiaTolto; }
    public void setKarmaGiaTolto(boolean stato) { this.karmaGiaTolto = stato; }
    public boolean isFrammentoRaccolto() { return frammentoRaccolto; }
    public void setFrammentoRaccolto(boolean stato) { this.frammentoRaccolto = stato; }
    public double npcX() { return npcX; }
    public double npcY() { return npcY; }
    public double fragX() { return fragX; }
    public double fragY() { return fragY; }
    public double doorX() { return config.doorX(); }
    public double doorY() { return config.doorY(); }
    public boolean hasFragment() { return config.hasFragment(); }
    public String ricordoSbloccato() { return config.ricordoSbloccato(); }
    public String backgroundAssetName() { return config.backgroundAssetName(); }
    public String doorAssetName() { return config.doorAssetName(); }
    public String fragmentAssetName() { return config.fragmentAssetName(); }
}