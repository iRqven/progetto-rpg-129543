package it.unicam.cs.mpgc.rpg129543.model;

import java.util.Objects;

public class Enemy {
    private static final int MIN_HP = 0;
    private final String nome;
    private int hp;
    private final int hpMax;
    private final String debolezza;
    private final String fraseRabbia;
    private final String frasePaura;
    private final String fraseColpa;

    public Enemy(String nome, int hpMax, String debolezza, String fraseRabbia, String frasePaura, String fraseColpa) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Il nome non può essere vuoto.");
        if (debolezza == null || debolezza.isBlank()) throw new IllegalArgumentException("La debolezza non può essere vuota.");

        this.nome = nome;
        this.hpMax = hpMax;
        this.hp = hpMax;
        this.debolezza = debolezza;
        this.fraseRabbia = Objects.requireNonNull(fraseRabbia);
        this.frasePaura = Objects.requireNonNull(frasePaura);
        this.fraseColpa = Objects.requireNonNull(fraseColpa);
    }

    public String getFraseTipica(String auraAttuale) {
        return switch (auraAttuale) {
            case "RABBIA" -> "\"" + nome + " urla: " + fraseRabbia + "\"";
            case "PAURA" -> "\"" + nome + " sussurra: " + frasePaura + "\"";
            default -> "\"" + nome + " piange: " + fraseColpa + "\"";
        };
    }

    public void takeDamage(int damage) {
        if (damage < 0) throw new IllegalArgumentException("Danno negativo.");
        this.hp = Math.max(MIN_HP, this.hp - damage);
    }

    public String getNome() { return nome; }
    public int getHp() { return hp; }
    public int getHpMax() { return hpMax; }
    public String getDebolezza() { return debolezza; }
    public void setHp(int hp) { this.hp = Math.max(MIN_HP, hp); }
}