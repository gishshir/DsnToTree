package fr.tsadeo.app.dsntotree.gui.table.dto;

import java.io.Serializable;
import java.util.Objects;

import fr.tsadeo.app.dsntotree.gui.table.common.AbstractTableItemDto;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public class SalarieDto extends AbstractTableItemDto implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    private String siren;
    private String nic;
    private String nir;
    private String nom;
    private String prenom;


    // ------------------------------------- accessors

    public String getNir() {
        return nir;
    }

    public String getSiren() {
		return Objects.isNull(siren)?"":siren;
	}

	public void setSiren(String siren) {
		this.siren = siren;
	}

	public String getNic() {
		return Objects.isNull(nic)?"":nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}


    public void setNir(String nir) {
        this.nir = nir;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

 


    // ------------------------------------ public methods
    public String getValueForSearch() {
        return StringUtils.concat(this.getSiren(), "-", this.getNic(), "-",
        		this.nir, "-", this.nom, "-", this.prenom).toUpperCase();
    }

    // ------------------------------------ constructor
    public SalarieDto(int index, ItemBloc itemBloc) {
        super(index, itemBloc);
    }

    @Override
    public String toString() {
        return StringUtils.concat("Salarie: NIR ", this.nir, " ", this.nom, " ", this.prenom);
    }
}
