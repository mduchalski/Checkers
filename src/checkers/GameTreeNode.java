// work in progress
package checkers;

import java.util.ArrayList;
import java.util.List;

public class GameTreeNode {
    private List<GameTreeNode> children = new ArrayList<>();
    private GameTreeNode parent = null;
    private BoardLogic data = null;

    public GameTreeNode(BoardLogic rootData) {
        data = rootData;
    }

    public BoardLogic getData() {
        return data;
    }

    public List<GameTreeNode> getChildren() {
        return children;
    }

    public void addChild(GameTreeNode newChild) {
        children.add(newChild);
    }
}
