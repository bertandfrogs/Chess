package api.services.clearApplication;

import api.services.base.Request;

/**
 * The ClearApplicationRequest to be sent to the server, contains the HTTP method and a URL path.
 */
public class ClearApplicationRequest extends Request {
    ClearApplicationRequest(){
        super("DELETE","/db");
    }
}
