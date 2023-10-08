package chess;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && column == position.column;
    }

    @Override
    public int hashCode() {
        return (row*10)+column;
    }
}
