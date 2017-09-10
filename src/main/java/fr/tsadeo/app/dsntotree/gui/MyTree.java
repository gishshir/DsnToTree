package fr.tsadeo.app.dsntotree.gui;

import java.util.List;
import java.util.logging.Logger;

import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;

public class MyTree extends MySimpleTree implements TreeSelectionListener, IGuiConstants {

	
	private static final Logger LOG = Logger.getLogger(MyTree.class.getName());

    /**
     * 
     */
    private static final long serialVersionUID = 1L;





    // ------------------------------- constructor
    MyTree(ItemBlocListener itemBlocListener) {
    	super (itemBlocListener, "DSN");
    }

    private void removeAllFromNode(DefaultMutableTreeNode node) {

        if (node.isRoot()) {
            return;
        }
        node.removeAllChildren();
    }




    // FIXME traiter le cas de rafraichissement quand la dsn est non stucturee
    // et
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

        TreePath result = this.search(new TreePath(this.getTop()), search.toLowerCase(), next);
        if (result != null) {
        	LOG.config("Search OK (".concat(search).concat(") ").concat(result.toString()));
            return true;
        }

        return false;
    }


    void cancelSearch() {
        this.selectedIndex = Integer.MIN_VALUE;
        // this.selectedItemBloc = null;
        // this.selectedItemRubrique = null;
        this.clearSelection();
    }

    // visualisation de la liste des rubriques sous forme lineaire
    void showListRubriques(ItemBloc rootBloc, List<ItemRubrique> listItemRubriques) {

        this.clearTree();
        super.setModel(new DefaultTreeModel(this.getTop()));
        this.getTop().setUserObject(rootBloc);

        DefaultTreeModel model = ((DefaultTreeModel) this.getModel());
        if (listItemRubriques != null) {
            int i = 0;
            for (ItemRubrique itemRubrique : listItemRubriques) {
                RubriqueNode rubNode = new RubriqueNode(itemRubrique);
                model.insertNodeInto(rubNode, this.getTop(), i++);
            }
        }
        this.expandRoot(true);
        this.repaint();
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


}
