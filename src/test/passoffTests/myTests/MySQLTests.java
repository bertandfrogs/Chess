package passoffTests.myTests;

import chess.Game;
import org.junit.jupiter.api.*;
import server.ServerException;
import models.AuthToken;
import models.GameData;
import models.UserData;
import server.dataAccess.GameDeserializer;

import java.util.Collection;

public class MySQLTests {
    static MySQLTestHelper db;

    UserData user1;
    UserData user2;
    UserData existingUser;
    static int initialSizeUsers;
    static int initialSizeGames;
    static int initialSizeAuths;

    @BeforeEach
    void setup() throws ServerException {
        db = new MySQLTestHelper();
        db.clear();
        initialSizeUsers = db.getUsers().size();
        initialSizeGames = db.getGames().size();
        initialSizeAuths = db.getSessions().size();
        user1 = new UserData("beans88", "12345", "beans88@gmail.com");
        user2 = new UserData("ducky", "quack", "duckyduck@gmail.com");
        existingUser = new UserData("regularGuy", "pass", "mail@mail.com");
    }

    @AfterAll
    static void cleanup() throws ServerException {
        db.clear();
    }

    @Test
    void testGameDeserializer() throws ServerException {
        Game testGame = new Game();
        testGame.newGame();
        String testGameJson = testGame.toString();
        Game testGameDeserialized = new GameDeserializer().deserialize(testGameJson);
    }

    @Test
    void clearDB() throws ServerException {
        // populate database
        db.createUser(user1);
        db.createUser(user2);
        db.createUser(existingUser);
        db.createAuthToken(user1.getUsername());
        db.createAuthToken(user2.getUsername());
        db.createAuthToken(existingUser.getUsername());
        db.createGame("game1");
        db.createGame("game2");
        db.createGame("game3");

        // check that data was added to db
        Assertions.assertEquals(initialSizeGames+3, db.getGames().size());
        Assertions.assertEquals(initialSizeUsers+3, db.getUsers().size());
        Assertions.assertEquals(initialSizeAuths+3, db.getSessions().size());

        // clear database
        Assertions.assertDoesNotThrow(() -> db.clear());

        // check that data was deleted from db
        Assertions.assertEquals(initialSizeGames, db.getGames().size());
        Assertions.assertEquals(initialSizeUsers, db.getUsers().size());
        Assertions.assertEquals(initialSizeAuths, db.getSessions().size());

        // can clear again without issue
        Assertions.assertDoesNotThrow(() -> db.clear());
    }

    @Test
    void findUserTest() throws ServerException {
        UserData userInDB = db.createUser(existingUser);
        Assertions.assertEquals(userInDB, db.findUser(existingUser.getUsername()));
    }

    @Test
    void findInvalidUserTest() throws ServerException {
        // try to find a username i just made up
        UserData invalidUser = db.findUser("invalid-user");
        Assertions.assertNull(invalidUser);

        // try to find the username of a user not added yet
        UserData invalidUser2 = db.findUser(user1.getUsername());
        Assertions.assertNull(invalidUser2);
    }

    @Test
    void createUserTests() throws ServerException {
        // Test that the user isn't found in db yet
        Assertions.assertNull(db.findUser(user1.getUsername()));

        UserData userInDB = db.createUser(user1);

        Assertions.assertEquals(userInDB, db.findUser(user1.getUsername()));

        // now test that it throws an exception when we try to add the same user again, or a different user with same username
        Assertions.assertThrows(ServerException.class, () -> db.createUser(user1));

        UserData sameUsername = new UserData(user1.getUsername(), "notthesame", "imposter@gmail.com");

        Assertions.assertThrows(ServerException.class, () -> db.createUser(sameUsername));

        // double check that it still only has one user added
        Assertions.assertEquals(initialSizeUsers + 1, db.getUsers().size());

        // add another user to the database
        UserData otherUserInDB = db.createUser(user2);

        Assertions.assertEquals(otherUserInDB, db.findUser(user2.getUsername()));

        // check that the number of users added is equal to two
        Assertions.assertEquals(initialSizeUsers + 2, db.getUsers().size());
    }

    @Test
    void updateUserTests() throws ServerException {
        // trying to update user that hasn't been added yet
        Assertions.assertThrows(ServerException.class, () -> db.updateUser(user1));

        Assertions.assertDoesNotThrow(() -> {
            db.createUser(user1);
        });

        user1.setPassword("MuchBetterAndMoreSecurePassword");

        Assertions.assertDoesNotThrow(() -> {
            db.updateUser(user1);
        });

        // verify that it's changed in the db
        Assertions.assertEquals("MuchBetterAndMoreSecurePassword", db.findUser(user1.getUsername()).getPassword());

        user1.setEmail("newEmail@future.com");
        Assertions.assertDoesNotThrow(() -> {
            db.updateUser(user1);
        });

        Assertions.assertEquals("newEmail@future.com", db.findUser(user1.getUsername()).getEmail());
    }

    @Test
    void deleteUserTests() throws ServerException  {
        // trying to delete user that hasn't been added yet
        Assertions.assertThrows(ServerException.class, () -> db.deleteUser(user1));

        // create the user
        db.createUser(user1);

        Assertions.assertEquals(new UserData(db.testString(user1.getUsername()), user1.getPassword(), user1.getEmail()), db.findUser(user1.getUsername()));

        Assertions.assertDoesNotThrow(() -> db.deleteUser(user1));

        Assertions.assertNull(db.findUser(user1.getUsername()));

        Assertions.assertEquals(initialSizeUsers, db.getUsers().size());
    }

    @Test
    void allUserOperationsTests() throws ServerException  {
        // create 10 users
        Assertions.assertDoesNotThrow(() -> {
            db.createUser(new UserData("test1", "pass", "sbemail"));
            db.createUser(new UserData("test2", "pass", "sbemail"));
            db.createUser(new UserData("test3", "pass", "sbemail"));
            db.createUser(new UserData("test4", "pass", "sbemail"));
            db.createUser(new UserData("test5", "pass", "sbemail"));
            db.createUser(new UserData("test6", "pass", "sbemail"));
            db.createUser(new UserData("test7", "pass", "sbemail"));
            db.createUser(new UserData("test8", "pass", "sbemail"));
            db.createUser(new UserData("test9", "pass", "sbemail"));
            db.createUser(new UserData("test10", "pass", "sbemail"));
        });

        Assertions.assertEquals(initialSizeUsers + 10, db.getUsers().size());

        // try to find nonexistent user with similar username
        Assertions.assertNull(db.findUser("test"));

        // try to find other nonexistent user
        Assertions.assertNull(db.findUser("coolDog23456"));

        // throws an exception when we try to add the same user again
        Assertions.assertThrows(ServerException.class, () -> db.createUser(new UserData("test3", "pass", "sbemail")));

        // throws an exception when we try to add a user with duplicate username
        Assertions.assertThrows(ServerException.class, () -> db.createUser(new UserData("test10", "something", "mail@mail.com")));

        Assertions.assertEquals(initialSizeUsers + 10, db.getUsers().size());

        // trying to update user that doesn't exist
        Assertions.assertThrows(ServerException.class, () -> db.updateUser(new UserData("fake2", "pass", "sbemail")));

        // update various users
        Assertions.assertDoesNotThrow(() -> {
            db.updateUser(new UserData("test5", "robin", "email"));
            db.updateUser(new UserData("test1", "chickadee", "email"));
            db.updateUser(new UserData("test10", "duck", "email"));
            db.updateUser(new UserData("test6", "goose", "email"));
        });

        // double check db size
        Assertions.assertEquals(initialSizeUsers + 10, db.getUsers().size());

        // make sure the user is updated in db
        UserData updated10 = new UserData("test10", "duck", "email");
        Assertions.assertEquals(new UserData(db.testString(updated10.getUsername()), updated10.getPassword(), updated10.getEmail()), db.findUser(updated10.getUsername()));

        // delete some users
        Assertions.assertDoesNotThrow(() -> {
            db.deleteUser(new UserData("test1", "pass", "sbemail"));
            db.deleteUser(new UserData("test2", "pass", "sbemail"));
            db.deleteUser(new UserData("test3", "pass", "sbemail"));
            db.deleteUser(new UserData("test4", "pass", "sbemail"));
            db.deleteUser(new UserData("test5", "pass", "sbemail"));
        });

        // double check db size is updated
        Assertions.assertEquals(initialSizeUsers + 5, db.getUsers().size());

        // make sure users no longer exist
        Assertions.assertNull(db.findUser("test1"));
        Assertions.assertNull(db.findUser("test2"));
        Assertions.assertNull(db.findUser("test3"));
        Assertions.assertNull(db.findUser("test4"));
        Assertions.assertNull(db.findUser("test5"));

        // create more users
        Assertions.assertDoesNotThrow(() -> {
            db.createUser(new UserData("test11", "pass", "sbemail"));
            db.createUser(new UserData("test12", "pass", "sbemail"));
            db.createUser(new UserData("test13", "pass", "sbemail"));
            db.createUser(new UserData("test14", "pass", "sbemail"));
            db.createUser(new UserData("test15", "pass", "sbemail"));
        });

        // double check db size is correct
        Assertions.assertEquals(initialSizeUsers + 10, db.getUsers().size());

        // CLEAR DATABASE
        Assertions.assertDoesNotThrow(() -> db.clear());

        // make sure users no longer exist
        Assertions.assertNull(db.findUser("test11"));

        // double check db size is correct
        Assertions.assertEquals(initialSizeUsers, db.getUsers().size());
    }

    @Test
    void findValidAuth() throws ServerException  {
        AuthToken newAuthToken = db.createAuthToken("beans");
        String tokenStr = newAuthToken.getAuthToken();
        Assertions.assertEquals(newAuthToken, db.findAuthToken(tokenStr));
    }

    @Test
    void findInvalidAuth() throws ServerException {
        Assertions.assertNull(db.findAuthToken("NOT_REAL"));
    }

    @Test
    void validAuthCreate() throws ServerException {
        AuthToken newAuthToken = db.createAuthToken("beans");
        String tokenStr = newAuthToken.getAuthToken();
        Assertions.assertEquals(newAuthToken, db.findAuthToken(tokenStr));
        Assertions.assertEquals(initialSizeAuths + 1, db.getSessions().size());
    }

    @Test
    void authCreationUniqueTokens() throws ServerException {
        String tokenStr = db.createAuthToken("beans").getAuthToken();
        String tokenStr2 = db.createAuthToken("beans").getAuthToken();
        String tokenStr3 = db.createAuthToken("beans").getAuthToken();
        Assertions.assertFalse(tokenStr.equals(tokenStr2) && tokenStr2.equals(tokenStr3));
    }

    @Test
    void deleteAuthTokenTests() throws ServerException {
        AuthToken newAuthToken = db.createAuthToken("birdy");
        String tokenStr = newAuthToken.getAuthToken();

        Assertions.assertEquals(newAuthToken, db.findAuthToken(tokenStr));

        // Normal AuthToken Deletion
        Assertions.assertDoesNotThrow(() -> db.deleteAuthToken(tokenStr));

        Assertions.assertNull(db.findAuthToken(tokenStr));
        Assertions.assertEquals(initialSizeAuths, db.getSessions().size());

        // trying to delete it again, should throw an error
        Assertions.assertThrows(ServerException.class, () -> db.deleteAuthToken(tokenStr));
    }

    @Test
    void createGameTests() throws ServerException  {
        GameData newGame = db.createGame("showdown");
        Assertions.assertEquals(initialSizeGames + 1, db.getGames().size());
        Assertions.assertTrue(newGame.getGameId() > 0);
        Assertions.assertEquals(newGame, db.findGameById(newGame.getGameId()));
    }

    @Test
    void testListOneGame() throws ServerException {
        GameData newGame = db.createGame("game1");
        Collection<GameData> list = db.listGames();
        Assertions.assertTrue(list.contains(newGame));
    }

    @Test
    void testListManyGames() throws ServerException {
        GameData newGame1 = db.createGame("game1");
        GameData newGame2 = db.createGame("game2");
        GameData newGame3 = db.createGame("game3");
        GameData newGame4 = db.createGame("game4");
        GameData newGame5 = db.createGame("game5");

        Collection<GameData> list = db.listGames();

        Assertions.assertTrue(list.contains(newGame1));
        Assertions.assertTrue(list.contains(newGame2));
        Assertions.assertTrue(list.contains(newGame3));
        Assertions.assertTrue(list.contains(newGame4));
        Assertions.assertTrue(list.contains(newGame5));
    }
}

