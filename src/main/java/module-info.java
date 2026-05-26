module it.unicam.cs.mpgc.rpg129543 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind; // Fondamentale per la Build

    // Esporta il package principale per JavaFX
    exports it.unicam.cs.mpgc.rpg129543;

    // Apre i package dei dati a Jackson per la persistenza
    opens it.unicam.cs.mpgc.rpg129543.model to com.fasterxml.jackson.databind;
    opens it.unicam.cs.mpgc.rpg129543 to javafx.graphics;
}