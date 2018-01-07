package fr.tsadeo.app.dsntotree.gui.table.common;

import fr.tsadeo.app.dsntotree.gui.component.StateButton;

public class ItemStateButton<T extends ITableItemDto> extends StateButton {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private T item;

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

}
