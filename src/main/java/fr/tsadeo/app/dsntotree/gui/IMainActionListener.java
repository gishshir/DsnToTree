package fr.tsadeo.app.dsntotree.gui;

import java.io.File;

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

    public void actionShowBlocItem(ItemBloc itemBloc);

    public void actionEditBlocItem(ItemBloc itemBloc, boolean selectInTree);

    public void actionShowSalarieDialog();

    public void actionDisplayProcessMessage(String message, boolean append);

    public void actionFileDroppedWithConfirmation(File file);

    public void actionItemBlocDroppedWithConfirmation(ItemBloc parentTarget, ItemBloc blocToDrop);
}
