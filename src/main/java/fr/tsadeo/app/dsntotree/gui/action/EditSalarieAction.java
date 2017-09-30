package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IMainActionListener;
import fr.tsadeo.app.dsntotree.gui.salarie.SalariesFrame.SalarieStateButton;

public class EditSalarieAction extends AbstractAction {

	private final IMainActionListener listener;

	/**
	 * @param myPanelBloc
	 */
	public EditSalarieAction(IMainActionListener listener) {
		this.listener = listener;
	}

	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent ev) {
		if (ev != null && ev.getSource() != null) {
			Object src = ev.getSource();
			if (src instanceof SalarieStateButton) {
				
				SalarieStateButton btSalarie = (SalarieStateButton) src;
				if (btSalarie.getSalarie() != null) {
				listener.actionEditBlocItem(btSalarie.getSalarie().getItemBloc(), btSalarie.getPathParent());
				}
			}
		}
	}
}