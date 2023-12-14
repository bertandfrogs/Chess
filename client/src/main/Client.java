import chess.Game;
import chess.InvalidMoveException;
import chess.Move;
import chess.Position;
import chess.ChessGame;
import chess.ChessPiece;
import models.GameData;
import service.*;
import ui.ConsoleOutput;
import utils.ClientDisplay;
import models.ClientState;
import webSocketMessages.client.GameCommand;
import webSocketMessages.client.JoinPlayer;
import webSocketMessages.client.MakeMove;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static ui.EscapeSequences.*;
import static models.ClientState.*;

public class Client implements ClientDisplay {
    // Class Variables
    static boolean activeConsole = true;
    static Console consoleInput = System.console();
    static String activeUsername = "";
    static String authToken;
    static GameData clientGame;
    private static final String HTTP = "http://";
    private static final String WS = "ws://";
    private static final String addressLocalhost = "localhost:";
    private static final String port = "8080";
    private static final String wsEndpoint = "/connect";
    static ClientState clientState;
    static WebSocketClient websocket;
    static ServerFacade server;

    public Client(String ip) throws Exception {
        String address = (ip != null) ? (ip + ":") : addressLocalhost;
        server = new ServerFacade(HTTP + address + port);
        websocket = new WebSocketClient(WS + address + port + wsEndpoint, this);
        clientState = logged_out;
    }

    public void run() {
        ConsoleOutput.printFormatted("Welcome to Chess!", THEME_ACCENT_2, SET_TEXT_BOLD);
        System.out.println(ConsoleOutput.getMenu(clientState));

        // a loop that continuously gets input from the console
        while (activeConsole) {
            String userInput = consoleInput.readLine(ConsoleOutput.mainConsolePrompt(activeUsername));
            if(userInput != null) {
                userInput = userInput.toLowerCase();
                String response = switch(userInput) {
                    case "help" -> help();
                    case "quit", "leave", "exit" -> exit();
                    case "register" -> register();
                    case "login" -> login();
                    case "create" -> create();
                    case "list" -> list();
                    case "join" -> join();
                    case "observe" -> observe();
                    case "logout" -> logout();
                    case "redraw" -> redraw();
                    case "move" -> move();
                    case "resign" -> resign();
                    case "moves" -> moves();
                    case "chat" -> chat();
                    default -> ConsoleOutput.getWarning("Unknown command. Enter \"help\" for valid commands.");
                };
                System.out.println(response);
            }
        }
    }


    private String help() {
        return ConsoleOutput.getMenu(clientState);
    }

    private String exit() {
        switch (clientState) {
            case logged_out -> {
                activeConsole = false;
                return ConsoleOutput.getActionSuccess("Exiting program. Bye!");
            }
            case logged_in -> {
                activeConsole = false;
                try {
                    server.logoutUser(authToken);
                    String response = ConsoleOutput.getActionSuccess("Logged out user " + activeUsername + ".");
                    authToken = null;
                    activeUsername = "";
                    clientState = logged_out;
                    return response + "\n" + ConsoleOutput.getActionSuccess("Exiting program. Bye!");
                }
                catch (ResponseException e) {
                    return ConsoleOutput.getError("Couldn't log out. Please try again.");
                }
            }
            case playing_game_black, playing_game_white, observing_game -> {
                try {
                    websocket.sendCommand(new GameCommand(authToken, GameCommand.CommandType.LEAVE, clientGame.getGameId()));
                    clientGame = null;
                    clientState = logged_in;
                    return ConsoleOutput.getActionSuccess("Exiting Game. Thanks for playing!") + ConsoleOutput.getMenu(clientState);
                } catch (IOException e) {
                    return ConsoleOutput.getError("Couldn't send message to server: " + e.getMessage());
                }
            }
        }
        return null;
    }

    private String register(){
        if (clientState == logged_out) {
            // ask them for username, password, and email
            ConsoleOutput.printFormatted("Register New User (or enter \"cancel\")", THEME_ACCENT_1, SET_TEXT_BOLD);

            String username = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter desired username: "));
            if(checkIfCanceled(username)) return ConsoleOutput.getActionSuccess("Canceled.");

            String password = String.valueOf(consoleInput.readPassword(ConsoleOutput.getConsolePrompt("> Enter desired password: ")));
            if(checkIfCanceled(password)) return ConsoleOutput.getActionSuccess("Canceled.");

            String email = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter your email: "));
            if(checkIfCanceled(email)) return ConsoleOutput.getActionSuccess("Canceled.");

            try {
                LoginResponse response = server.registerUser(username, password, email);
                authToken = response.authToken;
                clientState = logged_in;
                activeUsername = username;
                return ConsoleOutput.getActionSuccess("Registered user " + username + "! You are now logged in.");
            }
            catch (Exception e) {
                if(e.getMessage().contains("403")) {
                    return ConsoleOutput.getError("Couldn't register user: Username is already taken.");
                }
                else {
                    return ConsoleOutput.getError("Couldn't register user. Error: " + e.getMessage());
                }
            }
        }
        else {
            return ConsoleOutput.getWarning("Can't register a new user right now. User " + activeUsername + " is already logged in.");
        }
    }
    private String login(){
        if (clientState == logged_out) {
            // ask them for username and password
            ConsoleOutput.printFormatted("Login User (or enter \"cancel\")", THEME_ACCENT_1, SET_TEXT_BOLD);
            String username = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter username: "));
            if(checkIfCanceled(username)) return ConsoleOutput.getActionSuccess("Canceled.");

            String password = String.valueOf(consoleInput.readPassword(ConsoleOutput.getConsolePrompt("> Enter password: ")));
            if(checkIfCanceled(password)) return ConsoleOutput.getActionSuccess("Canceled.");

            try {
                LoginResponse response = server.loginUser(username, password);
                authToken = response.authToken;
                clientState = logged_in;
                activeUsername = username;
                return ConsoleOutput.getActionSuccess("Logged in user " + username + "!");
            }
            catch (ResponseException e) {
                if(e.getMessage().contains("401")) {
                    return ConsoleOutput.getError("Couldn't log in: Invalid username or password.");
                }
                else {
                    return ConsoleOutput.getError("Couldn't log in. Error: " + e.getMessage());
                }
            }
        }
        else {
            return ConsoleOutput.getWarning("Can't login right now. User " + activeUsername + " is already logged in.");
        }
    }
    private String create(){
        if (clientState == logged_in){
            // get params: gameName
            ConsoleOutput.printFormatted("Create Game (or enter \"cancel\")", THEME_ACCENT_1, SET_TEXT_BOLD);
            String gameName = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter game name: "));
            if(checkIfCanceled(gameName)) return ConsoleOutput.getActionSuccess("Canceled.");

            try {
                CreateGameResponse response = server.createGame(authToken, gameName);
                return ConsoleOutput.getActionSuccess("Created new game with gameID: " + response.gameID);
            }
            catch (Exception e) {
                if(e.getMessage().contains("401")) {
                    return ConsoleOutput.getError("Couldn't create game: Unauthorized.");
                }
                else {
                    return ConsoleOutput.getError("Couldn't create game. Error: " + e.getMessage());
                }
            }
        }
        else if (clientState == logged_out) {
            return ConsoleOutput.getWarning("Not logged in. Enter \"help\" for valid commands.");
        }
        else {
            return ConsoleOutput.getWarning("Can't create a new game right now. Enter \"help\" for valid commands.");
        }
    }
    private String list(){
        if (clientState == logged_in){
            try {
                GameResponse[] games = server.listGames(authToken).games;

                if(games.length == 0) {
                    return ConsoleOutput.getWarning("There are no games. Enter \"create\" to make one!");
                }
                else {
                    ArrayList<GameResponse> gamesAsList = new ArrayList<>(Arrays.stream(games).toList());
                    gamesAsList.sort(Comparator.comparingInt(a -> a.gameID));
                    return ConsoleOutput.getGameList(gamesAsList);
                }
            } catch (ResponseException e) {
                return ConsoleOutput.getError("Couldn't list games: " + e.getMessage());
            }
        }
        else if (clientState == logged_out) {
            return ConsoleOutput.getWarning("Not logged in. Enter \"help\" for valid commands.");
        }
        else {
            return ConsoleOutput.getWarning("Can't list games right now. Enter \"help\" for valid commands.");
        }
    }
    private String join(){
        if (clientState == logged_in){
            ConsoleOutput.printFormatted("Join Game (or enter \"cancel\")", THEME_ACCENT_1, SET_TEXT_BOLD);

            int gameID = getGameId();
            if(gameID == -1) return ConsoleOutput.getActionSuccess("Canceled.");

            String playerColorString = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter Desired Player Color (Black/White): "));
            ChessGame.TeamColor color = getTeamColorFromString(playerColorString);
            while (color == null) {
                if (checkIfCanceled(playerColorString)) return ConsoleOutput.getActionSuccess("Canceled.");
                ConsoleOutput.printWarning("Invalid input. Please enter \"black\" or \"white\"");
                playerColorString = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter Desired Player Color (Black/White): "));
                color = getTeamColorFromString(playerColorString);
            }

            try {
                GameJoinResponse response = server.joinGame(authToken, gameID, color);
                clientState = switch (color) {
                    case WHITE -> ClientState.playing_game_white;
                    case BLACK -> ClientState.playing_game_black;
                };
                websocket.sendCommand(new JoinPlayer(authToken, gameID, color));
                return ConsoleOutput.getActionSuccess("Joined game " + gameID + " as " + color);
            }
            catch (Exception e) {
                if(e.getMessage().contains("400")) {
                    return ConsoleOutput.getError("Couldn't join game: Game ID not found");
                }
                if(e.getMessage().contains("401")) {
                    return ConsoleOutput.getError("Couldn't join game: Unauthorized.");
                }
                else if(e.getMessage().contains("403")) {
                    return ConsoleOutput.getError("Couldn't join game: Position already taken.");
                }
                else {
                    return ConsoleOutput.getError("Couldn't join game. Error: " + e.getMessage());
                }
            }
        }
        else if (clientState == logged_out) {
            return ConsoleOutput.getWarning("Not logged in. Enter \"help\" for valid commands.");
        }
        else {
            return ConsoleOutput.getWarning("Can't join another game right now. Enter \"help\" for valid commands.");
        }
    }
    private String observe(){
        if (clientState == logged_in){
            ConsoleOutput.printFormatted("Observe Game (or enter \"cancel\")", THEME_ACCENT_1, SET_TEXT_BOLD);

            int gameID = getGameId();

            if(gameID == -1) return ConsoleOutput.getActionSuccess("Canceled.");

            try {
                GameJoinResponse response = server.joinGame(authToken, gameID, null);
                websocket.sendCommand(new GameCommand(authToken, GameCommand.CommandType.JOIN_OBSERVER, gameID));
                clientState = ClientState.observing_game;
                return ConsoleOutput.getActionSuccess("Joined game " + gameID + " as observer") + ConsoleOutput.getMenu(ClientState.observing_game);
            }
            catch (Exception e) {
                if(e.getMessage().contains("401")) {
                    return ConsoleOutput.getError("Couldn't join game: Unauthorized.");
                }
                else {
                    return ConsoleOutput.getError("Couldn't join game. Error: " + e.getMessage());
                }
            }
        }
        else if (clientState == logged_out) {
            return ConsoleOutput.getWarning("Not logged in. Enter \"help\" for valid commands.");
        }
        else {
            return ConsoleOutput.getWarning("Can't observe a game right now. Enter \"help\" for valid commands.");
        }
    }
    private String logout(){
        if (clientState == logged_in){
            try {
                LogoutResponse response = server.logoutUser(authToken);
                String logoutStr = ConsoleOutput.getActionSuccess("Logged out user " + activeUsername + ".");
                authToken = null;
                activeUsername = "";
                clientState = logged_out;
                return logoutStr;
            }
            catch (Exception e) {
                if(e.getMessage().contains("401")) {
                    return ConsoleOutput.getError("Couldn't log out: Invalid session token.");
                }
                else {
                    return ConsoleOutput.getError("Couldn't log out. Error: " + e.getMessage());
                }
            }
        }
        else if (clientState == logged_out) {
            return ConsoleOutput.getWarning("Not logged in. Enter \"help\" for valid commands.");
        }
        else {
            return ConsoleOutput.getWarning("Can't log out right now. Enter \"leave\" to exit current game.");
        }
    }
    private String redraw(){
        if (clientState == logged_out || clientState == logged_in) {
            return ConsoleOutput.getWarning("You're not currently in a game. Enter \"help\" for valid commands.");
        }
        else {
            // gets from its own Game object
            if(clientGame != null && clientGame.getGame() != null) {
                return ConsoleOutput.getBoard(clientGame.getGame(), clientState, null);
            }
            else {
                return ConsoleOutput.getError("Couldn't get game.");
            }
        }
    }
    private String move(){
        if (clientState == logged_out || clientState == logged_in) {
            return ConsoleOutput.getWarning("You're not currently in a game. Enter \"help\" for valid commands.");
        }
        else if (clientState == observing_game) {
            return ConsoleOutput.getWarning("You're not currently playing. Enter \"help\" for valid commands.");
        }
        else {
            if(clientGame.getGameState() == Game.State.pregame) {
                return ConsoleOutput.getWarning("Can't make a move yet, the game is not started.");
            }
            else if (clientGame.getGameState() == Game.State.finished) {
                return ConsoleOutput.getWarning("Can't make a move, the game is over.");
            }
            else if((clientState == playing_game_black && clientGame.getGame().getTeamTurn() != ChessGame.TeamColor.BLACK)
                || (clientState == playing_game_white && clientGame.getGame().getTeamTurn() != ChessGame.TeamColor.WHITE)) {
                return ConsoleOutput.getWarning("Can't make a move, it's not your turn.");
            }
            else {
                try {
                    Move move = getMove();
                    if (move == null) return ConsoleOutput.getActionSuccess("Canceled.");
                    Game gameCopy = new Game(clientGame.getGame());
                    gameCopy.makeMove(move); // will throw an exception if not valid, while keeping the client board in sync with the server.

                    websocket.sendCommand(new MakeMove(authToken, clientGame.getGameId(), move));
                    return ConsoleOutput.getActionSuccess("Made move " + move.toChessNotation());
                } catch (InvalidMoveException e) {
                    return ConsoleOutput.getError("Invalid move: " + e.getMessage());
                } catch (Exception e) {
                    return ConsoleOutput.getError("Couldn't make move: " + e.getMessage());
                }
            }
        }
    }

    private String resign(){
        if (clientState == logged_out || clientState == logged_in) {
            return ConsoleOutput.getWarning("You're not currently in a game. Enter \"help\" for valid commands.");
        }
        else if (clientState == observing_game) {
            return ConsoleOutput.getWarning("You're not currently playing. Enter \"help\" for valid commands.");
        }
        else {
            if(clientGame.getGameState() == Game.State.finished) {
                return ConsoleOutput.getWarning("Can't resign, game is already finished. Enter \"leave\" to leave the game.");
            }
            try {
                websocket.sendCommand(new GameCommand(authToken, GameCommand.CommandType.RESIGN, clientGame.getGameId()));
                clientGame.setGameState(Game.State.finished);
                return ConsoleOutput.getActionSuccess("You resigned from the game.");
            } catch (IOException e) {
                return ConsoleOutput.getError("Couldn't send message to server: " + e.getMessage());
            }
        }
    }
    private String moves(){
        if (clientState == logged_out || clientState == logged_in) {
            return ConsoleOutput.getWarning("You're not currently in a game. Enter \"help\" for valid commands.");
        }
        else {
            if(clientGame != null && clientGame.getGameState() != Game.State.finished) {
                Position highlightPos = getPosition();
                if(highlightPos == null) return ConsoleOutput.getActionSuccess("Canceled.");
                return ConsoleOutput.getBoard(clientGame.getGame(), clientState, highlightPos);
            }
            return ConsoleOutput.getWarning("No more moves can be made.");
        }
    }
    private String chat(){
        if (clientState == logged_out || clientState == logged_in) {
            return ConsoleOutput.getWarning("You're not currently in a game. Enter \"help\" for valid commands.");
        }
        else {
            String message = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter message: "));
            if(checkIfCanceled(message)) return ConsoleOutput.getActionSuccess("Canceled.");
            return "";
        }
    }

    private static int getGameId(){
        String gameToJoin = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter Game ID: "));
        while(inputNotInteger(gameToJoin)) {
            if(checkIfCanceled(gameToJoin)) return -1;
            ConsoleOutput.printWarning("Game ID must be a number.");
            gameToJoin = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter Game ID: "));
        }

        return Integer.parseInt(gameToJoin);
    }

    private static Move getMove() throws Exception {
        String moveStr = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter Chess Move (enter \"help\" for info): "));
        while (!isValidMove(moveStr) && clientGame.getGameState() == Game.State.active) {
            if(checkIfCanceled(moveStr)) return null;
            if(checkIfNeedHelp(moveStr)) {
                System.out.println(ConsoleOutput.getChessMoveInfo());
            }
            else {
                System.out.println(ConsoleOutput.getWarning("Invalid move syntax."));
            }
            moveStr = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter Chess Move (enter \"help\" for info): "));
        }
        if (clientGame.getGameState() == Game.State.active) {
            Move result = parseMove(moveStr);
            if (result != null) {
                return result;
            }
            else {
                throw new IOException("Couldn't parse move.");
            }
        }
        else {
            throw new Exception(ConsoleOutput.getError("Game is no longer active."));
        }
    }

    private static Position getPosition() {
        String position = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter Chess Position (or \"cancel\"): "));
        while (!isValidPosition(position)) {
            if (checkIfCanceled(position)) return null;
            position = consoleInput.readLine(ConsoleOutput.getConsolePrompt("> Enter Chess Position (or \"cancel\"): "));
        }
        return parsePosition(position);
    }

    private static boolean isValidMove(String input) {
        input = input.toLowerCase();
        return (input.matches("[a-h][1-8] [a-h][1-8].*")) && parseMove(input) != null;
    }

    private static boolean isValidPosition(String input) {
        input = input.toLowerCase();
        return (input.matches("[a-h][1-8]")) && parsePosition(input) != null;
    }

    private static Move parseMove(String input) {
        input = input.toLowerCase();
        String[] args = input.split(" ");
        if(args.length < 2 || args.length > 3) {
            return null;
        }
        Position startPos;
        Position endPos;
        startPos = parsePosition(args[0]);
        endPos = parsePosition(args[1]);
        ChessPiece.PieceType promotionPiece = null;
        if(args.length == 3) {
            try {
                promotionPiece = getPromotionPiece(args[2]);
            } catch (IllegalStateException e) {
                return null;
            }
        }
        if (startPos != null && endPos != null) {
            return new Move(startPos, endPos, promotionPiece);
        }
        else {
            return null;
        }
    }

    private static ChessPiece.PieceType getPromotionPiece(String input) {
        return switch (input) {
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            default -> throw new IllegalStateException("Invalid promotion piece: " + input);
        };
    }

    private static Position parsePosition(String input){
        input = input.toLowerCase();
        char[] chars = input.toCharArray();
        if(chars.length != 2) {
            return null;
        }
        int col = Character.getNumericValue(chars[0]) - 9; // uses the numeric value of the letter "a" in Unicode (10) and subtracts 9 to get a = column 1.
        int row = Character.getNumericValue(chars[1]);
        return new Position(row,col);
    }

    private static boolean checkIfCanceled(String input) {
        return input.equalsIgnoreCase("cancel");
    }

    private static boolean checkIfNeedHelp(String input) {
        return input.equalsIgnoreCase("help");
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
        System.out.println(ConsoleOutput.getNotification(message));
    }

    @Override
    public void showError(String error) {
        System.out.println(ConsoleOutput.getWebSocketError(error));
    }

    @Override
    public void updateGameData(GameData gameData) throws InterruptedException {
        clientGame = gameData;
        System.out.println(ConsoleOutput.getBoard(clientGame.getGame(), clientState, null));
        printConsolePrompt();
    }

    @Override
    public void updateGameState(Game.State gameState) {
        clientGame.setGameState(gameState);
    }

    public void printConsolePrompt() {
        System.out.print(ConsoleOutput.mainConsolePrompt(activeUsername));
    }
}

