package fr.tsadeo.app.dsntotree.gui.component;

import javax.swing.JButton;
import javax.swing.JComponent;

public class StateButton extends JButton implements IStateComponent, IFunctionnalChild {
	

	private static final long serialVersionUID = 1L;
	
	private StateComponentEnum state = StateComponentEnum.actif;
    private JComponent functionnalContainer;
	
	public StateButton() {
		super();
	}

	public StateButton(String text) {
		super(text);
	}

	@Override
	public void setEnabled(boolean enabled) {

        this.state = enabled?StateComponentEnum.actif:StateComponentEnum.inactif;
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

    public void setFunctionnalContainer(JComponent component) {
        this.functionnalContainer = component;
    }

    @Override
    public JComponent getFunctionnalContainer() {
        return functionnalContainer;
    }
	
}
