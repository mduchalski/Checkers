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
 *
 * @author Mateusz
 */
public class BoardLogic {
    private final double pieceMargin, startX, startY, sideLength, unitLength;
    private Board board;

    private List<BoardPos> legalPos;
    BoardPos activePos;
    boolean lastColor;
    
    public BoardLogic(double _startX, double _startY, double _sideLength,
                      double _pieceMargin, int sideCount, int startCount) {
        startX = _startX;
        startY = _startY;
        sideLength = _sideLength;
        pieceMargin = _pieceMargin;
        
        unitLength = _sideLength / sideCount;

        board = new Board(sideCount, startCount);
        
        legalPos = new ArrayList<>();

        activePos = null;
        lastColor = true;
    }
    
    public boolean someActivePos() {
        return activePos != null;
    }

    private void findCrown() {
        for (int i = 0; i < board.side(); i++) {
            if (!board.get(i, 0).isEmpty() && !board.get(i, 0).color())
                board.get(i, 0).setCrown();
            if (!board.get(i, board.side() - 1).isEmpty() &&
                    board.get(i, board.side() - 1).color())
                board.get(i, board.side() - 1).setCrown();
        }
    }

    public void attemptMove(double mouseX, double mouseY) {
        BoardPos newPos = decodeMouse(mouseX, mouseY);
        
        if (legalPos.contains(newPos)) {
            lastColor = board.get(activePos).color();
            board.set(newPos, board.get(activePos));
            for (BoardPos step : legalPos.get(legalPos.indexOf(newPos)).getRoute())
                board.get(step).setEmpty();
            findCrown();
        }

        legalPos.clear();
        activePos = null;
    }
    
    private BoardPos decodeMouse(double mouseX, double mouseY) {
        if (mouseX > startX && mouseY > startY && mouseX < startX + sideLength &&
                mouseY < startY + sideLength)
            return new BoardPos( (int)((mouseX - startX) / unitLength), 
                    (int)((mouseY - startY) / unitLength ));
        else return null;
    }

    public boolean gameEnd() {
        int whiteCnt = 0, blackCnt = 0;
        for (int i = 0; i < board.side(); i++)
            for (int j = 0; j < board.side(); j++)
                if (!board.get(i, j).isEmpty() && board.get(i, j).color())
                    blackCnt++;
                else if (!board.get(i, j).isEmpty()) whiteCnt++;
        return whiteCnt == 0 || blackCnt == 0;
    }

    private BoardPos longestAvailableStrike() {
        BoardPos lastLongest = null;
        for (int i = 0; i < board.side(); i++)
            for (int j = 0; j < board.side(); j++)
                if (!board.get(i, j).isEmpty() &&
                        board.get(i, j).color() != lastColor) {
                    List<BoardPos> _legalPos = highlightStrikes(new BoardPos(i, j));
                    if (!_legalPos.isEmpty() && (lastLongest == null ||
                            _legalPos.get(0).routeLen() > lastLongest.routeLen()))
                        lastLongest = new BoardPos(i, j);
                }
        return lastLongest;
    }

    public void highlightMoves(double mouseX, double mouseY) {
        activePos = longestAvailableStrike();

        if (activePos == null)
            activePos = decodeMouse(mouseX, mouseY);
        if (activePos == null || board.get(activePos).isEmpty() ||
                board.get(activePos).color() == lastColor) {
            activePos = null;
            return;
        }

        if (board.get(activePos).isCrown())
             legalPos = highlightStrikesCrown(activePos);
        else legalPos = highlightStrikes(activePos);

        if (legalPos.isEmpty() && !board.get(activePos).isEmpty()) {
            final int[] shifts = {-1, 1};
            for (int shift : shifts) {
                BoardPos move = activePos.add(new BoardPos(shift,
                        board.get(activePos).color() ? 1 : -1));

                if (board.get(move) != null && board.get(move).isEmpty())
                    legalPos.add(new BoardPos(move));
        } }

        for (BoardPos pos : legalPos)
            pos.addToRoute(new BoardPos(activePos));
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
        return "Turn: " + (lastColor ? "White" : "Black");
    }
}
