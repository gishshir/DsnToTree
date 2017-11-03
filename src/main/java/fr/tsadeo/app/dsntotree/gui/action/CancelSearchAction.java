package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.ISearchActionListener;

public class CancelSearchAction extends AbstractAction {

    /**
	 * 
	 */
	private final ISearchActionListener  listener;

	/**
	 * @param myFrame
	 */
	public CancelSearchAction(ISearchActionListener listener) {
		this.listener = listener;
	}

	private static final long serialVersionUID = 1L;

	@Override
    public void actionPerformed(ActionEvent ev) {
        this.listener.actionCancelSearch();
    }
}