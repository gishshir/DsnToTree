package fr.tsadeo.app.dsntotree.gui;

import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;

public interface ItemBlocListener {

    public enum ModifiedState {
        valider, annuler
    }

    public void onItemBlocSelected(ItemBloc itemBloc, int treeRowOfBloc, String path);

    public void onItemRubriqueSelected(ItemRubrique itemRubrique, int treeRowOfBloc, String path);

    public void onItemBlocModified(ItemBloc itemBloc, int treeRowOfBloc, ModifiedState state, boolean refresh);
    
    
}
