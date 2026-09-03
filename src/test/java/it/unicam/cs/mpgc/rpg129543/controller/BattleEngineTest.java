package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.api.BattleAction; // IMPORTA L'ENUM
import it.unicam.cs.mpgc.rpg129543.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BattleEngineTest {

    @Test
    void testCuraConsumaVolontaEIncrementaHp() {
        Player player = new Player("AnimaTest", "Classe", "Allineamento");
        player.setHp(30);

        Enemy enemy = new Enemy("Spettro", 100, "RABBIA", "r", "p", "c");
        GameState gameState = new GameState(player);

        BattleEngine engine = new BattleEngine(BattleEngine.BossMood.RABBIA);
        engine.setVolonta(6);

        // CORREZIONE: Usa BattleAction.CURA invece della stringa "CURA"
        engine.executeTurn(player, enemy, BattleAction.CURA, gameState);

        assertTrue(player.getHp() > 30, "Il giocatore doveva recuperare HP dopo aver usato CURA.");
        assertEquals(3, engine.getVolonta(), "L'azione CURA doveva consumare 3 punti Volontà (6 - 3 = 3).");
    }
}