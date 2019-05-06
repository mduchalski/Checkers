/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mateusz
 */
public class BoardPos {
    private int x, y, distFromActive;
    public List<BoardPos> route;

    public BoardPos() {
        x = 0;
        y = 0;
        distFromActive = 0;
    }

    public BoardPos(int _x, int _y) {
        x = _x;
        y = _y;
        distFromActive = 0;
    }

    public BoardPos(BoardPos old) {
        x = old.x;
        y = old.y;
        distFromActive = old.distFromActive;
        if (old.route != null)
            route = new ArrayList<>(old.route);
    }

    public BoardPos(BoardPos old, int _distFromActive) {
        x = old.x;
        y = old.y;
        distFromActive = _distFromActive;
    }

    public void addToRoute(BoardPos step) {
        if (route == null)
            route = new ArrayList<>();

        route.add(step);
    }

    public List<BoardPos> getRoute() {
        return route;
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

    public BoardPos add(BoardPos other) {
        return new BoardPos(x + other.x, y + other.y);
    }

    public BoardPos avg(BoardPos other) {
        return new BoardPos((x + other.x) / 2, (y + other.y) / 2);
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
