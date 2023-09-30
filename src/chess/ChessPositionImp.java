package chess;

public class ChessPositionImp implements ChessPosition {
    int row;
    int column;

    // constructor
    public ChessPositionImp(int r, int c) {
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
}
