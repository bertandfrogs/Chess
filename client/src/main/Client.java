import chess.*;
import chess.interfaces.ChessGame;

import service.*;

import ui.ConsoleOutput;
import utils.ClientState;

import static ui.EscapeSequences.*;

import java.util.*;

public class Client {
    // Class Variables
    static boolean activeConsole = true;
    static String activeUsername = "";
    static String authToken;
    static Game clientGame;
    static boolean loadGame = false;
    static final String urlHTTP = "http://localhost:8080";
    static final String urlWS = "ws://localhost:8080/connect";
    static ClientState clientState = ClientState.logged_out;
    static WebSocketClient websocket;
    static ServerFacade server;

    public static void main(String[] args) throws Exception {
        server = new ServerFacade(urlHTTP);
        websocket = new WebSocketClient(urlWS);

        ConsoleOutput.printFormatted("Welcome to Chess!", THEME_ACCENT_2, SET_TEXT_BOLD);
        ConsoleOutput.printMenu(clientState);

        // a loop that continuously gets input from the console
        while (activeConsole) {
            ConsoleOutput.printConsolePrompt(activeUsername);

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
                case playing_game_black, playing_game_white -> {
                    parsePlayerCommands(consoleCommand);
                }
                case observing_game -> {
                    parseObserverCommands(consoleCommand);
                }
            }
        }
    }

    private static void parseLoggedOutCommands(String[] input) {
        switch(input[0]) {
            case "quit" ->  {
                activeConsole = false;
                ConsoleOutput.printActionSuccess("Exiting program. Bye!");
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
                        clientState = ClientState.logged_in;
                        activeUsername = username;
                        ConsoleOutput.printActionSuccess("Registered user " + username + "! You are now logged in.");
                        ConsoleOutput.printMenu(clientState);
                    }
                    catch (Exception e) {
                        if(e.getMessage().contains("403")) {
                            ConsoleOutput.printError("Couldn't register user: Username is already taken.");
                        }
                        else {
                            ConsoleOutput.printError("Couldn't register user. Error: " + e.getMessage());
                        }
                    }
                }
                else {
                    ConsoleOutput.printWarning("Invalid format, use \"register <username> <password> <email>\"");
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
                        clientState = ClientState.logged_in;
                        activeUsername = username;
                        ConsoleOutput.printActionSuccess("Logged in user " + username + "!");
                        ConsoleOutput.printMenu(clientState);
                    }
                    catch (ResponseException e) {
                        if(e.getMessage().contains("401")) {
                            ConsoleOutput.printError("Couldn't log in: Invalid username or password.");
                        }
                        else {
                            ConsoleOutput.printError("Couldn't log in. Error: " + e.getMessage());
                        }
                    }
                }
                else {
                    ConsoleOutput.printWarning("Invalid format, use \"login <username> <password>\"");
                }
            }
            case "create", "list", "join", "observe", "logout" -> {
                ConsoleOutput.printWarning("Not logged in. Enter \"help\" for valid commands.");
            }
            case "help" -> {
                ConsoleOutput.printMenu(clientState);
            }
            default -> {
                ConsoleOutput.printWarning("Unknown command. Enter \"help\" for valid commands.");
            }
        }
    }

    private static void parseLoggedInCommands(String[] input) throws Exception {
        switch(input[0]) {
            case "quit" ->  {
                activeConsole = false;
                server.logoutUser(authToken);
                ConsoleOutput.printActionSuccess("Logged out user " + activeUsername + ".");
                authToken = null;
                activeUsername = "";
                clientState = ClientState.logged_out;
                ConsoleOutput.printActionSuccess("Exiting program. Bye!");
            }
            case "register" -> {
                ConsoleOutput.printWarning("User " + activeUsername + " is already logged in. Please log out to register a different user.");
            }
            case "login" -> {
                ConsoleOutput.printWarning("User " + activeUsername + " is already logged in.");
            }
            case "create" -> {
                // get params: gameName
                if (input.length == 2) {
                    String gameName = input[1];
                    try {
                        CreateGameResponse response = server.createGame(authToken, gameName);
                        ConsoleOutput.printActionSuccess("Created new game with gameID: " + response.gameID);
                    }
                    catch (Exception e) {
                        if(e.getMessage().contains("401")) {
                            ConsoleOutput.printError("Couldn't create game: Unauthorized.");
                        }
                        else {
                            ConsoleOutput.printError("Couldn't create game. Error: " + e.getMessage());
                        }
                    }
                }
                else {
                    ConsoleOutput.printWarning("Invalid format, use \"create <gameName>\" (one word)");
                }
            }
            case "list" -> {
                GameResponse[] games = server.listGames(authToken).games;

                if(games.length == 0) {
                    ConsoleOutput.printWarning("There are no games. Enter \"create <gameName>\" to make one!");
                }
                else {
                    ArrayList<GameResponse> gamesAsList = new ArrayList<>(Arrays.stream(games).toList());
                    gamesAsList.sort(Comparator.comparingInt(a -> a.gameID));
                    ConsoleOutput.printGameList(gamesAsList);
                }
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
                        String playerRole;
                        if(color == null) {
                            playerRole = "observer";
                            clientState = ClientState.observing_game;
                        }
                        else if (color == ChessGame.TeamColor.WHITE) {
                            playerRole = "WHITE";
                            clientState = ClientState.playing_game_white;
                        }
                        else {
                            playerRole = "BLACK";
                            clientState = ClientState.playing_game_black;
                        }
                        ConsoleOutput.printActionSuccess("Joined game " + gameID + " as " + playerRole);
                        ConsoleOutput.printMenu(clientState);
                        initializeClientGame(gameID); // this method is called when the client joins a game, so I thought it best to put this call here.
                    }
                    catch (Exception e) {
                        if(e.getMessage().contains("401")) {
                            ConsoleOutput.printError("Couldn't join game: Unauthorized.");
                        }
                        else if(e.getMessage().contains("403")) {
                            ConsoleOutput.printError("Couldn't join game: Position already taken.");
                        }
                        else {
                            ConsoleOutput.printError("Couldn't join game. Error: " + e.getMessage());
                        }
                    }
                }
                else {
                    ConsoleOutput.printWarning("Invalid format, use \"join <gameID> <BLACK|WHITE>\" (gameID is a number)");
                }
            }
            case "observe" -> {
                if(input.length == 2) {
                    int gameID = Integer.parseInt(input[1]);

                    try {
                        GameJoinResponse response = server.joinGame(authToken, gameID, null);
                        ConsoleOutput.printActionSuccess("Joined game " + gameID + " as observer");
                        clientState = ClientState.observing_game;
                        ConsoleOutput.printMenu(ClientState.observing_game);
                        initializeClientGame(gameID);
                    }
                    catch (Exception e) {
                        if(e.getMessage().contains("401")) {
                            ConsoleOutput.printError("Couldn't join game: Unauthorized.");
                        }
                        else {
                            ConsoleOutput.printError("Couldn't join game. Error: " + e.getMessage());
                        }
                    }
                }
                else {
                    ConsoleOutput.printWarning("Invalid format, use \"observe <gameID>\"");
                }
            }
            case "logout" -> {
                try {
                    LogoutResponse response = server.logoutUser(authToken);
                    ConsoleOutput.printActionSuccess("Logged out user " + activeUsername + ".");
                    authToken = null;
                    activeUsername = "";
                    clientState = ClientState.logged_out;
                }
                catch (Exception e) {
                    if(e.getMessage().contains("401")) {
                        ConsoleOutput.printError("Couldn't log out: Invalid session token.");
                    }
                    else {
                        ConsoleOutput.printError("Couldn't log out. Error: " + e.getMessage());
                    }
                }
            }
            case "help" -> {
                ConsoleOutput.printMenu(clientState);
            }
            default -> {
                ConsoleOutput.printWarning("Unknown command. Enter \"help\" for valid commands.");
            }
        }
    }

    private static void parsePlayerCommands(String[] input) throws Exception {
        switch(input[0]) {
            case "leave" ->  {
                clientState = ClientState.logged_in;
                ConsoleOutput.printActionSuccess("Exiting Game. Thanks for playing!");
                ConsoleOutput.printMenu(clientState);
            }
            case "help" -> {
                ConsoleOutput.printMenu(clientState);
            }
            case "redraw" -> {
                // gets from its own Game object
                ConsoleOutput.printBoard(clientGame, clientState);
            }
            case "move" -> {
                // should check if it's valid with its own Game object, then call the server
            }
            case "resign" -> {
                // calls the server
            }
            case "moves" -> {
                // can get this from its own Game object
                if(clientGame != null) {
                    // TODO: show valid moves
                }
                else {
                    ConsoleOutput.printError("Sorry, something went wrong.");
                }
            }
            default -> ConsoleOutput.printWarning("Unknown command. Enter \"help\" for valid commands.");
        }
    }

    private static void parseObserverCommands(String[] input) throws Exception {
        Game testGame = new Game();
        testGame.newGame();

        switch(input[0]) {
            case "quit" ->  {
                clientState = ClientState.logged_in;
                ConsoleOutput.printActionSuccess("Exiting Game. Thanks for watching!");
                ConsoleOutput.printMenu(clientState);
            }
            case "help" -> {
                ConsoleOutput.printMenu(clientState);
                ConsoleOutput.printBoard(testGame, clientState);
            }
            default -> ConsoleOutput.printWarning("Unknown command. Enter \"help\" for valid commands.");
        }
    }

    // This method is called the very first time the client joins a game
    private static void initializeClientGame(int gameID) {
        // TODO: This method should read in the game data from the server.
        clientGame = new Game();
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

