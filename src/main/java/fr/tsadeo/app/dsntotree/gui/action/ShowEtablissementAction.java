package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IMainActionListener;
import fr.tsadeo.app.dsntotree.gui.etabliss.EtablissementFrame.EtablissementStateButton;

public class ShowEtablissementAction extends AbstractAction {

	private final IMainActionListener listener;

	/**
	 * @param myPanelBloc
	 */
	public ShowEtablissementAction(IMainActionListener listener) {
		this.listener = listener;
	}

	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent ev) {
		if (ev != null && ev.getSource() != null) {
			Object src = ev.getSource();
			if (src instanceof EtablissementStateButton) {
				
				EtablissementStateButton btEtablissement = (EtablissementStateButton) src;
				if (btEtablissement.getEtablissement() != null) {
				listener.actionShowBlocFrame(btEtablissement.getEtablissement().getItemBloc());
				}
			}
		}
	}
}