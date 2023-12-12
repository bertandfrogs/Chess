package chess;

import com.google.gson.Gson;

public class Position implements chess.interfaces.ChessPosition {
    int row;
    int column;

    // constructor
    public Position(int r, int c) {
        row = r;
        column = c;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public boolean isOutOfBounds() {
        return (row > 8 || row < 1 || column > 8 || column < 1);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        if(isOutOfBounds() ^ position.isOutOfBounds()) return false;

        return hashCode() == position.hashCode()
                && row == position.row
                && column == position.column;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int hashCode() {
        return (row*10)+column;
    }
}
