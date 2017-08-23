package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IBddActionListener;

public class LoadSqlFileAction extends AbstractAction {
	 /**
	  * 
	  */
	 private static final long serialVersionUID = 1L;

	 private final IBddActionListener listener;

	 public LoadSqlFileAction(IBddActionListener listener) {
	     this.listener = listener;
	 }
	 
		public void actionPerformed(ActionEvent e) {
	        this.listener.actionLoadSqlFileByChrono();
	    }

}
