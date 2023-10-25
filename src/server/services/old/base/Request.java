package server.services.old.base;

/**
 * The Request base class provides the structure and basic functionality of all Request children classes.
 * It's an abstract class because it's not meant to be instantiated.
 */
public abstract class Request {
    /**
     * The HTTP request method (GET, POST, PUT, DELETE)
     */
    String httpMethod;

    /**
     * The URL path.
     */
    String url;

    /**
     * Base class Request constructor to be used by its children classes.
     * @param httpMethod The HTTP request method: GET, POST, PUT, DELETE.
     * @param url The URL path.
     */
    public Request(String httpMethod, String url) {
        this.httpMethod = httpMethod;
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }
}
