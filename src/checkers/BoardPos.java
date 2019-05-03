/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

/**
 *
 * @author Mateusz
 */
public class BoardPos {
    private final int x, y, distFromActive;
    
    public BoardPos(int _x, int _y) {
        x = _x;
        y = _y;
        distFromActive = 0;
    }
    
    public BoardPos(BoardPos old, int _distFromActive) {
        x = old.x;
        y = old.y;
        distFromActive = _distFromActive;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }

    public boolean inBounds(int sideCount) {
        return x >= 0 && y >= 0 && x < sideCount && y < sideCount;
    }
    
    public int distFromActive() {
        return distFromActive;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BoardPos))
            return false;
        BoardPos oo = (BoardPos)o;
        return oo.getX() == x && oo.getY() == y;
    }
}
