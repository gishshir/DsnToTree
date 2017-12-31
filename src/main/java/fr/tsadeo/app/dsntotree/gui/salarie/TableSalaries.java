package fr.tsadeo.app.dsntotree.gui.salarie;

import java.awt.Dimension;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import fr.tsadeo.app.dsntotree.business.SalarieDto;

public class TableSalaries extends JTable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static Column[] tabColumns = new Column[] { 
    		new Column(0, 5, "index"), 
    		new Column(1, 50, "Siren"),
    		new Column(2, 20, "Nic"),
    		new Column(3, 20, "Nir"),
            new Column(4, 50, "Nom"), 
            new Column(5, 75, "Prénoms") };

    private final SalariesTableModel model;
    private final ISalarieListener salarieListener;

    TableSalaries(ISalarieListener salarieListener) {

        super(new SalariesTableModel(tabColumns), new MyColumnModel(tabColumns));
        this.model = (SalariesTableModel) super.getModel();
        this.salarieListener = salarieListener;

        this.buildListSelectionListener();

        this.setPreferredScrollableViewportSize(new Dimension(300, 70));
        this.setFillsViewportHeight(true);

    }

    List<SalarieDto> getDatas() {
        return this.model.getDatas();
    }

    void setDatas(List<SalarieDto> listSalaries) {

        this.model.setDatas(listSalaries);
    }

    boolean search(String search) {
        boolean result = this.model.search(search);
        if (result) {
            this.getSelectionModel().clearSelection();
            if (this.model.getRowCount() == 1) {
                this.salarieListener.onSalarieSelected(this.model.getSalarie(0));
            } else {
                this.salarieListener.onSalarieSelected(null);
            }
        }
        return result;
    }

    void reinitSearch() {
        this.model.reinitSearch();
        this.salarieListener.onSalarieSelected(null);
    }

    // ---------------------------------- private methods
    private void buildListSelectionListener() {

        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {

                int[] selectedRows = TableSalaries.this.getSelectedRows();
                if (selectedRows != null && selectedRows.length == 1) {
                    SalarieDto salarie = model.getSalarie(selectedRows[0]);
                    salarieListener.onSalarieSelected(salarie);
                }
            }
        });
    }

    // =================================== INNER CLASS
    private static class MyColumnModel extends DefaultTableColumnModel {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private MyColumnModel(Column[] tabColumns) {

            Stream.of(tabColumns).forEachOrdered(column -> {
                TableColumn tableColumn = new TableColumn(column.index, column.width);
                tableColumn.setHeaderValue(column.title);
                this.addColumn(tableColumn);
            });
        }
    }

    static class Column {

        private final int index;
        private final int width;
        private final String title;

        int getIndex() {
            return index;
        }

        int getWidth() {
            return width;
        }

        String getTitle() {
            return title;
        }

        private Column(int index, int width, String title) {
            this.index = index;
            this.width = width;
            this.title = title;
        }
    }
}
