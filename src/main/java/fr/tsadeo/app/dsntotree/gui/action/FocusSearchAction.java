package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IMainActionListener;

public class FocusSearchAction extends AbstractAction {

    /**
	 * 
	 */
	private final IMainActionListener listener;

	/**
	 * @param myFrame
	 */
	public FocusSearchAction(IMainActionListener listener) {
		this.listener = listener;
	}

	private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent ev) {
        this.listener.setFocusOnSearch();
    }
}