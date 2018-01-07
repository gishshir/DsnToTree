package fr.tsadeo.app.dsntotree.gui.table.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.table.AbstractTableModel;

import fr.tsadeo.app.dsntotree.gui.IGuiConstants;
import fr.tsadeo.app.dsntotree.gui.table.dto.ITableItemDto;

public  class MyTableModel<T extends ITableItemDto> extends AbstractTableModel
        implements IMyTableModel<T>, IGuiConstants {

    private final List<T> listItems = new ArrayList<>();

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final Column[] tabColumns;
    private final RowMapper<T> rowMapper;

    // -------------------------------- constructor

    public MyTableModel(Column[] tabColumns, RowMapper<T> rowMapper) {
        this.tabColumns = tabColumns;
        this.rowMapper = rowMapper;
    }

    // ------------------------------- implementing TableModel
    @Override
    public int getRowCount() {
        Long count = listItems.stream().filter(item -> item.isVisible()).count();
        return count.intValue();
    }

    @Override
    public int getColumnCount() {
        return tabColumns.length;
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        return this.rowMapper == null ? null : this.rowMapper.getValue(this.getItem(rowIndex), columnIndex);
    }



    // ------------------ overriding AbstractTableModel
    @Override
    public String getColumnName(int col) {
        return tabColumns[col].getTitle();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    // ------------------- implementing IMyTableModel
    @Override
    public List<T> getDatas() {
        return this.listItems;
    }

    @Override
    public void setDatas(List<T> listItems) {
        this.clear();
        this.listItems.addAll(listItems);
    }

    @Override
    public boolean filter(String filter) {

        String searchUpperCase = filter.toUpperCase();

        AtomicBoolean result = new AtomicBoolean(false);
        this.getDatas().stream().forEachOrdered(item -> {
            if (item.getValueForSearch().indexOf(searchUpperCase) > -1) {
                item.setVisible(true);
                result.set(true);
            } else {
                item.setVisible(false);
            }
        });

        if (result.get()) {
            this.fireTableDataChanged();
        }
        return result.get();
    }

    @Override
    public void reinitSearch() {
        listItems.stream().forEach(item -> item.setVisible(true));
        this.fireTableDataChanged();
    }

    /**
     * Retourne le salarie visible pour la ligne rowIndex
     */
    @Override
    public T getItem(int rowIndex) {

        AtomicInteger compteur = new AtomicInteger(-1);
        return this.getDatas().stream().filter(item -> item.isVisible())
                .filter(item -> compteur.incrementAndGet() == rowIndex).findFirst().orElse(null);

    }


    // ----------------- private methods
    private void clear() {
        this.listItems.clear();
    }
}
