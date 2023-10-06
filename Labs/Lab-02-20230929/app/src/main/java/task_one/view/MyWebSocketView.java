package task_one.view;

import task_one.model.ModelObserver;
import task_one.model.ModelObserverSource;
import task_one.utils.MyWebSocket;
import java.util.logging.Logger;

public class MyWebSocketView implements ModelObserver {
    private static final Logger logger = Logger.getLogger(MyWebSocketView.class.getName());
    private final ModelObserverSource model;
    private static MyWebSocket webSocketServer;
    private final int port;

    public MyWebSocketView(ModelObserverSource model, int port) {
        this.model = model;
        this.port = port;
        model.addObserver(this);
    }

    public void start() {
        startWebSocketServer(port);
    }

    private static void startWebSocketServer(int port) {
        webSocketServer = new MyWebSocket(port);
        // Binding WebSocket to the specified port
        webSocketServer.start();
        logger.info("WebSocket Server started on port: " + webSocketServer.getPort());

        // Add shutdown hook to gracefully close WebSocket server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down WebSocket Server...");
            try {
                webSocketServer.stop();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Override
    public void notifyModelUpdated() {
        webSocketServer.broadcast("State: " + model.getState());
    }
}
