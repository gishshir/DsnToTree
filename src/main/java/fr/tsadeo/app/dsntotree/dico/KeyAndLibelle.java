package fr.tsadeo.app.dsntotree.dico;

public class KeyAndLibelle {

	private final String key;
	private final String libelle;
	
	
	public String getKey() {
		return key;
	}


	public String getLibelle() {
		return libelle;
	}


	public KeyAndLibelle(String key, String libelle) {
		this.key = key;
		this.libelle = libelle;
	}
	

	@Override
	public String toString() {
		return key.concat(" : ").concat(libelle);
	}
}
