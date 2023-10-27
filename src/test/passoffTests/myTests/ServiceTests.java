package passoffTests.myTests;

import chess.interfaces.ChessGame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ServerException;
import server.dataAccess.DataAccess;
import server.dataAccess.DataAccessException;
import server.models.AuthToken;
import server.models.GameData;
import server.models.UserData;
import server.services.AdminService;
import server.services.AuthService;
import server.services.GameService;
import server.services.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ServiceTests {
    DataAccess dataAccess;
    AdminService adminService;
    UserService userService;
    AuthService authService;
    GameService gameService;

    UserData user1;
    UserData user2;
    UserData invalidUser;

    @BeforeEach
    void setup() throws ServerException {
        this.dataAccess = new DataAccess();
        this.adminService = new AdminService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.authService = new AuthService(dataAccess);
        this.userService = new UserService(dataAccess);
        user1 = new UserData("ducky", "quack", "ducky@pond.net");
        user2 = new UserData("tortoise", "654321", "tortoise@shell.com");
        invalidUser = new UserData("fox", "77777", "foxy@fox.com");
    }

    @Test
    void clearApplication() throws DataAccessException, ServerException {
        // it's ok to clear an empty db
        Assertions.assertDoesNotThrow(() -> adminService.clearApplication());

        // populating the db
        userService.registerUser(user1);
        userService.registerUser(user2);
        gameService.createGame("game1");
        gameService.createGame("game2");

        Assertions.assertDoesNotThrow(() -> adminService.clearApplication());
    }

    @Test
    void registerUser() throws ServerException {
        // normal register new user
        AuthToken token1 = userService.registerUser(user1);

        Assertions.assertEquals(user1, dataAccess.findUser(user1.getUsername()));
        Assertions.assertEquals(token1, authService.findToken(token1.getAuthToken()));

        // register another user
        AuthToken token2 = userService.registerUser(user2);

        Assertions.assertEquals(user2, dataAccess.findUser(user2.getUsername()));
        Assertions.assertEquals(token2, authService.findToken(token2.getAuthToken()));
    }

    @Test
    void registerDuplicateUser() throws ServerException {
        // normal register new user
        AuthToken userToken = userService.registerUser(user1);

        Assertions.assertEquals(user1, dataAccess.findUser(user1.getUsername()));
        Assertions.assertEquals(userToken, authService.findToken(userToken.getAuthToken()));

        // try to register duplicate user throws an exception
        Assertions.assertThrows(ServerException.class, () -> {
            userService.registerUser(user1);
        });
    }

    @Test
    void loginUser() throws ServerException {
        // register user, then log them out
        AuthToken token1 = userService.registerUser(user1);
        authService.logout(token1.getAuthToken());

        // double-check that user is still in system
        Assertions.assertNotNull(dataAccess.getUsers());

        // Normal user login
        Assertions.assertDoesNotThrow(() -> {
            AuthToken token2 = authService.login(user1);

            // make sure the different sessions have different tokens
            Assertions.assertNotEquals(token1, token2);
        });
    }

    @Test
    void loginInvalidUser() throws ServerException {
        UserData correctCredentials = new UserData("bobby", "password", "bobby@bobby.com");
        UserData invalidCredentials = new UserData("bobby", "pazword", "bobby@bobby.com");

        // register user, then log them out
        AuthToken token1 = userService.registerUser(correctCredentials);
        authService.logout(token1.getAuthToken());

        // Trying to log in a user that's not registered (invalid username)
        Assertions.assertThrows(ServerException.class, () -> {
            authService.login(invalidUser);
        });

        // Trying to log in with invalid password
        Assertions.assertThrows(ServerException.class, () -> {
            authService.login(invalidCredentials);
        });

        // there should be no current sessions (no logged-in users)
        Assertions.assertEquals(0, dataAccess.getAuthTokens().size());
    }

    @Test
    void logoutUser() throws ServerException {
        // register user, then log them out
        AuthToken token1 = userService.registerUser(user1);
        Assertions.assertDoesNotThrow(() -> {
            authService.logout(token1.getAuthToken());
        });
    }

    @Test
    void logoutInvalid() throws ServerException {
        // Trying to log out with an invalid AuthToken
        Assertions.assertThrows(ServerException.class, () -> {
            authService.logout("FakeAuthToken");
        });

        // register user, then log them out
        AuthToken token1 = userService.registerUser(user1);
        Assertions.assertDoesNotThrow(() -> {
            authService.logout(token1.getAuthToken());
        });

        // Trying to log out again will throw an exception
        Assertions.assertThrows(ServerException.class, () -> {
            authService.logout(token1.getAuthToken());
        });
    }

    @Test
    void createGame() {
        GameData newGame = gameService.createGame("New Game");
        Assertions.assertEquals(newGame, dataAccess.findGameById(newGame.getGameId()));
    }

    @Test
    void joinGame() throws ServerException {
        // register users (to join as white, black, and observer)
        userService.registerUser(user1);
        userService.registerUser(user2);
        userService.registerUser(new UserData("observer", "pass", "email"));

        // create a new game
        GameData newGame = gameService.createGame("New Game");

        // valid join game for white player, black player, and observer
        Assertions.assertDoesNotThrow(() -> {
            gameService.joinGame(user1.getUsername(), ChessGame.TeamColor.WHITE, newGame.getGameId());
        });
        Assertions.assertDoesNotThrow(() -> {
            gameService.joinGame(user2.getUsername(), ChessGame.TeamColor.BLACK, newGame.getGameId());
        });
        Assertions.assertDoesNotThrow(() -> {
            gameService.joinGame("observer", null, newGame.getGameId());
        });
    }

    @Test
    void invalidJoinGame() throws ServerException {
        // register users
        userService.registerUser(user1);
        userService.registerUser(user2);

        // create a new game
        int newGameID = gameService.createGame("New Game").getGameId();

        // user 1 joins the new game as white player
        gameService.joinGame(user1.getUsername(), ChessGame.TeamColor.WHITE, newGameID);

        // Trying to join a game with an invalid gameID
        Assertions.assertThrows(ServerException.class, () -> {
            gameService.joinGame(user2.getUsername(), ChessGame.TeamColor.BLACK, newGameID+50);
        });

        // Trying to join as a color that's already taken
        Assertions.assertThrows(ServerException.class, () -> {
            gameService.joinGame(user2.getUsername(), ChessGame.TeamColor.WHITE, newGameID);
        });
    }

    @Test
    void listGames() throws ServerException {
        // create a single new game (empty)
        GameData newGame = gameService.createGame("New Game");
        GameData expectedNewGame = new GameData(newGame.getGameId(), null, null, newGame.getGameName(), newGame.getGame());

        Collection<GameData> expectedList = new ArrayList<>();
        expectedList.add(expectedNewGame);

        // Check that the game list is correct
        Assertions.assertEquals(expectedList.toString(), gameService.listGames().toString());

        // register users (to join as white, black, and observer)
        userService.registerUser(user1);
        userService.registerUser(user2);
        userService.registerUser(new UserData("observer", "pass", "email"));

        // valid join game for white and black players
        gameService.joinGame(user1.getUsername(), ChessGame.TeamColor.WHITE, newGame.getGameId());
        gameService.joinGame(user2.getUsername(), ChessGame.TeamColor.BLACK, newGame.getGameId());

        expectedNewGame.setWhiteUsername(user1.getUsername());
        expectedNewGame.setBlackUsername(user2.getUsername());

        expectedList.clear();
        expectedList.add(expectedNewGame);

        // Check that the game list is correct
        Assertions.assertEquals(expectedList.toString(), gameService.listGames().toString());
    }
}
