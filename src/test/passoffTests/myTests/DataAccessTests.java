package passoffTests.myTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.dataAccess.DataAccess;
import server.dataAccess.DataAccessException;
import models.AuthToken;
import models.GameData;
import models.UserData;

public class DataAccessTests {
    DataAccess dataAccess;

    @BeforeEach
    void setup() {
        dataAccess = new DataAccess();
    }

    @Test
    void createUserTests() {
        UserData newUser1 = new UserData("beans88", "12345", "beans88@gmail.com");

        // Test that the user isn't found in db yet
        Assertions.assertNull(dataAccess.findUser("beans88"));

        Assertions.assertDoesNotThrow(() -> {dataAccess.createUser(newUser1);});

        Assertions.assertEquals(newUser1, dataAccess.findUser("beans88"));

        // now test that it throws an exception when we try to add the same user again, or a different user with same username
        Assertions.assertThrows(DataAccessException.class, () -> {dataAccess.createUser(newUser1);});

        UserData sameUsername = new UserData("beans88", "notthesame", "imposter@gmail.com");

        Assertions.assertThrows(DataAccessException.class, () -> {dataAccess.createUser(sameUsername);});

        // double check that it still only has one user added
        Assertions.assertEquals(1, dataAccess.getUsers().size());

        // add another user to the database
        UserData newUser2 = new UserData("ducky", "quack", "duckyduck@gmail.com");
        Assertions.assertDoesNotThrow(() -> {dataAccess.createUser(newUser2);});
        Assertions.assertEquals(newUser2, dataAccess.findUser("ducky"));

        Assertions.assertEquals(2, dataAccess.getUsers().size());
    }

    @Test
    void updateUserTests() {
        UserData newUser1 = new UserData("beans88", "12345", "beans88@gmail.com");

        // trying to update user that hasn't been added yet
        Assertions.assertThrows(DataAccessException.class, () -> {dataAccess.updateUser(newUser1);});

        Assertions.assertDoesNotThrow(() -> {dataAccess.createUser(newUser1);});

        newUser1.setPassword("MuchBetterAndMoreSecurePassword");

        Assertions.assertDoesNotThrow(() -> {dataAccess.updateUser(newUser1);});

        // verify that it's changed in the db
        Assertions.assertEquals("MuchBetterAndMoreSecurePassword", dataAccess.findUser("beans88").getPassword());

        newUser1.setEmail("newEmail@future.com");
        Assertions.assertDoesNotThrow(() -> {dataAccess.updateUser(newUser1);});

        Assertions.assertEquals("newEmail@future.com", dataAccess.findUser("beans88").getEmail());
    }

    @Test
    void deleteUserTests() {
        UserData newUser1 = new UserData("beans88", "12345", "beans88@gmail.com");

        // trying to delete user that hasn't been added yet
        Assertions.assertThrows(DataAccessException.class, () -> {dataAccess.updateUser(newUser1);});

        Assertions.assertDoesNotThrow(() -> {dataAccess.createUser(newUser1);});

        Assertions.assertEquals(newUser1, dataAccess.findUser("beans88"));

        Assertions.assertDoesNotThrow(() -> {dataAccess.deleteUser(newUser1);});

        Assertions.assertNull(dataAccess.findUser("beans88"));

        Assertions.assertEquals(0, dataAccess.getUsers().size());
    }

    @Test
    void allUserOperationsTests() {
        // create 10 users
        Assertions.assertDoesNotThrow(() -> {
            dataAccess.createUser(new UserData("test1", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test2", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test3", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test4", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test5", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test6", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test7", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test8", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test9", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test10", "pass", "sbemail"));
        });

        Assertions.assertEquals(10, dataAccess.getUsers().size());

        // try to find nonexistent user with similar username
        Assertions.assertNull(dataAccess.findUser("test"));

        // try to find other nonexistent user
        Assertions.assertNull(dataAccess.findUser("coolDog23456"));

        // throws an exception when we try to add the same user again
        Assertions.assertThrows(DataAccessException.class, () -> {dataAccess.createUser(new UserData("test3", "pass", "sbemail"));});

        // throws an exception when we try to add a user with duplicate username
        Assertions.assertThrows(DataAccessException.class, () -> {dataAccess.createUser(new UserData("test10", "something", "mail@mail.com"));});

        Assertions.assertEquals(10, dataAccess.getUsers().size());

        // trying to update user that doesn't exist
        Assertions.assertThrows(DataAccessException.class, () -> {dataAccess.updateUser(new UserData("fake2", "pass", "sbemail"));});

        // update various users
        Assertions.assertDoesNotThrow(() -> {
            dataAccess.updateUser(new UserData("test5", "robin", "email"));
            dataAccess.updateUser(new UserData("test1", "chickadee", "email"));
            dataAccess.updateUser(new UserData("test10", "duck", "email"));
            dataAccess.updateUser(new UserData("test6", "goose", "email"));
        });

        // double check db size
        Assertions.assertEquals(10, dataAccess.getUsers().size());

        // make sure the user is updated in db
        UserData updated10 = new UserData("test10", "duck", "email");
        Assertions.assertEquals(updated10, dataAccess.findUser("test10"));

        // delete some users
        Assertions.assertDoesNotThrow(() -> {
            dataAccess.deleteUser(new UserData("test1", "pass", "sbemail"));
            dataAccess.deleteUser(new UserData("test2", "pass", "sbemail"));
            dataAccess.deleteUser(new UserData("test3", "pass", "sbemail"));
            dataAccess.deleteUser(new UserData("test4", "pass", "sbemail"));
            dataAccess.deleteUser(new UserData("test5", "pass", "sbemail"));
        });

        // double check db size is updated
        Assertions.assertEquals(5, dataAccess.getUsers().size());

        // make sure users no longer exist
        Assertions.assertNull(dataAccess.findUser("test1"));
        Assertions.assertNull(dataAccess.findUser("test2"));
        Assertions.assertNull(dataAccess.findUser("test3"));
        Assertions.assertNull(dataAccess.findUser("test4"));
        Assertions.assertNull(dataAccess.findUser("test5"));

        // create more users
        Assertions.assertDoesNotThrow(() -> {
            dataAccess.createUser(new UserData("test11", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test12", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test13", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test14", "pass", "sbemail"));
            dataAccess.createUser(new UserData("test15", "pass", "sbemail"));
        });

        // double check db size is correct
        Assertions.assertEquals(10, dataAccess.getUsers().size());

        // CLEAR DATABASE
        Assertions.assertDoesNotThrow(() -> { dataAccess.clear(); });

        // make sure users no longer exist
        Assertions.assertNull(dataAccess.findUser("test11"));

        // double check db size is correct
        Assertions.assertEquals(0, dataAccess.getUsers().size());
    }

    @Test
    void createAuthTokenTests() {
        AuthToken newAuthToken = dataAccess.createAuthToken("beans");
        String tokenStr = newAuthToken.getAuthToken();

        // trying to find nonexistent authToken
        Assertions.assertNull(dataAccess.findAuthToken("NOT_REAL"));

        Assertions.assertEquals(newAuthToken, dataAccess.findAuthToken(tokenStr));
    }

    @Test
    void deleteAuthTokenTests() throws DataAccessException {
        AuthToken newAuthToken = dataAccess.createAuthToken("birdy");
        String tokenStr = newAuthToken.getAuthToken();

        Assertions.assertEquals(newAuthToken, dataAccess.findAuthToken(tokenStr));

        // Normal AuthToken Deletion
        Assertions.assertDoesNotThrow(() -> {
            dataAccess.deleteAuthToken(tokenStr);
        });

        Assertions.assertNull(dataAccess.findAuthToken(tokenStr));
        Assertions.assertEquals(0, dataAccess.getAuthTokens().size());

        // trying to delete it again, should throw an error
        Assertions.assertThrows(DataAccessException.class, () -> {
            dataAccess.deleteAuthToken(tokenStr);
        });
    }

    @Test
    void createGameTests(){
        GameData newGame = dataAccess.createGame("the ultimate showdown");
        Assertions.assertNotNull(dataAccess.getGames());
        Assertions.assertTrue(newGame.getGameId() > 0);
        Assertions.assertEquals(newGame, dataAccess.findGameById(newGame.getGameId()));
    }
}
