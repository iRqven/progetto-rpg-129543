package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.api.BattleAction;
import it.unicam.cs.mpgc.rpg129543.model.Enemy;
import it.unicam.cs.mpgc.rpg129543.model.GameState;
import it.unicam.cs.mpgc.rpg129543.model.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BattleEngineTest {

    @Test
    void testCuraSuperaDannoNemico() {
        Player player = new Player("Test", "Classe", "Allineamento");
        player.setHp(50);
        Enemy enemy = new Enemy("Boss", 100, "RABBIA", "Frase1", "Frase2", "Frase3");
        GameState gameState = new GameState(player);

        BattleEngine engine = new BattleEngine(BattleEngine.BossMood.RABBIA);
        engine.setVolonta(3);

        engine.executeTurn(player, enemy, BattleAction.CURA, gameState);

        assertTrue(player.getHp() > 50, "La cura deve essere matematicamente superiore al danno base del nemico in questo range");
    }
}