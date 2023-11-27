import chess.*;
import chess.interfaces.ChessGame;
import chess.pieces.Piece;
import com.google.gson.Gson;
import models.AuthToken;
import models.UserData;
import ui.EscapeSequences;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    // Class Variables
    static boolean loggedIn = false;
    static boolean activeConsole = true;
    static UserData user;
    static AuthToken sessionToken;
    static final String url = "http://localhost:8080";

    // COLOR VARIABLES (( ADD INTO ESCAPE CODE CLASS?? ))
    static String commandTextColor = EscapeSequences.getCustomTextColorString(48);
    static String commandParameterTextColor = EscapeSequences.getCustomTextColorString(85);
    static String definitionTextColor = EscapeSequences.getCustomTextColorString(37);
    static String minorErrorTextColor = EscapeSequences.getCustomTextColorString(166);
    static String majorErrorTextColor = EscapeSequences.getCustomTextColorString(160);
    static String consoleUserStatusTextColor = EscapeSequences.getCustomTextColorString(135);
    static String consoleEnterCommandColor = EscapeSequences.getCustomTextColorString(12);


    public static void main(String[] args) throws Exception {
        Game testGame = new Game();
        testGame.newGame();

        String consoleUserStatus = "[not logged in]";

        // TODO: Print welcome message

        System.out.print(getBoardAsString((Board)testGame.getBoard(), ChessGame.TeamColor.WHITE));
        System.out.println();
        System.out.print(getBoardAsString((Board)testGame.getBoard(), ChessGame.TeamColor.BLACK));

        while (activeConsole) {
            System.out.print(consoleUserStatusTextColor + consoleUserStatus
                    + consoleEnterCommandColor + ": Enter a Command >>> "
                    + EscapeSequences.RESET_ALL_FORMATTING);

            Scanner consoleInput = new Scanner(System.in);
            String inputLine = consoleInput.nextLine();
            inputLine = inputLine.toLowerCase();

            String[] consoleCommand = inputLine.split("\s+");

            switch(consoleCommand[0]) {
                case "quit" ->  {
                    activeConsole = false;
                    if(loggedIn){
                        // TODO: IF LOGGED IN, LOG OUT USER
                    }
                    System.out.println("Exiting. Thanks for playing!");
                }
                case "register" -> {
                    // get params: username, password, email
                    if (consoleCommand.length == 4) {
                        String username = consoleCommand[1];
                        String password = consoleCommand[2];
                        String email = consoleCommand[3];
                        user = registerUser(username, password, email);
                        consoleUserStatus = "[" + user.getUsername() + "]";
                    }
                    else {
                        System.out.println(minorErrorTextColor + "Invalid format, use \"register <username> <password> <email>\"" + EscapeSequences.RESET_ALL_FORMATTING);
                    }
                }
                case "login" -> {
                    // get params: username, password
                    if (consoleCommand.length == 3) {
                        String username = consoleCommand[1];
                        String password = consoleCommand[2];
                        System.out.println("Logged in user " + username + "!");
                        loggedIn = true;

                        consoleUserStatus = "[" + username + "]";
                    }
                    else {
                        System.out.println(minorErrorTextColor + "Invalid format, use \"login <username> <password>\"" + EscapeSequences.RESET_ALL_FORMATTING);
                    }
                }
                case "create" -> {
                    // TODO: implement create
                }
                case "list" -> {
                    // TODO: implement list
                }
                case "join" -> {
                    // TODO: implement join
                }
                case "logout" -> {
                    if(!loggedIn){
                        System.out.println(minorErrorTextColor + "Not logged in.");
                        break;
                    }
                    // TODO: send logout
                    System.out.println("Logged out user ");
                }
                default -> {
                    // includes "help"
                    System.out.println(printMenu());
                }
            }
        }
    }

    private static UserData registerUser(String username, String password, String email) throws Exception {
        UserData newUser = new UserData(username, password, email);

        // send the request to the server
        var body = Map.of("username", username, "password", password, "email", email);

        HttpURLConnection connection = sendRequest(url + "/user", "POST", body.toString());
//        receiveResponse(connection);

        sessionToken = readResponseBody(connection, AuthToken.class);

        System.out.println("Registered user " + username + "!");
        loggedIn = true;
        return newUser;
    }

    private static String printMenu() {
        StringBuilder menuText = new StringBuilder();

        menuText.append("\n").append(EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_GREEN).append("Valid Commands:\n");
        if(loggedIn) {
            menuText.append(formatMenuItem("create", "<name>", "create a new game"));
            menuText.append(formatMenuItem("list", "", "show all games"));
            menuText.append(formatMenuItem("join", "<gameID> [WHITE|BLACK|<empty>]", "join an existing game as the white or black player, or just to observe"));
            menuText.append(formatMenuItem("logout", "", "log out of the game"));
        }
        else {
            menuText.append(formatMenuItem("register", "<username> <password> <email>", "create a new account"));
            menuText.append(formatMenuItem("login", "<username> <password>", "log in as an existing user"));
        }
        menuText.append(formatMenuItem("quit", "", "exit out of the console"));
        menuText.append(formatMenuItem("help", "", "show this menu"));

        return menuText.toString();
    }

    private static String formatMenuItem(String command, String params, String definition) {
        return "\t" + commandTextColor + command + " "
                + commandParameterTextColor + params
                + definitionTextColor + " - " + definition + "\n";
    }

    public static String getBoardAsString(Board board, ChessGame.TeamColor colorDown) {
        StringBuilder output = new StringBuilder();

        // black on top, white on bottom
        int[] rows = new int[]{8,7,6,5,4,3,2,1};
        int[] cols = new int[]{1,2,3,4,5,6,7,8};

        if(colorDown == ChessGame.TeamColor.BLACK) {
            // swap index direction (white on top, black on bottom)
            int[] temp = rows.clone();
            rows = cols;
            cols = temp;
        }

        // print the column markers
        output.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
        output.append("   ");
        for(int col : cols) { output.append(EscapeSequences.SPACER).append(colToLetter(col)).append(" "); }
        output.append(EscapeSequences.RESET_ALL_FORMATTING).append("\n");

        for(int row : rows) {
            // print the row marker
            output.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY).append(" ").append(row).append(" ");

            for(int col : cols) {
                if(row % 2 != col % 2) {
                    // color every other tile black
                    output.append(EscapeSequences.SET_BG_COLOR_BLACK);
                }
                else {
                    output.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                }

                Piece current = board.getPiece(row, col);
                if(current != null){
                    output.append(pieceToStringUnicode(current));
                }
                else {
                    output.append(EscapeSequences.EMPTY_SQUARE); // whitespace
                }

                output.append(EscapeSequences.RESET_ALL_FORMATTING);
            }
            // print the row marker
            output.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY).append(" ").append(row).append(" ").append(EscapeSequences.RESET_ALL_FORMATTING).append("\n");
        }

        // print column markers again
        output.append(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);
        output.append("   ");
        for(int col : cols) { output.append(EscapeSequences.SPACER).append(colToLetter(col)).append(" "); }
        output.append(EscapeSequences.RESET_ALL_FORMATTING).append("\n");

        return output.toString();
    }

    public static String pieceToStringUnicode(Piece piece) {
        String result;

        if(piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            result = EscapeSequences.BLACK_PIECE_COLOR;
        }
        else {
            result = EscapeSequences.WHITE_PIECE_COLOR;
        }
        result += switch (piece.getPieceType()) {
            case KING -> EscapeSequences.KING_ICON;
            case QUEEN -> EscapeSequences.QUEEN_ICON;
            case ROOK -> EscapeSequences.ROOK_ICON;
            case BISHOP -> EscapeSequences.BISHOP_ICON;
            case KNIGHT -> EscapeSequences.KNIGHT_ICON;
            case PAWN -> EscapeSequences.PAWN_ICON;
        };

        return result;
    }

    static String colToLetter(int col) {
        return switch (col) {
            case 1 -> "a";
            case 2 -> "b";
            case 3 -> "c";
            case 4 -> "d";
            case 5 -> "e";
            case 6 -> "f";
            case 7 -> "g";
            case 8 -> "h";
            default -> "";
        };
    }

    private static HttpURLConnection sendRequest(String url, String method, String body) throws Exception {
        URI uri = new URI(url);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
//        http.setReadTimeout();
        if (method.equals("POST")) {
            http.setDoOutput(true);
        }
        writeRequestBody(body, http);
        http.connect();
        System.out.printf("= Request =========\n[%s] %s\n\n%s\n\n", method, url, body);
        return http;
    }

    private static void writeRequestBody(String body, HttpURLConnection http) throws Exception {
        if (!body.isEmpty()) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private static void receiveResponse(HttpURLConnection http) throws Exception {
        var statusCode = http.getResponseCode();
        var statusMessage = http.getResponseMessage();

        Object responseBody = readResponseBody(http, Map.class);
        System.out.printf("= Response =========\n[%d] %s\n\n%s\n\n", statusCode, statusMessage, responseBody);
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

