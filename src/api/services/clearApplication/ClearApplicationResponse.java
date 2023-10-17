package api.services.clearApplication;

import api.services.base.Response;

/**
 * The ClearApplicationResponse from the server. It only returns success (200) or fail (500)
 */
public class ClearApplicationResponse extends Response {
    /**
     * Constructor for a success response.
     */
    ClearApplicationResponse(){
        super(200);
    }

    /**
     * Constructor for a fail response.
     * @param code The error code.
     * @param description Optional, a description of the error.
     */
    ClearApplicationResponse(int code, String description){
        super(code, description);
    }
}
