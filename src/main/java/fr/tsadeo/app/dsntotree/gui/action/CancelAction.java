package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IBlocActionListener;

public class CancelAction extends AbstractAction {

	private final IBlocActionListener blocActionListener;

	/**
	 * @param myPanelBloc
	 */
	public CancelAction(IBlocActionListener blocActionListener) {
		this.blocActionListener = blocActionListener;
	}

	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent ev) {
		this.blocActionListener.actionAnnulerSaisie();
	}
}