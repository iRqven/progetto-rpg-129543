package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Gestisce lo stato globale della progressione dei livelli del gioco.
 * Inizializza l'architettura dei 10 piani del Purgatorio alternando sfide e narrazione.
 */
public class GameState {
    private static final int MAX_ROOMS = 10;
    private static final int START_ROOM_INDEX = 0;
    private static final double DEFAULT_DOOR_X = 750.0;
    private static final double DEFAULT_DOOR_Y_CENTER = 300.0;

    private final Player player;
    private final List<Room> rooms;
    private int currentRoomIndex;

    /**
     * Costruisce lo stato di gioco associandolo al giocatore protagonista.
     */
    public GameState(Player player) {
        this.player = Objects.requireNonNull(player, "Il giocatore non può essere nullo.");
        this.rooms = new ArrayList<>();
        this.currentRoomIndex = START_ROOM_INDEX;
        initRooms();
    }

    /**
     * Configura l'ecosistema narrativo dei 10 piani del Purgatorio.
     * Segue una curva di apprendimento lineare con spiegazioni iniziali e boss unici.
     */
    private void initRooms() {
        // PIANO 0: L'Atrio iniziale (Corridoio introduttivo, nessuna sfida)
        rooms.add(new Room(0,
                "Atrio delle Ombre",
                "Un luogo sfuocato. Senti il peso di mille vite.",
                null, 0, 0, DEFAULT_DOOR_X, DEFAULT_DOOR_Y_CENTER,
                "Un piccolo orologio da taschino fermo alle 14:02. L'ora esatta del tuo incidente stradale.",
                200, 400, true));

        // PIANO 1: Spiegazione del Sistema di Combattimento e delle Aure
        rooms.add(new Room(1,
                "Incrocio delle Esitazioni",
                "Una figura minuta ti osserva. Ti spiega come difenderti dai Rimorsi in questo regno.",
                new NarrativeChallenge(
                        "Spirito Guida",
                        "Ascolta bene, Viandante. I Rimorsi che incontrerai hanno un'Aura colorata che cambia sempre.\n" +
                                "Se vedi l'Aura RABBIA (Rossa), contrastala con la PAZIENZA.\n" +
                                "Se vedi l'Aura PAURA (Blu), contrastala con il CORAGGIO.\n" +
                                "Se vedi l'Aura COLPA (Gialla), contrastala con il PERDONO.\n" +
                                "Attaccare quando l'Aura corrisponde alla vera debolezza del boss raddoppierà il tuo danno!",
                        "Un manuale di etica logorato dal tempo."
                ),
                400, 300, DEFAULT_DOOR_X, 500,
                "La consapevolezza che per superare l'Oblio devi capire la natura del dolore.",
                600, 100, true));

        // PIANO 2: Stanza sicura (Nessun combattimento, comprensione della trama)
        rooms.add(new Room(2,
                "Limbo dei Ricordi Perduti",
                "L'aria qui è calma. Un'anima pacifica siede per terra e ti invita a riflettere senza combattere.",
                new NarrativeChallenge(
                        "L'Ombra dell'Amico",
                        "Ti ricordi di me? Quella sera correvi in auto perché eri arrabbiato... mi hai lasciato indietro. " +
                                "Ma non sono qui per condannarti. Guarda dentro di te prima di varcare la prossima porta, perché oltre questa stanza la nebbia si farà violenta.",
                        "Una vecchia fotografia stropicciata che vi ritrae felici al liceo."
                ),
                300, 450, DEFAULT_DOOR_X, 100,
                "Il ricordo di una promessa di fratellanza che avevi infranto per orgoglio.",
                100, 100, true));

        // PIANO 3: Primo Combattimento Lineare Reale (Boss unico)
        rooms.add(new Room(3,
                "Vicolo dell'Invidia Astiosa",
                "La nebbia si squarcia. Il tuo primo vero ostacolo ti ringhia contro.",
                new CombatChallenge(
                        "Spettro dell'Invidia",
                        "La personificazione di tutte le volte che hai gioito segretamente dei fallimenti altrui durante la tua vita terrena."
                ),
                400, 300, DEFAULT_DOOR_X, DEFAULT_DOOR_Y_CENTER,
                "Una medaglia scolastica di secondo posto, scagliata rabbiosamente contro un muro.",
                500, 200, true));

        // PIANO 4: Boss Unico - L'Avarizia
        rooms.add(new Room(4,
                "Caveau del Rimpianto Materiale",
                "Un'ombra gigantesca accumula monete di fumo. Non ti lascerà passare gratis.",
                new CombatChallenge(
                        "Il Collezionista d'Oro",
                        "Rappresenta l'egoismo finanziario: quando hai negato aiuto economico a chi ne aveva un disperato bisogno pur di tenere i conti pieni."
                ),
                350, 250, DEFAULT_DOOR_X, DEFAULT_DOOR_Y_CENTER,
                "Un estratto conto bancario macchiato di caffè, datato il giorno prima della tua fine.",
                150, 350, true));

        // PIANO 5: Incontro Narrativo - Rivelazione Chiave
        rooms.add(new Room(5,
                "Belvedere della Verità Cruda",
                "Un'ombra fluttua sul baratro. Non combatte, vuole solo che tu veda.",
                new NarrativeChallenge(
                        "Eco della Coscienza",
                        "L'orologio da taschino che hai trovato all'inizio... si è fermato quando hai perso il controllo del volante sul ponte. Non c'erano altri veicoli. Sei fuggito da te stesso.",
                        "Il ritaglio di giornale che parla del tuo misterioso incidente sul ponte di nebbia."
                ),
                400, 300, DEFAULT_DOOR_X, DEFAULT_DOOR_Y_CENTER,
                "La chiave della verità: l'incidente è stato causato dalla tua totale distrazione.",
                200, 200, true));

        // PIANO 6: Boss Unico - La Menzogna
        rooms.add(new Room(6,
                "Labirinto delle False Parole",
                "Un'entità con molteplici maschere sussurra falsità per confonderti.",
                new CombatChallenge(
                        "Il Viso Camaleontico",
                        "L'incarnazione di tutte le bugie che hai raccontato per salvare la tua reputazione, distruggendo quella degli altri."
                ),
                500, 200, DEFAULT_DOOR_X, DEFAULT_DOOR_Y_CENTER,
                "Una lettera di scuse mai spedita, nascosta in fondo a un cassetto ideale.",
                400, 400, true));

        // PIANO 7: Boss Unico - L'Ira
        rooms.add(new Room(7,
                "Fonderia del Rancore Cieco",
                "Il calore è insopportabile. Un mostro fatto di fiamme grigie ruggisce.",
                new CombatChallenge(
                        "Il Distruttore di Legami",
                        "La rabbia incontrollata che ti ha portato a urlare parole imperdonabili alle persone che ti amavano, spezzando i rapporti per sempre."
                ),
                450, 350, DEFAULT_DOOR_X, DEFAULT_DOOR_Y_CENTER,
                "Un anello di fidanzamento spezzato in due pezzi simmetrici.",
                300, 150, true));

        // PIANO 8: Incontro Narrativo Finale prima del Guardiano
        rooms.add(new Room(8,
                "Santuario del Congedo",
                "Una figura anziana e saggia siede sui gradini dell'ultimo portale sicuro.",
                new NarrativeChallenge(
                        "Custode del Passaggio",
                        "Hai combattuto i tuoi mostri e ascoltato le tue colpe. Ormai sai chi eri. Ma resta un ultimo ostacolo prima del Giudizio: la personificazione della tua stessa paura di sparire nel nulla.",
                        "Una pergamena bianca pronta per essere scritta con il tuo destino."
                ),
                400, 300, DEFAULT_DOOR_X, DEFAULT_DOOR_Y_CENTER,
                "Il coraggio di accettare le conseguenze delle proprie azioni storiche.",
                600, 450, true));

        // PIANO 9: Il Boss Finale - Te Stesso (Il Rimorso Supremo)
        rooms.add(new Room(9,
                "Porto del Giudizio Finale",
                "Un'ombra speculare a te blocca l'uscita definitiva dal Purgatorio. Ha il tuo stesso aspetto.",
                new CombatChallenge(
                        "L'Ombra di Te Stesso",
                        "Il Guardiano Finale. Rappresenta il rifiuto di perdonare te stesso. Se lo sconfiggi, purificherai la tua anima per il Paradiso."
                ),
                400, 300, DEFAULT_DOOR_X, DEFAULT_DOOR_Y_CENTER,
                "L'orologio da taschino torna a ticchettare. Il tempo ha ripreso a scorrere.",
                400, 100, true));
    }

    public Room getCurrentRoom() {
        return rooms.get(currentRoomIndex);
    }

    public boolean nextRoom() {
        if (currentRoomIndex < rooms.size() - 1) {
            currentRoomIndex++;
            return true;
        }
        return false;
    }
}