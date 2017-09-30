package fr.tsadeo.app.dsntotree.gui.salarie;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import fr.tsadeo.app.dsntotree.business.SalarieDto;
import fr.tsadeo.app.dsntotree.gui.salarie.TableSalaries.Column;

public class SalariesTableModel extends AbstractTableModel {

    private final Column[] tabColumns;
    private final List<SalarieDto> listSalaries = new ArrayList<>();
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    void setDatas(List<SalarieDto> listSalaries) {
        this.clear();
        this.listSalaries.addAll(listSalaries);
    }

    List<SalarieDto> getDatas() {
        return this.listSalaries;
    }

    boolean search(String search) {

        String searchUpperCase = search.toUpperCase();

        boolean result = false;
        for (SalarieDto salarieDto : listSalaries) {
            if (salarieDto.getValueForSearch().indexOf(searchUpperCase) > -1) {
                salarieDto.setVisible(true);
                result = true;
            } else {
                salarieDto.setVisible(false);
            }
        }

        if (result) {
            this.fireTableDataChanged();
        }
        return result;
    }

    void reinitSearch() {
        for (SalarieDto salarieDto : listSalaries) {
            salarieDto.setVisible(true);
        }
        this.fireTableDataChanged();
    }

    // -------------------------------- constructor

    public SalariesTableModel(Column[] tabColumns) {
        this.tabColumns = tabColumns;
    }

    // ------------------ implementing TableModel
    @Override
    public int getRowCount() {
        int count = 0;
        for (SalarieDto salarieDto : listSalaries) {
            count = count + (salarieDto.isVisible() ? 1 : 0);
        }
        return count;
    }

    @Override
    public int getColumnCount() {
        return tabColumns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.getValue(this.getSalarie(rowIndex), columnIndex);
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

    // ----------------- private methods

    private void clear() {
        this.listSalaries.clear();
    }

    SalarieDto getSalarie(int rowIndex) {
        if (rowIndex < this.getRowCount()) {
            int index = -1;
            for (SalarieDto salarieDto : listSalaries) {
                if (salarieDto.isVisible()) {
                    index++;
                    if (rowIndex == index) {
                        return salarieDto;
                    }
                }
            }
        }
        return null;
    }

    private String getValue(SalarieDto salarie, int columnIndex) {

        if (salarie == null) {
            return "";
        }
        switch (columnIndex) {
        case 0:
            return (salarie.getIndex() + 1) + "";
        case 1:
            return salarie.getNir();
        case 2:
            return salarie.getNom();
        case 3:
            return salarie.getPrenom();

        default:
            return "";

        }
    }
}
