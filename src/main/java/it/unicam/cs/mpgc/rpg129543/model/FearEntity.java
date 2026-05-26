package it.unicam.cs.mpgc.rpg129543.model;

public record FearEntity(
        String nome,
        String descrizione,
        int difficoltaBase,
        String conseguenzaFallimento
) {}