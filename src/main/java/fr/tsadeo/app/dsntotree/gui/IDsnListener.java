package fr.tsadeo.app.dsntotree.gui;

import fr.tsadeo.app.dsntotree.model.Dsn;

public interface IDsnListener {

    public void onDsnReloaded(Dsn dsn);

    public void onDsnOpened();

    public void onSearch(String search, boolean next);

    public void onSearchCanceled();
}
