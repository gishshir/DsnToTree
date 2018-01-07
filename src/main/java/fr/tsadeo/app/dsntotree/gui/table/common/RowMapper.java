package fr.tsadeo.app.dsntotree.gui.table.common;

import fr.tsadeo.app.dsntotree.gui.table.dto.ITableItemDto;

@FunctionalInterface
public interface RowMapper<T extends ITableItemDto> {

    public String getValue(T item, int columnIndex);
}
