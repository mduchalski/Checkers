/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Mateusz
 */

public class Piece {
    private boolean empty, color, crown; // for color, true means black
    
    public Piece() {
        empty = true;
        color = false;
        crown = false;
    }

    public Piece(Piece piece) {
        empty = piece.empty;
        color = piece.color;
        crown = piece.crown;
    }
    
    public boolean color() {
        return color;
    }
    
    public boolean isEmpty() {
        return empty;
    }
    
    public void setBlack() {
        empty = false;
        color = true;
    }
    
    public void setWhite() {
        empty = false;
        color = false;
    }
    
    public void draw(GraphicsContext gc, double x, double y, double margin,
            double unitLength) {
        if (empty)
            return;
        
        if (color)
            gc.setFill(Color.BLACK);
        else 
            gc.setFill(Color.WHITE);
                        
        gc.fillOval(x + margin * unitLength, y + margin * unitLength,
                (1 - 2 * margin) * unitLength, (1 - 2 * margin) * unitLength);

        gc.setStroke(Color.BLACK);
        gc.strokeOval(x + margin * unitLength, y + margin * unitLength,
                (1 - 2 * margin) * unitLength, (1 - 2 * margin) * unitLength);
    } 

    public void setEmpty() {
        empty = true;
    }

    void setNotEmpty() {
        empty = false;
    }

    boolean isBlack() {
        return !empty && color;
    }
    
    boolean isWhite() {
        return !empty && !color;
    }
}