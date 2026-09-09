package it.unicam.cs.mpgc.rpg129543.controller;

import it.unicam.cs.mpgc.rpg129543.api.BattleAction;
import it.unicam.cs.mpgc.rpg129543.model.CombatChallenge;
import it.unicam.cs.mpgc.rpg129543.model.Enemy;
import it.unicam.cs.mpgc.rpg129543.model.GameState;
import it.unicam.cs.mpgc.rpg129543.model.Player;
import it.unicam.cs.mpgc.rpg129543.util.GameplayConstants;
import it.unicam.cs.mpgc.rpg129543.view.BattleView;
import it.unicam.cs.mpgc.rpg129543.view.MessageView;
import it.unicam.cs.mpgc.rpg129543.view.UIManager;

import java.util.Objects;

/** Controller dedicato alla gestione esclusiva del flusso di combattimento. */
public class BattleController {
    private final Player player;
    private final GameState gameState;
    private final BattleCallbacks callbacks;

    private BattleEngine currentBattle;
    private Enemy currentEnemy;
    private boolean isInSubMenuVirtu = false;

    public BattleController(Player player, GameState gameState, BattleCallbacks callbacks) {
        this.player = Objects.requireNonNull(player);
        this.gameState = Objects.requireNonNull(gameState);
        this.callbacks = Objects.requireNonNull(callbacks);
    }

    public void startBattleTutorial(CombatChallenge combatChallenge) {
        callbacks.clearOverlay();
        var tutorialMenu = UIManager.createBattleTutorialMenu(
                combatChallenge.getNomeNemico(),
                combatChallenge.getDescrizioneDettagliata(),
                player.getLivello(),
                () -> {
                    BattleEngine.BossMood mood = BattleEngine.BossMood.values()[(int) (Math.random() * 3)];
                    currentBattle = new BattleEngine(mood);
                    isInSubMenuVirtu = false;

                    int bossMaxHp = GameplayConstants.BOSS_HP_BASE
                            + ((player.getRunCorrente() - 1) * GameplayConstants.BOSS_HP_GROWTH_PER_RUN);

                    currentEnemy = new Enemy(
                            combatChallenge.getNomeNemico(),
                            bossMaxHp,
                            mood.name(),
                            combatChallenge.getFraseRabbia(),
                            combatChallenge.getFrasePaura(),
                            combatChallenge.getFraseColpa()
                    );
                    updateBattleUI("La nebbia si focalizza. Lo scontro ha inizio.");
                }
        );
        callbacks.showOverlay(tutorialMenu);
    }

    private void updateBattleUI(String logText) {
        callbacks.clearOverlay();

        if (currentEnemy.getHp() <= 0) {
            int premioXp = (gameState.getCurrentRoom().id() + 1) * GameplayConstants.XP_PER_ROOM_MULTIPLIER;
            player.addKarma(GameplayConstants.KARMA_REWARD_VICTORY);
            boolean haLivellato = player.addXp(premioXp);
            gameState.getCurrentRoom().solveChallenge();

            if (haLivellato) {
                callbacks.onLevelUp(premioXp);
            } else {
                finishInteraction("VITTORIA! Sconfiggi lo spettro ed accumuli +" + premioXp + " XP.", () -> {
                    player.setX(player.getX() + GameplayConstants.POST_INTERACTION_STEP);
                    callbacks.updateHud();
                    callbacks.onBattleEnd();
                });
            }
            return;
        } else if (player.getHp() <= 0) {
            finishInteraction("L'OSCURITÀ TI HA CONSUMATO...", callbacks::onGameOver);
            return;
        }

        BattleView battleView = new BattleView(
                player, currentEnemy, currentBattle, logText, isInSubMenuVirtu,
                (BattleAction action) -> {
                    if (action == BattleAction.INDIETRO) {
                        isInSubMenuVirtu = false;
                        updateBattleUI(logText);
                    } else if (action == BattleAction.APRI_VIRTU) {
                        isInSubMenuVirtu = true;
                        updateBattleUI(logText);
                    } else {
                        processBattle(action);
                    }
                },
                () -> {
                    gameState.getCurrentRoom().setSfidaGestita(true);
                    finishInteraction("Sei fuggito perdendo terreno.", () -> {
                        player.setX(player.getX() + GameplayConstants.POST_INTERACTION_STEP);
                        callbacks.updateHud();
                        callbacks.onBattleEnd();
                    });
                }
        );

        callbacks.showOverlay(battleView.getView());
    }

    private void processBattle(BattleAction action) {
        String logMessage;
        boolean playerWasHit;

        if (action == BattleAction.ACCETTA_PATTO) {
            AnomalyOutcome esito = currentBattle.getAnomalieEngine().applicaSceltaA(player, currentEnemy, currentBattle);
            logMessage = esito.message();
            playerWasHit = esito.playerWasHit();
        } else if (action == BattleAction.RIFIUTA_PATTO) {
            currentBattle.getAnomalieEngine().disattiva();
            logMessage = "Hai rifiutato il patto con l'ignoto. L'illusione svanisce e lo scontro riprende.";
            playerWasHit = false;
        } else {
            BattleTurnOutcome outcome = currentBattle.executeTurn(player, currentEnemy, action, gameState);
            logMessage = outcome.logMessage();
            playerWasHit = outcome.playerWasHit();

            if (currentEnemy.getHp() > 0 && player.getHp() > 0) {
                currentBattle.getAnomalieEngine().controllaInnesco();
            }
        }

        if (playerWasHit) {
            callbacks.onDamageEffect();
        }
        updateBattleUI(logMessage);
    }

    private void finishInteraction(String text, Runnable onClose) {
        callbacks.clearOverlay();
        MessageView finishView = new MessageView(
                "ESITO SCONTRO", text, "Prosegui", "#f1c40f", () -> {
            callbacks.clearOverlay();
            if (onClose != null) onClose.run();
        });
        callbacks.showOverlay(finishView.getView());
    }
}