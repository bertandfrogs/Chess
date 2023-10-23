package chess;

import chess.interfaces.ChessBoard;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;
import chess.interfaces.ChessPiece;

import static chess.interfaces.ChessGame.TeamColor.*;
import static chess.interfaces.ChessPiece.PieceType.*;
import chess.pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


// This class is the top-level management of the chess game.

public class Game implements chess.interfaces.ChessGame {
    private TeamColor teamTurn = TeamColor.WHITE;
    private Board chessBoard = new Board();

    public void newGame() {
        chessBoard.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    @Override
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    @Override
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    public void nextTeamTurn() {
        setTeamTurn(switch(teamTurn) {
            case WHITE -> BLACK;
            case BLACK -> WHITE;
        });
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Piece piece = chessBoard.getPiece(startPosition);
        if(piece == null){
            return null; // no piece found
        }

        Collection<ChessMove> allPossibleMoves = piece.pieceMoves(chessBoard, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        if(allPossibleMoves.isEmpty()){
            return validMoves; // empty (has no valid moves)
        }

        TeamColor myColor = piece.getTeamColor();

        for(ChessMove move : allPossibleMoves){
            if(!(isInCheckAfterMove(move, myColor))) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Piece movingPiece = chessBoard.getPiece(move.getStartPosition());
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        if(movingPiece == null){
            throw new InvalidMoveException("No piece located at starting position");
        }
        else if(movingPiece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("Piece being moved is not the current team's color");
        }
        else if(validMoves.isEmpty() || !validMoves.contains(move)){
            throw new InvalidMoveException("Not a valid move");
        }

        chessBoard.movePiece(move);
        nextTeamTurn();
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = chessBoard.getKingPosition(teamColor);
        TeamColor enemyColor = switch(teamColor){
            case WHITE -> BLACK;
            case BLACK -> WHITE;
        };
        Collection<Piece> attackers = getPiecesInRange(kingPosition, enemyColor, false);
        return !(attackers.isEmpty());
    }

    private boolean isInCheckAfterMove(ChessMove move, TeamColor color){
        Board savedBoard = new Board(chessBoard);
        chessBoard.movePiece(move);
        boolean isCheck = isInCheck(color);
        setBoard(savedBoard);
        return isCheck;
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = chessBoard.getKingPosition(teamColor);

        // check if the king is able to move away
        if(!validMoves(kingPosition).isEmpty()) {
            return false;
        }

        TeamColor enemyColor = switch(teamColor) {
            case BLACK -> WHITE;
            case WHITE -> BLACK;
        };

        Collection<Piece> attackers = getPiecesInRange(kingPosition, enemyColor, false);

        if(attackers.isEmpty()) {
            return false;
        }

        if(attackers.size() > 1) {
            return true; // if there are two pieces putting the king in check (and the king can't move) it's checkmate
        }

        Piece attacker = (Piece) attackers.toArray()[0];

        if(attacker.getPieceType() == KNIGHT || attacker.getPieceType() == PAWN) {
            // must be captured to escape checkmate
            if(getPiecesInRange(attacker.getMyPosition(), teamColor, false).isEmpty()){
                return true;
            }
            else {
                return false;
            }
        }
        else {
            // check if it can be captured or blocked
            // (check captured first)
            if(getPiecesInRange(attacker.getMyPosition(), teamColor, false).isEmpty()){
                // if it can't be captured, check if it can be blocked
                int changeInX = attacker.getMyPosition().getColumn() - kingPosition.getColumn();
                int changeInY = attacker.getMyPosition().getRow() - kingPosition.getRow() ;
                int rateX = 0;
                if(changeInX > 0){
                    rateX = 1;
                }
                else if(changeInX < 0){
                    rateX = -1;
                }

                int rateY = 0;
                if(changeInY > 0){
                    rateY = 1;
                }
                else if(changeInY < 0){
                    rateY = -1;
                }

                ChessPosition spaceInBetween = new Position(kingPosition.getRow()+rateY, kingPosition.getColumn()+rateX);

                while(!spaceInBetween.isOutOfBounds() && !spaceInBetween.equals(attacker.getMyPosition())) {
                    if(!getPiecesInRange(spaceInBetween, teamColor, true).isEmpty()){
                        return false;
                    }
                    spaceInBetween = new Position(spaceInBetween.getRow()+rateY, spaceInBetween.getColumn()+rateX);
                }
                return true;
            }
            else {
                // enemy piece can be captured
                return true;
            }
        }
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<Piece> teamPieces = chessBoard.getTeamPieces(teamColor);

        for(Piece piece : teamPieces) {
            if(!validMoves(piece.getMyPosition()).isEmpty()){
                return false;
            }
        }
        return true;
    }

    @Override
    public void setBoard(ChessBoard board) {
        chessBoard = (Board) board;
    }

    @Override
    public ChessBoard getBoard() {
        return chessBoard;
    }

    // given a certain position, return a collection of the pieces (of a specified color) that can move to reach that position
    // if the boolean "blocking" is true, then only valid blocking moves are considered.
    private Collection<Piece> getPiecesInRange(ChessPosition position, TeamColor color, boolean blocking) {
        Collection<Piece> pieces = new ArrayList<>();

        // check line of sight for rook or queen
        addPieceInLineOfSight(pieces, position, color, ROOK,0,1);   // up
        addPieceInLineOfSight(pieces, position, color, ROOK,0,-1);  // down
        addPieceInLineOfSight(pieces, position, color, ROOK,1,0);   // right
        addPieceInLineOfSight(pieces, position, color, ROOK,-1,0);  // left

        // check line of sight for bishop or queen
        addPieceInLineOfSight(pieces, position, color, BISHOP,1,1);  // up right
        addPieceInLineOfSight(pieces, position, color, BISHOP,-1,1); // up left
        addPieceInLineOfSight(pieces, position, color, BISHOP,1,-1);  // down right
        addPieceInLineOfSight(pieces, position, color, BISHOP,-1,-1); // down left

        // check knight positions
        addPieceInRange(pieces, position, color, KNIGHT,1, 2);  // up 2 right 1
        addPieceInRange(pieces, position, color, KNIGHT,-1, 2); // up 2 left 1
        addPieceInRange(pieces, position, color, KNIGHT,2,1);   // up 1 right 2
        addPieceInRange(pieces, position, color, KNIGHT,-2,1);  // up 1 left 2
        addPieceInRange(pieces, position, color, KNIGHT,1,-2);  // down 2 right 1
        addPieceInRange(pieces, position, color, KNIGHT,-1,-2); // down 2 left 1
        addPieceInRange(pieces, position, color, KNIGHT,2,-1);  // down 1 right 2
        addPieceInRange(pieces, position, color, KNIGHT,-2,-1); // down 1 left 2

        // check pawn position
        // get pawn direction
        int moveDirection = 1;
        if(color == WHITE){ // (white and black are reversed, because we're thinking backwards here)
            moveDirection = -moveDirection;
        }

        if(!blocking) {
            // check king position (kings can't block but they can capture/restrict movement)
            addPieceInRange(pieces, position, color, KING,1,1);    // right up
            addPieceInRange(pieces, position, color, KING,0,1);    // up
            addPieceInRange(pieces, position, color, KING,-1,1);   // left up
            addPieceInRange(pieces, position, color, KING,-1,0);   // left
            addPieceInRange(pieces, position, color, KING,-1,-1);  // left down
            addPieceInRange(pieces, position, color, KING,0,-1);   // down
            addPieceInRange(pieces, position, color, KING,1,-1);   // right down
            addPieceInRange(pieces, position, color, KING,1,0);    // right

            // pawn capture
            addPieceInRange(pieces, position, color, PAWN,1, moveDirection); // right
            addPieceInRange(pieces, position, color, PAWN,-1, moveDirection); // left
        }
        else {
            // pawn normal movement can block check
            addPieceInRange(pieces, position, color, PAWN, 0, moveDirection);
        }

        return pieces;
    }

    // TODO: refactor to take pieceType argument, then perform all the directional checks according to the piece type
    // or maybe break it into Collection<Piece> getAllInLineOfSight(... PieceType type)
    // and lineOfSightRecursive() which is the actual method that recurses each time
    private Piece getPieceInLineOfSight(ChessPosition currentPosition, int directionX, int directionY) {
        if(currentPosition == null){
            return null;
        }

        // avoid infinite recursion by accidentally not specifying a direction
        if(directionX == 0 && directionY == 0){
            return null;
        }

        ChessPosition newPosition = new Position(currentPosition.getRow()+directionY, currentPosition.getColumn()+directionX);

        // out of bounds check
        if(newPosition.isOutOfBounds()) {
            return null; // no piece in line of sight
        }

        // piece at new position check
        Piece piece = chessBoard.getPiece(newPosition);
        if (piece != null){
            return piece;
        }
        else {
            // recurse
            return getPieceInLineOfSight(newPosition, directionX, directionY);
        }
    }

    // uses the recursive getPieceInLineOfSight method to update the pieces collection
    private void addPieceInLineOfSight(Collection<Piece> pieces, ChessPosition position, TeamColor targetColor, ChessPiece.PieceType targetType, int directionX, int directionY) {
        if(position != null && !position.isOutOfBounds()){
            Piece targetPiece = getPieceInLineOfSight(position, directionX, directionY);
            if(targetPiece != null){
                // check if it's the specified type (rook, bishop) or queen
                boolean isValidType = targetPiece.getPieceType() == targetType || targetPiece.getPieceType() == QUEEN;
                if(targetPiece.getTeamColor() == targetColor && isValidType) {
                    pieces.add(targetPiece);
                }
            }
        }
    }

    private void addPieceInRange(Collection<Piece> pieces, ChessPosition position, TeamColor targetColor, ChessPiece.PieceType targetType, int directionX, int directionY){
        if(position != null && !position.isOutOfBounds()) {
            Position targetPosition = new Position(position.getRow() + directionY, position.getColumn() + directionX);
            if (!targetPosition.isOutOfBounds()) {
                Piece targetPiece = chessBoard.getPiece(targetPosition);
                if (targetPiece != null && targetPiece.getTeamColor() == targetColor && targetPiece.getPieceType() == targetType) {
                    if(targetType == KING) {
                        Move kingMove = new Move(targetPosition, position, null);
                        if(!isInCheckAfterMove(kingMove, targetColor)){
                            pieces.add(targetPiece);
                        }
                    }
                    else {
                        pieces.add(targetPiece);
                    }
                }
            }
        }
    }
}
