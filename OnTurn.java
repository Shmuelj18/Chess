public class OnTurn {
    ClientHandler white;
    ClientHandler black;
    ChessRules game;
    public OnTurn(ClientHandler wHite, ClientHandler bLack, ChessRules gAme){
        white = wHite;
        black = bLack;
        game = gAme;
      }
    public OnTurn(){
    }
    public int[] convertValue(String input) {
      int[] ints = new int[input.length()];
      for (int i = 0; i < input.length(); i++) {
          ints[i] = Character.getNumericValue(input.charAt(i));
      }
      return ints;
      }

    public String blackTurn(String coordinate){
         if(game.isCheckmate() == false) {
           int[] ints = convertValue(coordinate);
           return game.onBlackTurn(game,ints[0],ints[1],ints[2],ints[3]);
         }
         else {
            return ("Game Over white is victorious" + game.returnBoard());
         }
      } 
    public String whiteTurn(String coordinate){
        if(game.isCheckmate() == true) {
            int[] ints = convertValue(coordinate);
           return game.onWhiteTurn(game, ints[1], ints[2],
           ints[3], ints[4]);
        }
        else {
           return ("Game Over black is victorious" + game.returnBoard());
        }
      }
    
}
