package api.services.base;

/**
 * The Request base class provides the structure and basic functionality of all Request children classes. It is abstract because it shouldn't be implemented.
 */
public abstract class Request {
    String httpMethod;
    String url;

    /**
     * Base class Request constructor to be used by its children classes.
     * @param httpMethod - GET, POST, PUT, DELETE
     * @param url - the URL path
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
