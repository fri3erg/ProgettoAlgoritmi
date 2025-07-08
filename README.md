# Progetto Forza 4 AI

Questo progetto implementa un algoritmo incompleto per giocare a **Forza 4** (conosciuto anche come Connect Four), utilizzando un approccio basato sull'**albero di mosse forzate**. L'obiettivo è sviluppare un'intelligenza artificiale in grado di prendere decisioni strategiche per vincere contro l'avversario.

---

## Stato del Progetto e Funzionalità Attuali

L'algoritmo è attualmente in fase di sviluppo e presenta le seguenti aree chiave:

* **Valutazione delle Mosse:** L'AI valuta le possibili mosse basandosi su vari criteri, tra cui:
  * **Vittoria Immediata (Codice 104):** Identifica e gioca mosse che portano a una vittoria immediata.
  * **Sconfitta Immediata (Codice 103):** Controlla se la mossa dell'avversario può portare a una sua vittoria immediata, permettendo all'AI di bloccarla.
  * **Mosse Forzate (Codice 101/102):** Analizza le sequenze di mosse che possono forzare una vittoria (o impedire una sconfitta) entro un numero limitato di turni.

---

## Known Issues e Prossimi Sviluppi

Il progetto presenta alcune **problematiche note** e aree su cui si concentreranno i prossimi sviluppi:

* **Logica di Valutazione delle Mosse Forzate:** Attualmente, la logica per la "vittoria immediata avversaria" (103) e le "mosse forzate" (101) non distingue adeguatamente se la board analizzata include o meno il segno del giocatore corrente. Per le mosse forzate (101), la board analizzata tecnicamente dovrebbe essere quella *dopo* la nostra mossa, non prima.
* **Analisi Profonda dell'Albero di Gioco:** L'algoritmo non esamina ancora sufficientemente in profondità l'albero delle mosse, specialmente per le configurazioni che si estendono oltre la posizione dell'ultimo pezzo inserito.
* **Prevenzione di Vittorie Accidentali:** È necessario implementare meccanismi robusti per assicurarsi che l'AI non faccia mosse che, involontariamente, portino alla vittoria dell'avversario.
* **Strategie di Ricerca:**
  * Si valuta l'implementazione di una ricerca in profondità (DFS) per l'analisi avanzata delle mosse, data la complessità di una ricerca in ampiezza (BFS) per questo contesto.
  * Sarà importante determinare se l'analisi "generale" delle mosse si concentrerà solo sulla prima mossa o se esaminerà sequenze più complesse.
* **Gestione del Tempo:** Si definiranno strategie per l'allocazione del tempo di calcolo, bilanciando l'analisi approfondita con la reattività dell'AI.
* **Ottimizzazione delle Operazioni:** Valutare se "smarcare" i nodi è più efficiente che copiare l'intera board per ogni simulazione.
* **Hashmap per la Memorizzazione:** Come suggerito, l'uso di Hashmap per memorizzare stati della board già analizzati potrebbe migliorare significativamente le performance, evitando ricalcoli inutili.

---

## Contributi e Contatti

Questo è un progetto incompleto da riprendere in futuro.
