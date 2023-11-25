import chess.Board;
import chess.Game;
import chess.interfaces.ChessGame;
import chess.pieces.Piece;
import ui.EscapeSequences;

public class Client {

    public static void main(String[] args) {
        Game testGame = new Game();
        testGame.newGame();
        System.out.print(getBoardAsString((Board)testGame.getBoard(), ChessGame.TeamColor.WHITE));
        System.out.println();
        System.out.print(getBoardAsString((Board)testGame.getBoard(), ChessGame.TeamColor.BLACK));
    }

    public static String getBoardAsString(Board board, ChessGame.TeamColor colorDown) {
        StringBuilder output = new StringBuilder();

        // black on top, white on bottom
        int[] rows = new int[]{8,7,6,5,4,3,2,1};
        int[] cols = new int[]{1,2,3,4,5,6,7,8};

        if(colorDown == ChessGame.TeamColor.BLACK) {
            // swap index direction
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
        String ANSI_RED_TXT = "\u001B[31m";

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

