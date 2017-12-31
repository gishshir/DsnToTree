package fr.tsadeo.app.dsntotree.gui.salarie;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.table.AbstractTableModel;

import fr.tsadeo.app.dsntotree.business.SalarieDto;
import fr.tsadeo.app.dsntotree.gui.component.AbstractTable.Column;

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

        AtomicBoolean result = new AtomicBoolean(false);
        listSalaries.stream().forEachOrdered(salarieDto -> {
            if (salarieDto.getValueForSearch().indexOf(searchUpperCase) > -1) {
                salarieDto.setVisible(true);
                result.set(true);
            } else {
                salarieDto.setVisible(false);
            }
        });

        if (result.get()) {
            this.fireTableDataChanged();
        }
        return result.get();
    }

    void reinitSearch() {
        listSalaries.stream().forEach(salarieDto -> salarieDto.setVisible(true));
        this.fireTableDataChanged();
    }

    // -------------------------------- constructor

    public SalariesTableModel(Column[] tabColumns) {
        this.tabColumns = tabColumns;
    }

    // ------------------ implementing TableModel
    @Override
    public int getRowCount() {

        Long count = listSalaries.stream().filter(salarieDto -> salarieDto.isVisible()).count();
        return count.intValue();
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

    /**
     * Retourne le salarie visible pour la ligne rowIndex
     */
    SalarieDto getSalarie(int rowIndex) {

        AtomicInteger compteur = new AtomicInteger(-1);
        return listSalaries.stream().filter(salarieDto -> salarieDto.isVisible())
                    .filter(salarieDto -> compteur.incrementAndGet() == rowIndex).findFirst().orElse(null);


    }

    private String getValue(SalarieDto salarie, int columnIndex) {

        if (salarie == null) {
            return "";
        }
        switch (columnIndex) {
        case 0:
            return (salarie.getIndex() + 1) + "";
        case 1:
            return salarie.getSiren();
        case 2:
            return salarie.getNic();
        case 3:
            return salarie.getNir();
        case 4:
            return salarie.getNom();
        case 5:
            return salarie.getPrenom();

        default:
            return "";

        }
    }
}
