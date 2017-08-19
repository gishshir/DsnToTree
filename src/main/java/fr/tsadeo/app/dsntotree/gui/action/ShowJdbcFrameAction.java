package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IMainActionListener;

public class ShowJdbcFrameAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    private final IMainActionListener listener;

    /**
     * @param myFrame
     */
    public ShowJdbcFrameAction(IMainActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.listener.actionShowJdbcDialog();

    }

}
