package api.services.logout;

import api.services.base.Request;
import api.models.AuthToken;

/**
 * The LogoutRequest to be sent to the server. Contains the user's AuthToken.
 */
public class LogoutRequest extends Request {
    AuthToken authToken;

    /**
     * LogoutRequest constructor, sets the httpMethod and url in the base class, and the authToken here.
     * @param token - the session token that will be deleted.
     */
    LogoutRequest(AuthToken token) {
        super("DELETE", "/session");
        this.authToken = token;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
