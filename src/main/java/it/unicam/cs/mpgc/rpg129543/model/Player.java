package it.unicam.cs.mpgc.rpg129543.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Rappresenta l'entità principale del giocatore all'interno del dominio del gioco.
 * Gestisce lo stato vitale, il posizionamento spaziale e i progressi morali (Karma).
 */
public class Player {
    // Costanti di bilanciamento per evitare Magic Numbers
    private static final int MAX_HP = 100;
    private static final int MIN_HP = 0;
    private static final double DEFAULT_INITIAL_X = 50.0;
    private static final double DEFAULT_INITIAL_Y = 300.0;
    private static final double DEFAULT_SPEED = 5.0;

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

    /**
     * Costruttore principale utilizzato per l'inizializzazione del personaggio
     * e per la deserializzazione dei dati tramite la libreria Jackson.
     */
    @JsonCreator
    public Player(
            @JsonProperty("nome") String nome,
            @JsonProperty("aspetto") String aspetto,
            @JsonProperty("classe") String classe) {

        // CORREZIONE: Uso di isBlank() al posto di blank() (Programmazione Difensiva)
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome del giocatore non può essere nullo o composto da soli spazi.");
        }
        if (aspetto == null || aspetto.isBlank()) {
            throw new IllegalArgumentException("L'aspetto del giocatore non può essere nullo o vuoto.");
        }
        if (classe == null || classe.isBlank()) {
            throw new IllegalArgumentException("La classe del giocatore non può essere nulla o vuota.");
        }

        this.nome = nome;
        this.aspetto = aspetto;
        this.classe = classe;
        this.hp = MAX_HP;
        this.karma = 0;
        this.livello = 1;
        this.ricordi = new ArrayList<>();
        this.x = DEFAULT_INITIAL_X;
        this.y = DEFAULT_INITIAL_Y;
    }

    // METODI GETTER (Invariati)
    public String getNome() { return nome; }
    public String getAspetto() { return aspetto; }
    public String getClasse() { return classe; }
    public int getHp() { return hp; }
    public int getKarma() { return karma; }
    public int getLivello() { return livello; }
    public double getX() { return x; }
    public double getY() { return y; }
    public List<String> getRicordi() { return ricordi; }

    // METODI SETTER CON INCAPSULAMENTO PROTETTO
    public void setHp(int hp) {
        // Uso di Math.clamp per impedire che la vita assuma valori inconsistenti
        this.hp = Math.clamp(hp, MIN_HP, MAX_HP);
    }

    public void setKarma(int karma) { this.karma = karma; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setLivello(int livello) { this.livello = livello; }

    // LOGICA DI MOVIMENTO (Uso delle costanti)
    public void moveUp() { y -= DEFAULT_SPEED; }
    public void moveDown() { y += DEFAULT_SPEED; }
    public void moveLeft() { x -= DEFAULT_SPEED; }
    public void moveRight() { x += DEFAULT_SPEED; }

    // LOGICA DI GIOCO REFACTORIZZATA
    public void addKarma(int amount) { this.karma += amount; }

    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Il danno inflitto non può essere negativo.");
        }
        this.setHp(this.hp - damage);
    }

    public void addRicordo(String ricordo) {
        if (ricordo == null || ricordo.isBlank()) {
            throw new IllegalArgumentException("Non è possibile assimilare un ricordo nullo o vuoto.");
        }
        this.ricordi.add(ricordo);
    }

    // IL CONTRATTO FONDAMENTALE: equals() e hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        // Il giocatore è identificato univocamente dal suo nome all'interno del contesto di gioco
        return Objects.equals(nome, player.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}