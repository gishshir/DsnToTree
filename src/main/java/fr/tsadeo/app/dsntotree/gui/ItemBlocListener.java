package fr.tsadeo.app.dsntotree.gui;

import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;

public interface ItemBlocListener {

    public enum ModifiedState {
        valider, annuler
    }

    public void onItemBlocSelected(ItemBloc itemBloc);

    public void onItemRubriqueSelected(ItemRubrique itemRubrique);

    public void onItemBlocModified(ItemBloc itemBloc, ModifiedState state, boolean refresh);

    public BlocTree getTreeRoot();

    public void onItemBlocDragStarted();

    public void onItemBlocDropEnded();

}
