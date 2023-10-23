package api.services.logout;

import api.services.base.Response;

/**
 * The LogoutResponse from the server. Doesn't return anything besides status code or error code and message.
 */
public class LogoutResponse extends Response {
    /**
     * Constructor for a success response, doesn't need anything besides the status code
     */
    LogoutResponse() {
        super(200);
    }

    /**
     * Constructor for a fail response (uses base class implementation)
     * @param code - anything that's not 200.
     * @param description - optional - if there's a specific error description (error code 500)
     */
    LogoutResponse(int code, String description){
        super(code, description);
    }
}
