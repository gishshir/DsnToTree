package fr.tsadeo.app.dsntotree.gui.etabliss;

import java.awt.Dimension;
import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.tsadeo.app.dsntotree.business.EtablissementDto;
import fr.tsadeo.app.dsntotree.gui.component.AbstractTable;

public class TableEtablissement extends AbstractTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private static Column[] tabColumns = new Column[] { 
    		new Column(0, 5, "index"), 
    		new Column(1, 50, "Siren"),
    		new Column(2, 20, "Nic siege"),
    		new Column(3, 20, "Nic etab"),
            new Column(4, 50, "Localite")}; 

    private final EtablissementTableModel model;
    private final IEtablissementListener etablissementListener;

    TableEtablissement(IEtablissementListener etablissementListener) {

        super(new EtablissementTableModel(tabColumns), new MyColumnModel(tabColumns));
        this.model = (EtablissementTableModel) super.getModel();
        this.etablissementListener = etablissementListener;

        this.buildListSelectionListener();

        this.setPreferredScrollableViewportSize(new Dimension(300, 70));
        this.setFillsViewportHeight(true);

    }
    
    List<EtablissementDto> getDatas() {
        return this.model.getDatas();
    }

    void setDatas(List<EtablissementDto> listEtablissements) {

        this.model.setDatas(listEtablissements);
    }

    boolean search(String search) {
        boolean result = this.model.search(search);
        if (result) {
            this.getSelectionModel().clearSelection();
            if (this.model.getRowCount() == 1) {
                this.etablissementListener.onEtablissementSelected(this.model.getEtablissement(0));
            } else {
                this.etablissementListener.onEtablissementSelected(null);
            }
        }
        return result;
    }

    void reinitSearch() {
        this.model.reinitSearch();
        this.etablissementListener.onEtablissementSelected(null);
    }
    // ---------------------------------- private methods
    private void buildListSelectionListener() {

        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {

                int[] selectedRows = TableEtablissement.this.getSelectedRows();
                if (selectedRows != null && selectedRows.length == 1) {
                    EtablissementDto etablissement = model.getEtablissement(selectedRows[0]);
                    etablissementListener.onEtablissementSelected(etablissement);
                }
            }
        });
    }

}
