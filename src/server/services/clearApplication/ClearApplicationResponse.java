package server.services.clearApplication;

import server.services.base.Response;

/**
 * The ClearApplicationResponse only returns success (200) or fail (500)
 */
public class ClearApplicationResponse extends Response {
    /**
     * Success constructor
     */
    ClearApplicationResponse(){
        super(200);
    }

    /**
     * Fail constructor
     * @param code - the error code
     * @param description - a description of the error
     */
    ClearApplicationResponse(int code, String description){
        super(code, description);
    }
}
