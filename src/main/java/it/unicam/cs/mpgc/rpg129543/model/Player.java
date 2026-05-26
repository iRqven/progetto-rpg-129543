package it.unicam.cs.mpgc.rpg129543.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class Player {
    // Campi Identità (Final)
    private final String nome;
    private final String aspetto;
    private final String classe;

    // Statistiche di Gioco
    private int hp;
    private int karma;
    private int livello;
    private final List<String> ricordi;

    // Coordinate per il movimento WASD
    private double x;
    private double y;
    private final double speed = 5.0;

    @JsonCreator
    public Player(
            @JsonProperty("nome") String nome,
            @JsonProperty("aspetto") String aspetto,
            @JsonProperty("classe") String classe) {
        this.nome = nome;
        this.aspetto = aspetto;
        this.classe = classe;
        this.hp = 100;
        this.karma = 0;
        this.livello = 1;
        this.ricordi = new ArrayList<>();
        this.x = 50; // Posizione iniziale X
        this.y = 300; // Posizione iniziale Y
    }

    // METODI GETTER
    public String getNome() { return nome; }
    public int getHp() { return hp; }
    public int getKarma() { return karma; }
    public int getLivello() { return livello; }
    public double getX() { return x; }
    public double getY() { return y; }
    public List<String> getRicordi() { return ricordi; }

    // METODI SETTER
    public void setHp(int hp) { this.hp = hp; }
    public void setKarma(int karma) { this.karma = karma; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    // LOGICA DI MOVIMENTO
    public void moveUp() { y -= speed; }
    public void moveDown() { y += speed; }
    public void moveLeft() { x -= speed; }
    public void moveRight() { x += speed; }

    // LOGICA DI GIOCO
    public void addKarma(int amount) { this.karma += amount; }
    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) this.hp = 0;
    }
    public void addRicordo(String ricordo) {
        if (ricordo != null) this.ricordi.add(ricordo);
    }
}