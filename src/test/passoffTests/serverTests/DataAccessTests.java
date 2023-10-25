package passoffTests.serverTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.dataAccess.DataAccess;
import server.dataAccess.DataAccessException;
import server.models.UserData;

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
}
