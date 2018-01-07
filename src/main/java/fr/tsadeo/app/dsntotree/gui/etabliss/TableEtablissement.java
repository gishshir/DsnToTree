package fr.tsadeo.app.dsntotree.gui.etabliss;

import fr.tsadeo.app.dsntotree.gui.table.common.AbstractMyTable;
import fr.tsadeo.app.dsntotree.gui.table.common.Column;
import fr.tsadeo.app.dsntotree.gui.table.common.IItemListener;
import fr.tsadeo.app.dsntotree.gui.table.common.MyColumnModel;
import fr.tsadeo.app.dsntotree.gui.table.common.MyTableModel;
import fr.tsadeo.app.dsntotree.gui.table.common.RowMapper;
import fr.tsadeo.app.dsntotree.gui.table.dto.EtablissementTableDto;

public class TableEtablissement extends AbstractMyTable<EtablissementTableDto>{

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

    public TableEtablissement(IItemListener<EtablissementTableDto> etablissementListener) {

        super(new MyTableModel<EtablissementTableDto>(tabColumns, new RowMapper<EtablissementTableDto>() {

			@Override
			public String getValue(EtablissementTableDto etablissement, int columnIndex) {
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
        	
        }), new MyColumnModel(tabColumns), etablissementListener);

    }
}
