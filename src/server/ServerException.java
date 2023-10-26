package server;

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
