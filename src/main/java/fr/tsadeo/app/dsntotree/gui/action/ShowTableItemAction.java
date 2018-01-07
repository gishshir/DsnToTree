package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IMainActionListener;
import fr.tsadeo.app.dsntotree.gui.table.common.ItemStateButton;
import fr.tsadeo.app.dsntotree.gui.table.dto.ITableItemDto;

public class ShowTableItemAction<T extends ITableItemDto> extends AbstractAction {

	private final IMainActionListener listener;

	/**
	 * @param myPanelBloc
	 */
	public ShowTableItemAction(IMainActionListener listener) {
		this.listener = listener;
	}

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent ev) {
		if (ev != null && ev.getSource() != null) {
			Object src = ev.getSource();
			if (src instanceof ItemStateButton) {
				
				ItemStateButton<T> btItem = (ItemStateButton<T>) src;
				if (btItem.getItem() != null) {
				listener.actionShowBlocFrame(btItem.getItem().getItemBloc());
				}
			}
		}
	}
}