package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Modello di dominio che incapsula la progressione dei 7 piani della condanna[cite: 27, 375].
 */
public class GameState {
    private static final int START_INDEX = 0;
    private static final double DOOR_X = 750.0;
    private static final double DOOR_Y = 300.0;

    private final List<Room> rooms = new ArrayList<>();
    private int currentRoomIndex = START_INDEX;

    public GameState(Player player) {
        Objects.requireNonNull(player, "Il giocatore non puo essere nullo.");
        initRooms();
    }

    private void initRooms() {
        rooms.add(new Room(0, "Piano 1: La Superbia",
                "La nebbia assume la forma di specchi crepati. Un uomo anziano con una divisa logora da capostazione e bloccato sotto una grata di ferro.",
                new SkillCheckChallenge(), 400, 300, DOOR_X, DOOR_Y,
                "Un fischietto d'ottone arrugginito.", 200, 400, true));

        rooms.add(new Room(1, "Piano 2: L'Invidia",
                "L'aria si tinge di un verde acido. Una figura con una giacca da ufficio strappata ti sbarra la strada.",
                new CombatChallenge("Lo Spettro del Socio Tradito",
                        "Non sopportavi che fossi io quello talentuoso, vero? Non sopportavi che la gente si fidasse di me. Cosi hai firmato quei maledetti documenti a mio nome, mi hai rubato l'idea e mi hai addossato i debiti della tua prima societa fantasma. Affrontami, ladro!"),
                400, 300, DOOR_X, 500, "Un vecchio brevetto finanziario strappato.", 600, 100, true));

        rooms.add(new Room(2, "Piano 3: L'Ira",
                "Le pareti sembrano fiamme grigie. Un'ombra imponente brandisce una sbarra di ferro, muovendosi con rabbia cieca.",
                new CombatChallenge("L'Ombra del Padre Disperato",
                        "Ricordi quando sono venuto nel tuo ufficio a implorarti? Ti ho urlato contro, ero fuori di me perche avevi rubato i soldi per le cure di mio figlio! E tu cosa hai fatto? Hai chiamato la tua sicurezza e mi hai fatto minacciare. La mia ira era giusta, la tua era spietata. Combattimi!"),
                300, 450, DOOR_X, 100, "Una cartella clinica infantile respinta.", 100, 100, true));

        rooms.add(new Room(3, "Piano 4: L'Accidia",
                "Una panchina di ferro di una vecchia stazione emerge dalla nebbia. C'è una donna seduta, immobile e fredda.",
                new NarrativeChallenge("La Madre Impietrita",
                        "Sono rimasta seduta su questa banchina per giorni, con le valigie e i miei figli, dopo che il tuo finto fondo immobiliare ci ha portato via la casa. Sapevi benissimo cosa ci stava succedendo. Ti ho inviato decine di lettere, ma non hai mai risposto. La tua totale indifferenza ci ha ucciso dentro. E ora il tuo cuore e di pietra come me.",
                        "Una lettera di aiuto mai aperta."),
                400, 300, DOOR_X, DOOR_Y, "Una chiave da sfratto arrugginita.", 500, 200, true));

        rooms.add(new Room(4, "Piano 5: L'Avarizia",
                "Il pavimento e coperto di monete di fumo. Un'entità magra fluttua stringendo un salvadanaio vuoto.",
                new CombatChallenge("Il Rimpianto del Giovane Operaio",
                        "Erano cinque anni di turni di notte in fabbrica. Cinque anni di sacrifici che ti ho consegnato in mano, credendo alle tue promesse di un futuro sicuro. Ti sei preso tutto per comprarti macchine e vestiti. Anche sulla banchina della stazione, scappavi stringendo la valigia con i MIEI soldi!"),
                350, 250, DOOR_X, DOOR_Y, "Un libretto di risparmi azzerato.", 150, 350, true));

        rooms.add(new Room(5, "Piano 6: La Gola",
                "Un banchetto distorto coperto di cenere. Una gentile anziana signora fissa un piatto vuoto piangendo.",
                new SkillCheckChallenge(), 400, 300, DOOR_X, DOOR_Y,
                "Un vassoio di dolci d'alta pasticceria ammuffito.", 200, 200, true));

        rooms.add(new Room(6, "Piano 7: La Lussuria",
                "Davanti al portale finale c'e un'ombra speculare in abito elegante, con il volto coperto di sangue.",
                new CombatChallenge("Lo Spettro del Complice",
                        "Eravamo d'accordo. Abbiamo truffato mezza città insieme, usando il nostro fascino e le nostre bugie. Ma quando la polizia ci ha messi all'angolo su quella banchina alle 14:02, hai pensato solo a te stesso. Mi hai spinto sotto il treno per rallentare la mia corsa e usarmi come scudo umano. Ma siamo caduti entrambi. Guarda cosa hai fatto al tuo unico amico."),
                500, 200, DOOR_X, DOOR_Y, "Un biglietto ferroviario di sola andata macchiato di sangue.", 400, 400, true));
    }

    public Room getCurrentRoom() { return rooms.get(currentRoomIndex); }

    public boolean nextRoom() {
        if (currentRoomIndex < rooms.size() - 1) {
            currentRoomIndex++;
            return true;
        }
        return false;
    }
}