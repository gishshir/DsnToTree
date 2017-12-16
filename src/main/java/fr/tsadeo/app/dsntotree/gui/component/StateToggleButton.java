package fr.tsadeo.app.dsntotree.gui.component;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

public class StateToggleButton extends JToggleButton implements IStateComponent, IFunctionnalChild {

    private static final long serialVersionUID = 1L;

    private StateComponentEnum state = StateComponentEnum.actif;
    private JComponent functionnalContainer;

    public StateToggleButton(ImageIcon icon, boolean selected) {
        super(icon, selected);
    }

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

    public void setFunctionnalContainer(JComponent component) {
        this.functionnalContainer = component;
    }

    @Override
    public JComponent getFunctionnalContainer() {
        return functionnalContainer;
    }
}
