package api.services.registerUser;

import api.services.base.Response;
import api.models.AuthToken;

/**
 * The RegisterUserResponse from the server.
 * If successful, returns the new user's username and their new authToken.
 * If registration fails, returns an error message.
 */
public class RegisterUserResponse extends Response {
    private String username;
    private AuthToken authToken;

    /**
     * Constructor for a success response.
     * @param username - the username of the new user.
     * @param authToken - the newly generated authToken.
     */
    RegisterUserResponse(String username, AuthToken authToken){
        super(200);
        this.username = username;
        this.authToken = authToken;
    }

    /**
     * Constructor for a fail response (uses base class implementation)
     * @param code - anything that's not 200.
     * @param description - optional - if there's a specific error description (error code 500)
     */
    RegisterUserResponse(int code, String description){
        super(code, description);
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public AuthToken getAuthToken(){
        return this.authToken;
    }
    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
