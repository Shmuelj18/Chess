public class ClientHandler implements Runnable {
    private Socket socket;
    private ChessServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String clientId;
    private boolean isWaiting = false;

    public ClientHandler(Socket socket, ChessServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println("Please enter your name:");
        this.clientId = in.readLine();  // Set client name as client ID
        sendMessage("Welcome " + clientId + "! You are now connected.");
        printCommands();
    }

    private void printCommands() {
        sendMessage("Commands:\nLOGIN\nWAIT\nLIST\nREQUEST <player name>");
    }

    public void run() {
        try {
            while (true) {
                String input = in.readLine();
                if (input != null) {
                    processCommand(input);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Couldn't close a socket, what's going on?");
            }
        }
    }

    private void processCommand(String input) {
        String command = input.trim().toUpperCase(); // Normalize input to upper case
        if (command.contains(" ")) {
            // If the command includes an argument, split and normalize only the command part
            int firstSpaceIndex = command.indexOf(" ");
            command = command.substring(0, firstSpaceIndex);
            String argument = input.substring(firstSpaceIndex).trim();
            switch (command) {
                case "REQUEST":
                    server.requestGame(clientId, argument.toLowerCase()); // Normalize name to lower case
                    break;
                default:
                    processSimpleCommand(command);
            }
        } else {
            processSimpleCommand(command);
        }
    }

    private void processSimpleCommand(String command) {
        switch (command) {
            case "LOGIN":
                // handle login
                break;
            case "WAIT":
                isWaiting = true;
                server.sendAllWaitingClients(this);
                sendMessage("Added to waiting list.");
                break;
            case "LIST":
                server.listAllClients(this);
                break;
            default:
                sendMessage("Unknown command.");
                break;
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
    

    public boolean isWaiting() {
        return isWaiting;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getClientId() {
        return clientId;
    }
}