package checkers;

import java.util.List;

public class AIPlayer {
    boolean active;

    public AIPlayer() {
        active = false;
    }

    public void runTurn(BoardLogic boardLogic) {
        GameTreeNode root = new GameTreeNode(new BoardLogic(boardLogic));
        constructGameTree(root, 3);
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
