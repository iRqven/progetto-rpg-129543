package it.unicam.cs.mpgc.rpg129543.api;

/**
 * Tipologia di una sfida. Sostituisce i precedenti flag booleani
 * isCombat()/isSkillCheck() di Challenge, che costringevano il chiamante
 * a un if/else a catena invece di usare il polimorfismo o un dispatch pulito.
 */
public enum ChallengeType {
    COMBAT,
    SKILL_CHECK,
    NARRATIVE
}