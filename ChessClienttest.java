import java.io.*;
import java.net.*;

public class ChessClienttest {
    private static final String SERVER_ADDRESS = "localhost";  // Use your server IP if different
    private static final int SERVER_PORT = 55551;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        System.out.println("Connected to server.");
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        // Read the position in the queue from the server
        String positionInQueue = in.readLine();
        System.out.println("You are in position " + positionInQueue + " in the queue.");

        // Thread to read messages from the server
        new Thread(() -> {
            try {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    System.out.println("Server says: " + serverResponse);
                }
            } catch (IOException e) {
                System.out.println("Server connection lost.");
            }
        }).start();

        // Sending messages to the server
        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
        }

        socket.close();
        out.close();
        in.close();
        stdIn.close();
    }
}
