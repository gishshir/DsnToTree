package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import fr.tsadeo.app.dsntotree.gui.IBlocActionListener;
import fr.tsadeo.app.dsntotree.gui.MyPanelBloc.PanelRubrique;

abstract class AbstractPanelRubriqueAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	protected final IBlocActionListener blocActionListener;

	protected AbstractPanelRubriqueAction(IBlocActionListener blocActionListener) {
		this.blocActionListener = blocActionListener;
	}


	protected PanelRubrique getPanelRubrique(ActionEvent ev) {
		if (ev.getSource() != null && ev.getSource() instanceof JComponent) {
			JComponent source = (JComponent) ev.getSource();
			if (source.getParent() != null && source.getParent() instanceof PanelRubrique) {
				return (PanelRubrique) source.getParent();
			}

		}
		return null;
	}
}