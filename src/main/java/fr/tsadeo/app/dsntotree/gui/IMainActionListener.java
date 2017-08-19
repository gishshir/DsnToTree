package fr.tsadeo.app.dsntotree.gui;

import fr.tsadeo.app.dsntotree.model.Dsn;

public interface IMainActionListener {

    public void actionCancelSearch();

    public void setFocusOnSearch();

    public void searchNext();

    public void actionSaveDsn(boolean reload);

    public void actionShowErrors();

    public void actionShowOpenDialogWithConfirmation();

    public void actionShowJdbcDialog();
    
    public void actionShowDsnTreeWithConfirmation(Dsn dsn);
}
