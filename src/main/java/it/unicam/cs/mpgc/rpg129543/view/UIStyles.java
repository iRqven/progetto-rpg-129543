package it.unicam.cs.mpgc.rpg129543.view;

/** Costanti di stile CSS condivise tra le view di combattimento e i menu. */
public final class UIStyles {

    private UIStyles() {
    }

    public static final String BATTLE_BUTTON_BASE =
            "-fx-min-width: 135; -fx-min-height: 40; -fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: white;";

    public static String battleButton(String colorHex) {
        return BATTLE_BUTTON_BASE + "-fx-base: " + colorHex + ";";
    }
}