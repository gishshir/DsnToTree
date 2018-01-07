package fr.tsadeo.app.dsntotree.gui.table.common;

import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.util.IConstants;

public abstract class AbstractTableItemDto implements ITableItemDto, IConstants {

	 private final ItemBloc itemBloc;
    private int index;
    private boolean visible = true;

    // ------------------------------------ implementing ITableItemDto
    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public ItemBloc getItemBloc() {
        return itemBloc;
    }
    // ----------------------------- constructor
    protected AbstractTableItemDto() {
    	this(-1, null);
    }

    protected AbstractTableItemDto(int index, ItemBloc itemBloc) {
        this.index = index;
        this.itemBloc = itemBloc;
    }

}
