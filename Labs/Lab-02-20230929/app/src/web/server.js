// server.js
const WebSocket = require('ws');
const express = require('express');
const app = express();

const wss = new WebSocket.Server({ noServer: true });

wss.on('connection', (ws) => {
    console.log('Client connected');

    ws.on('message', (message) => {
        console.log('Received:', message);
        if (message === 'requestUpdate') {
            // Forward this to the Java WebSocket server
            javaWs.send('requestUpdate');
        }
    });

    javaWs.on('message', (data) => {
        ws.send(data.toString());  // Forward data to the client
    });
});

const server = app.listen(3000, () => {
    console.log('Listening on port 3000');
});

server.on('upgrade', (request, socket, head) => {
    wss.handleUpgrade(request, socket, head, (ws) => {
        wss.emit('connection', ws, request);
    });
});

app.use(express.static('public')); // Serve static files from 'public' folder
