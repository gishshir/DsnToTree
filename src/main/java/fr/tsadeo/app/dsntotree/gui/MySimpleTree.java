package fr.tsadeo.app.dsntotree.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.TransferHandler;
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
import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.util.DragAndDropUtil.ITreeDndController;
import fr.tsadeo.app.dsntotree.util.DragAndDropUtil.ItemBlocTransfertHandler;
import fr.tsadeo.app.dsntotree.util.IConstants;

public class MySimpleTree extends AbstractDsnTree
        implements TreeSelectionListener, IGuiConstants, IConstants, ActionListener {

    private static final Logger LOG = Logger.getLogger(MySimpleTree.class.getName());

    private static final String SHOW_DETAIL = "showDetail";

    // private DefaultMutableTreeNode top;
    // protected int selectedIndex = Integer.MIN_VALUE;

    // DefaultMutableTreeNode getTop() {
    // return this.getTop();
    // }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private IItemTree selectedItemTree = null;
    private IItemTree droppableItemTree = null;

    private final IMainActionListener mainActionListener;
    private final ItemBlocListener itemBlocListener;
    private MyTreePopupMenu popup;
    private TreePath currentPath;

    //---------------------------------------- overriding AbstractDsnTree
	@Override
	protected Logger getLog() {
		return LOG;
	}
	@Override
    protected TreePath searchNode(TreePath parent, String search, boolean next) {

        TreePath result = null;
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        // si on a atteint la fin du fichier et en next alors
        // on réinitialise le selectedIndex
        if (next && this.getRowForPath(parent) == this.getRowCount() - 1) {
            this.selectedIndex = Integer.MIN_VALUE;
        }

        if (node.isLeaf()) {
            // on recherche xx.xxx
            TreeNode nodeBloc = node.getParent();
            if (nodeBloc instanceof BlocNode) {
                String blocLabel = ((BlocNode) nodeBloc).getItemBloc().getBlocLabel();
                String keyRubrique = ((RubriqueNode) node).getItemRubrique().getRubriqueLabel();
                String blocAndRubrique = blocLabel.concat(".").concat(keyRubrique);
                if (blocAndRubrique.indexOf(search) > -1) {

                    int row = this.getRowForPath(parent);
                    LOG.info("found: " + search + " !!!!!" + " row: " + row);
                    if (!next || (next && (row == -1 || row > this.selectedIndex))) {

                        this.setSelectionPath(parent);
                        this.scrollPathToVisible(parent);

                        selectedIndex = this.getRowForPath(parent);
                        return parent;
                    }
                }

            }
        } else if (node instanceof BlocNode) {
            ItemBloc itemBloc = ((BlocNode) node).getItemBloc();
            if (itemBloc.getBlocLabel().toLowerCase().indexOf(search) > -1) {
                if (!next || (next && this.getRowForPath(parent) > this.selectedIndex)) {

                    this.setSelectionPath(parent);
                    this.scrollPathToVisible(parent);
                    selectedIndex = this.getRowForPath(parent);
                    return parent;
                }
            }
        }
        if (node.getChildCount() >= 0) {

            return IntStream.range(0, node.getChildCount()).mapToObj(i -> {
                TreeNode child = node.getChildAt(i);
                TreePath path = parent.pathByAddingChild(child);
                return searchNode(path, search, next);
            }).filter(r -> r != null).findFirst().orElse(null);

        }
        return null;
    }


    /*
     * Recherche une rubrique contenant le texte recherché
     */
    @Override
    protected TreePath searchValue(TreePath parent, String search, boolean next) {

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

                int row = this.getRowForPath(parent);
                LOG.info("found: " + search + " !!!!!" + " row: " + row);
                if (!next || (next && (row == -1 || row > this.selectedIndex))) {

                    this.setSelectionPath(parent);
                    this.scrollPathToVisible(parent);
                    selectedIndex = this.getRowForPath(parent);
                    return parent;
                }
            }
        }

        if (node.getChildCount() >= 0) {

            return IntStream.range(0, node.getChildCount()).mapToObj(i -> {
                TreeNode child = node.getChildAt(i);
                TreePath path = parent.pathByAddingChild(child);
                return searchValue(path, search, next);
            }).filter(r -> Objects.nonNull(r)).findFirst().orElse(null);
        }
        return null;
    }

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
        super(topLabel);
        this.itemBlocListener = itemBlocListener;
        this.mainActionListener = mainActionListener;
        this.addTreeSelectionListener(this);
        this.popup = new MyTreePopupMenu(this);

        this.setCellRenderer(this.createCellRenderer());
        this.buildMouseListener();

    }

    private TransferHandler transferHandler;

    protected TransferHandler getItemBlocTransferHandler() {
        if (this.transferHandler == null) {
            this.transferHandler = new ItemBlocTransfertHandler(this.mainActionListener, new ITreeDndController() {

                private TreePath path;

                @Override
                public boolean canPerformAction(ItemBloc blocToDrop, Point dropPoint) {

                    ItemBloc parentTarget = this.getTarget(dropPoint);
                    boolean canDrop = parentTarget == null ? false
                            : ServiceFactory.getDsnService().canDropItemBloc(itemBlocListener.getTreeRoot(),
                                    parentTarget, blocToDrop);
                    if (canDrop && this.path != null) {
                        BlocNode blocNode = MySimpleTree.this.getBlocNodeFromPath(this.path, true);
                        LOG.config("BlocNode target: " + blocNode.toString());
                        MySimpleTree.this.droppableItemTree = blocNode == null ? null : blocNode.getItemBloc();
                    } else {
                        MySimpleTree.this.droppableItemTree = null;
                    }
                    return canDrop;
                }

                @Override
                public ItemBloc getTarget(Point dropPoint) {

                    this.path = MySimpleTree.this.getPathForLocation(dropPoint.x, dropPoint.y);
                    return path == null ? null : MySimpleTree.this.getItemBlocFromPath(path, true);
                }

                @Override
                public void onItemBlocDropEnded() {
                    MySimpleTree.this.droppableItemTree = null;
                    MySimpleTree.this.itemBlocListener.onItemBlocDropEnded();
                }

                @Override
                public void onItemBlocDragStarted() {
                    MySimpleTree.this.itemBlocListener.onItemBlocDragStarted();
                }

            });

        }
        return this.transferHandler;
    }

    protected String getPathAsString(TreePath path) {

        StringBuffer sb = new StringBuffer();

        if (Objects.nonNull(path)) {

            IntStream.range(0, path.getPathCount()).forEachOrdered(i -> {
                Object obj = path.getPath()[i];
                if (i == path.getPathCount() - 1) {
                    sb.append("Bloc: ").append(obj.toString());
                } else {
                    sb.append(obj.toString()).append(TIRET_WITH_SPACE);
                }
            });
        }

        return sb.toString();
    }

    protected BlocNode getBlocNodeFromPath(TreePath path, boolean extendToRubrique) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (!node.isRoot() && !node.isLeaf()) {
            return (BlocNode) node;
        } else if (extendToRubrique && node.isLeaf()) {
            return this.getBlocNodeFromPath(path.getParentPath(), extendToRubrique);
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
        ItemBloc itemBloc = this.getItemBlocFromPath(path, true);
        if (itemBloc != null) {
            this.mainActionListener.actionShowBlocFrame(itemBloc);
        }
    }

    public ItemBloc getItemBlocFromSelection() {
        return getItemBlocFromPath(this.getSelectionPath(), true);
    }

    private ItemBloc getItemBlocFromPath(TreePath path, boolean extendToRubrique) {

        IItemTree itemTree = this.getIItemTreeFromPath(path);
        if (itemTree != null) {
            if (itemTree.isBloc()) {
                return (ItemBloc) itemTree;
            } else if (extendToRubrique) {
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
        if (itemBloc == null || this.getTop() == null) {
            return null;
        }

        if (this.getTop() instanceof BlocNode) {
            return this.findBlocNodeFromItemBloc(itemBloc, (BlocNode) this.getTop());
        }

        BlocNode result = null;

        return IntStream.range(0, getTop().getChildCount()).mapToObj(i -> getTop().getChildAt(i))
                .filter(n -> n instanceof BlocNode).map(n -> this.findBlocNodeFromItemBloc(itemBloc, (BlocNode) n))
                .filter(r -> Objects.nonNull(r)).findFirst().orElse(null);

//        for (int i = 0; i < getTop().getChildCount(); i++) {
//            TreeNode node = getTop().getChildAt(i);
//            if (node instanceof BlocNode) {
//                result = this.findBlocNodeFromItemBloc(itemBloc, (BlocNode) node);
//                if (result != null) {
//                    return result;
//                }
//            }
        // }
        // return null;
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
                // String pathParent =
                // this.getPathAsString(path.getParentPath());
                ItemBloc itemBloc = (ItemBloc) itemTree;
                if (this.itemBlocListener != null) {
                    this.itemBlocListener.onItemBlocSelected(itemBloc);
                }
                this.selectedItemTree = itemBloc;
            } else {
                ItemRubrique itemRubrique = (ItemRubrique) itemTree;
                row = this.getRowForPath(path.getParentPath());

                // String pathParent =
                // this.getPathAsString(path.getParentPath().getParentPath());
                if (this.itemBlocListener != null) {
                    this.itemBlocListener.onItemRubriqueSelected(itemRubrique);
                }
                this.selectedItemTree = itemRubrique;
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
                if (node.getUserObject() != null && node.getUserObject() instanceof IItemTree) {
                    IItemTree item = (IItemTree) node.getUserObject();
                    if (item.getNumLine() > 0) {
                        label.setToolTipText("ligne: " + item.getNumLine());
                    }

                    boolean droppable = item == droppableItemTree;
                    boolean selected = item == selectedItemTree;

                    label.setOpaque(true);
                    label.setBackground(droppable ? TREE_BACKGROUND_DROPPABLE_COLOR
                            : (selected ? Color.gray : TREE_BACKGROUND_COLOR));
                    if (droppable) {
                        label.setForeground(TREE_DROPPABLE_COLOR);
                    } else if (item.isError()) {
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

    // visualisation bloc et rubriques sous forme arborescente
    void createNodes(ItemBloc rootBloc, boolean root) {

        this.clearTree();
        super.setModel(new DefaultTreeModel(this.getTop()));

        if (root && rootBloc.isRoot() || !root) {
            if (rootBloc != null) {

                this.getTop().setUserObject(rootBloc);
                if (rootBloc.hasRubriques()) {
                    this.addTreeNodeRubriques(this.getTop(), rootBloc, false);
                }
                for (ItemBloc itemBloc : rootBloc.getChildrens()) {
                    this.createTreeNode(getTop(), itemBloc, true);
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
            this.expandBlocLabel(new TreePath(this.getTop()), blocLabel, expand);
        }
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
