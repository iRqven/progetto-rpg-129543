package it.unicam.cs.mpgc.rpg129543.util;

/** Funzioni geometriche condivise tra model e controller. */
public final class GeometryUtils {

    private GeometryUtils() {
        // Classe di utility: non istanziabile
    }

    public static double distanza(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}