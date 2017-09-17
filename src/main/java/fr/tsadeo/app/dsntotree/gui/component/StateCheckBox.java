package fr.tsadeo.app.dsntotree.gui.component;

import javax.swing.JCheckBox;

public class StateCheckBox extends JCheckBox implements IStateComponent {

    private static final long serialVersionUID = 1L;

    private StateComponentEnum state = StateComponentEnum.actif;

    public StateCheckBox(String text) {
        super(text);
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
}
