package server.services.old.login;

import server.services.old.base.Request;

/**
 * The LoginRequest sent to the server, containing the user's username and password.
 */
public class LoginRequest extends Request {
    String username;
    String password;

    /**
     * Initializes new LoginRequest object
     * Constructor uses the parent class's constructor to set the HTTP method and the url.
     * @param username Username entered by user trying to log in.
     * @param password Password entered by user trying to log in.
     */
    LoginRequest(String username, String password){
        super("POST", "/session");
        this.username = username;
        this.password = password;
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
}
