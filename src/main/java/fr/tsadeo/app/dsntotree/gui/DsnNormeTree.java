package fr.tsadeo.app.dsntotree.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import fr.tsadeo.app.dsntotree.dico.KeyAndLibelle;
import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.util.IConstants;

public class DsnNormeTree extends AbstractDsnTree implements IConstants {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private BlocTree selectedBlocTree;

    public DsnNormeTree(String topLabel) {
        super(topLabel);
        this.setCellRenderer(this.createCellRenderer());
    }

    // visualisation bloc et rubriques sous forme arborescente
    void createNodes(BlocTree rootBloc, boolean root) {

        this.clearTree();
        super.setModel(new DefaultTreeModel(this.getTop()));

        if (root && rootBloc.isRoot() || !root) {
            if (rootBloc != null) {

                this.getTop().setUserObject(rootBloc);

                for (BlocTree blocTree : rootBloc.getChildrens()) {
                    this.createTreeNode(getTop(), blocTree, true);
                }

            }
        }

        this.expandRoot(false);
        this.repaint();
    }

    // ------------------------------------------- private methods

    void expandBloc(String blocLabel, boolean expand) {

        if (blocLabel.equals(ALL)) {
            this.expandRoot(expand);
        } else {
            this.expandBlocLabel(new TreePath(this.getTop()), blocLabel, expand);
        }
    }

    private void expandBlocLabel(TreePath parent, String blocLabel, boolean expand) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {

            for (int i = 0; i < node.getChildCount(); i++) {
                TreeNode child = node.getChildAt(i);
                TreePath path = parent.pathByAddingChild(child);
                expandBlocLabel(path, blocLabel, expand);
            }
        }
        Object obj = node.getUserObject();
        if (obj instanceof BlocTree) {
            BlocTree bloc = (BlocTree) obj;
            if (bloc.getBlocLabel().equals(blocLabel)) {
                if (expand) {
                    this.expandPath(parent);
                } else {
                    System.out.println("collapse " + bloc.getBlocLabel());
                    this.collapsePath(parent);
                }
            }
        }

    }

    private DefaultTreeCellRenderer createCellRenderer() {

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {

            private static final long serialVersionUID = 1L;

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                    boolean leaf, int row, boolean hasFocus) {

                JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
                        hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node.getUserObject() != null && node.getUserObject() instanceof BlocTree) {
                    BlocTree blocTree = (BlocTree) node.getUserObject();

                    boolean selected = blocTree == selectedBlocTree;

                    label.setOpaque(true);
                    label.setBackground(selected ? Color.gray : TREE_BACKGROUND_COLOR);
                    label.setForeground(TREE_NORMAL_COLOR);
                } else if (node instanceof BlocTreeRubriqueNode) {

                    boolean selected = false;
                    label.setBackground(selected ? Color.gray : TREE_BACKGROUND_COLOR);
                    label.setForeground(TREE_CREATED_COLOR);
                }

                return label;
            }

        };

        renderer.setOpenIcon(GuiUtils.createImageIcon(PATH_BLOC_ICO));
        renderer.setLeafIcon(GuiUtils.createImageIcon(PATH_RUB_ICO));
        return renderer;
    }

    private void createTreeNode(DefaultMutableTreeNode parent, BlocTree blocTree, boolean withChildren) {

        DefaultMutableTreeNode node = new BlocTreeNode(blocTree);

        // ajouter les rubriques
        List<KeyAndLibelle> listRubriques = ServiceFactory.getDictionnaryService().getDsnDictionnary()
                .getOrderedListOfSubItem(blocTree.getBlocLabel());
        if (listRubriques != null) {
            for (KeyAndLibelle keyAndLibelle : listRubriques) {
                BlocTreeRubriqueNode rubrique = new BlocTreeRubriqueNode(
                        keyAndLibelle.getKey() + " - " + keyAndLibelle.getLibelle());
                node.add(rubrique);
            }
        }

        if (withChildren) {
            if (blocTree.hasChildrens()) {
                for (BlocTree childTree : blocTree.getChildrens()) {
                    this.createTreeNode(node, childTree, true);
                }
            }
        }
        parent.add(node);
    }

    // ========================================= INNER CLASS
    private static class BlocTreeNode extends DefaultMutableTreeNode {

        private static final long serialVersionUID = 1L;

        private BlocTree getBlocTree() {
            return (BlocTree) super.getUserObject();
        }

        private boolean hasChildren() {
            return this.getChildCount() > 0;
        }

        private List<BlocTreeNode> getBlocChildren() {

            if (this.hasChildren()) {

                List<BlocTreeNode> list = new ArrayList<>();
                for (int i = 0; i < this.getChildCount(); i++) {
                    TreeNode treeChild = this.getChildAt(i);
                    if (treeChild != null && treeChild instanceof BlocTreeNode) {
                        list.add((BlocTreeNode) treeChild);
                    }

                }
                return list;
            }
            return null;
        }

        protected BlocTreeNode(BlocTree blocTree) {
            super(blocTree);
        }

        @Override
        public boolean isLeaf() {
            return false;
        }
    }

    private static class BlocTreeRubriqueNode extends DefaultMutableTreeNode {

        private static final long serialVersionUID = 1L;

        private String getLibelleRubrique() {
            return (String) super.getUserObject();
        }

        protected BlocTreeRubriqueNode(String libelleRubrique) {
            super(libelleRubrique);
        }

        @Override
        public boolean isLeaf() {
            return true;
        }

    }

}
