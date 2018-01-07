package fr.tsadeo.app.dsntotree.gui.table.common;

@FunctionalInterface
public interface RowMapper<T extends ITableItemDto> {

    public String getValue(T item, int columnIndex);
}
