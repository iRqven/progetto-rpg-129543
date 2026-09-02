package it.unicam.cs.mpgc.rpg129543.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        // Viene eseguito prima di ogni singolo test per avere un Player "pulito"
        player = new Player("AnimaTest", "Ombra", "Viandante");
        player.setHp(100);
    }

    @Test
    void testTakeDamageNonScendeSottoZero() {
        player.takeDamage(150); // Il danno supera nettamente gli HP massimi
        assertEquals(0, player.getHp(), "Gli HP non devono mai assumere valori negativi.");
    }

    @Test
    void testSetHpNonSuperaIlMassimo() {
        player.takeDamage(50);
        player.setHp(200); // Tento di curarlo oltre il limite massimo consentito
        assertEquals(100, player.getHp(), "Gli HP curati non devono mai superare l'HpMax corrente.");
    }

    @Test
    void testLevelUpIncrementaStatistiche() {
        int xpNecessariIniziali = player.getXpNecessari();
        int determinazioneIniziale = player.getDeterminazione();

        // Fornisce XP appena sufficienti per attivare un level up con un piccolo resto
        boolean haLivellato = player.addXp(xpNecessariIniziali + 10);

        assertTrue(haLivellato, "Il metodo addXp deve restituire true al passaggio di livello.");
        assertEquals(2, player.getLivello(), "Il livello del giocatore deve essere salito a 2.");
        assertEquals(10, player.getXp(), "Gli XP in eccesso devono essere conservati nel nuovo livello.");
        assertTrue(player.getDeterminazione() > determinazioneIniziale, "La determinazione deve aumentare al level up.");
    }
}