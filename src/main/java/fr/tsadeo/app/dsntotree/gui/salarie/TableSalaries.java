package fr.tsadeo.app.dsntotree.gui.salarie;

import fr.tsadeo.app.dsntotree.gui.table.common.AbstractMyTable;
import fr.tsadeo.app.dsntotree.gui.table.common.Column;
import fr.tsadeo.app.dsntotree.gui.table.common.IItemListener;
import fr.tsadeo.app.dsntotree.gui.table.common.MyColumnModel;
import fr.tsadeo.app.dsntotree.gui.table.common.MyTableModel;
import fr.tsadeo.app.dsntotree.gui.table.common.RowMapper;
import fr.tsadeo.app.dsntotree.gui.table.dto.SalarieDto;

public class TableSalaries extends AbstractMyTable<SalarieDto> {

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


    public TableSalaries(IItemListener<SalarieDto> salarieListener) {

        super(new MyTableModel<SalarieDto>(tabColumns, new RowMapper<SalarieDto>() {

			@Override
			public String getValue(SalarieDto salarie, int columnIndex) {
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
        	
        }), new MyColumnModel(tabColumns), salarieListener);

    }

}
