package fr.tsadeo.app.dsntotree.gui.table.common;

import fr.tsadeo.app.dsntotree.gui.table.dto.ITableItemDto;
import fr.tsadeo.app.dsntotree.util.IConstants;

public abstract class AbstractTableItemDto implements ITableItemDto, IConstants {

    private int index;
    private boolean visible;

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

    // ----------------------------- constructor
    protected AbstractTableItemDto() {
    }

    protected AbstractTableItemDto(int index) {
        this.index = index;
    }

}
