package chess;

import chess.pieces.*;

import java.util.*;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.KING;

public class Board implements ChessBoard {
    public Board(){}

    // copy constructor
    public Board(Board copy){
        Map<Integer, Piece> copyPieces = copy.getPieces();
        for(Map.Entry<Integer, Piece> entry : copyPieces.entrySet()) {
            Piece oldPiece = entry.getValue();
            ChessGame.TeamColor newColor = oldPiece.getTeamColor();
            Piece pieceClone = switch(oldPiece.getPieceType()){
                case KING -> new King(newColor);
                case QUEEN -> new Queen(newColor);
                case BISHOP -> new Bishop(newColor);
                case KNIGHT -> new Knight(newColor);
                case ROOK -> new Rook(newColor);
                case PAWN -> new Pawn(newColor);
            };
            addPiece(entry.getKey(), pieceClone);
            // kingPosition variables get re-initialized with addPiece
        }
    }

    private ChessPosition blackKingPosition = null;
    private ChessPosition whiteKingPosition = null;

    private final Map<Integer, Piece> pieces = new HashMap<>(); // Stores pieces according to the hashCode of the position on the board

    @Override
    public void addPiece(ChessPosition pos, ChessPiece p) {
        Position position = (Position) pos;
        Piece piece = (Piece) p;
        piece.setMyPosition(position);

        pieces.put(position.hashCode(), piece);

        if(piece.getPieceType() == KING) {
            switch(piece.getTeamColor()) {
                case WHITE -> whiteKingPosition = position;
                case BLACK -> blackKingPosition = position;
            }
        }
    }

    public void addPiece(int hashCode, ChessPiece p) {
        Position pos = getPositionFromHash(hashCode);
        addPiece(pos, p);
    }

    @Override
    public Piece getPiece(ChessPosition pos) {
        Position position = (Position) pos;
        return pieces.get(position.hashCode());
    }
    public Piece getPiece(int hashCode) {
        return pieces.get(hashCode);
    }
    public Piece getPiece(int row, int col) {
        return pieces.get(getHashFromRowAndCol(row, col));
    }

    @Override
    public void resetBoard() {
        // clear pieces maps
        pieces.clear();

        // set row 1 (white)
        addPiece(getHashFromRowAndCol(1, 1), new Rook(WHITE));
        addPiece(getHashFromRowAndCol(1, 2), new Knight(WHITE));
        addPiece(getHashFromRowAndCol(1, 3), new Bishop(WHITE));
        addPiece(getHashFromRowAndCol(1, 4), new Queen(WHITE));
        addPiece(getHashFromRowAndCol(1, 5), new King(WHITE));
        addPiece(getHashFromRowAndCol(1, 6), new Bishop(WHITE));
        addPiece(getHashFromRowAndCol(1, 7), new Knight(WHITE));
        addPiece(getHashFromRowAndCol(1, 8), new Rook(WHITE));

        // set row 2 (white pawns)
        for(int i = 1; i <= 8; i++) {
            addPiece(getHashFromRowAndCol(2, i), new Pawn(WHITE));
        }

        // set row 7 (black pawns)
        for(int i = 1; i <= 8; i++) {
            addPiece(getHashFromRowAndCol(7, i), new Pawn(ChessGame.TeamColor.BLACK));
        }

        // set row 8 (black)
        addPiece(getHashFromRowAndCol(8, 1), new Rook(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 2), new Knight(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 3), new Bishop(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 4), new Queen(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 5), new King(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 6), new Bishop(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 7), new Knight(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 8), new Rook(ChessGame.TeamColor.BLACK));
    }

    public void movePiece(ChessMove move) {
        // this method doesn't check any logic, just does the physical movement of pieces
        // (assumes that Game has already checked logic)
        Position startPos = (Position) move.getStartPosition();
        Position endPos = (Position) move.getEndPosition();
        Piece movingPiece = getPiece(startPos);
        Piece pieceAtEnd = getPiece(endPos);
        ChessPiece.PieceType promotionType = move.getPromotionPiece();

        if(promotionType != null){
            if(movingPiece.getPieceType() != promotionType){
                movingPiece = switch(promotionType) {
                    case KING -> new King(movingPiece.getTeamColor());
                    case QUEEN -> new Queen(movingPiece.getTeamColor());
                    case BISHOP -> new Bishop(movingPiece.getTeamColor());
                    case KNIGHT -> new Knight(movingPiece.getTeamColor());
                    case ROOK -> new Rook(movingPiece.getTeamColor());
                    case PAWN -> new Pawn(movingPiece.getTeamColor());
                };
            }
        }

        if(pieceAtEnd == null) {
            pieces.remove(startPos.hashCode());
            pieces.put(endPos.hashCode(), movingPiece);
            movingPiece.setMyPosition(endPos);
        }
        else {
            pieces.remove(startPos.hashCode());
            pieces.replace(endPos.hashCode(), movingPiece);
            movingPiece.setMyPosition(endPos);
        }

        if(movingPiece.getPieceType() == KING) {
            switch(movingPiece.getTeamColor()) {
                case WHITE -> whiteKingPosition = endPos;
                case BLACK -> blackKingPosition = endPos;
            }
        }
    }

    public Map<Integer, Piece> getPieces() {
        return pieces;
    }

    public Collection<Piece> getTeamPieces(ChessGame.TeamColor color) {
        Collection<Piece> teamPieces = new ArrayList<>();
        for(Map.Entry<Integer, Piece> entry : pieces.entrySet()) {
            Piece currentPiece = entry.getValue();
            if(currentPiece.getTeamColor() == color) {
                teamPieces.add(currentPiece);
            }
        }
        return teamPieces;
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor color) {
        switch(color) {
            case WHITE -> {
                return whiteKingPosition;
            }
            case BLACK -> {
                return blackKingPosition;
            }
        }
        return null;
    }

    private Position getPositionFromHash(int hash){
        String str = String.valueOf(hash);
        int row = Character.getNumericValue(str.charAt(0));
        int col = Character.getNumericValue(str.charAt(1));
        return new Position(row, col);
    }

    private int getHashFromRowAndCol(int row, int col) {
        return (row*10) + col;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        if (pieces.isEmpty() ^ board.pieces.isEmpty()) return false;

        // check pieces
        if(!pieces.isEmpty()) {
            if (pieces.size() != board.pieces.size()) {
                return false;
            }
            for(Map.Entry<Integer, Piece> piece : pieces.entrySet()) {
                Piece boardPiece = board.pieces.get(piece.getKey());
                if(!piece.getValue().equals(boardPiece)) return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(blackKingPosition, whiteKingPosition, pieces);
    }
}