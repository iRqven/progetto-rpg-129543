package it.unicam.cs.mpgc.rpg129543.model;

import java.util.Objects;

/**
 * Rappresenta un'entità ostile (Vittima o Spettro) all'interno del dominio di gioco.
 * Gestisce il proprio stato vitale e incapsula le proprie linee di dialogo personalizzate.
 */
public class Enemy {
    private static final int MIN_HP = 0;

    private final String nome;
    private int hp;
    private final String debolezza;

    // Linee di dialogo personalizzate per ciascuno stato emotivo del boss
    private final String fraseRabbia;
    private final String frasePaura;
    private final String fraseColpa;

    /**
     * Costruisce un nemico configurando le sue statistiche vitali, vulnerabilità e dialoghi specifici.
     */
    public Enemy(String nome, int hp, String debolezza, String fraseRabbia, String frasePaura, String fraseColpa) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome del nemico non può essere nullo o vuoto.");
        }
        if (debolezza == null || debolezza.isBlank()) {
            throw new IllegalArgumentException("La debolezza del nemico non può essere nulla o vuota.");
        }

        this.nome = nome;
        this.hp = hp;
        this.debolezza = debolezza;
        this.fraseRabbia = Objects.requireNonNull(fraseRabbia, "La frase di rabbia non può essere nulla.");
        this.frasePaura = Objects.requireNonNull(frasePaura, "La frase di paura non può essere nulla.");
        this.fraseColpa = Objects.requireNonNull(fraseColpa, "La frase di colpa non può essere nulla.");
    }

    /**
     * Restituisce la linea di dialogo specifica e personalizzata del boss in base all'aura corrente dello scontro.
     *
     * @param auraAttuale Lo stato emotivo corrente del turno (RABBIA, PAURA, COLPA).
     * @return La frase d'accusa formattata.
     */
    public String getFraseTipica(String auraAttuale) {
        return switch (auraAttuale) {
            case "RABBIA" -> "\"" + nome + " urla: " + fraseRabbia + "\"";
            case "PAURA" -> "\"" + nome + " sussurra: " + frasePaura + "\"";
            default -> "\"" + nome + " piange: " + fraseColpa + "\"";
        };
    }

    /**
     * Riduce la salute del nemico in base al danno ricevuto.
     * Impedisce che gli HP assumano valori negativi inconsistenti.
     *
     * @param damage Il quantitativo di danno da infliggere.
     */
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Il danno inflitto non può essere negativo.");
        }
        this.hp -= damage;
        if (this.hp < MIN_HP) {
            this.hp = MIN_HP;
        }
    }

    // --- GETTER E SETTER INCAPSULATI ---
    public String getNome() { return nome; }
    public int getHp() { return hp; }
    public String getDebolezza() { return debolezza; }
    public void setHp(int hp) { this.hp = Math.max(MIN_HP, hp); }
}