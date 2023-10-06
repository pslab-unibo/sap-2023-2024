// server.js
const WebSocket = require('ws');
const express = require('express');
const app = express();

const wss = new WebSocket.Server({ noServer: true });

let javaWs;

function connectToJavaWebSocket() {
    javaWs = new WebSocket('ws://localhost:8080');

    javaWs.on('open', () => {
        console.log('Connected to Java WebSocket Server!');
    });

    javaWs.on('message', (data) => {
        console.log('Received from Java:', data);
        // Broadcast data to all connected clients
        wss.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(data.toString());
            }
        });
    });

    javaWs.on('error', (error) => {
        console.error('Error connecting to Java WebSocket Server:', error.message);
    });

    javaWs.on('close', () => {
        console.error('Connection to Java WebSocket Server closed, retrying in 2 seconds...');
        setTimeout(connectToJavaWebSocket, 2000);
    });
}

// Initially try to connect
connectToJavaWebSocket();

wss.on('connection', (ws) => {
    console.log('Client connected');

    ws.on('message', (message) => {
        console.log('Received:', message);
        if (message === 'requestUpdate') {
            // Check if the connection to the Java WebSocket server is open before sending a message
            if (javaWs && javaWs.readyState === WebSocket.OPEN) {
                javaWs.send('requestUpdate');
            } else {
                console.error('Cannot forward message, not connected to Java WebSocket Server');
            }
        }
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
