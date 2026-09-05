package it.unicam.cs.mpgc.rpg129543.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {
    private static final double VELOCITA_MOVIMENTO = 5.0;

    // Prima questi valori erano tutti letterali sparsi dentro addXp() (100, 2, 1.5, 5, 10):
    // estratti in costanti con nome per rendere leggibile la formula di crescita.
    private static final int HP_BASE = 100;
    private static final int STAT_BASE = 10;
    private static final int STAT_GROWTH_PER_LEVEL = 2;
    private static final double XP_GROWTH_MULTIPLIER = 1.5;
    private static final int HP_MAX_PER_SINTONIA_POINT = 5;
    private static final int XP_NECESSARI_INIZIALI = 100;

    private final String nome;
    private final String classe;
    private final String allineamento;

    private int hp;
    private int hpMax = HP_BASE;
    private int karma;
    private int livello = 1;
    private int xp = 0;
    private int xpNecessari = XP_NECESSARI_INIZIALI;
    private double x = 100.0;
    private double y = 300.0;

    private int determinazione = STAT_BASE;
    private int resilienza = STAT_BASE;
    private int sintonia = STAT_BASE;

    private final List<String> ricordi = new ArrayList<>();
    private String genereSprite = "male";

    private int runCorrente = 1;
    private boolean tutorialLivelloVisto = false;

    public Player(String nome, String classe, String allineamento) {
        this.nome = Objects.requireNonNull(nome, "Il nome non può essere nullo.");
        this.classe = classe;
        this.allineamento = allineamento;
    }

    public boolean addXp(int quantita) {
        if (quantita < 0) return false;
        this.xp += quantita;
        boolean haLivellato = false;
        while (this.xp >= xpNecessari) {
            this.xp -= xpNecessari;
            this.livello++;
            this.xpNecessari = (int) (this.xpNecessari * XP_GROWTH_MULTIPLIER);

            this.determinazione += STAT_GROWTH_PER_LEVEL;
            this.resilienza += STAT_GROWTH_PER_LEVEL;
            this.sintonia += STAT_GROWTH_PER_LEVEL;
            this.hpMax = HP_BASE + (this.sintonia - STAT_BASE) * HP_MAX_PER_SINTONIA_POINT;
            this.hp = this.hpMax;
            haLivellato = true;
        }
        return haLivellato;
    }

    /** Prepara lo stato del giocatore per un New Game+: livello, statistiche, XP, Karma e ricordi restano intatti. */
    public void preparaPerNuovaRun() {
        this.runCorrente++;
        this.hp = this.hpMax;
        this.x = 100.0;
        this.y = 300.0;
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
    public void addRicordo(String ricordo) { if (!ricordi.contains(ricordo)) ricordi.add(ricordo); }
    public String getGenereSprite() { return genereSprite; }
    public void setGenereSprite(String genereSprite) { this.genereSprite = genereSprite; }
    public int getRunCorrente() { return runCorrente; }
    public boolean isTutorialLivelloVisto() { return tutorialLivelloVisto; }
    public void setTutorialLivelloVisto(boolean tutorialLivelloVisto) { this.tutorialLivelloVisto = tutorialLivelloVisto; }

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