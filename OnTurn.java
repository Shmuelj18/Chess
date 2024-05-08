public class OnTurn {
    ClientHandler white;
    ClientHandler black;
    ChessRules game;
    public OnTurn(ClientHandler wHite, ClientHandler bLack, ChessRules gAme){
        white = wHite;
        black = bLack;
        game = gAme;
    }

  
    public int convertValue(String temp, int i){
        int j = Character.getNumericValue(temp.charAt(i));
        return j;
    }

    public String blackTurn(String coordinate){
         if(game.isCheckmate() == false) {
            return game.onWhiteTurn(game, convertValue(coordinate, 1), convertValue(coordinate, 2),
            convertValue(coordinate, 3), convertValue(coordinate, 4));
         }
         else {
            return ("Game Over white is victorious");
         }
    }
    public String whiteTurn(String coordinate){
        if(game.isCheckmate() == true) {
           return game.onWhiteTurn(game, convertValue(coordinate, 1), convertValue(coordinate, 2),
           convertValue(coordinate, 3), convertValue(coordinate, 4));
        }
        else {
           return ("Game Over black is victorious");
        }
   }
    
}
