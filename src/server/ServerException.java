package server;

/**
 * Used by the server to send an Exception with an error code and message when something goes wrong.
 */
public class ServerException extends Exception {
    int code;

    public ServerException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
