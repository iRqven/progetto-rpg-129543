package it.unicam.cs.mpgc.rpg129543.controller;

import javafx.scene.layout.VBox;

/** Operazioni di interfaccia richieste da BattleController (implementata da GameRouter). */
public interface BattleCallbacks {
    void showOverlay(VBox overlay);
    void clearOverlay();
    void updateHud();
    void onLevelUp(int xpGuadagnati);
    void onGameOver();
    void onBattleEnd();
    void onDamageEffect();
}