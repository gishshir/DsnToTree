package fr.tsadeo.app.dsntotree.model;

public abstract class AbstractItemTree implements IItemTree {
	
    // numéro de ligne dans le fichier d'origine
    private int numLine = Integer.MIN_VALUE;

	private boolean error = false;
	private ErrorMessage errorMessage ;
	
	private boolean modified = false;
	private boolean created = false;
	private boolean deleted = false;
	
	@Override
	public ErrorMessage getErrorMessage() {
	   return this.errorMessage;	
	}
	public void setErrorMessage(ErrorMessage errorMessage) {
		this.error =true;
		this.errorMessage = errorMessage;
	}

	@Override
    public int getNumLine() {
        return numLine;
    }

    public void setNumLine(int numLine) {
        this.numLine = numLine;
    }
	@Override
	public boolean isError() {
		return this.error;
	}

	@Override
	public boolean isModified() {
		return this.modified;
	}

	@Override
	public boolean isCreated() {
		return this.created;
	}

	@Override
	public boolean isDeleted() {
		return this.deleted;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public void setCreated(boolean created) {
		this.created = created;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	

}
