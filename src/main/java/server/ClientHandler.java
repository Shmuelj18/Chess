package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import common.GameSession;
import common.Position;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private GameSession gameSession;  // Specific game session for this handler

    public ClientHandler(Socket socket, GameSession gameSession) {
        this.clientSocket = socket;
        this.gameSession = gameSession;  // Pass the specific game session when creating the handler
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String response = processCommand(inputLine);
                if (response != null) {
                    out.println(response);
                }
                if ("disconnect".equalsIgnoreCase(inputLine)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Exception in handling client #" + Thread.currentThread().getId() + ": " + e.getMessage());
        } finally {
            cleanUp();
        }
    }

    private void cleanUp() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error when closing client socket or its streams: " + e.getMessage());
        }
    }

    private String processCommand(String input) {
        // Adjust to use the specific game session
        if (input.startsWith("move")) {
            // Assuming input format: "move from to", e.g., "move e2 e4"
            String[] parts = input.split(" ");
            if (parts.length == 3) {
                return gameSession.makeMove(new Position(parts[1]), new Position(parts[2]), this.clientSocket.toString()) ? "Move successful" : "Invalid move";
            }
        } else if (input.equals("status")) {
            return gameSession.toString();
        }
        return "Unknown command";
    }
}
