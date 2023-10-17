package server.services.joinGame;

import server.services.base.Response;

/**
 * The JoinGameResponse doesn't return anything but a status code if successful, or an error code and message if unsuccessful.
 */
public class JoinGameResponse extends Response {
    /**
     * Success response constructor
     */
    JoinGameResponse(){
        super(200);
    }

    /**
     * Error response constructor
     * @param code - the error code
     * @param description - for error 500, give a description of what went wrong
     */
    JoinGameResponse(int code, String description){
        super(code, description);
    }
}
