package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IBddInstanceListener;

public class FocusSearchInstanceAction extends AbstractAction {

    /**
     * 
     */
    private final IBddInstanceListener listener;

    /**
     * @param myFrame
     */
    public FocusSearchInstanceAction(IBddInstanceListener listener) {
        this.listener = listener;
    }

    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent ev) {
        this.listener.setFocusOnSearch();
    }
}