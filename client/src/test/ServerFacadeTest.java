import chess.ChessGame;
import models.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.CreateGameResponse;
import service.LoginResponse;
import service.ResponseException;

public class ServerFacadeTest {
    static ServerFacade serverFacade;
    final String url = "http://localhost:8080";
    UserData testUser = new UserData("beany", "12345", "beany1212");
    UserData registeredUser = new UserData("duck", "quack", "duck@duck.com");
    UserData loggedInUser = new UserData("kirby", "password", "kirbo@kirb.com");
    String loggedInAuth;

    @BeforeEach
    void setup() throws Exception {
        serverFacade = new ServerFacade(url);
        serverFacade.clearDb();

        // register test users (logged out, logged in)
        var responseLoggedOut = serverFacade.registerUser(registeredUser.getUsername(), registeredUser.getPassword(), registeredUser.getEmail());
        serverFacade.logoutUser(responseLoggedOut.authToken);
        var responseLoggedIn = serverFacade.registerUser(loggedInUser.getUsername(), loggedInUser.getPassword(), loggedInUser.getEmail());
        loggedInAuth = responseLoggedIn.authToken;
    }

    @AfterAll
    static void cleanup() throws ResponseException {
        serverFacade.clearDb();
    }

    @Test
    void validRegisterUser() {
        Assertions.assertDoesNotThrow(() -> {
            LoginResponse response = serverFacade.registerUser(testUser.getUsername(), testUser.getPassword(), testUser.getEmail());
            Assertions.assertEquals(testUser.getUsername(), response.username);
        });
    }

    @Test
    void invalidRegisterUser() {
        // duplicate user
        Assertions.assertThrows(Exception.class, () -> {
           serverFacade.registerUser(registeredUser.getUsername(), registeredUser.getPassword(), registeredUser.getEmail());
        });
    }

    @Test
    void validLoginUser() {
        Assertions.assertDoesNotThrow(() -> {
            LoginResponse response = serverFacade.loginUser(registeredUser.getUsername(), registeredUser.getPassword());
            Assertions.assertEquals(registeredUser.getUsername(), response.username);
        });
    }

    @Test
    void invalidLoginUser() {
        // user does not exist
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.loginUser("garbage", "garbage");
        });
    }

    @Test
    void validLogoutUser() {
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.logoutUser(loggedInAuth);
        });
    }

    @Test
    void invalidLogoutUser() {
        // authToken does not exist
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.logoutUser("garbage");
        });
    }

    @Test
    void validCreateGame() {
        Assertions.assertDoesNotThrow(() -> {
            CreateGameResponse response = serverFacade.createGame(loggedInAuth, "game");
            Assertions.assertTrue(response.gameID > 0);
        });
    }

    @Test
    void invalidCreateGame() {
        // try to create a game without valid auth
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.createGame("garbage", "garbage");
        });
    }

    @Test
    void validListGames() {
        // can list games with no games in db
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.listGames(loggedInAuth);
        });

        // list multiple games
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.createGame(loggedInAuth, "game1");
            serverFacade.createGame(loggedInAuth, "game2");
            serverFacade.createGame(loggedInAuth, "game3");
            serverFacade.listGames(loggedInAuth);
        });
    }

    @Test
    void invalidListGames() {
        // try to list games without valid auth
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.listGames("garbage");
        });
    }

    @Test
    void validJoinGame() {
        // create game, then join a game (as black)
        Assertions.assertDoesNotThrow(() -> {
            var res = serverFacade.createGame(loggedInAuth, "newGame1");
            serverFacade.joinGame(loggedInAuth, res.gameID, ChessGame.TeamColor.BLACK);
        });

        // create game, then join a game (as white)
        Assertions.assertDoesNotThrow(() -> {
            var res = serverFacade.createGame(loggedInAuth, "newGame2");
            serverFacade.joinGame(loggedInAuth, res.gameID, ChessGame.TeamColor.WHITE);
        });

        // create game, then join a game (as observer)
        Assertions.assertDoesNotThrow(() -> {
            var res = serverFacade.createGame(loggedInAuth, "newGame3");
            serverFacade.joinGame(loggedInAuth, res.gameID, null);
        });
    }

    @Test
    void invalidJoinGame() {
        // try to join a game without valid auth
        Assertions.assertThrows(ResponseException.class, () -> {
            var res = serverFacade.createGame(loggedInAuth, "newGame1");
            serverFacade.joinGame("garbage", res.gameID, ChessGame.TeamColor.BLACK);
        });

        // try to join a game without valid gameID
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.joinGame(loggedInAuth, 45346, ChessGame.TeamColor.BLACK);
        });
    }
}
