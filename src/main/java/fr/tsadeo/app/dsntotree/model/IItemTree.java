package fr.tsadeo.app.dsntotree.model;

public interface IItemTree {

    public ErrorMessage getErrorMessage();

    public boolean isError();

    public boolean isModified();

    public boolean isCreated();

    public boolean isDeleted();

    public boolean isBloc();

    public boolean isRubrique();

    public int getNumLine();

}
