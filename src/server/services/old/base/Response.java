package server.services.old.base;

/**
 * The Response class is the base class for all other Response classes.
 * Provides the basic functionality of a generic response of any HTTP request.
 * It's abstract because it's never going to need to be instantiated.
 */
public abstract class Response {
    /**
     * Response status code. Any status code that isn't 200 is an error code.
     */
    private int code;

    /**
     * The message returned when an error is thrown.
     */
    private String message;

    /**
     * Constructor for a success response.
     * @param code The status code (200)
     */
    public Response(int code) {
        setCode(code);
    }

    /**
     * Constructor for a fail response.
     * @param code The error code.
     * @param description Optional - if there's a specific error description (error code 500)
     */
    public Response(int code, String description){
        setCode(code);
        setMessage(getErrorMessage(code, description));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Gets an error message corresponding to a given status code.
     * @param code The status code of the HTTP request.
     * @param description For [500] responses, provide a description of the error. Can be a null string if not needed.
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
