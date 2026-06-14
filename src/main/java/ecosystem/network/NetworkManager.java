package ecosystem.network;

import ecosystem.engine.Environment;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class NetworkManager {
    private final Environment env;

    public NetworkManager(Environment env) {
        this.env = env;    }

    private static final int PORT = 8080;

    private volatile boolean running = false;
    private ServerSocket serverSocket;
    private ExecutorService clientPool;
    private Thread serverThread;

    // Start the server: spawns a background daemon thread that accepts connections
    public synchronized void startServer() {
        if (running) {
            System.out.println("Network server already running");
            return;
        }

        running = true;
        clientPool = Executors.newCachedThreadPool();

        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                serverSocket.setReuseAddress(true);
                System.out.println("Server is listening on port " + PORT);

                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept(); // blocking
                        // Handle client in separate thread so accept() can continue
                        clientPool.submit(() -> handleClient(clientSocket));
                    } catch (SocketException se) {
                        // SocketException often thrown when serverSocket.close() is called during shutdown
                        if (running) {
                            System.err.println("Socket exception in accept loop:");
                            se.printStackTrace();
                        } else {
                            // expected during shutdown; break out
                        }
                    } catch (IOException e) {
                        // Log and continue; do not let one accept error kill the server loop
                        System.err.println("I/O error accepting connection:");
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to create ServerSocket on port " + PORT);
                e.printStackTrace();
            } finally {
                // Ensure resources are cleaned up if thread exits
                shutdownResources();
            }
        }, "NetworkManager-ServerThread");

        serverThread.setDaemon(true); // won't prevent JVM from exiting
        serverThread.start();
    }

    // Gracefully stop the server and all client handlers
    public synchronized void stopServer() {
        if (!running) {
            return;
        }
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // will cause accept() to throw and loop to exit
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket:");
            e.printStackTrace();
        }
        if (clientPool != null) {
            clientPool.shutdownNow();
        }
        System.out.println("Network server stopped");
    }

    // Clean-up helper used in finally block
    private void shutdownResources() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // ignore during shutdown
        }
        if (clientPool != null) {
            clientPool.shutdownNow();
        }
    }

    // Each client handler: read all text lines until client closes stream
    private void handleClient(Socket clientSocket) {
        try (Socket socket = clientSocket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String incomingMessage;
            while ((incomingMessage = reader.readLine()) != null) {
                try {
                    EntityMessage msg = EntityMessage.parse(incomingMessage);
                    NetworkCommand command = NetworkCommandParser.parse(msg);
                    if (command != null) {
                        boolean accepted = env.submitAction(new NetworkSimulationAction(command));
                        if (!accepted) {
                            System.err.println("Environment action queue refused command: " + command);
                        } else {
                            System.out.println("Enqueued network command: " + command);
                        }
                    } else {
                        System.err.println("Unknown or unsupported network action: " + msg.getAction());
                    }
                } catch (IllegalArgumentException ex) {
                    System.err.println("Invalid protocol message: " + incomingMessage + " -> " + ex.getMessage());
                }
            }
            // client closed the connection
        } catch (IOException e) {
            System.err.println("Error handling client connection:");
            e.printStackTrace();
        }
    }

    // Existing sendEntity method left functionally the same but with try-with-resources
    public void sendEntity(String targetIP, String entityData) {
        try (Socket socket = new Socket(targetIP, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(entityData);
            System.out.println("Successfully sent entity to " + targetIP);

        } catch (IOException e) {
            System.err.println("Failed to send entity to " + targetIP);
            e.printStackTrace();
        }
    }
}