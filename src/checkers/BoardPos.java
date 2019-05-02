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
    private final int x, y;
    
    public BoardPos(int _x, int _y) {
        x = _x;
        y = _y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }

    boolean inBounds(int sideCount) {
        return x >= 0 && y >= 0 && x < sideCount && y < sideCount;
    }
}
