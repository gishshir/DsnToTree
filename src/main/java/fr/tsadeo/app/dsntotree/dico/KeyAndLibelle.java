package fr.tsadeo.app.dsntotree.dico;

public class KeyAndLibelle {

    private final String key;
    private final String libelle;
    
    private final boolean showLibelle;

    public String getKey() {
        return key;
    }

    public String getLibelle() {
        return libelle;
    }

    public KeyAndLibelle(String key, String libelle, boolean showLibelle) {
        this.key = key;
        this.libelle = libelle;
        this.showLibelle = showLibelle;
    }
    public KeyAndLibelle(String key, String libelle) {

    	this(key, libelle, true);
    }

    @Override
    public String toString() {
        return key == null ? "" :
        	showLibelle? 
        	key.concat(" : ").concat(libelle == null ? "" : libelle):
        		key;
    }
}
