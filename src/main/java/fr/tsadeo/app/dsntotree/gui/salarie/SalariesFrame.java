package fr.tsadeo.app.dsntotree.gui.salarie;

import fr.tsadeo.app.dsntotree.gui.IMainActionListener;
import fr.tsadeo.app.dsntotree.gui.table.common.AbstractItemFrame;
import fr.tsadeo.app.dsntotree.gui.table.common.AbstractMyTable;
import fr.tsadeo.app.dsntotree.gui.table.common.IItemListener;
import fr.tsadeo.app.dsntotree.gui.table.dto.SalarieTableDto;

public class SalariesFrame extends AbstractItemFrame<SalarieTableDto> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;



    // ----------------------------------- constructor
    public SalariesFrame(IMainActionListener mainActionListener) {
        super("Liste des salaries", mainActionListener);
    }

    //------------------------------------- implementing ISearchActionListener
	@Override
	public void searchNext() {
		// nothing
	}


    //------------------------- implementing AbstractItemFrame
	@Override
	protected AbstractMyTable<SalarieTableDto> createTable(IItemListener<SalarieTableDto> itemListener) {
		return new TableSalaries(this);
	}
}
