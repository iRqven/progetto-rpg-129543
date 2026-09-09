package it.unicam.cs.mpgc.rpg129543.model;

import it.unicam.cs.mpgc.rpg129543.api.Challenge;
import it.unicam.cs.mpgc.rpg129543.util.DefaultRandomSource;
import it.unicam.cs.mpgc.rpg129543.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class RoomFactory {
    private static final double DOOR_X = 730.0;
    private static final double DOOR_Y = 300.0;

    public static List<Room> createRooms() {
        List<Room> rooms = new ArrayList<>();
        RandomSource random = new DefaultRandomSource();

        rooms.add(new Room(0, "Piano 1: La Superbia",
                "Il rimbombo sordo di un treno in avvicinamento fa tremare la nebbia. Un uomo anziano con una divisa logora da capostazione è bloccato sotto una grata di ferro.",
                new SkillCheckChallenge("IL CAPOSTAZIONE:\n\"Ancora tu. Quante volte hai superato quella linea gialla? L'orologio gira, ma tu sei sempre qui, schiacciato dallo stesso orgoglio che ti ha reso sordo al mio fischietto. Ti piegherai questa volta, o aspetterai il prossimo impatto?\""),
                new RoomConfig(DOOR_X, DOOR_Y, "Un suono acuto ti trapassa i timpani. Una banchina affollata. Qualcuno urlava di non oltrepassare il limite.", true, "backgrounds/bg_0.jpg", "doors/door_0.png", "fragments/fragment_0.png"), random));

        rooms.add(new Room(1, "Piano 2: L'Invidia",
                "L'aria sa di ferro e freni bruciati, tingendosi di un verde acido.",
                new CombatChallenge("Lo Spettro del Socio Tradito",
                        "Sento l'odore dell'inchiostro fresco sui miei progetti... quelli che hai firmato tu. Ogni volta che torni, la cicatrice brucia. Guardati: cerchi una via d'uscita rubando i meriti altrui, ma questa nebbia non puoi frodarla.",
                        "Hai rubato la mia firma! Questa prigione di nebbia è opera tua!",
                        "Sento ancora il rumore dei fogli strappati... non puoi fuggire all'infinito.",
                        "Eravamo fratelli... e mi hai condannato ai tuoi debiti morali."),
                new RoomConfig(DOOR_X, DOOR_Y, "L'invidia mascherata da affari freddi. Un rancore coltivato all'ombra dei successi altrui.", true, "backgrounds/bg_1.jpg", "doors/door_1.png", "fragments/fragment_1.png"), random));

        rooms.add(new Room(2, "Piano 3: L'Ira",
                "Le pareti sembrano fiamme grigie, illuminate a intermittenza come dai fari di un convoglio in corsa.",
                new CombatChallenge("L'Ombra del Padre Disperato",
                        "La sicurezza mi ha trascinato fuori dal tuo ufficio innumerevoli volte. E innumerevoli volte io torno con questa spranga. I soldi di mio figlio sono cenere, ma la mia rabbia è un binario che percorrerai per l'eternità.",
                        "Guarda cosa fa la disperazione! Ti spezzerò in ogni singolo ciclo!",
                        "Hai paura del metallo? È freddo esattamente come il tuo cuore!",
                        "Mio figlio... avevi i nostri risparmi in pugno e non hai mosso un dito."),
                new RoomConfig(DOOR_X, DOOR_Y, "Fogli sparsi su un pavimento di marmo. 'Ti prego', implorava la voce. Tu chiamasti la sicurezza.", true, "backgrounds/bg_2.jpg", "doors/door_2.png", "fragments/fragment_2.png"), random));

        rooms.add(new Room(3, "Piano 4: L'Accidia",
                "Una panchina di ferro emerge dalla nebbia, scandita dal ticchettio di un orologio. C'è una donna seduta, immobile.",
                new NarrativeChallenge("La Madre Impietrita",
                        "Le lettere si accumulano. Sigillate. Il freddo di questa panchina è nulla in confronto al gelo del tuo silenzio. Continui a passare di qui, ciclo dopo ciclo, sperando che il tempo cancelli ciò che non hai mai voluto guardare.",
                        "Il peso di una busta mai aperta. Il sigillo è intatto, l'indifferenza eterna."),
                new RoomConfig(DOOR_X, DOOR_Y, "Una richiesta d'aiuto sepolta sotto il rumore del traffico fuori dalla finestra.", true, "backgrounds/bg_3.jpg", "doors/door_3.png", "fragments/fragment_3.png"), random));

        rooms.add(new Room(4, "Piano 5: L'Avarizia",
                "Il pavimento è coperto di monete di fumo. Un grande tabellone degli orari vuoto rotea furiosamente.",
                new CombatChallenge("Il Rimpianto del Giovane Operaio",
                        "La valigia di cuoio pesa, vero? È piena dei miei turni di notte. Corri verso il binario, ogni singola volta, ma il treno per la salvezza non arriva mai per chi ha le tasche gonfie del sangue altrui.",
                        "Mi hai rubato il futuro per comprarti vestiti su misura!",
                        "Corri sulla banchina... ma il peso della valigia ti trascina a fondo!",
                        "Credevo in te. Ho lavorato al buio solo per arricchire la tua ombra."),
                new RoomConfig(DOOR_X, DOOR_Y, "Sulla banchina, il peso del cuoio della valigia era l'unica cosa che ti sembrava reale.", true, "backgrounds/bg_4.jpg", "doors/door_4.png", "fragments/fragment_4.png"), random));

        rooms.add(new Room(5, "Piano 6: La Gola",
                "Un banchetto distorto coperto di cenere. In lontananza, una voce metallica annuncia un ritardo.",
                new SkillCheckChallenge("LA GENTILE ANZIANA:\n\"Un altro pasticcino? Un'altra firma sulla pensione? Hai divorato tutto quello che avevo, lasciandomi al freddo. Ogni volta che torni in questa stanza, il tuo vuoto è sempre più grande. Lancia il dado, vediamo se questa volta troverai la sazietà.\""),
                new RoomConfig(DOOR_X, DOOR_Y, "L'avidità di consumare. Un vuoto incolmabile, anche un istante prima dello schianto.", true, "backgrounds/bg_5.jpg", "doors/door_5.png", "fragments/fragment_5.png"), random));

        rooms.add(new Room(6, "Piano 7: Il Tradimento",
                "L'orologio digitale lampeggia rosso: 14:02. Un'ombra speculare, vestita con abiti identici ai tuoi, ha il volto coperto di sangue.",
                new CombatChallenge("Lo Spettro del Complice",
                        "14:02. Il fischio. Le tue mani sulla mia schiena. Mi usi come scudo sotto le ruote del treno, ciclo dopo ciclo. Ma cadiamo sempre insieme. L'orologio sta per scattare di nuovo. Pronti al prossimo impatto?",
                        "Mi hai spinto sotto le ruote! Il mio sangue è sui tuoi abiti!",
                        "14:02... lo senti il rumore dei freni che si spezzano?!",
                        "Eravamo complici fino all'ultimo secondo... prima della spinta."),
                new RoomConfig(DOOR_X, DOOR_Y, "14:02. Due mani che spingono. Uno scudo di carne per salvarti la vita. Non è bastato.", true, "backgrounds/bg_6.jpg", "doors/door_6.png", "fragments/fragment_6.png"), random));

        return rooms;
    }
}