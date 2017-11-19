package fr.tsadeo.app.dsntotree.gui.component;

import javax.swing.JComboBox;

public class StateComboBox<E> extends JComboBox<E> implements IStateComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private StateComponentEnum state = StateComponentEnum.actif;

	// ------------------------------------ implementing IStateComponent
	@Override
	public void setEnabled(boolean enabled) {

		this.state = enabled ? StateComponentEnum.actif : StateComponentEnum.inactif;
		super.setEnabled(enabled);
	}

	// -------------------------------- implementing IStateComponent
	@Override
	public void waitEndAction() {
		super.setEnabled(false);
	}

	@Override
	public void actionEnded() {
		super.setEnabled(this.state == StateComponentEnum.actif);
	}

}
