package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.util.GeometryUtils;
import it.unicam.cs.mpgc.rpg129543.util.RandomSource;

/**
 * Responsabile esclusivamente del calcolo delle posizioni casuali di spawn
 * all'interno di una stanza. Prima questa logica (calcolaInRange, calcolaDistanza,
 * e il ciclo do/while di "riprova finché non sei abbastanza lontano") viveva
 * dentro il costruttore di Room, che quindi si occupava sia di essere un
 * contenitore dati sia di generare coordinate: due responsabilità distinte.
 */
class SpawnPositionGenerator {
    private static final double MIN_SPAWN_X = 200.0;
    private static final double MAX_SPAWN_X = 600.0;
    private static final double MIN_SPAWN_Y = 100.0;
    private static final double MAX_SPAWN_Y = 480.0;
    private static final double MIN_DISTANZA_ENTITA = 130.0;

    private final RandomSource random;

    SpawnPositionGenerator(RandomSource random) {
        this.random = random;
    }

    /** Genera una posizione casuale nell'area di spawn (usata per il boss/NPC). */
    double[] generatePrimaryPosition() {
        return new double[]{inRange(MIN_SPAWN_X, MAX_SPAWN_X), inRange(MIN_SPAWN_Y, MAX_SPAWN_Y)};
    }

    /** Genera una seconda posizione ad almeno MIN_DISTANZA_ENTITA dalla prima (usata per il frammento). */
    double[] generateSecondaryPosition(double referenceX, double referenceY) {
        double x;
        double y;
        do {
            x = inRange(MIN_SPAWN_X, MAX_SPAWN_X);
            y = inRange(MIN_SPAWN_Y, MAX_SPAWN_Y);
        } while (GeometryUtils.distanza(referenceX, referenceY, x, y) < MIN_DISTANZA_ENTITA);
        return new double[]{x, y};
    }

    private double inRange(double min, double max) {
        return min + (random.nextDouble() * (max - min));
    }
}