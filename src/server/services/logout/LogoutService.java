package server.services.logout;

import server.services.base.Service;

/**
 * Logs out the user represented by the AuthToken.
 * Receives data (authToken) in the form of a LogoutRequest object, the Service parses it, and returns a LogoutResponse object.
 */
public class LogoutService extends Service {
    LogoutResponse logout(LogoutRequest request) {
        return null;
    }
}
