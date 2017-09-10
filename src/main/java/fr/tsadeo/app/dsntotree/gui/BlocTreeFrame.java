package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;
import fr.tsadeo.app.dsntotree.util.ListItemBlocListenerManager;

public class BlocTreeFrame extends AbstractFrame implements ItemBlocListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IMainActionListener mainActionListener;
	
	private MySimpleTree simpleTree;
	private ItemBloc itemBloc;

	
	
	
	//------------------------------------- implementing ItemBlocListener
	@Override
	public void onItemBlocSelected(ItemBloc itemBloc, int treeRowOfBloc, String path) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onItemRubriqueSelected(ItemRubrique itemRubrique, int treeRowOfBloc, String path) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onItemBlocModified(ItemBloc modifiedItemBloc, int treeRowOfBloc, ModifiedState state, boolean refresh) {

		if (this.itemBloc.isDeleted()) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			return;
		}
		if (this.itemBloc == modifiedItemBloc ||
				this.itemBloc.isAncestorBloc(modifiedItemBloc) || this.itemBloc.isDescendentBloc(modifiedItemBloc)) {

			this.simpleTree.clearTree();
			this.simpleTree.createNodes(itemBloc, false);
		}
	}



   //---------------------------------------------- constructor	
	protected BlocTreeFrame(String title, IMainActionListener listener) {
		super("Arborescence du bloc " + title, JFrame.DISPOSE_ON_CLOSE);
		this.mainActionListener = listener;

		// Set up the content pane.
		addComponentsToPane(this.getContentPane());

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ListItemBlocListenerManager.get().removeItemBlocListener((ItemBlocListener) BlocTreeFrame.this);
			}
		});

	}

	void setItemBloc(ItemBloc itemBloc) {
		this.itemBloc= itemBloc;
		this.simpleTree.createNodes(itemBloc, false);
	}

	//-------------------------------------------- private methode
	private void addComponentsToPane(Container pane) {
	      pane.setLayout(new BorderLayout());

	      createPanelTop(pane, BorderLayout.PAGE_START);
	      createPanelMiddle(pane, BorderLayout.CENTER);
	}


	private void createPanelMiddle(Container pane, String layout) {

		 this.simpleTree = new MySimpleTree(null, "Bloc");
	     JScrollPane scrollPane = new JScrollPane(this.simpleTree);
	     
	     pane.add(scrollPane, layout);
	}


	private void createPanelTop(Container pane, String pageStart) {
		// TODO Auto-generated method stub
		
	}





	
}
