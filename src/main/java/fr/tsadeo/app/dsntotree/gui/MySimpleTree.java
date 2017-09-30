package fr.tsadeo.app.dsntotree.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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

public class MySimpleTree extends JTree implements TreeSelectionListener, IGuiConstants, ActionListener {

    private static final Logger LOG = Logger.getLogger(MySimpleTree.class.getName());

    private static final String SHOW_DETAIL = "showDetail";

    private DefaultMutableTreeNode top;
    protected int selectedIndex = Integer.MIN_VALUE;

    DefaultMutableTreeNode getTop() {
        return this.top;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private IItemTree selectedItemTreee = null;

    private final IMainActionListener mainActionListener;
    private final ItemBlocListener itemBlocListener;
    private MyTreePopupMenu popup;
    private TreePath currentPath;

    // ------------------------------------------ implementing ActionListener
    @Override
    public void actionPerformed(ActionEvent e) {

        if (this.currentPath != null && e.getActionCommand().equals(SHOW_DETAIL)) {
            actionShowNode(this.currentPath);
        }
    }

    // ------------------------------------ implementing TreeSelectionListener
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        actionSelectNode(e.getPath());
    }

    // ------------------------------- constructor
    public MySimpleTree(IMainActionListener mainActionListener, ItemBlocListener itemBlocListener, String topLabel) {
        super((TreeNode) new DefaultMutableTreeNode(topLabel));
        super.setFont(FONT);
        super.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        super.setExpandsSelectedPaths(true);
        super.setBackground(TREE_BACKGROUND_COLOR);
        this.itemBlocListener = itemBlocListener;
        this.mainActionListener = mainActionListener;
        this.addTreeSelectionListener(this);
        this.top = (DefaultMutableTreeNode) this.getModel().getRoot();
        this.popup = new MyTreePopupMenu(this);

        this.setCellRenderer(this.createCellRenderer());
        this.buildMouseListener();

    }

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

    protected BlocNode getBlocNodeFromPath(TreePath path) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (!node.isRoot() && !node.isLeaf()) {
            return (BlocNode) node;
        }
        return null;
    }

    private void buildMouseListener() {

        MouseListener ml = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                MySimpleTree.this.currentPath = MySimpleTree.this.getPathForLocation(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {

                    popup.show(MySimpleTree.this, e.getX(), e.getY());
                }
            }
        };
        this.addMouseListener(ml);

    }

    private void actionShowNode(TreePath path) {
        LOG.config("path :" + path);
        ItemBloc itemBloc = this.getItemBlocFromPath(path);
        if (itemBloc != null) {
            this.mainActionListener.actionShowBlocItem(itemBloc, this.getPath(path.getParentPath().getParentPath()));
        }
    }

    private ItemBloc getItemBlocFromPath(TreePath path) {

        IItemTree itemTree = this.getIItemTreeFromPath(path);
        if (itemTree != null) {
            if (itemTree.isBloc()) {
                return (ItemBloc) itemTree;
            } else {
                ItemRubrique itemRubrique = (ItemRubrique) itemTree;
                return itemRubrique.getBlocContainer();
            }
        }
        return null;
    }

    private IItemTree getIItemTreeFromPath(TreePath path) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (node.isRoot()) {
            return null;
        }
        if (!node.isLeaf()) {
            return ((BlocNode) node).getItemBloc();
        } else if (node.isLeaf()) {
            return ((RubriqueNode) node).getItemRubrique();
        }
        return null;
    }

    protected BlocNode findBlocNodeFromItemBloc(ItemBloc itemBloc) {
        if (itemBloc == null || this.top == null) {
            return null;
        }

        if (this.top instanceof BlocNode) {
            return this.findBlocNodeFromItemBloc(itemBloc, (BlocNode) this.top);
        }

        BlocNode result = null;
        for (int i = 0; i < top.getChildCount(); i++) {
            TreeNode node = top.getChildAt(i);
            if (node instanceof BlocNode) {
                result = this.findBlocNodeFromItemBloc(itemBloc, (BlocNode) node);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /*
     * Fonction recursive de recherche du TreePath à partir d'un ItemBloc
     */
    private BlocNode findBlocNodeFromItemBloc(ItemBloc itemBloc, BlocNode blocNode) {
        if (itemBloc == null || blocNode == null) {
            return null;
        }

        if (blocNode.getItemBloc() == itemBloc) {
            return blocNode;
        }

        List<BlocNode> listBlocChildren = blocNode.getBlocChildren();
        if (listBlocChildren != null) {

            for (BlocNode blocChild : listBlocChildren) {

                BlocNode result = this.findBlocNodeFromItemBloc(itemBloc, blocChild);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private void actionSelectNode(TreePath path) {

        int row = this.getRowForPath(path);
        LOG.config("path :" + path + " - " + row);

        IItemTree itemTree = this.getIItemTreeFromPath(path);
        if (itemTree != null) {

            if (itemTree.isBloc()) {
                String pathParent = this.getPath(path.getParentPath());
                ItemBloc itemBloc = (ItemBloc) itemTree;
                if (this.itemBlocListener != null) {
                    this.itemBlocListener.onItemBlocSelected(itemBloc, pathParent);
                }
                this.selectedItemTreee = itemBloc;
            } else {
                ItemRubrique itemRubrique = (ItemRubrique) itemTree;
                row = this.getRowForPath(path.getParentPath());

                String pathParent = this.getPath(path.getParentPath().getParentPath());
                if (this.itemBlocListener != null) {
                    this.itemBlocListener.onItemRubriqueSelected(itemRubrique, pathParent);
                }
                this.selectedItemTreee = itemRubrique;
            }
        }

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
                    label.setBackground(item == selectedItemTreee ? Color.gray : TREE_BACKGROUND_COLOR);
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

    void clearTree() {
        this.top.removeAllChildren();
    }

    // visualisation bloc et rubriques sous forme arborescente
    void createNodes(ItemBloc rootBloc, boolean root) {

        this.clearTree();
        super.setModel(new DefaultTreeModel(this.top));

        if (root && rootBloc.isRoot() || !root) {
            if (rootBloc != null) {

                this.top.setUserObject(rootBloc);
                if (rootBloc.hasRubriques()) {
                    this.addTreeNodeRubriques(this.top, rootBloc, false);
                }
                for (ItemBloc itemBloc : rootBloc.getChildrens()) {
                    this.createTreeNode(top, itemBloc, true);
                }

            }
        }

        this.expandRoot(false);
        this.repaint();
    }

    void expandBloc(String blocLabel, boolean expand) {

        if (blocLabel.equals(ALL)) {
            this.expandRoot(expand);
        } else {
            this.expandBlocLabel(new TreePath(this.top), blocLabel, expand);
        }
    }
    

    void cancelSearch() {
        this.selectedIndex = Integer.MIN_VALUE;
        this.clearSelection();
    }

    boolean search(String search, boolean next) {

        TreePath result = this.search(new TreePath(this.getTop()), search.toLowerCase(), next);
        if (result != null) {
            LOG.config("Search OK (".concat(search).concat(") ").concat(result.toString()));
            return true;
        }

        return false;
    }

    protected void createTreeNode(DefaultMutableTreeNode parent, ItemBloc itemBloc, boolean withChildren) {

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

    private void addTreeNodeRubriques(DefaultMutableTreeNode node, ItemBloc itemBloc, boolean reloadModel) {

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
    private static class MyTreePopupMenu extends JPopupMenu {

        private final JMenuItem menuItem;

        private MyTreePopupMenu(ActionListener actionListener) {

            this.menuItem = new JMenuItem("voir détail");
            this.menuItem.setActionCommand(SHOW_DETAIL);
            this.menuItem.addActionListener(actionListener);
            this.add(this.menuItem);

            this.setOpaque(true);
            this.setLightWeightPopupEnabled(true);
        }
    }

    protected static class BlocNode extends DefaultMutableTreeNode {

        private static final long serialVersionUID = 1L;

        protected ItemBloc getItemBloc() {
            return (ItemBloc) super.getUserObject();
        }

        protected boolean hasChildren() {
            return this.getChildCount() > 0;
        }

        protected List<BlocNode> getBlocChildren() {

            if (this.hasChildren()) {

                List<BlocNode> list = new ArrayList<>();
                for (int i = 0; i < this.getChildCount(); i++) {
                    TreeNode treeChild = this.getChildAt(i);
                    if (treeChild != null && treeChild instanceof BlocNode) {
                            list.add((BlocNode) treeChild);
                    }

                }
                return list;
            }
            return null;
        }

        protected BlocNode(ItemBloc itemBloc) {
            super(itemBloc);
        }

        @Override
        public boolean isLeaf() {
            return false;
        }
    }

    protected static class RubriqueNode extends DefaultMutableTreeNode {

        private static final long serialVersionUID = 1L;

        protected ItemRubrique getItemRubrique() {
            return (ItemRubrique) super.getUserObject();
        }

        protected RubriqueNode(ItemRubrique itemRubrique) {
            super(itemRubrique);
        }

        @Override
        public boolean isLeaf() {
            return true;
        }
    }

}
