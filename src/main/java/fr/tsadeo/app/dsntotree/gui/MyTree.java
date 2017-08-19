package fr.tsadeo.app.dsntotree.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import fr.tsadeo.app.dsntotree.model.IItemTree;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;

public class MyTree extends JTree implements TreeSelectionListener, IGuiConstants {

    private DefaultMutableTreeNode top;

    DefaultMutableTreeNode getTop() {
        return this.top;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int selectedIndex = Integer.MIN_VALUE;

    private final ItemBlocListener itemBlocListener;

    private String getPath(TreePath path) {

        StringBuffer sb = new StringBuffer();

        if (path != null) {
            for (int i = 1; i < path.getPathCount(); i++) {
                Object obj = path.getPath()[i];
                sb.append(obj.toString()).append(" - ");
            }
        }

        return sb.toString();
    }

    // --------------------------------- implements TreeSelectionListener

    @Override
    public void valueChanged(TreeSelectionEvent event) {

        int row = this.getRowForPath(event.getPath());
        System.out.println("path :" + event.getPath() + " - " + row);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
        if (node.isRoot()) {
            return;
        }
        if (!node.isLeaf()) {

            String pathParent = this.getPath(event.getPath().getParentPath());
            ItemBloc itemBloc = ((BlocNode) node).getItemBloc();
            this.itemBlocListener.onItemBlocSelected(itemBloc, row, pathParent);
        } else if (node.isLeaf()) {

            ItemRubrique itemRubrique = ((RubriqueNode) node).getItemRubrique();
            row = this.getRowForPath(event.getPath().getParentPath());

            String pathParent = this.getPath(event.getPath().getParentPath().getParentPath());
            this.itemBlocListener.onItemRubriqueSelected(itemRubrique, row, pathParent);
        }

    }

    // ------------------------------- constructor
    MyTree(ItemBlocListener itemBlocListener) {
        super((TreeNode) new DefaultMutableTreeNode("DSN"));
        super.setFont(FONT);
        super.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        super.setExpandsSelectedPaths(true);
        super.setBackground(TREE_BACKGROUND_COLOR);
        this.itemBlocListener = itemBlocListener;
        this.addTreeSelectionListener(this);
        this.top = (DefaultMutableTreeNode) this.getModel().getRoot();

        this.setCellRenderer(this.createCellRenderer());

    }

    private void removeAllFromNode(DefaultMutableTreeNode node) {

        if (node.isRoot()) {
            return;
        }
        node.removeAllChildren();
    }

    private BlocNode getBlocNodeFromPath(TreePath path) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (!node.isRoot() && !node.isLeaf()) {
            return (BlocNode) node;
        }
        return null;
    }

//    private RubriqueNode getRubriqueNodeFromPath(TreePath path) {
//
//        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
//        if (!node.isRoot() && node.isLeaf()) {
//            return (RubriqueNode) node;
//        }
//        return null;
//    }

    // FIXME traiter le cas de rafraichissement quand la dsn est non stucturee et
    // qu'on a ajouté une rubrique
    void refreshBloc(int treeRowOfBloc, ItemBloc itemBloc) {

        TreePath path = this.getPathForRow(treeRowOfBloc);
        BlocNode node = this.getBlocNodeFromPath(path);

        if (node != null) {
            this.removeAllFromNode(node);
            this.addTreeNodeRubriques(node, itemBloc, false);
            this.addTreeNodeChildrens(node, itemBloc, true);
        }

    }


    boolean search(String search, boolean next) {

        TreePath result = this.search(new TreePath(this.top), search.toLowerCase(), next);
        if (result != null) {
            System.out.println("Search OK (".concat(search).concat(") ").concat(result.toString()));
            return true;
        }

        return false;
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
                if (node.getUserObject() != null && node.getUserObject() instanceof IItemTree) {
                    IItemTree item = (IItemTree) node.getUserObject();
					if (item.getNumLine() > 0) {
						label.setToolTipText("ligne: " + item.getNumLine());
					}
					label.setOpaque(true);
					label.setBackground(TREE_BACKGROUND_COLOR);
                    if (item.isError()) {
                        label.setForeground(TREE_ERROR_COLOR);
                    } else if (item.isModified()) {
                        label.setForeground(TREE_MODIFIED_COLOR);
                    } else if (item.isCreated()) {
                        label.setForeground(TREE_CREATED_COLOR);
                    } else {
                    	label.setForeground(TREE_NORMAL_COLOR);
                    }
                }

                return label;
            }

        };

        renderer.setOpenIcon(GuiUtils.createImageIcon(PATH_BLOC_ICO));
        renderer.setLeafIcon(GuiUtils.createImageIcon(PATH_RUB_ICO));
        return renderer;
    }

    void cancelSearch() {
        this.selectedIndex = Integer.MIN_VALUE;
        // this.selectedItemBloc = null;
        // this.selectedItemRubrique = null;
        this.clearSelection();
    }

    void clearTree() {
        this.top.removeAllChildren();
    }

    // visualisation de la liste des rubriques sous forme lineaire
    void showListRubriques(ItemBloc rootBloc, List<ItemRubrique> listItemRubriques) {

        this.clearTree();
        super.setModel(new DefaultTreeModel(this.top));
        this.top.setUserObject(rootBloc);

        DefaultTreeModel model = ((DefaultTreeModel) this.getModel());
        if (listItemRubriques != null) {
            int i = 0;
            for (ItemRubrique itemRubrique : listItemRubriques) {
                RubriqueNode rubNode = new RubriqueNode(itemRubrique);
                model.insertNodeInto(rubNode, this.top, i++);
            }
        }
        this.expandRoot(true);
        this.repaint();
    }

    // visualisation bloc et rubriques sous forme arborescente
    void createNodes(ItemBloc rootBloc) {

        this.clearTree();
        super.setModel(new DefaultTreeModel(this.top));

        if (rootBloc != null && rootBloc.isRoot()) {

            this.top.setUserObject(rootBloc);
            for (ItemBloc itemBloc : rootBloc.getChildrens()) {
                this.createTreeNode(top, itemBloc, true);
            }

        }

        this.expandRoot(true);
        this.repaint();
    }

    void expandBloc(String blocLabel, boolean expand) {

        if (blocLabel.equals(ALL)) {
            this.expandRoot(expand);
        } else {
            this.expandBlocLabel(new TreePath(this.top), blocLabel, expand);
        }
    }

    private void createTreeNode(DefaultMutableTreeNode parent, ItemBloc itemBloc, boolean withChildren) {

        DefaultMutableTreeNode node = new BlocNode(itemBloc);

        this.addTreeNodeRubriques((BlocNode) node, itemBloc, false);

        if (withChildren) {
            if (itemBloc.hasChildren()) {
                for (ItemBloc childBloc : itemBloc.getChildrens()) {
                    this.createTreeNode(node, childBloc, true);
                }
            }
        }
        parent.add(node);
    }

    private void addTreeNodeRubriques(BlocNode node, ItemBloc itemBloc, boolean reloadModel) {

        DefaultTreeModel model = ((DefaultTreeModel) this.getModel());

        if (itemBloc.hasRubriques()) {
            int i = 0;
            for (ItemRubrique itemRubrique : itemBloc.getListRubriques()) {
                RubriqueNode rubNode = new RubriqueNode(itemRubrique);
                model.insertNodeInto(rubNode, node, i++);
            }
        }

        if (reloadModel) {
            model.reload(node);
        }
    }
    
	private void addTreeNodeChildrens(BlocNode node, ItemBloc itemBloc, boolean reloadModel) {

		 DefaultTreeModel model = ((DefaultTreeModel) this.getModel());
		//
		if (itemBloc.hasChildren()) {
			for (ItemBloc itemChild : itemBloc.getChildrens()) {
				this.createTreeNode(node, itemChild, true);
			}
		}
		if (reloadModel) {
			model.reload(node);
		}
	}

    private void expandRoot(boolean expand) {
        this.expandAll(new TreePath(this.top), expand);
    }

    /*
     * Recherche une rubrique contenant le texte recherché
     */
    private TreePath search(TreePath parent, String search, boolean next) {

        TreePath result = null;
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        // si on a atteint la fin du fichier et en next alors
        // on réinitialise le selectedIndex
        if (next && this.getRowForPath(parent) == this.getRowCount() - 1) {
            this.selectedIndex = Integer.MIN_VALUE;
        }
        if (node.isLeaf()) {
            ItemRubrique itemRubrique = ((RubriqueNode) node).getItemRubrique();
            if (itemRubrique.toString().toLowerCase().indexOf(search) > -1) {

                if (!next || (next && this.getRowForPath(parent) > this.selectedIndex)) {

                    this.setSelectionPath(parent);
                    this.scrollPathToVisible(parent);
                    selectedIndex = this.getRowForPath(parent);
                    return parent;
                }
            }
        }

        if (node.getChildCount() >= 0) {

            for (int i = 0; i < node.getChildCount(); i++) {
                TreeNode child = node.getChildAt(i);
                TreePath path = parent.pathByAddingChild(child);
                result = search(path, search, next);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private void expandAll(TreePath parent, boolean expand) {
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
        if (obj instanceof ItemBloc) {
            ItemBloc bloc = (ItemBloc) obj;
            if (bloc.getBlocLabel().equals(blocLabel)) {
                if (expand) {
                    this.expandPath(parent);
                } else {
                    this.collapsePath(parent);
                }
            }
        }

    }

    // ================================ inner CLASS
    private static class BlocNode extends DefaultMutableTreeNode {

        private static final long serialVersionUID = 1L;

        private ItemBloc getItemBloc() {
            return (ItemBloc) super.getUserObject();
        }

        private BlocNode(ItemBloc itemBloc) {
            super(itemBloc);
        }

        @Override
        public boolean isLeaf() {
            return false;
        }
    }

    private static class RubriqueNode extends DefaultMutableTreeNode {

        private static final long serialVersionUID = 1L;

        private ItemRubrique getItemRubrique() {
            return (ItemRubrique) super.getUserObject();
        }

        private RubriqueNode(ItemRubrique itemRubrique) {
            super(itemRubrique);
        }

        @Override
        public boolean isLeaf() {
            return true;
        }
    }

}
