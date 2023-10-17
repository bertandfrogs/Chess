package server.services.login;

import server.services.base.Request;

public class LoginRequest extends Request {
    String username;
    String password;

    /**
     * Initializes new LoginRequest object
     * @param username - username entered by user trying to log in
     * @param password - password entered by user trying to log in
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
