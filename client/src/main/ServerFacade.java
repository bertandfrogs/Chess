import chess.adapters.ChessAdapter;
import chess.interfaces.ChessGame;
import com.google.gson.Gson;
import service.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public LoginResponse registerUser(String username, String password, String email) throws ResponseException {
        var body = Map.of("username", username, "password", password, "email", email);
        return this.request("/user", "POST", body.toString(), null, LoginResponse.class);
    }

    public LoginResponse loginUser(String username, String password) throws ResponseException {
        var body = Map.of("username", username, "password", password);
        return this.request("/session", "POST", body.toString(), null, LoginResponse.class);
    }

    public LogoutResponse logoutUser(String authToken) throws ResponseException {
        return this.request("/session", "DELETE", null, authToken, LogoutResponse.class);
    }

    public CreateGameResponse createGame(String authToken, String gameName) throws ResponseException {
        var body = Map.of("gameName", gameName);
        return this.request("/game", "POST", body.toString(), authToken, CreateGameResponse.class);
    }

    public ListGamesResponse listGames(String authToken) throws ResponseException {
        return this.request("/game", "GET", null, authToken, ListGamesResponse.class);
    }

    public GameJoinResponse joinGame(String authToken, int gameID, ChessGame.TeamColor color) throws ResponseException {
        var body = new GameJoinRequest(color, gameID);
        return this.request("/game", "PUT", body.toString(), authToken, GameJoinResponse.class);
    }

    public void clearDb() throws ResponseException {
        var response = this.request("/db", "DELETE", null, null, Map.class);
    }

    private <T> T request(String path, String method, String body, String authToken, Class<T> classType) throws ResponseException {
        try {
            HttpURLConnection connection = sendRequest(serverUrl + path, method, body, authToken);
            return readResponseBody(connection, classType);
        }
        catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
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
//        System.out.printf("= Request =========\n[%s] %s\n\n%s\n\n", method, url, body);
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

    private static <T> T readResponseBody(HttpURLConnection http, Class<T> classType) throws ResponseException, IOException {
        var statusCode = http.getResponseCode();
        var statusMessage = http.getResponseMessage();

        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            if(http.getResponseCode() == 200){
                if(classType != null) {
                    Gson gson = ChessAdapter.getGson();
                    return gson.fromJson(inputStreamReader, classType);
                }
                return null;
            }
            throw new ResponseException(http.getResponseCode(), inputStreamReader.toString());
        }
    }
}
