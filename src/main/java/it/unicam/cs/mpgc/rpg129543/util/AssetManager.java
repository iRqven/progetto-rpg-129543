package it.unicam.cs.mpgc.rpg129543.util;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestore centralizzato degli asset grafici con supporto al ritaglio dei fogli di sprite (Sprite Sheet).
 *
 * NOTA: prima gli errori di caricamento venivano inghiottiti silenziosamente
 * (catch generico che restituiva null senza traccia). Questo rende invisibile
 * un asset mancante o un bug reale: ora viene stampato un avviso sullo standard error.
 */
public class AssetManager {
    private static final Map<String, Image> imageCache = new HashMap<>();

    private AssetManager() {
    }

    public static Image getImage(String fileName) {
        return getImage(fileName, 0, 0, 0, 0); // Caricamento intero standard
    }

    /**
     * Carica un'immagine o ritaglia una porzione specifica (utile per gli sprite sheet a griglia).
     */
    public static Image getImage(String fileName, int x, int y, int width, int height) {
        String cacheKey = fileName + "_" + x + "_" + y + "_" + width + "_" + height;
        if (imageCache.containsKey(cacheKey)) {
            return imageCache.get(cacheKey);
        }

        try {
            String path = "/assets/" + fileName;
            InputStream is = AssetManager.class.getResourceAsStream(path);
            if (is == null) {
                System.err.println("AVVISO - Asset non trovato: " + path);
                return null;
            }

            Image fullImage = new Image(is);
            Image resultImg;

            // Se vengono passate dimensioni valide, ritaglia il singolo frame dallo sheet
            if (width > 0 && height > 0) {
                resultImg = new WritableImage(fullImage.getPixelReader(), x, y, width, height);
            } else {
                resultImg = fullImage;
            }

            imageCache.put(cacheKey, resultImg);
            return resultImg;
        } catch (Exception e) {
            System.err.println("ERRORE - Fallimento nel caricamento dell'asset: " + fileName);
            e.printStackTrace();
            return null;
        }
    }
}