package it.unicam.cs.mpgc.rpg129543.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Rappresenta il protagonista etereo nel Purgatorio.
 * Incapsula lo stato vitale, i ricordi sbloccati, il karma, le coordinate spaziali e le statistiche RPG.
 */
public class Player {
    private final String nome;
    private final String classe;
    private final String allineamento;

    private int hp;
    private int hpMax = 100;
    private int karma;
    private int livello = 1;
    private int xp = 0;
    private int xpNecessari = 100;

    private double x = 100.0;
    private double y = 300.0;
    private static final double VELOCITA_MOVIMENTO = 5.0;

    private int determinazione = 10;
    private int resilienza = 10;
    private int sintonia = 10;

    private final List<String> ricordi = new ArrayList<>();

    public Player(String nome, String classe, String allineamento) {
        this.nome = Objects.requireNonNull(nome, "Il nome non può essere nullo.");
        this.classe = classe;
        this.allineamento = allineamento;
    }

    /**
     * Incrementa i punti esperienza dell'entità.
     * * @param quantita XP ottenuti.
     * @return true se l'incremento ha causato un passaggio di livello, false altrimenti.
     */
    public boolean addXp(int quantita) {
        if (quantita < 0) return false;
        this.xp += quantita;
        boolean haLivellato = false;

        while (this.xp >= xpNecessari) {
            this.xp -= xpNecessari;
            this.livello++;
            this.xpNecessari = (int) (this.xpNecessari * 1.5);

            // Auto-scaling bilanciato delle proprietà core
            this.determinazione += 2;
            this.resilienza += 2;
            this.sintonia += 2;
            this.hpMax = 100 + (this.resilienza - 10) * 5;
            this.hp = this.hpMax;
            haLivellato = true;
        }
        return haLivellato;
    }

    public void moveUp() { this.y -= VELOCITA_MOVIMENTO; }
    public void moveDown() { this.y += VELOCITA_MOVIMENTO; }
    public void moveLeft() { this.x -= VELOCITA_MOVIMENTO; }
    public void moveRight() { this.x += VELOCITA_MOVIMENTO; }

    public String getNome() { return nome; }
    public int getHp() { return hp; }
    public int getHpMax() { return hpMax; }
    public void setHp(int hp) { this.hp = Math.clamp(hp, 0, this.hpMax); }
    public void takeDamage(int danno) { this.hp = Math.clamp(this.hp - danno, 0, this.hpMax); }

    public int getKarma() { return karma; }
    public void setKarma(int karma) { this.karma = karma; }
    public void addKarma(int qta) { this.karma += qta; }

    public int getLivello() { return livello; }
    public int getXp() { return xp; }
    public int getXpNecessari() { return xpNecessari; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public int getDeterminazione() { return determinazione; }
    public int getResilienza() { return resilienza; }
    public int getSintonia() { return sintonia; }
    public List<String> getRicordi() { return ricordi; }
    public void addRicordo(String ricordo) { if(!ricordi.contains(ricordo)) ricordi.add(ricordo); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Player other = (Player) obj;
        return Objects.equals(this.nome, other.getNome());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}