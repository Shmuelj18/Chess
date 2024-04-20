import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ChessServer {
    private static final int PORT = 55555;
    private static ExecutorService pool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Chess Server is running...");

        while (true) {
            try {
                System.out.println("Waiting for clients...");
                Socket client1 = serverSocket.accept();
                System.out.println("Client 1 connected.");

                Socket client2 = serverSocket.accept();
                System.out.println("Client 2 connected.");

                pool.execute(new ChessGame(client1, client2));
            } catch (IOException e) {
                System.out.println("Error connecting to client: " + e.getMessage());
            }
        }
    }
}

class ChessGame implements Runnable {
    private Socket player1;
    private Socket player2;

    public ChessGame(Socket player1, Socket player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    public void run() {
        // Handle the chess game between player1 and player2
        try {
            PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
            BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));

            PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);
            BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));

            String inputLine;
            // Simple communication protocol for example
            while ((inputLine = in1.readLine()) != null) {
                out2.println(inputLine);  // Relay move from player1 to player2
                inputLine = in2.readLine();
                out1.println(inputLine);  // Relay move from player2 to player1
            }
        } catch (IOException e) {
            System.out.println("Error in game between players: " + e.getMessage());
        } finally {
            try {
                player1.close();
                player2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
