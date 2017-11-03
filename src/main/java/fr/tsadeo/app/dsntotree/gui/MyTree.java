package fr.tsadeo.app.dsntotree.gui;

import java.util.List;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;

public class MyTree extends MySimpleTree implements IGuiConstants {

    private static final Logger LOG = Logger.getLogger(MyTree.class.getName());

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    //---------------------------------------- overriding AbstractDsnTree
  	@Override
  	protected Logger getLog() {
  		return LOG;
  	}
    // ------------------------------- constructor
    MyTree(IMainActionListener mainActionListener, ItemBlocListener itemBlocListener) {
        super(mainActionListener, itemBlocListener, "DSN");
        this.setTransferHandler(this.getItemBlocTransferHandler());
    }

    private void removeAllFromNode(DefaultMutableTreeNode node) {

        if (node.isRoot()) {
            return;
        }
        node.removeAllChildren();
    }


    String getPathAsString(ItemBloc itemBloc){
    	TreePath path = this.getPath(itemBloc);
    	return path == null? "":super.getPathAsString(path);
    }
    TreePath getPath(ItemBloc itemBloc) {
    	BlocNode node = this.findBlocNodeFromItemBloc(itemBloc);
    	if (node != null) {
    		TreePath path = new TreePath(node.getPath());
    		return path;
    	}
    	return null;
    }
    TreePath refreshBloc(ItemBloc itemBloc) {

        return this.refreshBloc(itemBloc, this.findBlocNodeFromItemBloc(itemBloc));
    }

    TreePath refreshBloc(ItemBloc itemBloc, BlocNode node) {

        if (node != null) {
            this.removeAllFromNode(node);
            this.addTreeNodeRubriques(node, itemBloc, false);
            this.addTreeNodeChildrens(node, itemBloc, true);
            
            return new TreePath(node.getPath());
        }
        return null;
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

}
