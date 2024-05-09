import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
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
    private boolean startGame;
    //Stack<ChessRules> gameHolder = new Stack<>();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        startGame = false;
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
                OnTurn empty = new OnTurn();
                
                if(startGame==true){
                        OnTurn moveControl = new OnTurn(this,this.opponent);
                        processInput(inputLine,moveControl);
                }else{
                    processInput(inputLine,empty);
                }
            }
        } catch (IOException e) {
            System.out.println("Error handling client " + username + ": " + e.getMessage());
        } 
        finally {
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

    private void processInput(String input, OnTurn moveControl) throws IOException {
        while (input.length()!=100000) {
            while(input.length()==4 &&canConvertToInt(input)){
                if(startGame == true){
                        moveControl.allTurns(input);
                        this.out.println("\n"+moveControl.printBoard());
                        opponent.out.println("\n"+moveControl.printBoard()+"\ninput coordinates");
                        if(moveControl.game.isCheckmate()==true){
                            startGame=false;
                        }
                        break;
                }else{
                    out.println("move must be inputted as coordinates");
                    break;
                }
                
            }
            if (input.startsWith( "listPlayers")){
                listPlayers();
                break;
            }
            if (input.startsWith("acceptGame")){
                if (opponent != null) {
                    opponent.out.println(username + " has accepted your game request.");
                    startGame(true);
                }
                break;
            }
            if (input.startsWith("rejectGame")){
                if (opponent != null) {
                    opponent.out.println(username + " has rejected your game request.");
                    opponent = null;
                    out.println("Game request denied.");
                }
                break;
            }
            if (input.startsWith("disconnect")){
                cleanup();
                break;
            }
            if (input.startsWith("menu")){
                sendMenu();
                break;
            }
            if (input.startsWith("requestPlayer ")) {
                String requestedUsername = input.split(" ")[1];
                requestPlayer(requestedUsername);
                break;
            }
        
            else {
                    out.println("Unknown command. Please try again. Type 'menu' for list of commands.");
                    break; 
            } 
            
                
            
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

    private void startGame(boolean start) {
        String temp = "\n to make a move enter the coordinates of the piece like this\n  x,y(old location)->i,j(new location) as xyij\n"+
        "\n"+ this.username+" is white & "+ this.opponent.username+ " is black \n white goes first";
        out.println("Game started with " + opponent.username+"\n"+temp);
        opponent.out.println("Game started with " + username+"\n"+temp);
       // createPush(username);
       startGame = start;
       this.out.println("input coordinates\n"+startBoard());
       this.opponent.out.println(startBoard());
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
    

    public static boolean canConvertToInt(String input) {
        try {
            Integer.parseInt(input);
            return true; // If no exception is thrown, the conversion is successful
        } catch (NumberFormatException e) {
            return false; // If a NumberFormatException is caught, the conversion failed
        }
    }

    public void createPush(String username){
        Map<String, ChessRules> chessRulesMap = new HashMap<>();
        chessRulesMap.put(username, new ChessRules(true));
        //gameHolder.push(chessRulesMap.get(username));
    }

    private String startBoard(){
        char[][] board = new char[][]{
            {'0', '1', '2', '3', '4', '5', '6', '7', '8'},
            {'1', 'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
            {'2', 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
            {'3', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {'4', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {'5', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {'6', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {'7', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
            {'8', 'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };
        String gameBoard ="";
        for (int i = 0; i < 9; i++) {
            if(i>0){
                gameBoard = gameBoard+"\n";
            }
            for (int j = 0; j < 9; j++) {
                gameBoard = gameBoard+(board[i][j] + " ");
            }
        }
        return gameBoard;
    }

}

