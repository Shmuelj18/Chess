import java.io.*;
import java.net.*;

public class ChessClient {
    private static final String SERVER_ADDRESS = "192.168.40.224";  // Server IP address
    private static final int SERVER_PORT = 55551;  // Server port

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server. Type \"disconnect\" to leave.");

            // Thread to read messages from the server
            Thread readerThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println("Server says: " + serverResponse);
                    }
                } catch (IOException e) {
                    System.out.println("Server connection lost.");
                }
            });
            readerThread.start();

            // Sending messages to the server
            String userInput;
            while ((userInput = stdIn.readLine()) != null && !userInput.equalsIgnoreCase("disconnect")) {
                out.println(userInput);
            }

            // Clean up on disconnect
            readerThread.interrupt();
            System.out.println("Disconnected from server.");
        } catch (IOException e) {
            System.out.println("Could not connect to the server at " + SERVER_ADDRESS + ":" + SERVER_PORT + ": " + e.getMessage());
        }
    }
}