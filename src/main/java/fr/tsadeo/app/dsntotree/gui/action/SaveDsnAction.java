package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IMainActionListener;

public class SaveDsnAction extends AbstractAction {

    /**
	 * 
	 */
	private final IMainActionListener listener;

	/**
	 * @param myFrame
	 */
	public SaveDsnAction(IMainActionListener listener) {
		this.listener = listener;
	}

	private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent ev) {
        this.listener.actionSaveDsn(true);
    }
}