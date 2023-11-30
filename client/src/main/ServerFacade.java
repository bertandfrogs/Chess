import chess.interfaces.ChessGame;
import com.google.gson.Gson;
import service.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public LoginResponse registerUser(String username, String password, String email) throws Exception {
        var body = Map.of("username", username, "password", password, "email", email);
        return this.request("/user", "POST", body.toString(), null, LoginResponse.class);
    }

    public LoginResponse loginUser(String username, String password) throws Exception {
        var body = Map.of("username", username, "password", password);
        return this.request("/session", "POST", body.toString(), null, LoginResponse.class);
    }

    public LogoutResponse logoutUser(String authToken) throws Exception {
        return this.request("/session", "DELETE", null, authToken, LogoutResponse.class);
    }

    public CreateGameResponse createGame(String authToken, String gameName) throws Exception {
        var body = Map.of("gameName", gameName);
        return this.request("/game", "POST", body.toString(), authToken, CreateGameResponse.class);
    }

    public ListGamesResponse listGames(String authToken) throws Exception {
        return this.request("/game", "GET", "", authToken, ListGamesResponse.class);
    }

    public GameJoinResponse joinGame(String authToken, int gameID, ChessGame.TeamColor color) throws Exception {
        var body = new GameJoinRequest(color, gameID);
        return this.request("/game", "PUT", body.toString(), authToken, GameJoinResponse.class);
    }

    private <T> T request(String path, String method, String body, String authToken, Class<T> classType) throws Exception {
        HttpURLConnection connection = sendRequest(serverUrl + path, method, body, authToken);
        return readResponseBody(connection, classType);
    }

    private static HttpURLConnection sendRequest(String url, String method, String body, String authToken) throws Exception {
        URI uri = new URI(url);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);

        if (authToken != null) {
            http.setRequestProperty("authorization", authToken);
        }

        writeRequestBody(body, http);
        http.connect();
        System.out.printf("= Request =========\n[%s] %s\n\n%s\n\n", method, url, body);
        return http;
    }

    private static void writeRequestBody(String body, HttpURLConnection http) throws Exception {
        if (body != null) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private static <T> T readResponseBody(HttpURLConnection http, Class<T> classType) throws Exception {
        var statusCode = http.getResponseCode();
        var statusMessage = http.getResponseMessage();

        T responseBody;
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            responseBody = new Gson().fromJson(inputStreamReader, classType);
        }

        System.out.printf("= Response =========\n[%d] %s\n\n%s\n\n", statusCode, statusMessage, responseBody.toString());
        return responseBody;
    }
}
