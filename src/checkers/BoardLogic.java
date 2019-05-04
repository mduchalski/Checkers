/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static java.lang.Math.abs;

/**
 *
 * @author Mateusz
 */


public class BoardLogic {
    private final double pieceMargin, startX, startY, sideLength, unitLength;
    //private Piece[][] pieces;
    private Board board;

    private List<BoardPos> highlights;
    BoardPos activePos;
    
    public BoardLogic(double _startX, double _startY, double _sideLength,
                      double _pieceMargin, int sideCount, int startCount) {
        startX = _startX;
        startY = _startY;
        sideLength = _sideLength;
        pieceMargin = _pieceMargin;
        
        unitLength = _sideLength / sideCount;

        board = new Board(sideCount, startCount);
        
        highlights = new ArrayList<>();
    }
    
    public boolean someActivePos() {
        return activePos != null;
    }
    
    public void attemptMove(double mouseX, double mouseY) {
        BoardPos newPos = decodeMouse(mouseX, mouseY);
        
        
        
        //pieces[newPos.getX()][newPos.getY()] 
        //        = new Piece(pieces[activePos.getX()][activePos.getY()]);
        //pieces[activePos.getX()][activePos.getY()].setEmpty();
        highlights.clear();
        activePos = null;
    }
    
    private BoardPos decodeMouse(double mouseX, double mouseY) {
        if (mouseX > startX && mouseY > startY && mouseX < startX + sideLength &&
                mouseY < startY + sideLength)
            return new BoardPos( (int)((mouseX - startX) / unitLength), 
                    (int)((mouseY - startY) / unitLength ));
        else return null;
    }

    public void highlightMoves(double mouseX, double mouseY) {
        activePos = decodeMouse(mouseX, mouseY);
        if (activePos == null) return;
        
        highlightStrikes(activePos);

        if (highlights.size() == 1 && !board.get(activePos).isEmpty()) {
            final int[] shifts = {-1, 1};
            for (int shift : shifts) {
                BoardPos move = activePos.add(new BoardPos(shift,
                        board.get(activePos).color() ? 1 : -1));

                if (board.get(move) != null && board.get(move).isEmpty())
                    highlights.add(move);
        } }
    }
    
    public void highlightStrikes(BoardPos from) {
        Queue<BoardPos> search = new LinkedList<>();
        List<BoardPos> result = new ArrayList<>();
        search.add(from);
        
        final int[] offsets = {-2, 2};
        while (!search.isEmpty()) {
            for (int offX : offsets)
                for (int offY : offsets) {
                    BoardPos to = new BoardPos(search.peek().getX() + offX, 
                            search.peek().getY() + offY);
                    
                    if ( !result.contains(to) && to.inBounds(board.side()) &&
                            board.get(to).isEmpty() &&
                            !board.get(to.avg(search.peek())).isEmpty() &&
                            board.get(activePos).color() !=
                                    board.get(to.avg(search.peek())).color() &&
                            !search.contains(to) )
                        search.add(new BoardPos(to, search.peek().distFromActive() + 1));
                }
            
            result.add(new BoardPos(search.poll()));
        }

        int maxDepth = result.get(result.size() - 1).distFromActive();
        for (BoardPos end : result) {
            if (end.distFromActive() == maxDepth) {
                int i = maxDepth - 1;
                BoardPos nextStep = new BoardPos(end);
                for (int j = result.size() - 1; j > 0; j--)
                    if (result.get(j).distFromActive() == i &&
                            abs(result.get(j).getX() - nextStep.getX()) == 2 &&
                            abs(result.get(j).getY() - nextStep.getY()) == 2) {
                        end.addToRoute(result.get(j));
                        end.addToRoute(new BoardPos((result.get(j).getX() + nextStep.getX()) / 2,
                                (result.get(j).getY() + nextStep.getY()) / 2));

                        if (i == 1) {
                            end.addToRoute(new BoardPos((result.get(j).getX() + from.getX()) / 2,
                                    (result.get(j).getY() + from.getY()) / 2));
                            end.addToRoute(from);
                        }

                        i--;
                        nextStep = new BoardPos(result.get(j));
                    }
                highlights.add(new BoardPos(end));
            }
        }
    }
    
    public void draw(GraphicsContext gc) {
        // Draw the base grid
        gc.setFill(Color.LIGHTGREY);
        for (int i = 0; i < board.side(); i++)
            for (int j = (i % 2 == 0) ? 1 : 0; j < board.side(); j += 2)
                gc.fillRect(startX + j * unitLength, startY + i * unitLength, 
                        unitLength, unitLength);
        
        gc.setStroke(Color.BLACK);
        gc.strokeRect(startX, startY, sideLength, sideLength);
        
        // Draw highlights
        for (BoardPos pos : highlights) {
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
}
