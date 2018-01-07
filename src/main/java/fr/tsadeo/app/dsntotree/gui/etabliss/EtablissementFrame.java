package fr.tsadeo.app.dsntotree.gui.etabliss;

import fr.tsadeo.app.dsntotree.gui.IMainActionListener;
import fr.tsadeo.app.dsntotree.gui.table.common.AbstractItemFrame;
import fr.tsadeo.app.dsntotree.gui.table.common.AbstractMyTable;
import fr.tsadeo.app.dsntotree.gui.table.common.IItemListener;
import fr.tsadeo.app.dsntotree.gui.table.dto.EtablissementTableDto;

public class EtablissementFrame extends AbstractItemFrame<EtablissementTableDto> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;



    // ----------------------------------- constructor
    public EtablissementFrame(IMainActionListener mainActionListener) {
        super("Liste des établissement", mainActionListener);
    }

    //------------------------------------- implementing ISearchActionListener
	@Override
	public void searchNext() {
		// nothing
	}


    //------------------------- implementing AbstractItemFrame
	@Override
	protected AbstractMyTable<EtablissementTableDto> createTable(IItemListener<EtablissementTableDto> itemListener) {
		return new TableEtablissement(this);
	}

}
