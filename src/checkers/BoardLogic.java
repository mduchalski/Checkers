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
 * This class implements basic checkers logic and (some of) graphics.
 */
public class BoardLogic {
    // graphics parameters
    private final double pieceMargin, startX, startY, sideLength, unitLength;
    private Board board;             // board configuration
    private List<BoardPos> legalPos; // a list of active (highlighted) legal positions
    boolean lastColor, gameOver;     // internal logic parameters, color true if black

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
    }

    /**
     * Checks if there are some legal move positions active (i.e. highlighted).
     * @return true if there are, false otherwise
     */
    public boolean someLegalPos() {
        return !legalPos.isEmpty();
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

    /**
     * Checks if game over condition has occurred after the previous click.
     * @return true if it has, false otherwise
     */
    public boolean isGameOverDelayed() {
        return gameOver;
    }


    private boolean isGameOver() {
        gameOver = longestAvailableMoves(1, true).isEmpty() ||
                longestAvailableMoves(1, false).isEmpty();
        return gameOver;
    }

    private List<BoardPos> longestAvailableMoves(int minDepth, boolean color) {
        List<BoardPos> result = new ArrayList<>();
        for (int i = 0; i < board.side(); i++)
            for (int j = 0; j < board.side(); j++)
                if (!board.get(i, j).isEmpty() &&
                        board.get(i, j).color() != color) {
                    List<BoardPos> _legalPos = getMoves(new BoardPos(i, j));
                    if (!_legalPos.isEmpty()) {
                        if (_legalPos.get(0).routeLen() > minDepth) {
                            result.clear();
                            minDepth = _legalPos.get(0).routeLen();
                        }
                        if (_legalPos.get(0).routeLen() == minDepth)
                            result.add(new BoardPos(i, j));
                    }
                }
        return result;
    }

    private List<BoardPos> getMoves(BoardPos from) {
        List<BoardPos> result;

        if (board.get(from).isCrown())
            result = highlightStrikesCrown(from);
        else result = highlightStrikes(from);

        if (result.isEmpty() && !board.get(from).isEmpty()) {
            final int[] shifts = {-1, 1};
            for (int shift : shifts) {
                BoardPos move = from.add(new BoardPos(shift,
                        board.get(from).color() ? 1 : -1));

                if (board.get(move) != null && board.get(move).isEmpty())
                    result.add(new BoardPos(move));
            } }

        for (BoardPos pos : result)
            pos.addToRoute(new BoardPos(from));

        return result;
    }

    public void highlightMoves(double mouseX, double mouseY) {
        List<BoardPos> longest = longestAvailableMoves(2, lastColor);


        if (longest.isEmpty()) {
            BoardPos mouse = decodeMouse(mouseX, mouseY);
            if (mouse.inBounds(board.side()) && !board.get(mouse).isEmpty() &&
                board.get(mouse).color() != lastColor)
                legalPos = getMoves(decodeMouse(mouseX, mouseY));
        }
        else if (!longest.isEmpty())
            for (BoardPos strike : longest)
                legalPos.addAll(getMoves(strike));
    }

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

    public List<BoardPos> highlightStrikesCrown(BoardPos from) {
        Queue<BoardPos> search = new LinkedList<>();
        List<BoardPos> result = new ArrayList<>();
        final int[] direction = {-1, 1};

        search.add(from);
        while (!search.isEmpty()) {
            boolean finalPos = true;
            for (int dirX : direction)
                for (int dirY : direction) {
                    BoardPos pos = search.peek().add(dirX, dirY);
                    BoardPos strike = null;
                    pos.setRoute(new ArrayList<>(search.peek().getRoute()));

                    while (pos.inBounds(board.side()) &&
                            (board.get(pos).isEmpty() ||
                                    (pos.add(dirX, dirY).inBounds(board.side()) &&
                                            board.get(pos.add(dirX, dirY)).isEmpty() &&
                                            board.get(from).color() != board.get(pos).color()))) {
                        if (!board.get(pos).isEmpty() && board.get(from).color()
                                != board.get(pos).color() && !pos.getRoute().contains(pos) &&
                                pos.add(dirX, dirY).inBounds(board.side()) &&
                                board.get(pos.add(dirX, dirY)).isEmpty()) {
                            strike = new BoardPos(pos);
                            finalPos = false;
                            pos = pos.add(dirX, dirY);
                            pos.addToRoute(strike);
                            continue;
                        }
                        if (strike != null)
                            search.add(pos);
                        pos = pos.add(dirX, dirY);
                    }
                }
            if (finalPos && !search.peek().equals(from) &&
                    search.peek().getRouteLast().isNextTo(search.peek()))
                result.add(search.peek());
            search.poll();
        }

        return filterShorter(result);
    }

    public List<BoardPos> highlightStrikes(BoardPos from) {
        Queue<BoardPos> search = new LinkedList<>();
        List<BoardPos> result = new ArrayList<>();
        final int[] offsets = {-2, 2};

        search.add(from);
        while (!search.isEmpty()) {
            boolean finalPos = true;
            for (int offX : offsets)
                for (int offY : offsets) {
                    BoardPos to = new BoardPos(search.peek().getX() + offX,
                            search.peek().getY() + offY);
                    to.setRoute(search.peek().getRoute());

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
            if (finalPos && !search.peek().equals(from))
                result.add(search.peek());
            search.poll();
        }

        return filterShorter(result);
    }
    
    public void draw(GraphicsContext gc) {
        gc.clearRect(startX, startY, sideLength, sideLength);

        // Draw the base grid
        gc.setFill(Color.LIGHTGREY);
        for (int i = 0; i < board.side(); i++)
            for (int j = (i % 2 == 0) ? 1 : 0; j < board.side(); j += 2)
                gc.fillRect(startX + j * unitLength, startY + i * unitLength, 
                        unitLength, unitLength);
        
        gc.setStroke(Color.BLACK);
        gc.strokeRect(startX, startY, sideLength, sideLength);
        
        // Draw legalPos
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
        
        // Draw pieces
        for (int i = 0; i < board.side(); i++)
            for (int j = 0; j < board.side(); j++)
                board.get(i, j).draw(gc, startX + i * unitLength,
                        startY + j * unitLength, pieceMargin, unitLength);
    }

    public String message() {
        if (!longestAvailableMoves(2, lastColor).isEmpty())
            return "Strike(s) available";
        else if (isGameOver())
            return "Game over! Click somewhere to continue";
        else return "Turn: " + (lastColor ? "White" : "Black");
    }
}
