package passoffTests.myTests;

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

public class ServiceTests {
    DataAccess dataAccess;
    AdminService adminService;
    UserService userService;
    AuthService authService;
    GameService gameService;

    @BeforeEach
    void setup() {
        this.dataAccess = new DataAccess();
        this.adminService = new AdminService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.authService = new AuthService(dataAccess);
        this.userService = new UserService(dataAccess);
    }

    @Test
    void clearApplicationTest() throws DataAccessException, ServerException {
        UserData testUser = new UserData("turtle", "123456", "turtle@shell.com");

        // it's ok to clear an empty db
        Assertions.assertDoesNotThrow(() -> adminService.clearApplication());

        GameData game = gameService.createGame("game1");
        AuthToken userToken = userService.registerUser(testUser);

        Assertions.assertDoesNotThrow(() -> adminService.clearApplication());
    }

    @Test
    void registerUserTest() throws DataAccessException, ServerException {
        UserData testUser = new UserData("turtle", "123456", "turtle@shell.com");

        AuthToken userToken = userService.registerUser(testUser);

        Assertions.assertEquals(userToken, authService.findToken(userToken.getAuthToken()));
    }
}
