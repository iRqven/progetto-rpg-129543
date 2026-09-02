package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.model.*;
import it.unicam.cs.mpgc.rpg129543.view.BattleView;
import it.unicam.cs.mpgc.rpg129543.view.MessageView;
import it.unicam.cs.mpgc.rpg129543.view.UIManager;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Controller dedicato alla gestione esclusiva del flusso di combattimento.
 */
public class BattleController {
    private final Player player;
    private final GameState gameState;

    private final Consumer<VBox> onShowOverlay;
    private final Runnable onClearOverlay;
    private final Runnable onUpdateHud;
    private final Consumer<Integer> onLevelUp;
    private final Runnable onGameOver;
    private final Runnable onBattleEnd;
    private final Runnable onDamageEffect;

    private BattleEngine currentBattle;
    private Enemy currentEnemy;
    private boolean isInSubMenuVirtu = false;

    public BattleController(Player player, GameState gameState,
                            Consumer<VBox> onShowOverlay, Runnable onClearOverlay,
                            Runnable onUpdateHud, Consumer<Integer> onLevelUp,
                            Runnable onGameOver, Runnable onBattleEnd, Runnable onDamageEffect) {
        this.player = player;
        this.gameState = gameState;
        this.onShowOverlay = onShowOverlay;
        this.onClearOverlay = onClearOverlay;
        this.onUpdateHud = onUpdateHud;
        this.onLevelUp = onLevelUp;
        this.onGameOver = onGameOver;
        this.onBattleEnd = onBattleEnd;
        this.onDamageEffect = onDamageEffect;
    }

    public void startBattleTutorial(CombatChallenge combatChallenge) {
        onClearOverlay.run();

        VBox tutorialMenu = UIManager.createBattleTutorialMenu(
                combatChallenge.getNomeNemico(),
                combatChallenge.getDescrizioneDettagliata(),
                player.getLivello(),
                () -> {
                    BattleEngine.BossMood mood = BattleEngine.BossMood.values()[(int)(Math.random() * 3)];
                    currentBattle = new BattleEngine(mood);
                    isInSubMenuVirtu = false;

                    currentEnemy = new Enemy(
                            combatChallenge.getNomeNemico(),
                            100,
                            mood.name(),
                            combatChallenge.getfRabbia(),
                            combatChallenge.getfPaura(),
                            combatChallenge.getfColpa()
                    );

                    updateBattleUI("La nebbia si focalizza. Lo scontro ha inizio.");
                }
        );

        onShowOverlay.accept(tutorialMenu);
    }

    private void updateBattleUI(String logText) {
        onClearOverlay.run();

        // Vittoria
        if (currentEnemy.getHp() <= 0) {
            int premioXp = (gameState.getCurrentRoom().id() + 1) * 50;
            player.addKarma(30);
            boolean haLivellato = player.addXp(premioXp);
            gameState.getCurrentRoom().solveChallenge();

            if (haLivellato) {
                onLevelUp.accept(premioXp);
            } else {
                // Aspetta il click per sbloccare il gioco
                finishInteraction("VITTORIA! Sconfiggi lo spettro ed accumuli +" + premioXp + " XP.", () -> {
                    player.setX(player.getX() + 60);
                    onUpdateHud.run();
                    onBattleEnd.run();
                });
            }
            return;
        }
        // Sconfitta
        else if (player.getHp() <= 0) {
            // Aspetta il click per resettare il gioco
            finishInteraction("L'OSCURITÀ TI HA CONSUMATO...", () -> {
                onGameOver.run();
            });
            return;
        }

        BattleView battleView = new BattleView(
                player, currentEnemy, currentBattle, logText, isInSubMenuVirtu,
                mossa -> {
                    if (mossa.equals("INDIETRO")) {
                        isInSubMenuVirtu = false;
                        updateBattleUI(logText);
                    } else if (mossa.equals("APRI_VIRTU")) {
                        isInSubMenuVirtu = true;
                        updateBattleUI(logText);
                    } else {
                        processBattle(mossa);
                    }
                },
                () -> {
                    gameState.getCurrentRoom().setSfidaGestita(true);
                    finishInteraction("Sei fuggito perdendo terreno.", () -> {
                        player.setX(player.getX() + 60);
                        onUpdateHud.run();
                        onBattleEnd.run();
                    });
                }
        );

        onShowOverlay.accept(battleView.getView());
    }

    private void processBattle(String mossaInput) {
        String res = "";

        if (mossaInput.equals("ACCETTA_PATTO")) {
            res = currentBattle.getAnomalieEngine().applicaSceltaA(player, currentEnemy, currentBattle);
        } else if (mossaInput.equals("RIFIUTA_PATTO")) {
            currentBattle.getAnomalieEngine().disattiva();
            res = "Hai rifiutato il patto con l'ignoto. L'illusione svanisce e lo scontro riprende.";
        } else {
            String mossaPulita = mossaInput.split(" ")[0].trim();
            res = currentBattle.executeTurn(player, currentEnemy, mossaPulita, gameState);

            if (currentEnemy.getHp() > 0 && player.getHp() > 0) {
                currentBattle.getAnomalieEngine().controllaInnesco();
            }
        }

        if (res.contains("violentemente")) {
            onDamageEffect.run();
        }
        updateBattleUI(res);
    }

    // Aggiunto il parametro Runnable onClose per gestire l'azione DOPO il click
    private void finishInteraction(String text, Runnable onClose) {
        onClearOverlay.run();
        MessageView finishView = new MessageView(
                "ESITO SCONTRO", text, "Prosegui", "#f1c40f", () -> {
            onClearOverlay.run();
            if (onClose != null) onClose.run();
        });
        onShowOverlay.accept(finishView.getView());
    }
}