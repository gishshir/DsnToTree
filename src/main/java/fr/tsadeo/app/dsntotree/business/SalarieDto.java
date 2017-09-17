package fr.tsadeo.app.dsntotree.business;

import java.io.Serializable;

import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public class SalarieDto implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final ItemBloc itemBloc;

    private final int index;
    private String nir;
    private String nom;
    private String prenom;

    // ------------------------------------- accessors
    public String getNir() {
        return nir;
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
