package server.services.logout;

import server.services.base.Request;
import server.models.AuthToken;

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
