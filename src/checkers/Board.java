package checkers;

public class Board {
    private Piece[][] pieces;

    public Board(int sideCount, int startCount) {

        pieces = new Piece[sideCount][sideCount];
        for (int i = 0; i < pieces.length; i++)
            for (int j = 0; j < pieces[i].length; j++)
                pieces[i][j] = new Piece();

        for (int j = 0; j < startCount; j++)
            for (int i = (j % 2 == 0) ? 1 : 0; i < sideCount; i += 2) {
                pieces[i][j].setBlack();
                pieces[sideCount - 1 - i][sideCount - 1 - j].setWhite();
            }

        /*
        pieces[2][2].setBlack();
        pieces[3][3].setWhite();
        pieces[5][5].setWhite();
        pieces[5][3].setWhite();
        pieces[5][7].setWhite();*/
    }

    public Piece get(int x, int y) {
        return get(new BoardPos(x, y));
    }

    public Piece get(BoardPos pos) {
        if (pos.inBounds(pieces.length))
            return pieces[pos.getX()][pos.getY()];
        else return null;
    }

    public int side() {
        return pieces.length;
    }

}
