package checkers;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.abs;

/**
 * This class implements a board position with its coordinates and route,
 * alongside relevant helper functions.
 */
public class BoardPos {
    private int x, y;
    public List<BoardPos> route;


    // initialization functions
    /**
     * Initializes a BoardPos object from a pair of coordinates.
     * @param _x x-coordinate of the desired position
     * @param _y y-coordinate of the desired position
     */
    public BoardPos(int _x, int _y) {
        x = _x;
        y = _y;
    }

    /**
     * Initializes a new BoardPos object as a copy of a given one.
     * @param pos a BoardPos object to copy
     */
    public BoardPos(BoardPos pos) {
        x = pos.x;
        y = pos.y;
        if (pos.route != null)
            route = new ArrayList<>(pos.route);
    }


    // public interface field status & manipulation functions
    /**
     * Adds a step to the route list.
     * @param step step to add, a BoardPos object
     */
    public void addToRoute(BoardPos step) {
        // avoid null pointer exception
        if (route == null)
            route = new ArrayList<>();
        route.add(step);
    }

    /**
     * Returns current route length.
     * @return route length
     */
    public int routeLen() {
        // avoid null pointer exception
        if (route == null)
            return 0;
        else return route.size();
    }

    /**
     * Returns the current route list, or, if it's null, a new empty one.
     * @return route, a list of BoardPos objects
     */
    public List<BoardPos> getRoute() {
        if (route == null)
            return new ArrayList<>();
        return route;
    }

    /**
     * Sets a new route given as a list in place of the current one.
     * @param _route route to set, a list of BoardPos objects
     */
    public void setRoute(List<BoardPos> _route) {
        route = new ArrayList<>(_route);
    }

    /**
     * Returns the x-coordinate of the BoardPos object.
     * @return x-coordinate of the position
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the x-coordinate of the BoardPos object.
     * @return x-coordinate of the position
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the last element of the route. Please note that this method does
     * not check whether or not it actually exists.
     * @return the last element, a BoardPos object
     */
    public BoardPos getRouteLast() {
        return route.get(route.size() - 1);
    }


    // public interface misc boolean functions
    /**
     * Checks whether or not two BoardPos objects are equal. Note that this
     * method only evaluates coordinates, not routes.
     * @param o second BoardPos object
     * @return true if their coordinates are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BoardPos))
            return false;
        BoardPos oo = (BoardPos)o;
        return oo.x == x && oo.y == y;
    }

    /**
     * Checks whether or not position is in bounds of a square of a given side
     * unit count.
     * @param sideCount square side unit count
     * @return true if position in that square, false otherwise
     */
    public boolean inBounds(int sideCount) {
        return x >= 0 && y >= 0 && x < sideCount && y < sideCount;
    }

    /**
     * Checks whether or not two BoardPos objects are adjacent to each other.
     * @param pos a BoardPos object to check against
     * @return true if they are, false otherwise
     */
    public boolean isNextTo(BoardPos pos) {
        return abs(x - pos.x) == 1 && abs(y - pos.y) == 1;
    }


    // public interface arithmetic functions
    /**
     * Returns a sum of two BoardPos points.
     * @param pos position to add to, a BoardPos object
     * @return sum of positions, a BoardPos object
     */
    public BoardPos add(BoardPos pos) {
        return new BoardPos(x + pos.x, y + pos.y);
    }

    /**
     * Returns a sum of two points - a BoardPos object and x-y coordinates. Note
     * that this method also copies route.
     * @param _x x-coordinate to add
     * @param _y y-coordinate to add
     * @return sum of positions, a BoardPos object
     */
    public BoardPos add(int _x, int _y) {
        BoardPos retVal = new BoardPos(x + _x, y + _y);
        if (route != null)
            retVal.route = new ArrayList<>(route);
        return retVal;
    }

    /**
     * Returns an average of two BoardPos points.
     * @param pos position to average, a BoardPos object
     * @return average of positions, a BoardPos object
     */
    public BoardPos avg(BoardPos pos) {
        return new BoardPos((x + pos.x) / 2, (y + pos.y) / 2);
    }
}
