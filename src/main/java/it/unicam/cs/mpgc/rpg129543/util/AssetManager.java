package it.unicam.cs.mpgc.rpg129543.util;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/** Gestore centralizzato degli asset grafici, con cache e ritaglio da sprite sheet. */
public class AssetManager {
    private static final Map<String, Image> imageCache = new HashMap<>();

    private AssetManager() {
    }

    public static Image getImage(String fileName) {
        return getImage(fileName, 0, 0, 0, 0);
    }

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