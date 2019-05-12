/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This class implements a checkers piece with its properties and graphics.
 */
public class Piece {
    private boolean empty, color, crown; // for color, true means black


    // initialization functions
    /**
     * Initializes a Piece object as empty, white, non-crown.
     */
    public Piece() {
        empty = true;
        color = false;
        crown = false;
    }

    /**
     * Initializes a new Piece object as a copy of a given one.
     * @param piece a Piece object to copy
     */
    public Piece(Piece piece) {
        empty = piece.empty;
        color = piece.color;
        crown = piece.crown;
    }


    // public interface read functions
    /**
     * Checks whether or not the piece is empty
     * @return true if piece is empty, false otherwise
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * Checks whether or not the piece is crown
     * @return true if piece is crown, false otherwise
     */
    public boolean isCrown() {
        return crown;
    }

    /**
     * Returns piece color. Note that this method doesn't provide information
     * about whether or not piece is empty
     * @return true for black, false for white
     */
    public boolean color() {
        return color;
    }


    // public interface write functions
    /**
     * Sets piece color to white. Note that this also changes empty status to
     * non-empty.
     */
    public void setWhite() {
        empty = false;
        crown = false;
        color = false;
    }

    /**
     * Sets piece color to black. Note that this also changes empty status to
     * non-empty.
     */
    public void setBlack() {
        empty = false;
        crown = false;
        color = true;
    }

    /**
     * Promotes piece to crown.
     */
    public void setCrown() {
        crown = true;
    }

    /**
     * Sets piece to empty.
     */
    public void setEmpty() {
        empty = true;
    }


    // other public interface functions
    /**
     * Draws a piece. Called when drawing a board.
     * @see BoardLogic#draw(GraphicsContext)
     * @param gc desired GraphicsContext object
     * @param x x-coordinate of the upper-left board unit corner, pixels
     * @param y y-coordinate of the upper-left board unit corner, pixels
     * @param margin margin to leave on each side when drawing a piece,
     *               fraction [0, 0.5) of single unit's side length
     * @param unitLength board unit's side length
     */
    public void draw(GraphicsContext gc, double x, double y, double margin,
            double unitLength) {
        if (empty)
            return;
        
        if (color)
            gc.setFill(Color.BLACK);
        else gc.setFill(Color.WHITE);
                        
        gc.fillOval(x + margin * unitLength, y + margin * unitLength,
                (1 - 2 * margin) * unitLength, (1 - 2 * margin) * unitLength);

        gc.setStroke(Color.BLACK);
        gc.strokeOval(x + margin * unitLength, y + margin * unitLength,
                (1 - 2 * margin) * unitLength, (1 - 2 * margin) * unitLength);

        if (color)
            gc.setStroke(Color.WHITE);
        if (crown)
            gc.strokeOval(x + 2*margin*unitLength, y + 2*margin*unitLength,
                    (1 - 4*margin) * unitLength, (1 - 4*margin) * unitLength);
    }
}