package server.services.listGames;

/**
 * This class is for storing data about a single game to be represented as JSON output from ListGames
 * Implemented in listGamesResponse as ArrayList<Game> games
 */
public class Game {
    public String gameID, whiteUsername, blackUsername, gameName;

    Game(String gameID, String whiteUsername, String blackUsername, String gameName){
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
    }

    public String getGameID() {
        return gameID;
    }
    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }
    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }
    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getGameName() {
        return gameName;
    }
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * @return the JSON string of the game
     */
    @Override
    public String toString(){
        return "{\"gameID\": \"" + gameID + "\", " +
                "\"whiteUsername\": \"" + whiteUsername + "\", " +
                "\"blackUsername\": \"" + blackUsername + "\", " +
                "\"gameName\": \"" + gameName + "\"}";
    }
}
