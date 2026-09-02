Questo template ricalca esattamente la struttura pulita e leggibile del tuo esempio, adattandola alle meccaniche e alle tecnologie specifiche del tuo progetto.

```markdown
# Purgatorio RPG 

Purgatorio RPG è un'avventura narrativa ed esplorativa con visuale top-down. 
Il giocatore impersona un'entità spettrale intrappolata in una stazione ferroviaria avvolta nella nebbia, costretta ad affrontare i rimorsi del proprio passato tramite scelte morali (Karma) e combattimenti a turni basati sull'intelligenza emotiva.

![Screenshot del Gioco](inserisci_qui_link_immagine.png)

---

## 🚀 Come eseguire il progetto

### Prerequisiti
- Java 25 (LTS)
- Groovy
- Gradle

### Istruzioni

```bash
git clone [https://github.com/TuoUsername/PurgatorioRPG.git](https://github.com/TuoUsername/PurgatorioRPG.git)
cd PurgatorioRPG

```

### Build del progetto

```bash
./gradlew build

```

### Esecuzione

Per aggirare le problematiche legate ai moduli JavaFX, il punto di ingresso principale è situato in una classe separata:

```bash
./gradlew run

```

*(In alternativa, eseguire la classe `it.unicam.cs.mpgc.rpg129543.Launcher` direttamente da IntelliJ IDEA).*

---

## 🤖 Uso di strumenti di AI

L'uso dell'AI è stato mirato al refactoring architetturale, alla code-review e all'apprendimento delle best practice relative al Clean Code, mantenendo la paternità logica del progetto originale.

* Utilizzato Gemini per:
* Comprendere e applicare concetti teorici (Design Pattern, principi SOLID).


* Refactoring strutturale per eliminare "God Object" e disaccoppiare la logica dalla UI.


* Ottimizzazione della generazione casuale delle entità sulla mappa.



---

## ⚠️ Nota

Gli asset grafici (sprite del personaggio, sfondi delle stanze e frammenti) sono stati [inserisci qui la fonte degli asset, es. disegnati su Figma / presi da itch.io].

---

## 🎮 Funzionalità Presenti

* Movement System fluido da tastiera (W, A, S, D / Frecce) limitato entro la "Safe Zone" della mappa.
* Generazione procedurale delle posizioni di Boss e Frammenti (con controlli anti-sovrapposizione).
* Sistema di combattimento a turni con debolezze basate sugli stati emotivi (Rabbia, Paura, Colpa).
* Eventi stocastici e "Anomalie" durante la battaglia che offrono bivi morali.
* Zainetto Spirituale (Inventario) per la raccolta dei frammenti di lore.
* Finali multipli basati sul punteggio di Karma accumulato.

---

## ⚙️ Note Tecniche

Per questo progetto d'esame (Informatica per la Comunicazione Digitale) è stato fatto un uso rigoroso dell'architettura **MVC (Model View Controller)**, garantendo una netta separazione tra la logica di dominio e il rendering grafico in JavaFX.

Sono stati inoltre implementati:

* **Factory Pattern** (`RoomFactory`) per l'istanziazione modulare dei livelli.
* **DTO (Data Transfer Object)** (`ChallengeResult`) per far comunicare in modo sicuro Modello e Controller.
* Architettura a **Callback (Lambda)** per il routing dinamico dei menu.

---

## 📋 TODO

### In Sviluppo

* Copertura totale dei test di unità (JUnit 5) per il Modello (`Player`, `Room`, `BattleEngine`).
* Sistema di salvataggio/caricamento tramite `PersistenceManager` su formato JSON.

### Pianificati

* Aggiunta di un comparto sonoro e colonna sonora ambientale (tramite `AudioClip`).

### Possibili Miglioramenti Futuri

* Animazioni di attacco specifiche per ogni nemico.
* Nuovi piani esplorabili oltre al settimo.

```

```