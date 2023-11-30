public class ClientException extends Exception {
    int code;

    public ClientException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
