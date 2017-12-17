package fr.tsadeo.app.dsntotree.gui.component;

import javax.swing.JComponent;
import javax.swing.JTextField;

public class StateTextField extends JTextField implements IStateComponent, IFunctionnalChild {

	private static final long serialVersionUID = 1L;

	private StateComponentEnum state = StateComponentEnum.actif;
    private JComponent functionnalContainer;

	//------------------------------------ constructors
	public StateTextField() {
		super();
	}
	public StateTextField(int size) {
		super(size);
	}
	public StateTextField(String text) {
		super(text);
	}

	@Override
	public void setEnabled(boolean enabled) {

		this.state = enabled ? StateComponentEnum.actif : StateComponentEnum.inactif;
		super.setEnabled(enabled);
	}

    public void setFunctionnalContainer(JComponent component) {
        this.functionnalContainer = component;
    }

    //-------------------------------- implementing IFunctionnalChild
    @Override
    public JComponent getFunctionnalContainer() {
        return functionnalContainer;
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
