import chess.*;
import chess.interfaces.ChessGame;
import chess.pieces.Piece;

import service.CreateGameResponse;
import service.GameJoinResponse;
import service.LoginResponse;
import service.LogoutResponse;

import ui.EscapeSequences;
import static ui.EscapeSequences.*;

import java.util.*;


public class Client {
    // Class Variables
    static boolean activeConsole = true;
    static String activeUsername = "";
    static String authToken;
    static final String url = "http://localhost:8080";
    enum State {
        logged_in,
        logged_out,
        playing_game,
        observing_game
    }
    static State clientState = State.logged_out;
    static ServerFacade server;

    public Client() {
        server = new ServerFacade(url);
    }

    public static void main(String[] args) throws Exception {
        server = new ServerFacade(url);

        Game testGame = new Game();
        testGame.newGame();

        // TODO: Print welcome message

        System.out.print(getBoardAsString((Board)testGame.getBoard(), ChessGame.TeamColor.WHITE));
        System.out.println();
        System.out.print(getBoardAsString((Board)testGame.getBoard(), ChessGame.TeamColor.BLACK));

        while (activeConsole) {
            printConsolePrompt();

            Scanner consoleInput = new Scanner(System.in);
            String inputLine = consoleInput.nextLine();
            inputLine = inputLine.toLowerCase();

            String[] consoleCommand = inputLine.split("\s+");

            switch (clientState) {
                case logged_out -> {
                    parseLoggedOutCommands(consoleCommand);
                }
                case logged_in -> {
                    parseLoggedInCommands(consoleCommand);
                }
                case playing_game -> {
                    parsePlayerCommands(consoleCommand);
                }
                case observing_game -> {
                    parseObserverCommands(consoleCommand);
                }
            }
        }
    }

    private static void parseLoggedOutCommands(String[] input) throws Exception {
        switch(input[0]) {
            case "quit" ->  {
                activeConsole = false;
                printActionSuccess("Exiting. Thanks for playing!");
            }
            case "register" -> {
                // get params: username, password, email
                if (input.length == 4) {
                    String username = input[1];
                    String password = input[2];
                    String email = input[3];
                    try {
                        LoginResponse response = server.registerUser(username, password, email);
                        authToken = response.authToken;
                        clientState = State.logged_in;
                        activeUsername = username;
                        printActionSuccess("Registered user " + username + "! You are now logged in.");
                    }
                    catch (Exception e) {
                        if(e.getMessage().contains("403")) {
                            printError("Couldn't register user: Username is already taken.");
                        }
                        else {
                            printError("Couldn't register user. Error: " + e.getMessage());
                        }
                    }
                }
                else {
                    printWarning("Invalid format, use \"register <username> <password> <email>\"");
                }
            }
            case "login" -> {
                // get params: username, password
                if (input.length == 3) {
                    String username = input[1];
                    String password = input[2];
                    try {
                        LoginResponse response = server.loginUser(username, password);
                        authToken = response.authToken;
                        clientState = State.logged_in;
                        activeUsername = username;
                        printActionSuccess("Logged in user " + username + "!");
                    }
                    catch (Exception e) {
                        if(e.getMessage().contains("401")) {
                            printError("Couldn't log in: Invalid username or password.");
                        }
                        else {
                            printError("Couldn't log in. Error: " + e.getMessage());
                        }
                    }
                }
                else {
                    printWarning("Invalid format, use \"login <username> <password>\"");
                }
            }
            case "create", "list", "join", "observe", "logout" -> {
                printWarning("Not logged in. Enter \"help\" for valid commands.");
            }
            case "help" -> {
                printMenu();
            }
            default -> {
                printWarning("Unknown command. Enter \"help\" for valid commands.");
            }
        }
    }

    private static void parseLoggedInCommands(String[] input) throws Exception {
        switch(input[0]) {
            case "quit" ->  {
                activeConsole = false;
                server.logoutUser(authToken);
                printActionSuccess("Exiting. Thanks for playing!");
            }
            case "register" -> {
                printWarning("User " + activeUsername + " is already logged in. Please log out to register a different user.");
            }
            case "login" -> {
                printWarning("User " + activeUsername + " is already logged in.");
            }
            case "create" -> {
                // get params: gameName
                if (input.length == 2) {
                    String gameName = input[1];
                    try {
                        CreateGameResponse response = server.createGame(authToken, gameName);
                        printActionSuccess("Created new game with gameID: " + response.gameID);
                    }
                    catch (Exception e) {
                        if(e.getMessage().contains("401")) {
                            printError("Couldn't create game: Unauthorized.");
                        }
                        else {
                            printError("Couldn't create game. Error: " + e.getMessage());
                        }
                    }
                }
                else {
                    printWarning("Invalid format, use \"create <gameName>\" (one word)");
                }
            }
            case "list" -> {
                // TODO: implement list
            }
            case "join" -> {
                if(input.length == 3 && inputIsInteger(input[1])) {
                    int gameID = Integer.parseInt(input[1]);
                    String playerColorString = input[2];
                    ChessGame.TeamColor color = null;
                    if(playerColorString.equalsIgnoreCase("BLACK")){
                        color = ChessGame.TeamColor.BLACK;
                    }
                    else if(playerColorString.equalsIgnoreCase("WHITE")) {
                        color = ChessGame.TeamColor.WHITE;
                    }
                    try {
                        GameJoinResponse response = server.joinGame(authToken, gameID, color);
                        String playerRole = (color != null) ? color.toString() : "observer";
                        printActionSuccess("Joined game " + gameID + " as " + playerRole);
                    }
                    catch (Exception e) {
                        if(e.getMessage().contains("401")) {
                            printError("Couldn't join game: Unauthorized.");
                        }
                        else {
                            printError("Couldn't join game. Error: " + e.getMessage());
                        }
                    }
                }
                else {
                    printWarning("Invalid format, use \"join <gameID> <BLACK|WHITE>\" (gameID is a number)");
                }
            }
            case "observe" -> {
                if(input.length == 2) {
                    int gameID = Integer.parseInt(input[1]);

                    try {
                        GameJoinResponse response = server.joinGame(authToken, gameID, null);
                        printActionSuccess("Joined game " + gameID + " as observer");
                    }
                    catch (Exception e) {
                        if(e.getMessage().contains("401")) {
                            printError("Couldn't join game: Unauthorized.");
                        }
                        else {
                            printError("Couldn't join game. Error: " + e.getMessage());
                        }
                    }
                }
                else {
                    printWarning("Invalid format, use \"observe <gameID>\"");
                }
            }
            case "logout" -> {
                try {
                    LogoutResponse response = server.logoutUser(authToken);
                    printActionSuccess("Logged out user " + activeUsername + ".");
                    authToken = null;
                    activeUsername = "";
                    clientState = State.logged_out;
                }
                catch (Exception e) {
                    if(e.getMessage().contains("401")) {
                        printError("Couldn't log out: Invalid session token.");
                    }
                    else {
                        printError("Couldn't log out. Error: " + e.getMessage());
                    }
                }
            }
            case "help" -> {
                printMenu();
            }
            default -> {
                printWarning("Unknown command. Enter \"help\" for valid commands.");
            }
        }
    }

    private static void parsePlayerCommands(String[] input) throws Exception {
        // TODO: implement
    }

    private static void parseObserverCommands(String[] input) throws Exception {
        // TODO: implement
    }

    // Helper methods for console output
    private static void printConsolePrompt() {
        String userStatus = (!activeUsername.isEmpty()) ? activeUsername : "Logged Out";

        System.out.print("\n" + THEME_DARK + "[" + userStatus + "]"
                + THEME_PRIMARY + " Enter a Command >>> "
                + EscapeSequences.THEME_PRIMARY_LIGHT);
    }

    private static void printMenu() {
        printFormatted("Valid Commands:", THEME_PRIMARY, SET_TEXT_ITALIC);
        if(clientState == State.logged_in) {
            printMenuItem("create", "<name>", "create a new game");
            printMenuItem("list", "", "show all games");
            printMenuItem("join", "<gameID> [WHITE|BLACK]", "join an existing game as white or black");
            printMenuItem("observe", "<gameID>", "observe an existing game");
            printMenuItem("logout", "", "log out of the game");
        }
        else if (clientState == State.logged_out){
            printMenuItem("register", "<username> <password> <email>", "create a new account");
            printMenuItem("login", "<username> <password>", "log in as an existing user");
        }
        else if (clientState == State.playing_game){
            // TODO: implement
        }
        else if (clientState == State.observing_game){
            // TODO: implement
        }
        printMenuItem("quit", "", "exit out of the console");
        printMenuItem("help", "", "show this menu");
    }

    private static void printFormatted(String message, String color, String style) {
        System.out.println(style + color + message + RESET_ALL_FORMATTING);
    }

    private static void printMenuItem(String command, String params, String definition) {
        System.out.println("\t" + THEME_PRIMARY_LIGHT + command + " "
                + THEME_ACCENT_2 + params
                + THEME_PRIMARY + " - " + definition + RESET_ALL_FORMATTING);
    }

    private static void printActionSuccess(String message) {
        System.out.println(SET_TEXT_ITALIC + THEME_ACCENT_1 + message + RESET_ALL_FORMATTING);
    }

    private static void printWarning(String message) {
        printFormatted(message, THEME_WARNING, "");
    }

    private static void printError(String message) {
        printFormatted(message, THEME_ERROR, "");
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

    static boolean inputIsInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}

