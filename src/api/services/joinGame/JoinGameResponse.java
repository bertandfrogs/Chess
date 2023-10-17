package api.services.joinGame;

import api.services.base.Response;

/**
 * The JoinGameResponse from the server.
 * It doesn't return anything but a status code if successful, or an error code and message if unsuccessful.
 */
public class JoinGameResponse extends Response {
    /**
     * Success response constructor.
     */
    JoinGameResponse(){
        super(200);
    }

    /**
     * Error response constructor.
     * @param code The error code.
     * @param description Optional, a description of the error
     */
    JoinGameResponse(int code, String description){
        super(code, description);
    }
}
