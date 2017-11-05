package fr.tsadeo.app.dsntotree.gui;

import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public abstract class AbstractDsnTree extends JTree implements IGuiConstants {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private DefaultMutableTreeNode top;
    protected int selectedIndex = Integer.MIN_VALUE;

    protected DefaultMutableTreeNode getTop() {
        return this.top;
    }

    protected AbstractDsnTree(String topLabel) {
        super((TreeNode) new DefaultMutableTreeNode(topLabel));
        super.setFont(FONT);
        super.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        super.setExpandsSelectedPaths(true);
        super.setBackground(TREE_BACKGROUND_COLOR);
        this.top = (DefaultMutableTreeNode) this.getModel().getRoot();
    }
    

    boolean search(String search, boolean node, boolean next) {

        TreePath result = node?
        		this.searchNode(new TreePath(this.getTop()), search.toLowerCase(), next):
        		this.searchValue(new TreePath(this.getTop()), search.toLowerCase(), next);
        if (result != null) {
            getLog().config("Search OK (".concat(search).concat(") ").concat(result.toString()));
            return true;
        }

        return false;
    }
    
    void cancelSearch() {
        this.selectedIndex = Integer.MIN_VALUE;
        this.clearSelection();
    }

    protected abstract Logger getLog();
    protected abstract TreePath searchValue(TreePath treePath, String search, boolean next);
    protected abstract TreePath searchNode(TreePath treePath, String search, boolean next);

	protected void clearTree() {
        this.top.removeAllChildren();
    }

    protected void expandRoot(boolean expand) {
        this.expandAll(new TreePath(this.top), expand);
    }

    protected void expandAll(TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() > 0) {

            for (int i = 0; i < node.getChildCount(); i++) {
                TreeNode child = node.getChildAt(i);
                if (!node.isLeaf()) {
                    TreePath path = parent.pathByAddingChild(child);
                    expandAll(path, expand);
                }
            }
        }
        if (expand) {
            this.expandPath(parent);
        } else {
            this.collapsePath(parent);
        }
    }
}
