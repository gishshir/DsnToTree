package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IMainActionListener;

public class ShowDsnNormeAction extends AbstractAction {

    private final IMainActionListener listener;

    /**
     * @param listener
     */
    public ShowDsnNormeAction(IMainActionListener listener) {
        this.listener = listener;
    }

    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent ev) {
        if (ev != null && ev.getSource() != null) {

            this.listener.actionShowDnsNormeFrame();
        }
    }

}
