package fr.tsadeo.app.dsntotree.gui.table.dto;

import java.io.Serializable;

import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public class EtablissementTableDto extends AbstractTableItemDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
    private String sirenSiege;
    private String nicSiege;
    private String nicEtab;
    private String localiteEtab;
    

    
    //--------------------------------- constructor
    public EtablissementTableDto(int index,ItemBloc itemBloc) {
        super(index, itemBloc);
    }

    
    //----------------------------------- accessor
    

	public String getSirenSiege() {
		return sirenSiege;
	}

	public void setSirenSiege(String sirenSiege) {
		this.sirenSiege = sirenSiege;
	}

	public String getNicSiege() {
		return nicSiege;
	}

	public void setNicSiege(String nicSiege) {
		this.nicSiege = nicSiege;
	}

	public String getNicEtab() {
		return nicEtab;
	}

	public void setNicEtab(String nicEtab) {
		this.nicEtab = nicEtab;
	}

	public String getLocaliteEtab() {
		return localiteEtab;
	}

	public void setLocaliteEtab(String localiteEtab) {
		this.localiteEtab = localiteEtab;
	}

    
	 // ------------------------------------ public methods
    public String getValueForSearch() {
        return StringUtils.concat(this.sirenSiege, "-", this.nicSiege, "-",
        		this.nicEtab, "-", this.localiteEtab).toUpperCase();
    }
    //----------------------------------- overring Object
    @Override
    public String toString() {
        return StringUtils.concat("Ebt: Siret siege ", this.sirenSiege, this.nicSiege, " - nic ", this.nicEtab);
    }

    //-------------------------------- implementing ITableItemDto
	@Override
	public String getNom() {
		// TODO Auto-generated method stub
		return "";
	}
	
}
