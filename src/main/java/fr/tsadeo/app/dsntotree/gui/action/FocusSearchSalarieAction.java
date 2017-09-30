package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.salarie.ISalarieListener;

public class FocusSearchSalarieAction extends AbstractAction {

    /**
     * 
     */
    private final ISalarieListener listener;

    /**
     * @param myFrame
     */
    public FocusSearchSalarieAction(ISalarieListener listener) {
        this.listener = listener;
    }

    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent ev) {
        this.listener.setFocusOnSearch();
    }
}