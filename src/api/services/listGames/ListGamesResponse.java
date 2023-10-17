package api.services.listGames;

import api.services.base.Response;

import java.util.ArrayList;

/**
 * The Response object of the ListGamesService. Should return a list of all games, if not, returns an error and an error message.
 */
public class ListGamesResponse extends Response {
    ArrayList<Game> games = new ArrayList<>();

    /**
     * Constructor for a success response
     * @param games - an array with data of all games, each wrapped up in a Games object
     */
    ListGamesResponse(ArrayList<Game> games){
        super(200);
        this.games = games;
    }

    /**
     * Constructor for a fail response (uses base class implementation)
     * @param code - specific error code
     * @param description - optional - if there's an error description (error code 500)
     */
    ListGamesResponse(int code, String description){
        super(code, description);
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }

    public void addGame(Game game) {
        this.games.add(game);
    }
}
