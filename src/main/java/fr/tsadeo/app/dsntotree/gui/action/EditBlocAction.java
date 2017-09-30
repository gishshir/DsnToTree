package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import fr.tsadeo.app.dsntotree.gui.IBlocActionListener;
import fr.tsadeo.app.dsntotree.gui.MyPanelBloc.PanelRubrique;

public class EditBlocAction extends AbstractPanelRubriqueAction {


	/**
	 * @param myPanelBloc
	 */
	public EditBlocAction(IBlocActionListener listener) {
		super(listener);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent ev) {
		PanelRubrique panelRubrique = super.getPanelRubrique(ev);
		if (panelRubrique != null) {
			this.blocActionListener.actionDeleteRubrique(panelRubrique);
		}
	}
}