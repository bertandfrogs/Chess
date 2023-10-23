package api.services.clearApplication;

import api.services.base.Request;

/**
 * The ClearApplicationRequest to be sent to the server, contains the HTTP method and a URL path.
 */
public class ClearApplicationRequest extends Request {
    /**
     * The constructor uses the parent class's constructor to set the httpMethod and URL.
     */
    ClearApplicationRequest(){
        super("DELETE","/db");
    }
}
