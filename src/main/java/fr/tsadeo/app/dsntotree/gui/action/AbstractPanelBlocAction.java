package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import fr.tsadeo.app.dsntotree.gui.IBlocActionListener;
import fr.tsadeo.app.dsntotree.gui.MyPanelBloc.PanelChild;

abstract class AbstractPanelBlocAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	protected final IBlocActionListener blocActionListener;
	

	protected AbstractPanelBlocAction(IBlocActionListener blocActionListener) {
		this.blocActionListener = blocActionListener;
	}
	protected PanelChild getPanelChild(ActionEvent ev) {
		if (ev.getSource() != null && ev.getSource() instanceof JComponent) {
			JComponent source = (JComponent) ev.getSource();
			if (source.getParent() != null && source.getParent() instanceof PanelChild) {
				return (PanelChild) source.getParent();
			}

		}
		return null;
	}
}