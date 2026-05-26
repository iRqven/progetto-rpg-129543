package it.unicam.cs.mpgc.rpg129543.model;

public class Enemy {
    private String nome;
    private int hp;
    private String debolezza;

    public Enemy(String nome, int hp, String debolezza) {
        this.nome = nome;
        this.hp = hp;
        this.debolezza = debolezza;
    }

    public String getNome() { return nome; }
    public int getHp() { return hp; }
    public void takeDamage(int damage) { this.hp -= damage; }
    public String getDebolezza() { return debolezza; }
}