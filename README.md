# Purgatorio RPG

**Progetto d'esame per Informatica per la Comunicazione Digitale**
*Università degli Studi di Camerino*
*Sviluppato da: Nicoletta Ciaffoni (Matricola: 129543)*

Purgatorio RPG è un'avventura narrativa ed esplorativa con visuale top-down. Il giocatore impersona un'entità spettrale intrappolata, costretta ad affrontare i rimorsi del proprio passato tramite scelte morali (Karma) e combattimenti a turni basati sull'intelligenza emotiva.

<p align="center">
<img width="798" height="647" alt="Gameplay Purgatorio RPG" src="https://github.com/user-attachments/assets/98ad51b7-972b-4a24-8071-b3f128105acf" />
</p>

---

## Come eseguire il progetto

### Prerequisiti
* Java 25 (LTS)
* Gradle

### Istruzioni

```bash
git clone https://github.com/iRqven/progetto-rpg-129543.git
cd progetto-rpg-129543
```

### Build ed Esecuzione

Per aggirare le problematiche legate ai moduli JavaFX, il punto di ingresso principale è situato in una classe separata (`Launcher`):

```bash
./gradlew build
./gradlew run
```

*(In alternativa, eseguire la classe `it.unicam.cs.mpgc.rpg129543.Launcher` direttamente da IntelliJ IDEA).*

---

## Uso di strumenti di AI

L'uso dell'AI è stato mirato al refactoring architetturale, alla code-review e all'apprendimento delle best practice relative al Clean Code e ai principi SOLID, mantenendo la paternità logica e le scelte di design del progetto originale.

Sono stati utilizzati modelli linguistici di intelligenza artificiale per:

* Comprendere e applicare concetti teorici avanzati (Design Pattern, principi SOLID).
* Effettuare code-review e refactoring strutturale per eliminare l'anti-pattern "God Object" (es. suddivisione di `GameRouter`), disaccoppiare la logica di dominio dalla UI e sostituire il type-checking con enum e polimorfismo (`ChallengeType`).
* Individuare bug concreti durante la revisione.
* Ottimizzare la generazione casuale e procedurale delle entità sulla mappa, introducendo l'astrazione `RandomSource` per la testabilità.

Una dichiarazione dettagliata, con l'indicazione precisa di quale strumento è stato usato per quale parte del lavoro, è disponibile nella Wiki del repository, come richiesto dalla consegna.

---

## Nota sugli Asset

Gli asset grafici (sprite del personaggio, sfondi delle stanze e frammenti) sono stati generati tramite intelligenza artificiale (Gemini), seguendo rigorosamente il worldbuilding narrativo e le atmosfere del gioco.

---

## Funzionalità Presenti

* **Movement System:** Movimento fluido da tastiera (W, A, S, D / Frecce) limitato entro la "Safe Zone" della mappa.
* **Generazione procedurale:** Calcolo stocastico per lo spawn di Boss e Frammenti con rigidi controlli anti-sovrapposizione.
* **Sistema di combattimento emotivo:** Battaglie a turni con debolezze basate sugli stati emotivi (Rabbia, Paura, Colpa).
* **Anomalie e Bivi Morali:** Eventi casuali durante la battaglia che offrono scelte narrative.
* **Zainetto Spirituale:** Inventario per la raccolta dei frammenti di lore. Il ritrovo di memorie già note innesca una *Risonanza Spirituale* che cura il giocatore.
* **Persistenza JSON:** Sistema di salvataggio e caricamento automatico delle statistiche del giocatore.
* **Dannazione Ciclica:** Finali multipli basati sul Karma. La mancanza di redenzione innesca un loop in cui le statistiche vengono mantenute ma la difficoltà dei boss scala.

---

## Note Tecniche e Architettura

Per questo progetto d'esame è stato fatto un uso rigoroso dell'architettura **MVC (Model-View-Controller)** e dei principi **SOLID**, garantendo una netta separazione tra la logica di dominio e il rendering grafico in JavaFX.

Sono stati implementati i seguenti pattern e metodologie:

* **Router Pattern:** `GameRouter` centralizza la navigazione tra le schermate e la gestione degli overlay è a sua volta delegata a `OverlayManager`, per rispettare la Single Responsibility.
* **Factory Pattern (`RoomFactory`):** Istanziazione modulare dei livelli e iniezione della narrativa.
* **DTO (Data Transfer Object):** Classi come `ChallengeResult` e record come `RoomConfig` trasferiscono dati in modo pulito tra Modello e Controller senza formattazione visiva.
* **Dependency Inversion:** Anziché far comunicare la logica di dominio direttamente con le finestre visive o con la generazione procedurale, sono state introdotte delle interfacce intermedie (`RandomSource`, `BattleCallbacks`, `GameNavigationCallbacks`). Questo garantisce che i Controller e i Model non sappiano nulla di JavaFX, facilitando enormemente i test unitari e le modifiche future.
* **Eliminazione del Type Checking:** Adozione dell'enum `ChallengeType` al posto di flag booleani, per una gestione polimorfica delle sfide.
* **Test Unitari (JUnit 5):** Copertura dei comportamenti fondamentali del Modello (`Player`, `Enemy`, `Room`), resa possibile e deterministica grazie all'iniezione della dipendenza `RandomSource`.

### Estendibilità

La logica di dominio (package `model` e `controller`) non dipende da JavaFX se non nel punto di contatto con la `view`, tramite le interfacce di callback sopra citate. Questo rende possibile in futuro sostituire l'interfaccia grafica (ad esempio con un client mobile o web) riutilizzando model e controller così come sono.

---

## TODO & Sviluppi Futuri

### Pianificati

* Aggiunta di un comparto sonoro e colonna sonora ambientale (tramite `AudioClip`).

### Possibili Miglioramenti Futuri

* Animazioni di attacco specifiche per ogni nemico.
* Nuovi piani esplorabili oltre al settimo.
