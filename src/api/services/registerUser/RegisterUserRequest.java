package api.services.registerUser;

import api.services.base.Request;

/**
 * The RegisterUserRequest to the server, containing the user's information.
 */
public class RegisterUserRequest extends Request {
    String username;
    String password;
    String email;

    /**
     * Constructor to create a new RegisterUserRequest
     * @param username - the new user's desired username
     * @param password - the new user's desired password
     * @param email - the new user's email
     */
    RegisterUserRequest(String username, String password, String email) {
        super("POST", "/user");
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
