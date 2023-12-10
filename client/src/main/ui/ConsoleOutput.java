package ui;

import chess.Board;
import chess.Game;
import chess.interfaces.ChessGame;
import chess.pieces.Piece;
import service.GameResponse;
import utils.ClientState;

import java.util.ArrayList;

import static ui.EscapeSequences.*;

public class ConsoleOutput {

    // Helper methods for console output
    public static String mainConsolePrompt(String activeUsername) {
        String userStatus = (!activeUsername.isEmpty()) ? activeUsername : "Logged Out";

        return(THEME_DARK + "[" + userStatus + "]"
                + THEME_PRIMARY + " Enter a Command >>> "
                + EscapeSequences.THEME_PRIMARY_LIGHT);
    }

    public static String formatConsolePrompt(String message) {
        return (THEME_PRIMARY + message + RESET_ALL_FORMATTING);
    }

    public static void printMenu(ClientState clientState) {
        printFormatted("Valid Commands:", THEME_PRIMARY, SET_TEXT_ITALIC);
        if(clientState == ClientState.logged_in) {
            printMenuItem("create", "create a new game");
            printMenuItem("list", "show all games");
            printMenuItem("join", "join an existing game as white or black");
            printMenuItem("observe", "observe an existing game");
            printMenuItem("logout", "log out user");
            printMenuItem("quit", "exit out of the console");
        }
        else if (clientState == ClientState.logged_out){
            printMenuItem("register", "create a new account");
            printMenuItem("login", "log in as an existing user");
            printMenuItem("quit", "exit out of the console");
        }
        else if (clientState == ClientState.playing_game_white || clientState == ClientState.playing_game_black){
            printMenuItem("leave", "leave current game");
            // TODO: implement
        }
        else if (clientState == ClientState.observing_game){
            printMenuItem("leave", "leave current game");
            // TODO: implement
        }

        printMenuItem("help", "show this menu");
        System.out.println();
    }

    public static void printBoard(Game game, ClientState clientState) {
        System.out.println();
        if(clientState == ClientState.playing_game_white) {
            System.out.print(getBoardAsString((Board)game.getBoard(), ChessGame.TeamColor.WHITE));
        }
        else if(clientState == ClientState.playing_game_black) {
            System.out.print(getBoardAsString((Board)game.getBoard(), ChessGame.TeamColor.BLACK));
        }
        else {
            System.out.print(getBoardAsString((Board)game.getBoard(), ChessGame.TeamColor.WHITE));
        }
    }

    public static void printFormatted(String message, String color, String style) {
        System.out.println("\n" + style + color + message + RESET_ALL_FORMATTING);
    }

    public static void printMenuItem(String command, String definition) {
        System.out.println("\t" + THEME_PRIMARY_LIGHT + command + " "
                + THEME_PRIMARY + " - " + definition + RESET_ALL_FORMATTING);
    }

    public static void printGameList(ArrayList<GameResponse> games) {
        System.out.println();
        final String line = "--------------------------------------------------------------------%n";
        printFormatted("Game List:", THEME_PRIMARY, SET_TEXT_ITALIC);
        System.out.print(THEME_ACCENT_2);
        System.out.printf(line);
        System.out.printf("| %-4s | %-12s | %-12s | %-12s | %-12s |%n",
                "ID", "Game Name", "White Player", "Black Player", "Game State");
        System.out.printf(line);

        for (GameResponse game : games){
            System.out.printf("| %-4d | %-12s | %-12s | %-12s | %-12s |%n",
                    game.gameID, game.gameName, (game.whiteUsername != null) ? game.whiteUsername : "", (game.blackUsername != null) ? game.blackUsername : "", (game.gameState != null) ? game.gameState : "");
        }
        System.out.printf(line);
        System.out.println(RESET_ALL_FORMATTING);
    }

    public static void printActionSuccess(String message) {
        System.out.println("\n" + SET_TEXT_ITALIC + THEME_ACCENT_1 + message + RESET_ALL_FORMATTING);
    }

    public static void printWarning(String message) {
        printFormatted(message, THEME_WARNING, "");
    }

    public static void printError(String message) {
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
        output.append(EscapeSequences.RESET_ALL_FORMATTING);

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
}
