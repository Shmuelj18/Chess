public class OnTurn {
    ClientHandler white;
    ClientHandler black;
    ChessRules game;
    public OnTurn(ClientHandler wHite, ClientHandler bLack){
        white = wHite;
        black = bLack;
        game = new ChessRules(true);
      }

    public OnTurn(){
    }

    public String allTurns(String coordinate){
        int[] ints = convertValue(coordinate);
        if(game.matchUpdate()==true){
          return game.onWhiteTurn(game, ints[0], ints[1],ints[2], ints[3]);
        }
        if(game.matchUpdate()==false){
          return game.onBlackTurn(game, ints[0], ints[1],ints[2], ints[3]);
        }
      else return "error in game play";
    }
   
    public int[] convertValue(String input) {
      int[] ints = new int[input.length()];
      for (int i = 0; i < input.length(); i++) {
          ints[i] = Character.getNumericValue(input.charAt(i));
      }
      return ints;
    }
     public String printBoard(){
      return game.returnBoard();
     }
    
}