package chess;

import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.pieces.Piece;

import java.sql.SQLSyntaxErrorException;
import java.util.Collection;
import java.util.Scanner;

public class Main {
    private static Game game = new Game();
    public static void main(String[] args) {
        // Using Scanner class to get input from console until "exit" is entered
        boolean activeConsole = true;
        boolean activeGame = false;

        while(activeConsole){
            Scanner consoleInput = new Scanner(System.in);

            String command = consoleInput.next();
            command = command.toLowerCase();

            switch(command) {
                case "exit" ->  {
                    activeConsole = false;
                }
                case "start" -> {
                    activeGame = true;
                    game.newGame();
                }
                case "info" -> {
                    if(activeGame){
                        printInfo(null); // print general information about the game (not any given position)
                    }
                    else {
                        System.out.println("There is not an active game in play. Enter \"start\" to start a new game.");
                    }
                }
                case "reset" -> {
                    if(activeGame){
                        game.newGame();
                    }
                    else {
                        System.out.println("There is not an active game in play. Enter \"start\" to start a new game.");
                    }
                }
                case "move" -> {
                    if(activeGame){

                    }
                    else {
                        System.out.println("There is not an active game in play. Enter \"start\" to start a new game.");
                    }
                }
                case "position" -> {
                    if(activeGame){
                        if(consoleInput.hasNext()){
                            String position = consoleInput.next();
//                            printInfo(position);
                        }
                    }
                    else {
                        System.out.println("There is not an active game in play. Enter \"start\" to start a new game.");
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
        System.out.println("Valid commands:");
        System.out.println("\t- exit");
        System.out.println("\t- move [start] [end]");
    }

    private Position parsePosition(String input){
        return new Position(1,1);
    }

    private static void printInfo(Position position) {
        if(position == null){
            // print general game info
            System.out.println("-----===Game Information===-----");
            System.out.print("\tCurrent team turn: ");
            System.out.println(game.getTeamTurn().toString());
            System.out.println("\t- Black pieces left: ");
            System.out.println("\t- White pieces left: ");
            if(game.isInCheck(ChessGame.TeamColor.WHITE)){
                System.out.println("\t- White is in check!");
            }
            else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                System.out.println("\t- Black is in check!");
            }
        }
        else {
            // print info about a given position
            System.out.println("-----===Position Information===-----");
            Piece piece = (Piece) game.getBoard().getPiece(position);
            if(piece != null){
                System.out.println("\t- Piece at position: " + piece.getTeamColor().toString() + " " + piece.getPieceType().toString());
                Collection<ChessMove> moves = game.validMoves(position);
            }
        }
    }

    private void move() {

    }

    private void printBoard() {

    }

    private void getValidMoves() {

    }
}
