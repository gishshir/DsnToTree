package fr.tsadeo.app.dsntotree.gui;

import java.io.File;

import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ItemBloc;

public interface ISearchActionListener {

    public void actionCancelSearch();

    public void setFocusOnSearch();

    public void searchNext();

}
