package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.model.Room;

/** Callback di navigazione richieste da GameController (implementata da GameRouter). */
public interface GameNavigationCallbacks {
    void onStartCombat(Challenge challenge);
    void onStartSkillCheck(Challenge challenge);
    void onStartNarrative(Challenge challenge);
    void onLoreOnly(Room room);
    void onMemoryCollected(String memoryText);
    void onNextRoom();
    void onFinalJudgment();
}