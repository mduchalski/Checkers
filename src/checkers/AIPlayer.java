package checkers;

import java.util.List;

public class AIPlayer {
    BoardLogic boardLogic;

    public AIPlayer() {
        boardLogic = null;
    }

    public void attach(BoardLogic _boardLogic) {
        boardLogic = _boardLogic;
    }

    public BoardLogic attachedBoard() {
        return boardLogic;
    }

    public void runTurn() {
        GameTreeNode root = new GameTreeNode(new BoardLogic(boardLogic));
        constructGameTree(root, 3);
        if (!root.getChildren().isEmpty())
            boardLogic = root.getChildren().get(0).getData();
        return;
    }

    private void constructGameTree(GameTreeNode node, int depth) {
        if (depth < 0)
            return;

        // AI always plays as black
        List<BoardPos> moves = node.getData().longestAvailableMoves(1, true);

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
