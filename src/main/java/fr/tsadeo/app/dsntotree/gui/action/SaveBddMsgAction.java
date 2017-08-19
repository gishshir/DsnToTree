package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IBddActionListener;

public class SaveBddMsgAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final IBddActionListener listener;

    public SaveBddMsgAction(IBddActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // this.listener.getMessageByChrono();
    }

}
