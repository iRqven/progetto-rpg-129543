package it.unicam.cs.mpgc.rpg129543.controller;

/** Esito di un turno di combattimento: testo da mostrare e se il giocatore ha subito un colpo. */
public record BattleTurnOutcome(String logMessage, boolean playerWasHit) {}