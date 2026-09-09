package it.unicam.cs.mpgc.rpg129543.util;

/**
 * Astrae la generazione di numeri casuali (Dependency Inversion Principle).
 */
public interface RandomSource {
    int nextInt(int bound);
    boolean nextBoolean();
    double nextDouble();
}