package fr.tsadeo.app.dsntotree.model;

import java.util.ArrayList;
import java.util.List;

public  class DsnState {
	
	// la construction de l'arborescence a-t-elle eu lieu ?
    private boolean structured;
	
	private boolean error;
	private boolean modified;
	private List<ErrorMessage> listErrorMessage;
	
	private int nbrubAdded = 0;
	private int nbrubDeleted = 0;
	
	private int nbblocAdded = 0;
	private int nbblocDeleted = 0;
	
	//---------------------------- accessor
	
	public boolean isError() {
		return error;
	}
	public boolean isStructured() {
		return structured;
	}
	public void setStructured(boolean structured) {
		this.structured = structured;
	}
	public List<ErrorMessage> getListErrorMessage() {
		return this.listErrorMessage;
	}
	public void addErrorMessage(ErrorMessage errorMessage) {
		this.error = true;
		if (this.listErrorMessage == null) {
			this.listErrorMessage = new ArrayList<ErrorMessage>();
		}
		this.listErrorMessage.add(errorMessage);
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public boolean isModified() {
		return modified;
	}
	public void setModified(boolean modified) {
		this.modified = modified;
	}
	public int getNbrubAdded() {
		return nbrubAdded;
	}
	public void incrementsNbrubAdded() {
		this.nbrubAdded++;
	}
	public int getNbrubDeleted() {
		return nbrubDeleted;
	}
	public void incrementsNbrubDeleted() {
		this.nbrubDeleted++;
	}
	public int getNbblocAdded() {
		return nbblocAdded;
	}
	public void incrementsNbblocAdded() {
		this.nbblocAdded++;
	}
	public int getNbblocDeleted() {
		return nbblocDeleted;
	}
	public void incrementsNbblocDeleted() {
		this.nbblocDeleted++;
	}
	
	

}
