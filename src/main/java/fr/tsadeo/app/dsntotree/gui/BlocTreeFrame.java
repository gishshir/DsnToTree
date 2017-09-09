package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;

public class BlocTreeFrame extends AbstractFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IMainActionListener mainActionListener;
	
	private MySimpleTree simpleTree;
	private ItemBloc itemBloc;

	
	protected BlocTreeFrame(String title, IMainActionListener listener) {
		 super("Arborescence du bloc " + title, JFrame.DISPOSE_ON_CLOSE);
	     this.mainActionListener = listener;

	     // Set up the content pane.
	     addComponentsToPane(this.getContentPane());

	}

	void setItemBloc(ItemBloc itemBloc) {
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
