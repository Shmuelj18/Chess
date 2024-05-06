import java.io.*;
import java.net.*;

public class ChessClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader stdIn;

    public ChessClient(String address, int port) throws IOException {
        socket = new Socket(address, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        stdIn = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start() {
        System.out.println("Connected to server. Enter commands:");
        new Thread(this::listenToServer).start();
        try {
            while (true) {
                String userInput = stdIn.readLine();
                out.println(userInput);
            }
        } catch (IOException e) {
            System.out.println("Error reading from user input: " + e.getMessage());
        }
    }

    private void listenToServer() {
        try {
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
            }
        } catch (IOException e) {
            System.out.println("Server connection lost: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        ChessClient client = new ChessClient("localhost", 5555);
        client.start();
    }
}
