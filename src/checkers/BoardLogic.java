/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

import java.util.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This class implements basic checkers logic and (some of) its graphics.
 */
public class BoardLogic {
    // graphics parameters
    private final double pieceMargin, startX, startY, sideLength, unitLength;
    private Board board;             // board configuration
    private List<BoardPos> legalPos; // a list of active (highlighted) legal positions
    // internal logic parameters, color true if black, type true if human
    boolean lastColor, gameOver, opponentSet;


    // initialization and reset functions
    /**
     * Initializes a BoardLogic object.
     * @param _startX x-coordinate of the upper-left board corner, pixels
     * @param _startY y-coordinate of the upper-left board corner, pixels
     * @param _sideLength board side length, pixels
     * @param _pieceMargin margin to leave on each side when drawing a piece,
     *                     fraction [0, 0.5) of single unit's side length
     * @param sideCount number of units per side, count
     * @param startCount number of units initially filled with either player's
     *                   pieces, count
     */
    public BoardLogic(double _startX, double _startY, double _sideLength,
                      double _pieceMargin, int sideCount, int startCount) {
        // simple copying
        startX = _startX;
        startY = _startY;
        sideLength = _sideLength;
        pieceMargin = _pieceMargin;
        // calculating single unit's side length in pixels for further use
        unitLength = _sideLength / sideCount;
        // board configuration & initial logic initialization
        board = new Board(sideCount, startCount);
        legalPos = new ArrayList<>();
        lastColor = true;
        gameOver = false;
        opponentSet = false;
    }

    public BoardLogic(BoardLogic boardLogic) {
        startX = boardLogic.startX;
        startY = boardLogic.startY;
        sideLength = boardLogic.sideLength;
        pieceMargin = boardLogic.pieceMargin;
        unitLength = boardLogic.unitLength;
        board = new Board(boardLogic.board);
        legalPos = new ArrayList<>(boardLogic.legalPos);
        lastColor = boardLogic.lastColor;
        gameOver = boardLogic.gameOver;
        opponentSet = boardLogic.opponentSet;
    }

    /**
     * Copies over parameters from another BoardLogic object.
     * @param boardLogic another BoardLogic object
     */
    public void update(BoardLogic boardLogic) {
        board = new Board(boardLogic.board);
        legalPos = new ArrayList<>(boardLogic.legalPos);
        lastColor = boardLogic.lastColor;
        gameOver = boardLogic.gameOver;
        opponentSet = boardLogic.opponentSet;
    }

    /**
     * Resets all game-specific fields, including board configuration,
     * to their initial values.
     * @see Board#reset()
     */
    public void reset() {
        // corresponding function called in the Board object to reset piece
        // positions
        board.reset();
        // internal logic reset
        lastColor = true;
        gameOver = false;
        opponentSet = false;
    }


    // public interface functions
    /**
     * Highlights all legal positions & routes from the given start position.
     * @param from start position, a BoardPos object
     */
    public void highlightMoves(BoardPos from) {
        // force longest strikes
        List<BoardPos> longest = longestAvailableMoves(2, !lastColor);

        // no strikes available - player chooses some regular move
        if (longest.isEmpty() && from.inBounds(board.side()) &&
                !board.get(from).isEmpty() && board.get(from).color() != lastColor)
            legalPos = getMoves(from);
        // some strikes available - player chooses from the longest ones
        else for (BoardPos strike : longest)
            legalPos.addAll(getMoves(strike));
    }

    /**
     * Moves an active (highlighted) piece corresponding to a given desired end
     * position to that position, if valid, and sets relevant logic parameters
     * for the next turn.
     * @param to desired move end position, a BoardPos object
     */
    public void attemptMove(BoardPos to) {
        if (legalPos.contains(to)) {
            lastColor = !lastColor; // next turn
            // move striking piece to the end position
            board.set(to, board.get(legalPos.get(legalPos.indexOf(to)).getRouteLast()));
            // clear positions "en route" - pieces to strike and the initial position
            for (BoardPos step : legalPos.get(legalPos.indexOf(to)).getRoute())
                board.get(step).setEmpty();
            // promote qualifying pieces to crown
            findCrown();
        }

        legalPos.clear(); // next turn - no highlights
    }

    /**
     * Draws the board and pieces. Please note that this method doesn't clear
     * working region.
     * @param gc desired GraphicsContext object
     */
    public void draw(GraphicsContext gc) {
        // draw the base grid
        gc.setFill(Color.LIGHTGREY);
        for (int i = 0; i < board.side(); i++)
            for (int j = (i % 2 == 0) ? 1 : 0; j < board.side(); j += 2)
                gc.fillRect(startX + j * unitLength, startY + i * unitLength,
                        unitLength, unitLength);

        // draw boundaries
        gc.setStroke(Color.BLACK);
        gc.strokeRect(startX, startY, sideLength, sideLength);

        // highlight legal positions
        for (BoardPos pos : legalPos) {
            gc.setFill(Color.ORANGE);
            gc.fillRect(startX + pos.getX() * unitLength,
                    startY + pos.getY() * unitLength, unitLength, unitLength);
            gc.setFill(Color.LIGHTYELLOW);
            if (pos.route != null)
                for (BoardPos step : pos.route)
                    gc.fillRect(startX + step.getX() * unitLength,
                            startY + step.getY() * unitLength, unitLength, unitLength);
        }

        // draw pieces
        for (int i = 0; i < board.side(); i++)
            for (int j = 0; j < board.side(); j++)
                board.get(i, j).draw(gc, startX + i * unitLength,
                        startY + j * unitLength, pieceMargin, unitLength);
    }

    /**
     * Generates a appropriate game status message.
     * @return current message, a String message
     */
    public String message() {
        if (!longestAvailableMoves(2, !lastColor).isEmpty())
            return "Strike available";
        else if (isGameOver())
            return "Game over! Click somewhere to continue";
        else return "Turn: " + (lastColor ? "White" : "Black");
    }

    /**
     * Checks if there are some legal move positions active (i.e. highlighted).
     * @return true if there are, false otherwise
     */
    public boolean someLegalPos() {
        return !legalPos.isEmpty();
    }

    /**
     * Checks if game over condition has occurred after the previous click.
     * @see BoardLogic#isGameOver()
     * @return true if it has, false otherwise
     */
    public boolean isGameOverDelayed() {
        return gameOver;
    }

    public boolean getLastColor() {
        return lastColor;
    }

    /**
     * Identifies board position corresponding to a given mouse click coordinates.
     * @param mouseX
     * @param mouseY
     * @return board position as BoardPos object if coordinates in range, null otherwise
     */
    public BoardPos decodeMouse(double mouseX, double mouseY) {
        if (mouseX > startX && mouseY > startY && mouseX < startX + sideLength &&
                mouseY < startY + sideLength) // range check
            return new BoardPos( (int)((mouseX - startX) / unitLength),
                    (int)((mouseY - startY) / unitLength ));
        else return null;
    }

    // private game logic functions
    /**
     * Returns a list of legal end positions achievable from the given start
     * position complete with routes.
     * @param from start position, a BoardPos object
     * @return a collection (list) of end positions, complete with routes
     */
    public List<BoardPos> getMoves(BoardPos from) {
        List<BoardPos> result;

        // strike check
        if (board.get(from).isCrown())
            result = getStrikesCrown(from);
        else result = getStrikes(from);

        // regular moves
        final int[] shifts = {-1, 1};
        if (result.isEmpty() && !board.get(from).isEmpty()) {
            if (board.get(from).isCrown())
                for (int shiftX : shifts)
                    for (int shiftY : shifts) {
                        BoardPos to = from.add(shiftX, shiftY);
                        while (to.inBounds(board.side()) && board.get(to).isEmpty()) {
                            result.add(to);
                            to = to.add(shiftX, shiftY);
                        }
                    }
            else for (int shift : shifts) { // add adjacent empty positions
                BoardPos move = from.add(new BoardPos(shift,
                        board.get(from).color() ? 1 : -1));
                if (board.get(move) != null && board.get(move).isEmpty())
                    result.add(new BoardPos(move));
        } }

        // complete by adding the start position to every legal route, so that
        // it will be cleared as well when the player will move
        for (BoardPos pos : result)
            pos.addToRoute(new BoardPos(from));

        return result;
    }

    /**
     * Returns a list of legal end strike positions achievable from the given
     * start position complete with routes, assuming piece is not crown.
     * @param from start position, a BoardPos object
     * @return a collection (list) of end positions, complete with routes
     */
    private List<BoardPos> getStrikes(BoardPos from) {
        Queue<BoardPos> search = new LinkedList<>(); search.add(from);
        List<BoardPos> result = new ArrayList<>();
        final int[] offsets = {-2, 2};

        // below is essentially a level-order tree transverse algorithm
        while (!search.isEmpty()) {
            // some new positions found from the current search position?
            boolean finalPos = true;
            // go in all 4 directions, to corresponding potential next position
            for (int offX : offsets)
                for (int offY : offsets) {
                    BoardPos to = new BoardPos(search.peek().getX() + offX,
                            search.peek().getY() + offY);
                    // copy route up to this point
                    to.setRoute(search.peek().getRoute());

                    // position between the current search and potential next one
                    // contains a piece that can be stricken for the first time
                    // in this route (no infinite loops)
                    if (to.inBounds(board.side()) && board.get(to).isEmpty() &&
                            !board.get(to.avg(search.peek())).isEmpty() &&
                            board.get(from).color() !=
                                    board.get(to.avg(search.peek())).color() &&
                            !to.getRoute().contains(to.avg(search.peek()))) {
                        to.addToRoute(new BoardPos(to.avg(search.peek())));
                        search.add(to);
                        finalPos = false;
                    }
                }

            // only add positions at the end of the route to result
            if (finalPos && !search.peek().equals(from))
                result.add(search.peek());

            // next element search
            search.poll();
        }

        // filter strikes shorter than maximum length
        return filterShorter(result);
    }

    /**
     * Returns a list of legal end strike positions achievable from the given
     * start position complete with routes, assuming piece is crown.
     * @param from start position, a BoardPos object
     * @return a collection (list) of end positions, complete with routes
     */
    private List<BoardPos> getStrikesCrown(BoardPos from) {
        Queue<BoardPos> search = new LinkedList<>(); search.add(from);
        List<BoardPos> result = new ArrayList<>();
        final int[] direction = {-1, 1};

        // below is essentially a level-order tree transverse algorithm
        while (!search.isEmpty()) {
            // some new positions found from the current search position?
            boolean finalPos = true;
            // go in all 4 orthogonal directions
            for (int dirX : direction)
                for (int dirY : direction) {
                    // initial next position to check in this direction
                    BoardPos pos = search.peek().add(dirX, dirY);
                    // some pieces already stricken in this direction
                    BoardPos strike = null;
                    // copy route up to this point
                    pos.setRoute(new ArrayList<>(search.peek().getRoute()));

                    // this goes through all potential legal positions in this
                    // direction, before and after first(!) strike
                    while (pos.inBounds(board.side()) &&
                            (board.get(pos).isEmpty() ||
                                    (pos.add(dirX, dirY).inBounds(board.side()) &&
                                            board.get(pos.add(dirX, dirY)).isEmpty() &&
                                            board.get(from).color() != board.get(pos).color()))) {
                        // this position contains a piece that can be stricken
                        // for the first time in this route (no infinite loops)
                        if (!board.get(pos).isEmpty() && board.get(from).color()
                                != board.get(pos).color() && !pos.getRoute().contains(pos) &&
                                pos.add(dirX, dirY).inBounds(board.side()) &&
                                board.get(pos.add(dirX, dirY)).isEmpty()) {
                            strike = new BoardPos(pos);
                            finalPos = false;
                            pos = pos.add(dirX, dirY);
                            // stricken pieces added to route so that they will
                            // be highlighted & removed later
                            pos.addToRoute(strike);
                        }
                        // add all positions after strike to
                        if (strike != null && !pos.equals(strike))
                            search.add(pos);

                        // next position in current direction
                        pos = pos.add(dirX, dirY);
                    }
                }

            if (finalPos && !search.peek().equals(from))
                result.add(search.peek());

            // next element in search
            search.poll();
        }

        // filter strikes shorter than maximum length
        return filterShorter(result);
    }

    /**
     * Checks and returns longest moves (longer than specified) available for
     * pieces of a specified color.
     * @param minDepth minimum length required, for example for 1 will list all
     *                 moves, for 2 only strikes
     * @param color piece color
     * @return a collection (list) of end positions with routes of route length
     * equal to the maximum one within given constrains
     */
    public List<BoardPos> longestAvailableMoves(int minDepth, boolean color) {
        List<BoardPos> result = new ArrayList<>();

        for (int i = 0; i < board.side(); i++)
            for (int j = 0; j < board.side(); j++)
                if (!board.get(i, j).isEmpty() &&
                        board.get(i, j).color() == color) {
                    List<BoardPos> _legalPos = getMoves(new BoardPos(i, j));
                    // some moves are available from the current position...
                    if (!_legalPos.isEmpty()) {
                        // ...with routes longer then  the last longest...
                        if (_legalPos.get(0).routeLen() > minDepth) {
                            // contains positions with routes shorter than new
                            // longest, so clear it
                            result.clear();
                            // update last longest route length
                            minDepth = _legalPos.get(0).routeLen();
                        }
                        // ...and equal to the last longest
                        if (_legalPos.get(0).routeLen() == minDepth)
                            result.add(new BoardPos(i, j));
                    }
                }

        return result;
    }

    /**
     * Checks relevant board positions to see if some pieces can be promoted
     * to crown, does so if there are.
     */
    private void findCrown() {
        // iterate over all x-positions
        for (int i = 0; i < board.side(); i++) {
            // only extreme elements are relevant
            if (!board.get(i, 0).isEmpty() && !board.get(i, 0).color())
                board.get(i, 0).setCrown();
            if (!board.get(i, board.side() - 1).isEmpty() &&
                    board.get(i, board.side() - 1).color())
                board.get(i, board.side() - 1).setCrown();
        }
    }

    /**
     * Checks if game over condition has occurred, saves status to a internal
     * variable for further use.
     * @see BoardLogic#isGameOverDelayed()
     * @return true if has, false otherwise
     */
    private boolean isGameOver() {
        // either all black or all white pieces don't have any moves left, save
        // to internal field so that a delayed status is available
        gameOver = longestAvailableMoves(1, true).isEmpty() ||
                longestAvailableMoves(1, false).isEmpty();
        return gameOver;
    }

    // private helper functions
    /**
     * Deletes all end positions with route length less than the longest one
     * from the input list.
     * @param route input collection (list) of BoardPos objects
     * @return output collection (list) of only BoardPos objects with the
     * longest routes
     */
    private List<BoardPos> filterShorter(List<BoardPos> route) {
        int maxDepth = route.isEmpty() ? 0 : route.get(route.size() - 1).routeLen();
        Iterator<BoardPos> it = route.iterator();

        while (it.hasNext()) {
            BoardPos pos = it.next();
            if (pos.routeLen() != maxDepth)
                it.remove();
        }

        return route;
    }

    // work in progress below
    public Board getBoard() {
        return board;
    }

    public boolean isOpponentSet() {
        return opponentSet;
    }

    public boolean turn() {
        return !lastColor;
    }

    public void setOpponent() {
        opponentSet = true;
    }
}
