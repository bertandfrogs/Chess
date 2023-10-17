package server.services.clearApplication;

import server.services.base.Request;

/**
 * The ClearApplicationRequest doesn't have any data, just an HTTP method and a URL path.
 */
public class ClearApplicationRequest extends Request {
    ClearApplicationRequest(){
        super("DELETE","/db");
    }
}
