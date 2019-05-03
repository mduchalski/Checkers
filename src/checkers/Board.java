/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Mateusz
 */


public class Board {
    private final int sideCount, startCount;
    private final double pieceMargin, startX, startY, sideLength, unitLength;
    private Piece[][] pieces;
    private List<BoardPos> highlights;
    BoardPos activePos;
    
    public Board(double _startX, double _startY, double _sideLength,
            double _pieceMargin, int _sideCount, int _startCount) {
        startX = _startX;
        startY = _startY;
        sideLength = _sideLength;
        pieceMargin = _pieceMargin;
        sideCount = _sideCount;
        startCount = _startCount;
        
        unitLength = _sideLength / sideCount;
        
        pieces = new Piece[sideCount][sideCount];
        for (int i = 0; i < sideCount; i++)
            for (int j = 0; j < sideCount; j++)
                pieces[i][j] = new Piece();
        
        
        highlights = new ArrayList<BoardPos>();
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
    
    public void init() {
        
        /*
        for (int j = 0; j < startCount; j++)
            for (int i = (j % 2 == 0) ? 1 : 0; i < sideCount; i += 2) {
                pieces[i][j].setBlack();
                pieces[sideCount - 1 - i][sideCount - 1 - j].setWhite();
            }
        */
        
        pieces[2][2].setBlack();
        pieces[3][3].setWhite();
        pieces[5][5].setWhite();
        pieces[5][3].setWhite();
        pieces[5][7].setWhite();
        pieces[5][1].setWhite();
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
        if (highlights.size() == 0) {
            if (pieces[activePos.getX() + 1][activePos.getY() + 1].isEmpty())
                highlights.add(new BoardPos(activePos.getX() + 1, activePos.getY() + 1));
            if (pieces[activePos.getX() - 1][activePos.getY() + 1].isEmpty())
                highlights.add(new BoardPos(activePos.getX() - 1, activePos.getY() + 1));
        }  
    }
    
    private Piece pieceOnPos(BoardPos pos) {
        return pieces[pos.getX()][pos.getY()];
    }
    
    
    public void highlightStrikes(BoardPos from) {
        Queue<BoardPos> search = new LinkedList<>();
        Stack<BoardPos> result = new Stack<>();
        search.add(from);
        
        final int[] offsets = {-2, 2};
        int depth = 1;
        while (!search.isEmpty()) {
            for (int offX : offsets)
                for (int offY : offsets) {
                    BoardPos to = new BoardPos(search.peek().getX() + offX, 
                            search.peek().getY() + offY);
                    
                    if ( !result.contains(to) && to.inBounds(sideCount) &&
                            pieceOnPos(to).isEmpty() && 
                            !pieceOnPos(new BoardPos((to.getX()+search.peek().getX()) / 2, 
                                (to.getY()+search.peek().getY()) / 2)).isEmpty() &&
                        pieceOnPos(activePos).color() != 
                        pieceOnPos(new BoardPos((to.getX()+search.peek().getX()) / 2, 
                            (to.getY()+search.peek().getY()) / 2)).color() &&
                            !search.contains(to) ) 
                        search.add(new BoardPos(to, search.peek().distFromActive() + 1));
                }
            
            result.add(search.poll());
        }
        
        if (!result.isEmpty()) {
            int maxDepth = result.peek().distFromActive();
            while (!result.isEmpty() && result.peek().distFromActive() == maxDepth)
                highlights.add(result.pop());
        }
    }
    
    public void draw(GraphicsContext gc) {
        // Draw the base grid
        gc.setFill(Color.LIGHTGREY);
        for (int i = 0; i < sideCount; i++)
            for (int j = (i % 2 == 0) ? 1 : 0; j < sideCount; j += 2)
                gc.fillRect(startX + j * unitLength, startY + i * unitLength, 
                        unitLength, unitLength);
        
        gc.setStroke(Color.BLACK);
        gc.strokeRect(startX, startY, sideLength, sideLength);
        
        // Draw highlights
        gc.setFill(Color.LIGHTYELLOW);
        for (BoardPos pos : highlights)
            gc.fillRect(startX + pos.getX() * unitLength, 
                    startY + pos.getY() * unitLength, unitLength, unitLength);
        
        // Draw pieces
        for (int i = 0; i < sideCount; i++)
            for (int j = 0; j < sideCount; j++)
                pieces[i][j].draw(gc, startX + i * unitLength,
                        startY + j * unitLength, pieceMargin, unitLength);
    }

    public void clearHighlights() {
        highlights.clear();
    }
}
