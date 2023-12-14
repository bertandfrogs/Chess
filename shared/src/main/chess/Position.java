package chess;

import com.google.gson.Gson;

public class Position implements ChessPosition {
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

    public String toChessNotation() {
        String rowStr = String.valueOf(row);
        String colStr = String.valueOf(column);
        colStr = switch (colStr) {
            case "1" -> "a";
            case "2" -> "b";
            case "3" -> "c";
            case "4" -> "d";
            case "5" -> "e";
            case "6" -> "f";
            case "7" -> "g";
            case "8" -> "h";
            default -> throw new IllegalStateException("Unexpected value: " + colStr);
        };
        return colStr + rowStr;
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
