// work in progress
package checkers;

import java.util.List;

import static java.lang.Math.min;

public class AIPlayer {
    boolean active;

    public AIPlayer() {
        active = false;
    }

    public void runTurn(BoardLogic boardLogic) {
        GameTreeNode root = new GameTreeNode(new BoardLogic(boardLogic));
        constructGameTree(root, 3);
        //int a = alphaBeta(root,-1000, 1000);
        if (!root.getChildren().isEmpty())
            boardLogic.update(root.getChildren().get(0).getData());
        return;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive() {
        active = true;
    }

    public void setInactive() {
        active = false;
    }

    private int alphaBeta(GameTreeNode node, int alpha, int beta) {
        if (node.getChildren().isEmpty())
            return heuristic(node.getData());
        else if (node.getData().getLastColor()) {
            for (GameTreeNode child : node.getChildren()) {
                beta = min(beta, alphaBeta(child, alpha, beta));
                if (alpha >= beta)
                    break;
            }
            return beta;
        } else {
            for (GameTreeNode child : node.getChildren()) {
                beta = min(beta, alphaBeta(child, alpha, beta));
                if (alpha >= beta)
                    break;
            }
            return alpha;
        }
    }

    private int heuristic(BoardLogic boardLogic) {
        Board board = boardLogic.getBoard();
        int retVal = 0;
        for (int i = 0; i < board.side(); i++)
            for (int j = 0; j < board.side(); j++)
                if (!board.get(i, j).isEmpty()) {
                    if (board.get(i, j).color()) retVal++;
                    else retVal--;
                }
        return retVal;
    }

    private void constructGameTree(GameTreeNode node, int depth) {
        if (depth < 0)
            return;

        // AI always plays as black
        List<BoardPos> moves = node.getData().longestAvailableMoves(1,
                !node.getData().getLastColor());

        for (BoardPos move : moves) {
            BoardLogic newData = new BoardLogic(node.getData());
            newData.highlightMoves(move);
            newData.attemptMove(newData.getMoves(move).get(0));
            GameTreeNode newChild = new GameTreeNode(newData);
            constructGameTree(newChild, depth - 1);
            node.addChild(newChild);
        }
    }
}
