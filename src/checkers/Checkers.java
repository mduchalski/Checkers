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
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Main class. This JavaFX application implements modified Polish/International
 * checkers game between two human players.
 * @author Mateusz
 */
public class Checkers extends Application {
    /**
     * Retrieves and draws game status message.
     * @see BoardLogic#message()
     * @param gc a given GraphicsContext object
     * @param boardLogic a given BoardLogic object
     * @param x text start position x-coordinate, pixels
     * @param y text start position y-coordinate, pixels
     * @param size text size, pixels
     */
    private void drawMessage(GraphicsContext gc, BoardLogic boardLogic,
                             double x, double y, double size) {
        gc.setFont(new Font("Arial", size));
        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.fillText(boardLogic.message(), x, y);
    }

    /**
     * @param primaryStage JavaFX primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Checkers");
        
        Group root = new Group();
        Scene primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);
        
        Canvas canvas = new Canvas(500, 500);
        root.getChildren().add(canvas);
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        BoardLogic board = new BoardLogic(50.0, 50.0, 400.0, 0.1, 8, 3);
        board.draw(gc);
        drawMessage(gc, board, 50.0, 40.0, 22.0);
        boolean endOnClick = false;

        primaryScene.setOnMouseClicked(
        new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent e)
            {
                if (board.isGameOverDelayed())
                    board.reset();

                gc.clearRect(0, 0, gc.getCanvas().getWidth(),
                        gc.getCanvas().getHeight());
                if (board.someLegalPos())
                    board.attemptMove(board.decodeMouse(e.getX(), e.getY()));
                else
                    board.highlightMoves(board.decodeMouse(e.getX(), e.getY()));
                
                board.draw(gc);
                drawMessage(gc, board, 50.0, 40.0, 22.0);
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
