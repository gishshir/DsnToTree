package fr.tsadeo.app.dsntotree.business;

import java.io.Serializable;
import java.util.Objects;

import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public class SalarieDto implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final ItemBloc itemBloc;

    private final int index;
    private String siren;
    private String nic;
    private String nir;
    private String nom;
    private String prenom;

    private boolean visible = true;

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

	public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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

    public ItemBloc getItemBloc() {
        return itemBloc;
    }

    public int getIndex() {
        return index;
    }

    // ------------------------------------ public methods
    public String getValueForSearch() {
        return StringUtils.concat(this.getSiren(), "-", this.getNic(), "-",
        		this.nir, "-", this.nom, "-", this.prenom).toUpperCase();
    }

    // ------------------------------------ constructor
    public SalarieDto(int index, ItemBloc itemBloc) {
        this.index = index;
        this.itemBloc = itemBloc;
    }

    @Override
    public String toString() {
        return StringUtils.concat("Salarie: NIR ", this.nir, " ", this.nom, " ", this.prenom);
    }
}
