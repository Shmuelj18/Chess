import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ChessServer {
    private static final int PORT = 55551;
    private static ExecutorService pool = Executors.newCachedThreadPool();
    static AtomicInteger clientCounter = new AtomicInteger(0);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Chess Server is running...");

        while (true) {
            try {
                System.out.println("Waiting for clients...");
                Socket clientSocket = serverSocket.accept();
                
                int clientNumber = clientCounter.incrementAndGet();  // Get the client number
                pool.execute(new ClientHandler(clientSocket, clientNumber)); // Pass handlers array to the constructor
                System.out.println("Client " + clientNumber + " connected.");
            } catch (IOException e) {
                System.out.println("Error connecting to client: " + e.getMessage());
            }
        }
    }

    public static synchronized int getClientCounter() {
        return clientCounter.get();
    }

    public static synchronized void decrementClientCounter() {
        clientCounter.decrementAndGet();
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private int clientNumber;
    private boolean inGame = false;
    private boolean isWaiting = false;
    private ClientHandler opponent;
    public static Stack<ClientHandler> handlers = new Stack<>();

    public ClientHandler(Socket clientSocket, int clientNumber) {
        this.clientSocket = clientSocket;
        this.clientNumber = clientNumber;
        handlers.push(this);
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            if (!isWaiting) {
                out.println("You are in position " + clientNumber + " in the queue.");
                isWaiting = true;
            }

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client " + clientNumber + ": " + inputLine);
                if (inputLine.equals("requestGame")) {
                    requestGame(out);
                } else if (inputLine.equals("acceptGame") && opponent != null && !opponent.isInGame()) {
                    startGame(out, opponent.getOut());
                } else if (inputLine.equals("rejectGame") && opponent != null) {
                    opponent.getOut().println("Game request rejected.");
                } else if (inputLine.equals("listPlayers") && isWaiting) {
                    listPlayers(out);
                    System.out.println("Received");
                } else if (inputLine.startsWith("requestPlayer") && isWaiting) {
                    String[] tokens = inputLine.split(" ");
                    int playerNumber = Integer.parseInt(tokens[1]);
                    requestPlayer(out, playerNumber);
                } else {
                    System.out.println("Invalid command: " + inputLine);
                }
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                if (opponent != null) {
                    opponent.setOpponent(null);
                }
                ChessServer.decrementClientCounter();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void listPlayers(PrintWriter out) throws IOException {
        for (ClientHandler handler : ClientHandler.handlers) {
            if (handler != null && handler.isWaiting() && handler != this) {
                out.println("Player " + handler.getClientNumber());
            }
        }
    }
    
    public void requestPlayer(PrintWriter out, int playerNumber) {
        ClientHandler handler = null;
        // Iterate over the stack to find the ClientHandler with the specified playerNumber
        for (ClientHandler clientHandler : handlers) {
            if (clientHandler.getClientNumber() == playerNumber) {
                handler = clientHandler;
                break;
            }
        }
    
        if (handler != null && handler.isWaiting() && handler != this) {
            try {
                handler.getOut().println("Game request from player " + clientNumber + ". Do you accept? (yes/no)");
                opponent = handler;
            } catch (IOException e) {
                System.out.println("Error sending message to client: " + e.getMessage());
            }
        } else {
            out.println("Invalid player number.");
        }
    }
    
    public void setOpponent(ClientHandler opponent) {
        this.opponent = opponent;
    }
    
    public void startGame(PrintWriter out1, PrintWriter out2) {
        // Logic for starting the game between two players
        // This could involve sending initial game state or messages to both players
        // For now, let's just print a message indicating the game has started
        out1.println("Game started!");
        out2.println("Game started!");
    }

    public PrintWriter getOut() throws IOException {
        return new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public boolean isInGame() {
        return inGame;
    }

    public void requestGame(PrintWriter out) {
        // Logic for requesting a game with another player
        // This could involve sending a message to the other player or updating game state
        // For now, let's just print a message indicating the request
        out.println("Game requested. Waiting for response...");
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public int getClientNumber() {
        return clientNumber;
    }
}