package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import fr.tsadeo.app.dsntotree.gui.IBlocActionListener;

public class AddRubriquedAction extends AbstractPanelRubriqueAction {

	/**
	 * @param myPanelBloc
	 */
	public AddRubriquedAction(IBlocActionListener listener) {
		super(listener);
	}

	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent ev) {
		this.blocActionListener.actionAddRubrique();
	}
}