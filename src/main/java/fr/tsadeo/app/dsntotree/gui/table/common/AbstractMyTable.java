package fr.tsadeo.app.dsntotree.gui.table.common;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;

public abstract class AbstractMyTable<T extends ITableItemDto> extends JTable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final IMyTableModel<T> model;
    private final IItemListener<T> itemListener;


    protected AbstractMyTable(IMyTableModel<T> tableModel, TableColumnModel tableColumnModel,
            IItemListener<T> itemListener) {
        super(tableModel, tableColumnModel);
        this.model = tableModel;
        this.itemListener = itemListener;

        this.buildListSelectionListener();

        this.setPreferredScrollableViewportSize(new Dimension(300, 70));
        this.setFillsViewportHeight(true);

    }

    List<T> getDatas() {
        return this.model.getDatas();
    }

    public void setDatas(List<T> listItems) {

        this.model.setDatas(listItems);
        super.resizeAndRepaint();
    }

    public boolean filter(String filter) {
        boolean result = this.model.filter(filter);
        if (result) {
            this.getSelectionModel().clearSelection();
            if (this.model.getRowCount() == 1) {
                this.itemListener.onItemSelected(this.model.getItem(0));
            } else {
                this.itemListener.onItemSelected(null);
            }
        }
        return result;
    }

    public void reinitSearch() {
        this.model.reinitSearch();
        this.itemListener.onItemSelected(null);
    }

    // ---------------------------------- private methods
    private void buildListSelectionListener() {

        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.getSelectionModel().addListSelectionListener(e -> {

            int[] selectedRows = AbstractMyTable.this.getSelectedRows();
            if (selectedRows != null && selectedRows.length == 1) {
                T item = model.getItem(selectedRows[0]);
                itemListener.onItemSelected(item);
            }

        });
    }

}
