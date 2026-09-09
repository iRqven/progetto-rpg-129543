package it.unicam.cs.mpgc.rpg129543.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    public void testCreazionePlayerValida() {
        Player player = new Player("Anima", "Ombra", "Viandante");
        player.setHp(100);

        assertEquals("Anima", player.getNome());
        assertTrue(player.getHp() > 0, "Gli HP iniziali devono essere maggiori di zero");
        assertEquals(1, player.getLivello(), "Il livello iniziale deve essere 1");
        assertEquals(0, player.getXp(), "Gli XP iniziali devono essere 0");
    }

    @Test
    public void testGuadagnoXpELivellamento() {
        Player player = new Player("Test", "Classe", "Allineamento");
        player.setHp(100);

        int xpNecessari = player.getXpNecessari();
        int hpMaxIniziale = player.getHpMax();

        boolean haLivellato = player.addXp(xpNecessari + 10);

        assertTrue(haLivellato, "Il giocatore deve salire di livello superando la soglia");
        assertEquals(2, player.getLivello(), "Il livello deve aumentare a 2");
        assertTrue(player.getXp() >= 0, "Gli XP in eccesso devono essere mantenuti");
        assertTrue(player.getHpMax() > hpMaxIniziale, "Gli HP Max devono crescere col livello");
    }

    @Test
    public void testDannoELimitiHp() {
        Player player = new Player("Test", "Classe", "Allineamento");
        player.setHp(100);

        int hpIniziale = player.getHp();

        player.takeDamage(15);
        assertTrue(player.getHp() < hpIniziale, "Subire danni deve ridurre gli HP correnti");

        player.setHp(player.getHpMax() + 50);
        assertTrue(player.getHp() <= player.getHpMax(), "La cura non deve mai superare il tetto degli HP Max");
    }
}