package it.unicam.cs.mpgc.rpg129543.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnemyTest {

    @Test
    void testTakeDamageNormale() {
        Enemy enemy = new Enemy("Spettro", 100, "RABBIA", "Frase1", "Frase2", "Frase3");
        enemy.takeDamage(30);
        assertEquals(70, enemy.getHp());
    }

    @Test
    void testTakeDamageNonScendeSottoZero() {
        Enemy enemy = new Enemy("Spettro", 100, "RABBIA", "Frase1", "Frase2", "Frase3");
        enemy.takeDamage(150); // Danno letale superiore agli HP massimi
        assertEquals(0, enemy.getHp());
    }

    @Test
    void testEccezioneSuDannoNegativo() {
        Enemy enemy = new Enemy("Spettro", 100, "RABBIA", "Frase1", "Frase2", "Frase3");

        // Verifica che la Clausola di Guardia scatti correttamente
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            enemy.takeDamage(-10);
        });

        assertEquals("Danno negativo.", exception.getMessage());
    }

    @Test
    void testEccezioneSuNomeVuoto() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Enemy("", 100, "RABBIA", "Frase1", "Frase2", "Frase3");
        });

        assertEquals("Il nome non può essere vuoto.", exception.getMessage());
    }
}