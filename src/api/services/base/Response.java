package api.services.base;

/**
 * The Response class is the base class for all other Response classes. Provides the basic functionality of a generic response of any HTTP request.
 * It's abstract because it's never going to need to be instantiated.
 */
public abstract class Response {
    private int code;
    private String message;

    /**
     * Constructor for a success response.
     */
    public Response(int code) {
        setCode(code);
    }

    /**
     * Constructor for a fail response.
     * @param code - anything that's not 200.
     * @param description - optional - if there's a specific error description (error code 500)
     */
    public Response(int code, String description){
        setCode(code);
        setMessage(getErrorMessage(code, description));
    }

    /**
     * @return the status message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message - error message for fail response
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the status code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code - the status code: 200, 400, 500, etc
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return a stringified version of the Response. Overrides default Object toString function.
     */
    @Override
    public String toString() {
        return "[" + code + "] - " + message;
    }

    /**
     * Gets an error message corresponding to a given status code.
     * @param code - the status code of the HTTP request.
     * @param description - for [500] responses, provide a description of the error. Can be a null string if not needed.
     * @return the message.
     */
    public String getErrorMessage(int code, String description){
        if(code == 200){
            return "";
        }
        return "Error: " + switch(code) {
            case 400 -> "bad request";
            case 401 -> "unauthorized";
            case 403 -> "already taken";
            case 500 -> description;
            default -> "unknown status code";
        };
    }
}
