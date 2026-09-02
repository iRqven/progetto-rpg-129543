package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import java.util.ArrayList;
import java.util.List;

public class RoomFactory {
    private static final double DOOR_X = 730.0;
    private static final double DOOR_Y = 300.0;

    public static List<Room> createRooms() {
        List<Room> rooms = new ArrayList<>();

        // Room 0 (Piano 1)
        rooms.add(new Room(0, "Piano 1: La Superbia",
                "Il rimbombo sordo di un treno in avvicinamento fa tremare la nebbia, che assume la forma di specchi crepati. Un uomo anziano con una divisa logora da capostazione è bloccato sotto una grata di ferro.",
                new SkillCheckChallenge("IL CAPOSTAZIONE:\n\"Ti ricordi di me? Camminavi sulla linea gialla ignorando i miei richiami, convinto che le regole non valessero per uno splendido uomo d'affari come te. Quando mi sono opposto ai tuoi traffici, mi hai fatto licenziare con false accuse, distruggendo la mia dignità. Ora sono io a terra. Vuoi piegare il tuo orgoglio per aiutarmi o passerai oltre pensando di essere superiore a un vecchio barbone?\""),
                DOOR_X, DOOR_Y,
                "Un suono acuto ti trapassa i timpani. Una banchina affollata. Qualcuno urlava di non oltrepassare la linea gialla.",
                true,
                "backgrounds/bg_0.jpg",
                "doors/door_0.png",
                "fragments/fragment_0.png"));

        // Room 1 (Piano 2)
        rooms.add(new Room(1, "Piano 2: L'Invidia",
                "L'aria sa di ferro e freni bruciati, tingendosi di un verde acido. Una figura con una giacca da ufficio strappata ti sbarra la strada.",
                new CombatChallenge("Lo Spettro del Socio Tradito",
                        "Non sopportavi che fossi io quello talentuoso, vero? Non sopportavi che la gente si fidasse di me. Così hai firmato quei maledetti documenti a mio nome, mi hai rubato l'idea e mi hai addossato i debiti della tua prima società fantasma. Affrontami, ladro!",
                        "Hai rubato la mia idea, ti sei arricchito sulle mie spalle, viscido parassita!",
                        "Sento ancora l'odore di bruciato dei bilanci falsi della tua società fantasma...",
                        "Mi fidavo di te... eravamo cresciuti insieme e mi hai addossato i tuoi debiti morali!"),
                DOOR_X, DOOR_Y,
                "L'invidia per il talento altrui mascherata da affari freddi. Un rancore coltivato all'ombra dei successi altrui.",
                true,
                "backgrounds/bg_1.jpg",
                "doors/door_1.png",
                "fragments/fragment_1.png"));

        // Room 2 (Piano 3)
        rooms.add(new Room(2, "Piano 3: L'Ira",
                "Le pareti sembrano fiamme grigie, illuminate a intermittenza come dai fari di un convoglio in corsa. Un'ombra imponente brandisce una sbarra di ferro.",
                new CombatChallenge("L'Ombra del Padre Disperato",
                        "Ricordi quando sono venuto nel tuo ufficio a implorarti? Ti ho urlato contro, ero fuori di me perché avevi rubato i soldi per le cure di mio figlio! E tu cosa hai fatto? Hai chiamato la tua sicurezza e mi hai fatto minacciare. La mia ira era giusta, la tua era spietata. Combattimi!",
                        "Hai minacciato la mia famiglia con la tua sicurezza! Ti strapperò quel sorriso arrogante!",
                        "Hai paura della mia mazza di ferro? Guarda cosa fa la disperazione di un padre!",
                        "Mio figlio aveva bisogno di cure e tu hai rubato i nostri risparmi senza battere ciglio!"),
                DOOR_X, DOOR_Y,
                "Fogli sparsi sul pavimento di marmo del tuo ufficio. 'Ti prego', implorava la voce. Tu hai semplicemente chiamato la sicurezza.",
                true,
                "backgrounds/bg_2.jpg",
                "doors/door_2.png",
                "fragments/fragment_2.png"));

        // Room 3 (Piano 4)
        rooms.add(new Room(3, "Piano 4: L'Accidia",
                "Una panchina di ferro di una vecchia stazione emerge dalla nebbia, scandita dal ticchettio invisibile di un orologio. C'è una donna seduta, immobile e fredda.",
                new NarrativeChallenge("La Madre Impietrita",
                        "Sono rimasta seduta su questa banchina per giorni, con le valigie e i miei figli, dopo che il tuo finto fondo immobiliare ci ha portato via la casa. Sapevi benissimo cosa ci stava succedendo. Ti ho inviato decine di lettere, ma non hai mai risposto. La tua totale indifferenza ci ha ucciso dentro. E ora il tuo cuore è di pietra come me.",
                        "La busta è ancora sigillata. Sentivi il peso delle parole non lette, ma il rumore del traffico fuori dalla finestra era più interessante."),
                DOOR_X, DOOR_Y,
                "Una lettera di aiuto mai aperta.",
                true,
                "backgrounds/bg_3.jpg",
                "doors/door_3.png",
                "fragments/fragment_3.png"));

        // Room 4 (Piano 5)
        rooms.add(new Room(4, "Piano 5: L'Avarizia",
                "Il pavimento è coperto di monete di fumo. Sopra di te, un grande tabellone degli orari vuoto rotea furiosamente. Un'entità magra fluttua stringendo un salvadanaio vuoto.",
                new CombatChallenge("Il Rimpianto del Giovane Operaio",
                        "Erano cinque anni di turni di notte in fabbrica. Cinque anni di sacrifici che ti ho consegnato in mano, credendo alle tue promesse di un futuro sicuro. Ti sei preso tutto per comprarti macchine e vestiti. Anche sulla banchina della stazione, scappavi stringendo la valigia con i MIEI soldi!",
                        "Ti sei preso i miei cinque anni di turni di notte in fabbrica per comprarti vestiti di lusso!",
                        "Scappavi sulla banchina della stazione stringendo la valigia... ma da qui non scappi!",
                        "Credevo alle tue promesse di un investimento sicuro... mi hai lasciato senza un futuro!"),
                DOOR_X, DOOR_Y,
                "Soldi sottratti per riempire la valigia che avevi con te. Sulla banchina, il peso del cuoio era l'unica cosa reale.",
                true,
                "backgrounds/bg_4.jpg",
                "doors/door_4.png",
                "fragments/fragment_4.png"));

        // Room 5 (Piano 6)
        rooms.add(new Room(5, "Piano 6: La Gola",
                "Un banchetto distorto coperto di cenere. In lontananza, una voce metallica annuncia un ritardo. Una gentile anziana signora fissa un piatto vuoto piangendo.",
                new SkillCheckChallenge("LA GENTILE ANZIANA:\n\"Ti fidavi di me, mi portavi i pasticcini a casa per convincermi a firmare la girata della mia pensione. Mi hai lasciato senza un soldo persino per fare la spesa. Ho passato gli ultimi mesi della mia vita a pane e acqua per colpa dei tuoi contratti. Ora c'è un ultimo raggio di luce in questa stanza. La prenderai tutta per te o lancerai il dado per nutrirmi?\""),
                DOOR_X, DOOR_Y,
                "L'avidità di consumare tutto ciò che appartiene ad altri. Un vuoto incolmabile, anche davanti allo schianto imminente.",
                true,
                "backgrounds/bg_5.jpg",
                "doors/door_5.png",
                "fragments/fragment_5.png"));

        // Room 6 (Piano 7)
        rooms.add(new Room(6, "Piano 7: Il Tradimento",
                "L'orologio digitale sopra la porta d'uscita lampeggia rosso: 14:02. Davanti al portale finale c'è un'ombra speculare, vestita con abiti ordinari identici ai tuoi, ma con il volto coperto di sangue.",
                new CombatChallenge("Lo Spettro del Complice",
                        "Eravamo d'accordo. Abbiamo truffato mezza città insieme, usando il nostro fascino e le nostre bugie. Ma quando la polizia ci ha messi all'angolo su quella banchina alle 14:02, hai pensato solo a te stesso. Mi hai spinto sotto il treno per rallentare la mia corsa e usarmi come scudo umano. Ma siamo caduti entrambi. Guarda cosa hai fatto al tuo unico amico.",
                        "Mi hai spinto sotto le ruote del treno! Scudo umano... ecco cosa ero per te!",
                        "L'orologio scatta... senti il fischio del treno delle 14:02 che arriva?!",
                        "Abbiamo truffato mezza città insieme... come hai potuto tradirmi all'ultimo secondo?"),
                DOOR_X, DOOR_Y,
                "14:02. Il rumore metallico dei freni. Due mani che spingono. Uno scudo di carne per salvarti la vita. Non è bastato.",
                true,
                "backgrounds/bg_6.jpg",
                "doors/door_6.png",
                "fragments/fragment_6.png"));

        return rooms;
    }
}