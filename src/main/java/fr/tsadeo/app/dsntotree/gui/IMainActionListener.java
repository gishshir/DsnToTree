package fr.tsadeo.app.dsntotree.gui;

import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ItemBloc;

public interface IMainActionListener {

    public void actionCancelSearch();

    public void setFocusOnSearch();

    public void searchNext();

    public void actionSaveDsn(boolean reload);

    public void actionShowErrors();

    public void actionShowOpenDialogWithConfirmation();

    public void actionShowJdbcDialog();

    public void actionShowDsnTreeWithConfirmation(Dsn dsn, String messageDialog);

    public void actionShowBlocItem(ItemBloc itemBloc, String pathParent);

    public void actionShowSalarieDialog();
}
