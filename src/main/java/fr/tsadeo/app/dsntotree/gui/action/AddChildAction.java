package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import fr.tsadeo.app.dsntotree.gui.IBlocActionListener;
import fr.tsadeo.app.dsntotree.gui.MyPanelBloc.PanelChild;

public class AddChildAction extends AbstractPanelBlocAction {


	/**
	 * @param myPanelBloc
	 */
	public AddChildAction(IBlocActionListener listener) {
		super(listener);
	}

	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent ev) {
		PanelChild panelChild = super.getPanelChild(ev);
		if (panelChild != null) {
			this.blocActionListener.actionAddChild(panelChild);
		} else {
			
			if (this.blocActionListener.isSourceBtAddBloc(ev.getSource())) {
				
				this.blocActionListener.actionAddOtherChild();
			}

		}
	}
}