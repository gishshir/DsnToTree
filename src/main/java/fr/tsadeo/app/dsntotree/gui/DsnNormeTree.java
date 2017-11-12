package fr.tsadeo.app.dsntotree.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
    private static final Logger LOG = Logger.getLogger(DsnNormeTree.class.getName());

    private BlocTree selectedBlocTree;

    public DsnNormeTree(String topLabel) {
        super(topLabel);
        this.setCellRenderer(this.createCellRenderer());
    }

    //---------------------------------------------- overriding AbstractDsnTree
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
        	if (nodeBloc instanceof BlocTreeNode) {
        		String blocLabel = ((BlocTreeNode) nodeBloc).getBlocTree().getBlocLabel();
        		String keyRubrique = ((BlocTreeRubriqueNode) node).getKeyRubrique();
        		String blocAndRubrique = blocLabel.concat(".").concat(keyRubrique);
                if (blocAndRubrique.indexOf(search) > -1) {

                    int row = this.getRowForPath(parent);
                    LOG.info("found: " + search + " !!!!!" + " row: " + row);
                    if (!next || (next && (row == -1 || row > this.selectedIndex))) {

                        this.setSelectionPath(parent);
                        this.scrollPathToVisible(parent);
                        LOG.info("found: " + search + " !!!!!");
                        selectedIndex = this.getRowForPath(parent);
                        return parent;
                    }
                }

        	}
                    } else 
		if ( node instanceof BlocTreeNode) {
			BlocTree blocTree = ((BlocTreeNode) node).getBlocTree();
			if (blocTree.getBlocLabel().toLowerCase().indexOf(search) > -1) {

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

             for (int i = 0; i < node.getChildCount(); i++) {
                 TreeNode child = node.getChildAt(i);
                 TreePath path = parent.pathByAddingChild(child);
                 result = searchNode(path, search, next);
                 if (result != null) {
                     return result;
                 }
             }
         }
         return null;
	}


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
              String libelleRubrique = ((BlocTreeRubriqueNode) node).getLibelleRubrique();
              if (libelleRubrique.toLowerCase().indexOf(search) > -1) {

                int row = this.getRowForPath(parent);
                LOG.info("found: " + search + " !!!!!" + " row: " + row);
                if (!next || (next && (row == -1 || row > this.selectedIndex))) {

                      this.setSelectionPath(parent);
                      this.scrollPathToVisible(parent);
                      LOG.info("found: " + search + " !!!!!");
                      selectedIndex = this.getRowForPath(parent);
                      return parent;
                  }
              }
          } else 
			if (node instanceof BlocTreeNode) {
				BlocTree blocTree = ((BlocTreeNode) node).getBlocTree();
				if (blocTree.toString().toLowerCase().indexOf(search) > -1) {

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

              for (int i = 0; i < node.getChildCount(); i++) {
                  TreeNode child = node.getChildAt(i);
                  TreePath path = parent.pathByAddingChild(child);
                  result = searchValue(path, search, next);
                  if (result != null) {
                      return result;
                  }
              }
          }
          return null;
	}
    //--------------------------------------------------------
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

                    label.setOpaque(true);
                    label.setBackground(sel ? Color.gray : TREE_BACKGROUND_COLOR);
                    label.setForeground(TREE_NORMAL_COLOR);
                } else if (node instanceof BlocTreeRubriqueNode) {

                    label.setBackground(sel ? Color.gray : TREE_BACKGROUND_COLOR);
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
                        keyAndLibelle);
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
        
        private final KeyAndLibelle keyAndLibelle;

        private String getKeyRubrique() {
            return this.keyAndLibelle.getKey();
        }
        private String getLibelleRubrique() {
            return this.keyAndLibelle.getLibelle();
        }

        protected BlocTreeRubriqueNode(KeyAndLibelle keyAndLibelle) {
            super(keyAndLibelle);
            this.keyAndLibelle = keyAndLibelle;
        }

        @Override
        public boolean isLeaf() {
            return true;
        }

    }


}
