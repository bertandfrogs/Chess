package api.services.logout;

import api.services.base.Service;

/**
 * Manages the requests and responses to and from the server for the Logout endpoint.
 * Logs out the user represented by the AuthToken.
 * Receives a LogoutRequest, parses it, and returns a LogoutResponse.
 */
public class LogoutService extends Service {
    /**
     * @param request A LogoutRequest object to be sent to the server.
     * @return The LogoutResponse from the server.
     */
    LogoutResponse logout(LogoutRequest request) {
        return null;
    }
}
