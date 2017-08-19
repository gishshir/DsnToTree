package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IBddActionListener;

public class EditBddMsgAction extends AbstractAction {

	  /**
	  * 
	  */
	 private static final long serialVersionUID = 1L;

	 private final IBddActionListener listener;

	 public EditBddMsgAction(IBddActionListener listener) {
	     this.listener = listener;
	 }
	 
		public void actionPerformed(ActionEvent e) {
	        this.listener.actionEditMessageInTree();
	    }

}
