package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IBddActionListener;

public class FocusSearchChronoAction extends AbstractAction {

    /**
     * 
     */
    private final IBddActionListener listener;

    /**
     * @param myFrame
     */
    public FocusSearchChronoAction(IBddActionListener listener) {
        this.listener = listener;
    }

    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent ev) {
        this.listener.setFocusOnSearch();
    }
}