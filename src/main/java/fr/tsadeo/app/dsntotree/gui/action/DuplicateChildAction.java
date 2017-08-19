package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import fr.tsadeo.app.dsntotree.gui.IBlocActionListener;
import fr.tsadeo.app.dsntotree.gui.MyPanelBloc.PanelChild;

public class DuplicateChildAction extends AbstractPanelBlocAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param myPanelBloc
	 */
	public DuplicateChildAction(IBlocActionListener listener) {
		super(listener);
	}


	@Override
	public void actionPerformed(ActionEvent ev) {
		PanelChild panelChild = super.getPanelChild(ev);
		if (panelChild != null) {
			this.blocActionListener.actionDuplicateChildWithContirmation(panelChild);
		}	
		
	}

}
