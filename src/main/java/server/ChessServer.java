package server;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class ChessServer {
    private static final int PORT = 55551;
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private GameManager gameManager;

    public ChessServer() {
        this.gameManager = new GameManager();
        this.pool = Executors.newCachedThreadPool();
    }

    public void startServer() {
        addShutdownHook();

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Chess Server is running on port " + PORT);

            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    String sessionId = gameManager.createNewSession();  // Method in GameManager to handle session creation
                    ClientHandler clientHandler = new ClientHandler(clientSocket, gameManager.getSession(sessionId));
                    pool.execute(clientHandler);
                } catch (IOException e) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port " + PORT + ": " + e.getMessage());
        }
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down the chess server...");
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing server socket: " + e.getMessage());
            }
            pool.shutdownNow();
            System.out.println("Server shut down.");
        }));
    }

    public static void main(String[] args) {
        ChessServer server = new ChessServer();
        server.startServer();
    }
}
