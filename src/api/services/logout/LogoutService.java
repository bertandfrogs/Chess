package api.services.logout;

import api.services.base.Service;

/**
 * Manages the requests and responses to and from the server for the Logout endpoint.
 * Logs out the user represented by the AuthToken.
 * Receives a LogoutRequest, parses it, and returns a LogoutResponse.
 */
public class LogoutService extends Service {
    LogoutResponse logout(LogoutRequest request) {
        return null;
    }
}
