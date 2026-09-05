package it.unicam.cs.mpgc.rpg129543.util;

import java.util.Random;

/**
 * Implementazione di produzione di RandomSource, basata su java.util.Random.
 */
public class DefaultRandomSource implements RandomSource {
    private final Random random = new Random();

    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    @Override
    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }
}