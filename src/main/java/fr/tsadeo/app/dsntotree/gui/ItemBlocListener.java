package fr.tsadeo.app.dsntotree.gui;

import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;

public interface ItemBlocListener {

    public enum ModifiedState {
        valider, annuler
    }

    public void onItemBlocSelected(ItemBloc itemBloc, String path);

    public void onItemRubriqueSelected(ItemRubrique itemRubrique, String path);

    public void onItemBlocModified(ItemBloc itemBloc, ModifiedState state, boolean refresh);
    
    
}
