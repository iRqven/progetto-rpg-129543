package it.unicam.cs.mpgc.rpg129543.controller;

/** Esito dell'applicazione della scelta del giocatore di fronte a un'anomalia. */
public record AnomalyOutcome(String message, boolean playerWasHit) {}