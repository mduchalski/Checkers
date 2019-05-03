/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package checkers;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author Mateusz
 */
public class Checkers extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Checkers");
        
        Group root = new Group();
        Scene primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);
        
        Canvas canvas = new Canvas(500, 500);
        root.getChildren().add(canvas);
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Board board = new Board(50.0, 50.0, 400.0, 0.1, 8, 3);
        board.init();
        board.draw(gc);
        
        primaryScene.setOnMouseClicked(
        new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent e)
            {
                if (board.someActivePos())
                    board.attemptMove(e.getX(), e.getY());
                else
                    board.highlightMoves(e.getX(), e.getY());
                
                board.draw(gc);
            }
        });
        
        
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
