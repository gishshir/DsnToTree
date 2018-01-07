package fr.tsadeo.app.dsntotree.gui.component;

import java.util.stream.Stream;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import fr.tsadeo.app.dsntotree.gui.table.common.Column;

public abstract class AbstractTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected AbstractTable(TableModel tableModel, TableColumnModel tcModel) {
		super(tableModel, tcModel);
	}
	
	
    // =================================== INNER CLASS
    protected static class MyColumnModel extends DefaultTableColumnModel {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public MyColumnModel(Column[] tabColumns) {

            Stream.of(tabColumns).forEachOrdered(column -> {
                TableColumn tableColumn = new TableColumn(column.getIndex(), column.getWidth());
                tableColumn.setHeaderValue(column.getTitle());
                this.addColumn(tableColumn);
            });
        }
    }

    /*
     * public static class Column {
     * 
     * private final int index; private final int width; private final String
     * title;
     * 
     * int getIndex() { return index; }
     * 
     * int getWidth() { return width; }
     * 
     * public String getTitle() { return title; }
     * 
     * public Column(int index, int width, String title) { this.index = index;
     * this.width = width; this.title = title; } }
     */

}
