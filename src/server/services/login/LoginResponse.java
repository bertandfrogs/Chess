package server.services.login;

import server.services.base.Response;
import server.models.AuthToken;

/**
 * If login is successful, return the user's username and their authToken for the current session.
 * If login fails, return an error message.
 */
public class LoginResponse extends Response {
    String username;
    AuthToken authToken;

    /**
     * Constructor for a success response
     * @param username - the username of the current user (successfully logged in)
     * @param token - the AuthToken for current session
     */
    LoginResponse(String username, AuthToken token){
        super(200);
        this.username = username;
        this.authToken = token;
    }

    /**
     * Constructor for a fail response (uses base class implementation)
     * @param code - anything that's not 200.
     * @param description - optional - if there's a specific error description (error code 500)
     */
    LoginResponse(int code, String description){
        super(code, description);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
