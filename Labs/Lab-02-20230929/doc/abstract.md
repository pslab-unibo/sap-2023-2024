# Descrizione Runtime del Sistema

Il sistema è composto principalmente da quattro componenti: `User Trigger`, `Java Application`, `Java WebSocket Server` e `Node.js WebSocket Server con WebClient`. Di seguito è fornita una descrizione dettagliata del funzionamento di ciascun componente durante il runtime.

## 1. User Trigger
`User Trigger` rappresenta l'input dell'utente finale nel sistema.

- **Interazione con MyInputUI:**
    - L'utente interagisce con l'interfaccia grafica fornita da `MyInputUI`.
    - `User Trigger` rappresenta concettualmente questa interazione.

## 2. Java Application
`Java Application` è il cuore dell'applicazione, responsabile della logica principale.

### MyInputUI
- Rileva input forniti dall'utente attraverso l'interfaccia grafica (UI).
- Notifica `MyController` riguardo al nuovo input ricevuto.

### MyController
- Elabora l'input ricevuto da `MyInputUI`.
- Comunica con `MyModel` per aggiornare lo stato dell'applicazione.

### MyModel
- Aggiorna il suo stato quando riceve istruzioni da `MyController`.
- Notifica `MyView` che lo stato dell'applicazione è cambiato.

### MyView
- Riceve notifiche da `MyModel` e aggiorna la visualizzazione a schermo per l'utente.

## 3. Java WebSocket Server
`Java WebSocket Server` agisce come ponte tra l'applicazione Java e il server Node.js.

### MyWebSocketView
- Riceve notifiche da `MyModel` per gli aggiornamenti di stato.
- Invia aggiornamenti al server WebSocket Node.js attraverso `MyWebSocket`.

### MyWebSocket
- Gestisce la connessione WebSocket e la trasmissione dei dati tra `MyWebSocketView` e il server Node.js.

## 4. Node.js WebSocket Server e WebClient
Questo componente funge da intermediario e gestisce la visualizzazione lato client.

### Server (server.js)
- Riceve aggiornamenti da `MyWebSocket` e li inoltra al `WebClient`.
- Gestisce le connessioni WebSocket con il client web e il server WebSocket Java.

### WebClient (index.html)
- Riceve aggiornamenti dal server Node.js e aggiorna la visualizzazione nel browser per gli utenti finali.

## Flusso Operativo
1. `User Trigger` interagisce con `MyInputUI`.
2. `MyController` elabora l'input e aggiorna `MyModel`.
3. `MyModel` notifica `MyView` e `MyWebSocketView` degli aggiornamenti.
4. `MyWebSocketView` invia aggiornamenti a `Server` (Node.js).
5. `Server` inoltra gli aggiornamenti a `WebClient`.
6. `WebClient` visualizza gli aggiornamenti all'utente nel browser.

In questo modo, il sistema assicura che gli input dell'utente siano elaborati e che lo stato aggiornato dell'applicazione sia visualizzato tempestivamente sia nell'interfaccia utente Java che nel client web.
