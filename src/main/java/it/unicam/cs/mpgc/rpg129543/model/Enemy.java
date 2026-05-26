package it.unicam.cs.mpgc.rpg129543.model;

import java.util.Objects;

/**
 * Rappresenta un'entità ostile (Rimorso o Spettro) all'interno del dominio di gioco.
 * Gestisce il proprio stato vitale e definisce la propria vulnerabilità emotiva.
 */
public class Enemy {
    // Costante per evitare Magic Numbers (Punto 5)
    private static final int MIN_HP = 0;

    private final String nome;
    private int hp;
    private final String debolezza;

    /**
     * Costruisce un nemico impostando le sue statistiche vitali e la sua debolezza.
     */
    public Enemy(String nome, int hp, String debolezza) {
        // Clausole di Guardia per la Programmazione Difensiva (Punto 4)
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome del nemico non può essere nullo o vuoto.");
        }
        if (debolezza == null || debolezza.isBlank()) {
            throw new IllegalArgumentException("La debolezza del nemico non può essere nulla o vuota.");
        }
        if (hp <= 0) {
            throw new IllegalArgumentException("I punti vita iniziali del nemico devono essere superiori a zero.");
        }

        this.nome = nome;
        this.hp = hp;
        this.debolezza = debolezza;
    }

    // --- METODI GETTER ---
    public String getNome() { return nome; }
    public int getHp() { return hp; }
    public String getDebolezza() { return debolezza; }

    /**
     * Riduce la salute del nemico in base al danno ricevuto.
     * Impedisce che gli HP assumano valori negativi inconsistenti (Incapsulamento Protetto).
     *
     * @param damage Il quantitativo di danno da infliggere.
     */
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Il danno inflitto non può essere negativo.");
        }
        this.hp -= damage;

        // Clausola di protezione per non scendere sotto lo zero
        if (this.hp < MIN_HP) {
            this.hp = MIN_HP;
        }
    }

    /**
     * Permette di impostare o resettare la salute del nemico (utile per riutilizzare l'entità).
     */
    public void setHp(int hp) {
        this.hp = Math.max(MIN_HP, hp);
    }
}