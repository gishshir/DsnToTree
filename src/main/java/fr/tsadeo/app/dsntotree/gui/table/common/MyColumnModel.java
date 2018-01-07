package fr.tsadeo.app.dsntotree.gui.table.common;

import java.util.stream.Stream;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;


public class MyColumnModel extends DefaultTableColumnModel {

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
