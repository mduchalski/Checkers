/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.abs;

/**
 *
 * @author Mateusz
 */
public class BoardPos {
    private int x, y;
    public List<BoardPos> route;

    public BoardPos(int _x, int _y) {
        x = _x;
        y = _y;
    }

    public BoardPos(BoardPos old) {
        x = old.x;
        y = old.y;
        if (old.route != null)
            route = new ArrayList<>(old.route);
    }

    public void addToRoute(BoardPos step) {
        if (route == null)
            route = new ArrayList<>();

        route.add(step);
    }

    public int routeLen() {
        if (route == null)
            return 0;
        else return route.size();
    }

    public List<BoardPos> getRoute() {
        if (route == null)
            return new ArrayList<>();
        return route;
    }

    public void setRoute(List<BoardPos> _route) {
        route = new ArrayList<>(_route);
    }

    public boolean isNextTo(BoardPos other) {
        return abs(x - other.x) == 1 && abs(y - other.y) == 1;
    }

    public BoardPos getRouteLast() {
        return route.get(route.size() - 1);
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

    public BoardPos add(BoardPos other) {
        return new BoardPos(x + other.x, y + other.y);
    }

    public BoardPos add(int _x, int _y) {
        BoardPos retVal = new BoardPos(x + _x, y + _y);
        if (route != null)
            retVal.route = new ArrayList<>(route);
        return retVal;
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
        return oo.x == x && oo.y == y;
    }
}
