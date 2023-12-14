package ui;

import chess.*;
import chess.pieces.Piece;
import service.GameResponse;
import models.ClientState;

import java.util.*;

import static ui.EscapeSequences.*;

public class ConsoleOutput {

    // Helper methods for console output
    public static String mainConsolePrompt(String activeUsername) {
        String userStatus = (!activeUsername.isEmpty()) ? activeUsername : "Logged Out";

        return(THEME_DARK + "[" + userStatus + "]"
                + THEME_PRIMARY + " Enter a Command >>> "
                + EscapeSequences.THEME_PRIMARY_LIGHT);
    }

    public static String getConsolePrompt(String message) {
        return (THEME_PRIMARY + message + RESET_ALL_FORMATTING);
    }

    public static String getMenu(ClientState clientState) {
        StringBuilder sb = new StringBuilder();
        sb.append(getFormatted("Valid Commands:\n", THEME_PRIMARY, SET_TEXT_ITALIC));
        sb.append(switch (clientState) {
            case logged_out -> getMenuLoggedOut();
            case logged_in -> getMenuLoggedIn();
            case playing_game_black, playing_game_white -> getMenuPlayingGame();
            case observing_game -> getMenuObserver();
        });
        sb.append(getMenuItem("help", "show this menu"));
        return sb.toString();
    }

    private static String getMenuLoggedOut(){
        return getMenuItem("register", "create a new account") +
                getMenuItem("login", "log in as an existing user") +
                getMenuItem("quit", "exit out of the console");
    }

    private static String getMenuLoggedIn() {
        return getMenuItem("create", "create a new game")
            + getMenuItem("list", "show all games")
            + getMenuItem("join", "join an existing game as white or black")
            + getMenuItem("observe", "observe an existing game")
            + getMenuItem("logout", "log out user")
            + getMenuItem("quit", "exit out of the console");
    }

    private static String getMenuPlayingGame() {
        return getMenuItem("move", "move a piece on the board") +
            getMenuItem("redraw", "redraw the current game board") +
            getMenuItem("chat", "send a message to others in the game") +
            getMenuItem("moves", "show valid moves that can be made") +
            getMenuItem("resign", "resign from the game") +
            getMenuItem("leave", "leave current game");
    }

    private static String getMenuObserver() {
        return getMenuItem("chat", "send a message to others in the game") +
            getMenuItem("leave", "leave current game");
    }

    public static String getBoard(Game game, ClientState clientState, Position positionToHighlight) {
        String output;
        if(clientState == ClientState.playing_game_white) {
            output = "\n" + getBoardAsString((Board)game.getBoard(), ChessGame.TeamColor.WHITE, positionToHighlight)
                    + getActionSuccess("It is currently " + ((game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? "your" : "BLACK's")  + " turn.\n");
        }
        else if(clientState == ClientState.playing_game_black) {
            output = "\n" + getBoardAsString((Board)game.getBoard(), ChessGame.TeamColor.BLACK, positionToHighlight)
            + getActionSuccess("It is currently " + ((game.getTeamTurn() == ChessGame.TeamColor.BLACK) ? "your" : "WHITE's")  + " turn.\n");
        }
        else {
            output = "\n" + getBoardAsString((Board)game.getBoard(), ChessGame.TeamColor.WHITE, positionToHighlight)
                    + getActionSuccess("It is currently " + ((game.getTeamTurn() == ChessGame.TeamColor.BLACK) ? "BLACK's" : "WHITE's")  + " turn.\n");
        }
        return output;
    }

    public static void printFormatted(String message, String color, String style) {
        System.out.println("\n" + style + color + message + RESET_ALL_FORMATTING);
    }

    public static String getFormatted(String message, String color, String style) {
        return "\n" + style + color + message + RESET_ALL_FORMATTING;
    }

    public static void printMenuItem(String command, String definition) {
        System.out.println("\t" + THEME_PRIMARY_LIGHT + command + " "
                + THEME_PRIMARY + " - " + definition + RESET_ALL_FORMATTING);
    }

    public static String getMenuItem(String command, String definition) {
        return "\t" + THEME_PRIMARY_LIGHT + command + " "
                + THEME_PRIMARY + " - " + definition + RESET_ALL_FORMATTING + "\n";
    }

    public static String getGameList(ArrayList<GameResponse> games) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        final String line = "--------------------------------------------------------------------\n";
        sb.append(getFormatted("Game List:", THEME_PRIMARY, SET_TEXT_ITALIC)).append("\n");
        sb.append(THEME_ACCENT_2).append(line);
        sb.append(String.format("| %-4s | %-12s | %-12s | %-12s | %-12s |%n",
                "ID", "Game Name", "White Player", "Black Player", "Game State"));
        sb.append(line);

        for (GameResponse game : games){
            sb.append(String.format("| %-4d | %-12s | %-12s | %-12s | %-12s |%n",
                    game.gameID, game.gameName,
                    (game.whiteUsername != null) ? game.whiteUsername : "",
                    (game.blackUsername != null) ? game.blackUsername : "",
                    (game.gameState != null) ? game.gameState : ""));
        }

        sb.append(line).append(RESET_ALL_FORMATTING);
        return sb.toString();
    }

    public static String getNotification(String message){
        return getFormatted("**" + message + "**", THEME_WARNING, SET_TEXT_ITALIC);
    }

    public static String getWebSocketError(String message){
        return getFormatted("**" + message + "**", THEME_ERROR, SET_TEXT_ITALIC);
    }

    public static void printActionSuccess(String message) {
        System.out.println("\n" + SET_TEXT_ITALIC + THEME_ACCENT_1 + message + RESET_ALL_FORMATTING);
    }

    public static String getActionSuccess(String message) {
        return "\n" + SET_TEXT_ITALIC + THEME_ACCENT_1 + message + RESET_ALL_FORMATTING;
    }

    public static void printWarning(String message) {
        printFormatted(message, THEME_WARNING, "");
    }

    public static String getWarning(String message) {
        return getFormatted(message, THEME_WARNING, "");
    }

    public static void printError(String message) {
        printFormatted(message, THEME_ERROR, "");
    }

    public static String getError(String message) {
        return getFormatted(message, THEME_ERROR, "");
    }

    public static String getBoardAsString(Board board, ChessGame.TeamColor colorDown, Position positionToHighlight) {
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

        String highlightMessage = null;
        Set<Integer> highlightHashCodes = new HashSet<>();
        int selectedPositionHash = -1;

        // get the valid moves at that position, or return a message saying why it can't
        if(positionToHighlight != null) {
            selectedPositionHash = positionToHighlight.hashCode();
            Piece selected = board.getPiece(positionToHighlight);
            if (selected == null) {
                highlightMessage = "There isn't a piece at " + positionToHighlight.toChessNotation() + ".";
            }
            else {
                Collection<ChessMove> validMoves = selected.pieceMoves(board, positionToHighlight);
                if(validMoves.isEmpty()) {
                    highlightMessage = "There are no valid moves from " + positionToHighlight.toChessNotation() + ".";
                }
                else {
                    for (ChessMove move : validMoves) {
                        highlightHashCodes.add(move.getEndPosition().hashCode());
                    }
                    highlightMessage = "Showing valid moves from " + positionToHighlight.toChessNotation() + ".";
                }
            }
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
                int currentHash = (row*10)+col;
                if(currentHash != selectedPositionHash){
                    if(row % 2 != col % 2) {
                        // color every other tile black (dark green if highlighted)
                        if(highlightHashCodes.contains(currentHash)){
                            output.append(SET_BG_COLOR_DARK_GREEN);
                        }
                        else {
                            output.append(EscapeSequences.SET_BG_COLOR_BLACK);
                        }
                    }
                    else {
                        // color every other tile gray (green if highlighted)
                        if(highlightHashCodes.contains(currentHash)){
                            output.append(SET_BG_COLOR_GREEN);
                        }
                        else {
                            output.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                        }
                    }
                }
                else {
                    // the selected position
                    output.append(SET_BG_COLOR_YELLOW);
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
        output.append(EscapeSequences.RESET_ALL_FORMATTING);
        output.append("\n");
        if(highlightMessage != null) output.append(highlightMessage);

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

    public static String getChessMoveInfo() {
        return getFormatted("How To Enter Chess Moves:", THEME_PRIMARY, SET_TEXT_ITALIC) +
                getFormatted(" - Enter in the start position, a space, and the end position.", THEME_PRIMARY_LIGHT, "") +
                getFormatted("    - Example: \"e2 e4\"", THEME_PRIMARY, "") +
                getFormatted(" - If your pawn will get promoted, enter your move and the name of the promotion piece.", THEME_PRIMARY_LIGHT, "") +
                getFormatted("    - Example: \"g2 g1 queen\"", THEME_PRIMARY, "");
    }
}
