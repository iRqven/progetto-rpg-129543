package it.unicam.cs.mpgc.rpg129543.model;

/**
 * Data Aggregation class (Record) per raggruppare le configurazioni della Room,
 * eliminando il code smell della lunga lista di parametri.
 */
public record RoomConfig(
        double doorX,
        double doorY,
        String ricordoSbloccato,
        boolean hasFragment,
        String backgroundAssetName,
        String doorAssetName,
        String fragmentAssetName
) {}