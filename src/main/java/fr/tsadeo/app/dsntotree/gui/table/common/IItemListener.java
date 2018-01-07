package fr.tsadeo.app.dsntotree.gui.table.common;

import fr.tsadeo.app.dsntotree.gui.table.dto.ITableItemDto;

public interface IItemListener<T extends ITableItemDto> {

    public void onItemSelected(T item);

    public void setFocusOnSearch();
}
