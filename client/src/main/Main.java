import chess.*;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPiece;
import chess.pieces.Piece;

import java.util.Scanner;

public class Main {
    private static final Game game = new Game();
    public static void main(String[] args) {
        // Using Scanner class to get input from console until "exit" is entered
        boolean activeConsole = true;

        game.newGame();
        System.out.println("Starting new game! Enter move [start] [end] to move your piece. (i.e. b2 b4)");
        printBoard();

        while(activeConsole){
            System.out.println("Current Team's Turn: " + game.getTeamTurn().toString());
            Scanner consoleInput = new Scanner(System.in);

            String inputLine = consoleInput.nextLine();
            inputLine = inputLine.toLowerCase();

            String[] consoleCommand = inputLine.split(" ");

            switch(consoleCommand[0]) {
                case "exit" ->  {
                    activeConsole = false;
                    System.out.println("Exiting. Thanks for playing!");
                }
                case "info" -> {
                    if(consoleCommand.length >= 2 && consoleCommand[1] != null){
                        String strPos = consoleCommand[1];
                        Position pos = parsePosition(strPos);
                        printInfo(pos);
                    }
                    else {
                        printInfo(null); // print general information about the game (not any given position)
                    }
                }
                case "reset" -> {
                    System.out.println("Resetting the game!");
                    game.newGame();
                    printBoard();
                }
                case "move" -> {
                    Position startPos, endPos;
                    ChessPiece.PieceType promotionPiece;

                    // get first position from user input
                    if(consoleCommand.length >= 2 && consoleCommand[1] != null){
                        String strStartPos = consoleCommand[1];
                        startPos = parsePosition(strStartPos);
                        if(startPos == null) {
                            System.out.println("Invalid syntax for start position. Positions are formatted like this: [column (a-h)][row (1-8)]");
                            break;
                        }
                    }
                    else {
                        System.out.println("Enter movement in this format: \"move b1 c3\"");
                        break;
                    }

                    // get second position from user input
                    if(consoleCommand.length >= 3 && consoleCommand[2] != null){
                        String strEndPos = consoleCommand[2];
                        endPos = parsePosition(strEndPos);
                        if(endPos == null) {
                            System.out.println("Invalid syntax for end position. Positions are formatted like this: [column (a-h)][row (1-8)]");
                            break;
                        }
                    }
                    else {
                        System.out.println("Enter movement in this format: \"move b1 c3\", or \"move d7 d8 queen\" (for pawn promotion)");
                        break;
                    }

                    // get promotion piece, if necessary
                    if(consoleCommand.length >= 4 && consoleCommand[3] != null){
                        String strPromotion = consoleCommand[3];
                        promotionPiece = parseType(strPromotion);
                    }
                    else {
                        promotionPiece = null;
                    }

                    ChessMove move = new Move(startPos, endPos, promotionPiece);

                    try {
                        game.makeMove(move);
                        printBoard();
                    } catch (InvalidMoveException e) {
                        System.out.println(e.getMessage());
                    }

                }
                case "help" -> {
                    printMenu(true);
                }
                default -> {
                    printMenu(false);
                }
            }
        }
    }

    private static void printMenu(boolean detailed) {
        if(!detailed){
            System.out.println("-----===Valid Commands===-----");
            System.out.println("\t- move [start] [end]");
            System.out.println("\t- move [start] [end] [promotion]");
            System.out.println("\t- reset");
            System.out.println("\t- info");
            System.out.println("\t- info [position]");
            System.out.println("\t- help");
            System.out.println("\t- exit");
            System.out.println("------------------------------\n");
        }
        else {
            System.out.println("-----===Information about Chess Commands===-----");
            System.out.println("\t- move [start] [end]");
            System.out.println("\t\tMoves a piece on the board.");
            System.out.println("\t\tPositions are formatted: column(a-h) row(1-8)");
            System.out.println("\t\tExample: move b2 b4");
            System.out.println("\t- move [start] [end] [promotion]");
            System.out.println("\t\tMoves a piece on the board, where a pawn reaches the end and gets promoted.");
            System.out.println("\t\tExample: move d7 d8 queen");
            System.out.println("\t- reset");
            System.out.println("\t\tResets the game.");
            System.out.println("\t- info");
            System.out.println("\t\tPrints info about the game.");
            System.out.println("\t- info [position]");
            System.out.println("\t\tPrints info about a certain position, and gets the valid moves from that position.");
            System.out.println("\t\tPositions are formatted: column(a-h) row(1-8)");
            System.out.println("\t\tExample: info h5");
            System.out.println("\t- help");
            System.out.println("\t\tPrints this help menu.");
            System.out.println("\t- exit");
            System.out.println("\t\tExits the game and ends the program.");
            System.out.println("------------------------------------------------\n");
        }
    }

    private static Position parsePosition(String input){
        input = input.toLowerCase();
        char[] chars = input.toCharArray();
        if(chars.length != 2) {
            return null;
        }
        int row = Character.getNumericValue(chars[1]);
        int col = Character.getNumericValue(chars[0]) - 9; // uses the numeric value of the letter "a" in Unicode (10) and subtracts 9 to get a = column 1.
        return new Position(row,col);
    }

    private static String positionToString(Position position) {
        String colAsLetter = switch (position.getColumn()) {
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
        return colAsLetter + position.getRow();
    }

    private static ChessPiece.PieceType parseType(String input){
        input = input.toUpperCase();
        try {
            return ChessPiece.PieceType.valueOf(input);
        } catch (IllegalArgumentException e){
            return null;
        }
    }

    private static void printInfo(Position position) {
        if(position == null){
            // print general game info
            System.out.println();
            System.out.println("-----===Game Information===-----");
            System.out.println("\tCurrent team turn: "+ game.getTeamTurn().toString());

            Board board = (Board) game.getBoard();

            try {
                int numBlack = board.getTeamPieces(ChessGame.TeamColor.BLACK).size();
                int numWhite = board.getTeamPieces(ChessGame.TeamColor.WHITE).size();
                System.out.println("\t- Black pieces left: " + numBlack);
                System.out.println("\t- White pieces left: " + numWhite);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }

            if(game.isInCheck(ChessGame.TeamColor.WHITE)){
                System.out.println("\t- White is in check!");
            }
            else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                System.out.println("\t- Black is in check!");
            }
            System.out.println("--------------------------------");
            System.out.println();
        }
        else {
            // print info about a given position
            System.out.println();
            System.out.println("-----===Position Information===-----");
            Piece piece = (Piece) game.getBoard().getPiece(position);
            if(piece != null){
                System.out.println("Piece at " + positionToString(position) + piece.getTeamColor().toString() + " " + piece.getPieceType().toString());

                System.out.println("Valid moves from " + positionToString(position) + ":");
                for(ChessMove move : game.validMoves(position)) {
                    System.out.println("\t" + positionToString((Position) move.getEndPosition()));
                }
            }
            System.out.println("--------------------------------");
            System.out.println();
        }
    }

    private static void printBoard() {
        Board board = (Board) game.getBoard();
        System.out.println();
//        System.out.println(board.toStringUnicode());
    }
}
