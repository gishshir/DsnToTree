package fr.tsadeo.app.dsntotree.gui.salarie;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;

import fr.tsadeo.app.dsntotree.business.SalarieDto;

public class TableSalaries extends JTable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final SalariesTableModel model;

    TableSalaries() {

        super(new SalariesTableModel(), new MyColumnModel());
        this.model = (SalariesTableModel) super.getModel();
        this.setAutoCreateColumnsFromModel(true);

        this.setPreferredScrollableViewportSize(new Dimension(300, 70));
        this.setFillsViewportHeight(true);

    }

    void setDatas(List<SalarieDto> listSalaries) {

        this.model.setDatas(listSalaries);
    }

    // =================================== INNER CLASS
    private static class MyColumnModel extends DefaultTableColumnModel {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private MyColumnModel() {

        }
    }
}
