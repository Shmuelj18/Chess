import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChessServer {
    private ServerSocket serverSocket;
    private ExecutorService pool = Executors.newFixedThreadPool(10);
    private List<ClientHandler> clients = new ArrayList<>();

    public ChessServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
    }

    public void start() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.out.println("Error accepting connection: " + e.getMessage());
        }
    }

    public synchronized void listAllClients(ClientHandler requester) {
        StringBuilder sb = new StringBuilder("List of all connected clients:\n");
        for (ClientHandler client : clients) {
            sb.append(client.getClientId()).append("\n");  // Assuming getClientId returns a unique identifier or name
        }
        requester.sendMessage(sb.toString());
    }

    public synchronized List<ClientHandler> getWaitingClients() {
        List<ClientHandler> waitingClients = new ArrayList<>();
        for (ClientHandler client : clients) {
            if (client.isWaiting()) {
                waitingClients.add(client);
            }
        }
        return waitingClients;
    }

    public synchronized void sendAllWaitingClients(ClientHandler requester) {
        List<ClientHandler> waitingClients = getWaitingClients();
        for (ClientHandler client : waitingClients) {
            if (client != requester) {
                client.sendMessage("Player " + requester.getClientId() + " is available to play");
            }
        }
    }

    public synchronized void requestGame(String requesterId, String targetId) {
        for (ClientHandler client : clients) {
            if (client.getClientId().equals(targetId)) {
                client.sendMessage("Game request from: " + requesterId);
                return;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 5555; // Choose your port
        ChessServer server = new ChessServer(port);
        server.start();
    }
}
