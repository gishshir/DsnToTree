package fr.tsadeo.app.dsntotree.gui.etabliss;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.table.AbstractTableModel;

import fr.tsadeo.app.dsntotree.gui.table.common.Column;
import fr.tsadeo.app.dsntotree.gui.table.dto.EtablissementDto;

public class EtablissementTableModel extends AbstractTableModel {

	private final Column[] tabColumns;
	private final List<EtablissementDto> listEtablissement = new ArrayList<>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    void setDatas(List<EtablissementDto> listEtablissement) {
        this.clear();
        this.listEtablissement.addAll(listEtablissement);
    }

    List<EtablissementDto> getDatas() {
        return this.listEtablissement;
    }

    boolean search(String search) {

        String searchUpperCase = search.toUpperCase();

        AtomicBoolean result = new AtomicBoolean(false);
        listEtablissement.stream().forEachOrdered(etablissementDto -> {
            if (etablissementDto.getValueForSearch().indexOf(searchUpperCase) > -1) {
            	etablissementDto.setVisible(true);
                result.set(true);
            } else {
            	etablissementDto.setVisible(false);
            }
        });

        if (result.get()) {
            this.fireTableDataChanged();
        }
        return result.get();
    }

    void reinitSearch() {
    	listEtablissement.stream().forEach(salarieDto -> salarieDto.setVisible(true));
        this.fireTableDataChanged();
    }

	// -------------------------------- constructor

	public EtablissementTableModel(Column[] tabColumns) {
		this.tabColumns = tabColumns;
	}

	  // ------------------ implementing TableModel
    @Override
    public int getRowCount() {

        Long count = listEtablissement.stream().filter(salarieDto -> salarieDto.isVisible()).count();
        return count.intValue();
    }

    @Override
    public int getColumnCount() {
        return tabColumns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.getValue(this.getEtablissement(rowIndex), columnIndex);
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
        this.listEtablissement.clear();
    }

    /**
     * Retourne le salarie visible pour la ligne rowIndex
     */
    EtablissementDto getEtablissement(int rowIndex) {

        AtomicInteger compteur = new AtomicInteger(-1);
        return listEtablissement.stream().filter(etablissementDto -> etablissementDto.isVisible())
                    .filter(etablissementDto -> compteur.incrementAndGet() == rowIndex).findFirst().orElse(null);


    }
    
    private String getValue(EtablissementDto etablissement, int columnIndex) {

        if (etablissement == null) {
            return "";
        }
        switch (columnIndex) {
        case 0:
            return (etablissement.getIndex() + 1) + "";
        case 1:
            return etablissement.getSirenSiege();
        case 2:
            return etablissement.getNicSiege();
        case 3:
            return etablissement.getNicEtab();
        case 4:
            return etablissement.getLocaliteEtab();

        default:
            return "";

        }
    }

}
