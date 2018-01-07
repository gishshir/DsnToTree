package fr.tsadeo.app.dsntotree.gui.table.common;

public interface IItemListener<T extends ITableItemDto> {

    public void onItemSelected(T item);

    public void setFocusOnSearch();
}
