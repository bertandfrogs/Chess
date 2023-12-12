import chess.Game;
import chess.InvalidMoveException;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import jakarta.websocket.DeploymentException;
import models.GameData;
import service.*;
import ui.ConsoleOutput;
import utils.ClientDisplay;
import utils.ClientState;
import webSocketMessages.client.GameCommand;
import webSocketMessages.client.JoinPlayer;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static ui.EscapeSequences.*;

public class Client implements ClientDisplay {
    // Class Variables
    static boolean activeConsole = true;
    static Console consoleInput = System.console();
    static String activeUsername = "";
    static String authToken;
    static GameData clientGame;
    static final String urlHTTP = "http://localhost:8080";
    static final String urlWS = "ws://localhost:8080/connect";
    static ClientState clientState = ClientState.logged_out;
    static WebSocketClient websocket;
    static ServerFacade server;

    public Client() throws Exception {
        server = new ServerFacade(urlHTTP);
        websocket = new WebSocketClient(urlWS, this);
    }

    public void run() throws Exception {
        try {
            ConsoleOutput.printFormatted("Welcome to Chess!", THEME_ACCENT_2, SET_TEXT_BOLD);
            ConsoleOutput.printMenu(ClientState.logged_out);

            // a loop that continuously gets input from the console
            while (activeConsole) {
                String userInput = consoleInput.readLine(ConsoleOutput.mainConsolePrompt(activeUsername));
                if(userInput != null) {
                    userInput = userInput.toLowerCase();

                    switch (clientState) {
                        case logged_out -> {
                            parseLoggedOutCommands(userInput);
                        }
                        case logged_in -> {
                            parseLoggedInCommands(userInput);
                        }
                        case playing_game_black, playing_game_white -> {
                            parsePlayerCommands(userInput);
                        }
                        case observing_game -> {
                            parseObserverCommands(userInput);
                        }
                    }
                }
            }
        } catch (DeploymentException e) {
            ConsoleOutput.printError("Could not connect to server, it may not be running.");
        }
    }

    private static void parseLoggedOutCommands(String input) {
        switch(input) {
            case "quit", "leave", "exit" ->  {
                activeConsole = false;
                ConsoleOutput.printActionSuccess("Exiting program. Bye!");
            }
            case "register" -> {
                // ask them for username, password, and email
                ConsoleOutput.printFormatted("Register New User (or enter \"cancel\")", THEME_ACCENT_1, SET_TEXT_BOLD);

                String username = consoleInput.readLine(ConsoleOutput.formatConsolePrompt("> Enter desired username: "));
                if(checkIfCanceled(username, input)) return;

                String password = String.valueOf(consoleInput.readPassword(ConsoleOutput.formatConsolePrompt("> Enter desired password: ")));
                if(checkIfCanceled(password, input)) return;

                String email = consoleInput.readLine(ConsoleOutput.formatConsolePrompt("> Enter your email: "));
                if(checkIfCanceled(email, input)) return;

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
            case "login" -> {
                // ask them for username and password
                ConsoleOutput.printFormatted("Login User (or enter \"cancel\")", THEME_ACCENT_1, SET_TEXT_BOLD);
                String username = consoleInput.readLine(ConsoleOutput.formatConsolePrompt("> Enter username: "));
                if(checkIfCanceled(username, input)) return;

                String password = String.valueOf(consoleInput.readPassword(ConsoleOutput.formatConsolePrompt("> Enter password: ")));
                if(checkIfCanceled(password, input)) return;

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

    private static void parseLoggedInCommands(String input) throws Exception {
        switch(input) {
            case "quit", "leave", "exit" ->  {
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
                ConsoleOutput.printFormatted("Create Game (or enter \"cancel\")", THEME_ACCENT_1, SET_TEXT_BOLD);
                String gameName = consoleInput.readLine(ConsoleOutput.formatConsolePrompt("> Enter game name: "));
                if(checkIfCanceled(gameName, input)) return;

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
            case "list" -> {
                GameResponse[] games = server.listGames(authToken).games;

                if(games.length == 0) {
                    ConsoleOutput.printWarning("There are no games. Enter \"create\" to make one!");
                }
                else {
                    ArrayList<GameResponse> gamesAsList = new ArrayList<>(Arrays.stream(games).toList());
                    gamesAsList.sort(Comparator.comparingInt(a -> a.gameID));
                    ConsoleOutput.printGameList(gamesAsList);
                }
            }
            case "join" -> {
                ConsoleOutput.printFormatted("Join Game (or enter \"cancel\")", THEME_ACCENT_1, SET_TEXT_BOLD);

                int gameID = getGameId(input);
                if(gameID == -1) return;
                
                String playerColorString = consoleInput.readLine(ConsoleOutput.formatConsolePrompt("> Enter Desired Player Color (Black/White): "));
                ChessGame.TeamColor color = getTeamColorFromString(playerColorString);
                while (color == null) {
                    if (checkIfCanceled(playerColorString, input)) return;
                    ConsoleOutput.printWarning("Invalid input. Please enter \"black\" or \"white\"");
                    playerColorString = consoleInput.readLine(ConsoleOutput.formatConsolePrompt("> Enter Desired Player Color (Black/White): "));
                    color = getTeamColorFromString(playerColorString);
                }

                try {
                    GameJoinResponse response = server.joinGame(authToken, gameID, color);
                    clientState = switch (color) {
                        case WHITE -> ClientState.playing_game_white;
                        case BLACK -> ClientState.playing_game_black;
                    };
                    websocket.sendCommand(new JoinPlayer(authToken, gameID, color));
                    ConsoleOutput.printActionSuccess("Joined game " + gameID + " as " + color);
                    ConsoleOutput.printMenu(clientState);
                }
                catch (Exception e) {
                    if(e.getMessage().contains("400")) {
                        ConsoleOutput.printError("Couldn't join game: Game ID not found");
                    }
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
            case "observe" -> {
                ConsoleOutput.printFormatted("Observe Game (or enter \"cancel\")", THEME_ACCENT_1, SET_TEXT_BOLD);

                int gameID = getGameId(input);

                if(gameID == -1) return;

                try {
                    GameJoinResponse response = server.joinGame(authToken, gameID, null);
                    websocket.sendCommand(new GameCommand(authToken, GameCommand.CommandType.JOIN_OBSERVER, gameID));
                    ConsoleOutput.printActionSuccess("Joined game " + gameID + " as observer");
                    clientState = ClientState.observing_game;
                    ConsoleOutput.printMenu(ClientState.observing_game);
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

    private static void parsePlayerCommands(String input) throws Exception {
        switch(input) {
            case "leave", "quit", "exit" ->  {
                websocket.sendCommand(new GameCommand(authToken, GameCommand.CommandType.LEAVE, clientGame.getGameId()));
                clientState = ClientState.logged_in;
                clientGame = null;
                ConsoleOutput.printActionSuccess("Exiting Game. Thanks for playing!");
                ConsoleOutput.printMenu(clientState);
            }
            case "help" -> {
                ConsoleOutput.printMenu(clientState);
            }
            case "redraw" -> {
                // gets from its own Game object
                ConsoleOutput.printBoard(clientGame.getGame(), clientState);
            }
            case "move" -> {
                // should check if it's valid with its own Game object, then call the server

                /* old code ------
                // Position startPos, endPos;
                //                    ChessPiece.PieceType promotionPiece;
                //
                //                    // get first position from user input
                //                    if(consoleCommand.length >= 2 && consoleCommand[1] != null){
                //                        String strStartPos = consoleCommand[1];
                //                        startPos = parsePosition(strStartPos);
                //                        if(startPos == null) {
                //                            System.out.println("Invalid syntax for start position. Positions are formatted like this: [column (a-h)][row (1-8)]");
                //                            break;
                //                        }
                //                    }
                //                    else {
                //                        System.out.println("Enter movement in this format: \"move b1 c3\"");
                //                        break;
                //                    }
                //
                //                    // get second position from user input
                //                    if(consoleCommand.length >= 3 && consoleCommand[2] != null){
                //                        String strEndPos = consoleCommand[2];
                //                        endPos = parsePosition(strEndPos);
                //                        if(endPos == null) {
                //                            System.out.println("Invalid syntax for end position. Positions are formatted like this: [column (a-h)][row (1-8)]");
                //                            break;
                //                        }
                //                    }
                //                    else {
                //                        System.out.println("Enter movement in this format: \"move b1 c3\", or \"move d7 d8 queen\" (for pawn promotion)");
                //                        break;
                //                    }
                //
                //                    // get promotion piece, if necessary
                //                    if(consoleCommand.length >= 4 && consoleCommand[3] != null){
                //                        String strPromotion = consoleCommand[3];
                //                        promotionPiece = parseType(strPromotion);
                //                    }
                //                    else {
                //                        promotionPiece = null;
                //                    }
                //
                //                    ChessMove move = new Move(startPos, endPos, promotionPiece);
                //
                //                    try {
                //                        game.makeMove(move);
                //                        printBoard();
                //                    } catch (InvalidMoveException e) {
                //                        System.out.println(e.getMessage());
                //                    }

                 */
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

    private static void parseObserverCommands(String input) throws Exception {
        Game testGame = new Game();
        testGame.newGame();

        switch(input) {
            case "leave", "quit", "exit" ->  {
                clientState = ClientState.logged_in;
                ConsoleOutput.printActionSuccess("Exiting Game. Thanks for watching!");
                ConsoleOutput.printMenu(clientState);
            }
            case "help", "info" -> {
                ConsoleOutput.printMenu(clientState);
                ConsoleOutput.printBoard(testGame, clientState);
            }
            default -> ConsoleOutput.printWarning("Unknown command. Enter \"help\" for valid commands.");
        }
    }

    private static int getGameId(String input){
        String gameToJoin = consoleInput.readLine(ConsoleOutput.formatConsolePrompt("> Enter Game ID: "));
        while(inputNotInteger(gameToJoin)) {
            if(checkIfCanceled(gameToJoin, input)) return -1;
            ConsoleOutput.printWarning("Game ID must be a number.");
            gameToJoin = consoleInput.readLine(ConsoleOutput.formatConsolePrompt("> Enter Game ID: "));
        }

        return Integer.parseInt(gameToJoin);
    }

    private static boolean checkIfCanceled(String input, String process) {
        if(input.equalsIgnoreCase("cancel")) {
            ConsoleOutput.printWarning("Canceled " + process + ".");
            return true;
        }
        return false;
    }

    // returns the TeamColor object given a String input. Returns null if input is bad.
    private static ChessGame.TeamColor getTeamColorFromString(String colorString) {
        ChessGame.TeamColor color = null;

        if(colorString.equalsIgnoreCase("BLACK")){
            color = ChessGame.TeamColor.BLACK;
        }
        if(colorString.equalsIgnoreCase("WHITE")) {
            color = ChessGame.TeamColor.WHITE;
        }

        return color;
    }

    static boolean inputNotInteger(String input) {
        try {
            Integer.parseInt(input);
            return false;
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

    // From the utils.ClientDisplay interface
    @Override
    public void showNotification(String message) {
        ConsoleOutput.printNotification(message);
        printConsolePrompt();
    }

    @Override
    public void showError(String error) {
        ConsoleOutput.printWebSocketError(error);
        printConsolePrompt();
    }

    @Override
    public void updateGameData(GameData gameData) {
        clientGame = gameData;
        if(clientGame != null){
            ConsoleOutput.printBoard(gameData.getGame(), clientState);
            printConsolePrompt();
        }
    }

    @Override
    public void updateGameWithMove(ChessMove move) {
        try {
            Game game = clientGame.getGame();
            game.makeMove(move);
            clientGame.setGame(game);
            ConsoleOutput.printBoard(game, clientState);
        }
        catch (InvalidMoveException e) {
            ConsoleOutput.printError("Something went wrong while updating your board.");
        }
        printConsolePrompt();
    }

    public void printConsolePrompt() {
        System.out.print(ConsoleOutput.mainConsolePrompt(activeUsername));
    }
}

