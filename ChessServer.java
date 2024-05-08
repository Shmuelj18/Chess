import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChessServer {
    private static final int PORT = 55551;
    private static ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chess Server is running...");
            while (true) {
                try {
                    System.out.println("Waiting for clients...");
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    pool.execute(clientHandler);
                } catch (IOException e) {
                    System.out.println("Error connecting to client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port " + PORT + ": " + e.getMessage());
        }
    }
}

class ClientHandler implements Runnable {
    private static ConcurrentHashMap<String, ClientHandler> handlers = new ConcurrentHashMap<>();
    private Socket clientSocket;
    private String username;
    private PrintWriter out;
    private BufferedReader in;
    private ClientHandler opponent;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            requestUsername();
        } catch (IOException e) {
            System.out.println("Error creating streams: " + e.getMessage());
        }
    }

    private void requestUsername() throws IOException {
        out.println("Enter your username:");
        String name = in.readLine().trim();
        while (name.isEmpty() || handlers.containsKey(name)) {
            if (handlers.containsKey(name)) {
                out.println("Username already taken, please choose another:");
            } else {
                out.println("Username cannot be empty, please enter a username:");
            }
            name = in.readLine().trim();
        }
        username = name;
        handlers.put(username, this);
        out.println("Welcome, " + username + "!");
        sendMenu();
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from " + username + ": " + inputLine);
                processInput(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Error handling client " + username + ": " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void sendMenu() {
        out.println("\nMenu:");
        out.println("  listPlayers - List all available players");
        out.println("  requestPlayer <username> - Request a game with player <username>");
        out.println("  acceptGame - Accept a pending game request");
        out.println("  rejectGame - Reject a pending game request");
        out.println("  disconnect - Disconnect from the server");
        out.println("  menu - Show this menu again");
        out.println("Enter a command:");
    }

    private void processInput(String input) throws IOException {
        switch (input) {
            case "listPlayers":
                listPlayers();
                break;
            case "acceptGame":
                if (opponent != null) {
                    opponent.out.println(username + " has accepted your game request.");
                    startGame();
                }
                break;
            case "rejectGame":
                if (opponent != null) {
                    opponent.out.println(username + " has rejected your game request.");
                    opponent = null;
                    out.println("Game request denied.");
                }
                break;
            case "disconnect":
                cleanup();
                break;
            case "menu":
                sendMenu();
                break;
            default:
                if (input.startsWith("requestPlayer ")) {
                    String requestedUsername = input.split(" ")[1];
                    requestPlayer(requestedUsername);
                } else {
                    out.println("Unknown command. Please try again. Type 'menu' for list of commands.");
                }
                break;
        }
    }

    private void listPlayers() {
        out.println("Available players:");
        handlers.forEach((key, handler) -> {
            if (!key.equals(username) && handler.opponent == null) {
                out.println("Player: " + key);
            }
        });
    }

    private void requestPlayer(String requestedUsername) {
        ClientHandler requestedHandler = handlers.get(requestedUsername);
        if (requestedHandler != null && !requestedUsername.equals(username) && requestedHandler.opponent == null) {
            this.opponent = requestedHandler;
            requestedHandler.opponent = this;
            requestedHandler.out.println(username + " wants to play with you. Accept (acceptGame) or Reject (rejectGame)?");
            out.println("Game request sent to " + requestedUsername + ". Waiting for response...");
        } else {
            out.println("Invalid username or user busy.");
        }
    }

    private void startGame() {
        out.println("Game started with " + opponent.username);
        opponent.out.println("Game started with " + username);
    }

    private void cleanup() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (opponent != null) {
                opponent.out.println(username + " has disconnected.");
                opponent.opponent = null;
            }
            handlers.remove(username);
            out.close();
            in.close();
            System.out.println(username + " has disconnected.");
        } catch (IOException e) {
            System.out.println("Error cleaning up " + username + ": " + e.getMessage());
        }
    }
}
