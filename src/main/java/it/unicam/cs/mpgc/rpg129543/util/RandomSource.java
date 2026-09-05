package it.unicam.cs.mpgc.rpg129543.util;

/**
 * Astrae la generazione di numeri casuali (Dependency Inversion Principle).
 * Prima, ogni classe che aveva bisogno di casualità (Room, AnomalieEngine,
 * BattleEngine, SkillCheckChallenge...) creava un proprio "new Random()" interno:
 * questo rendeva impossibile testare in modo deterministico la logica di gioco.
 * Iniettando questa interfaccia si può passare un'implementazione finta (mock)
 * nei test unitari.
 */
public interface RandomSource {
    int nextInt(int bound);
    boolean nextBoolean();
    double nextDouble();
}