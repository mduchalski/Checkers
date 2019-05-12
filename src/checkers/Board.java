package checkers;

/**
 * This class encapsulates board data table and provides helper functions
 */
public class Board {
    private Piece[][] pieces; // data table
    private int startCount, sideCount;


    // initialization and reset function
    /**
     * Initializes a Board object. Called when initializing a BoardLogic object.
     * @see BoardLogic#BoardLogic(double, double, double, double, int, int)
     * @param _sideCount number of units per side, count
     * @param _startCount number of units initially filled with either player's
     *                    pieces, count
     */
    public Board(int _sideCount, int _startCount) {
        // save startCount for reset
        startCount = _startCount;
        sideCount = _sideCount;

        // initialize all table elements
        pieces = new Piece[sideCount][sideCount];
        for (int i = 0; i < pieces.length; i++)
            for (int j = 0; j < pieces[i].length; j++)
                pieces[i][j] = new Piece();

        // set black and white pieces at appropriate positions
        for (int j = 0; j < startCount; j++)
            for (int i = (j % 2 == 0) ? 1 : 0; i < sideCount; i += 2) {
                pieces[i][j].setBlack();
                pieces[sideCount - 1 - i][sideCount - 1 - j].setWhite();
            }
    }

    public Board(Board board) {
        startCount = board.startCount;
        sideCount = board.sideCount;

        // copy all table elements
        pieces = new Piece[sideCount][sideCount];
        for (int i = 0; i < pieces.length; i++)
            for (int j = 0; j < pieces[i].length; j++)
                pieces[i][j] = new Piece(board.pieces[i][j]);
    }

    /**
     * Resets the board configuration. Is it actually needed?
     */
    public void reset() {
        for (int i = 0; i < pieces.length; i++)
            for (int j = 0; j < pieces[i].length; j++)
                pieces[i][j].setEmpty();

        for (int j = 0; j < startCount; j++)
            for (int i = (j % 2 == 0) ? 1 : 0; i < pieces.length; i += 2) {
                pieces[i][j].setBlack();
                pieces[pieces.length - 1 - i][pieces.length - 1 - j].setWhite();
            }
    }


    // interface read functions
    /**
     * Returns a piece on the desired position given as a BoardPos object
     * @param pos desired position
     * @return piece, Piece object on desired position if in bounds, null otherwise
     */
    public Piece get(BoardPos pos) {
        if (pos.inBounds(pieces.length))
            return pieces[pos.getX()][pos.getY()];
        else return null;
    }

    /**
     * Returns a piece on the desired position given as x, y coordinates
     * @param x x-coordinate of the desired position
     * @param x y-coordinate of the desired position
     * @return piece, Piece object on desired position if in bounds, null otherwise
     */
    public Piece get(int x, int y) {
        return get(new BoardPos(x, y));
    }


    /**
     * Returns number of units per board side
     * @return side count;
     */
    public int side() {
        return pieces.length;
    }


    // public interface write functions
    /**
     * Sets a given piece given as a Piece object on a board position given as
     * a BoardPos object
     * @param pos position to set piece on, a BoardPos object
     * @param piece piece to set on position, a Piece object
     */
    public void set(BoardPos pos, Piece piece) {
        pieces[pos.getX()][pos.getY()] = new Piece(piece);
    }
}
