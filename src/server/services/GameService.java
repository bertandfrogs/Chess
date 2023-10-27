package server.services;

import chess.interfaces.ChessGame;
import server.ServerException;
import server.dataAccess.DataAccess;
import server.dataAccess.DataAccessException;
import server.models.GameData;

import java.util.Collection;
import java.util.Objects;

public class GameService extends Service {
    private DataAccess dataAccess;

    public GameService(DataAccess data) {
        dataAccess = data;
    }

    public Collection<GameData> listGames() {
        return dataAccess.listGames().values();
    }

    public GameData createGame(String gameName) {
        return dataAccess.createGame(gameName);
    }

    public GameData joinGame(String username, ChessGame.TeamColor color, int gameID) throws ServerException {
        try {
            GameData game = dataAccess.findGameById(gameID);
            if (game == null) {
                throw new ServerException(400, "unknown gameID");
            }
            else if (color == null) {
                return game;
            }
            else {
                if(color == ChessGame.TeamColor.WHITE) {
                    if (game.getWhiteUsername() == null || Objects.equals(game.getWhiteUsername(), username)) {
                        game.setWhiteUsername(username);
                    }
                    else {
                        throw new ServerException(403, "color taken");
                    }
                }
                else if(color == ChessGame.TeamColor.BLACK) {
                    if (game.getBlackUsername() == null || Objects.equals(game.getBlackUsername(), username)) {
                        game.setBlackUsername(username);
                    }
                    else {
                        throw new ServerException(403, "color taken");
                    }
                }
                dataAccess.updateGame(game);
            }
            return game;
        }
        catch (DataAccessException ignored) {
            throw new ServerException(500, "server error");
        }
    }
}
