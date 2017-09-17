package fr.tsadeo.app.dsntotree.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.tsadeo.app.dsntotree.gui.ItemBlocListener;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;

public class ListItemBlocListenerManager implements ItemBlocListener {

    private static ListItemBlocListenerManager instance;

    public static ListItemBlocListenerManager get() {
        if (instance == null) {
            instance = new ListItemBlocListenerManager();
        }
        return instance;
    }

    private ListItemBlocListenerManager() {
    }

    private final List<ItemBlocListener> listItemBlocListener = Collections
            .synchronizedList(new ArrayList<ItemBlocListener>());

    // -------------------------------------- public methods
    public void addItemBlocListener(ItemBlocListener itemBlocListener) {
        this.listItemBlocListener.add(itemBlocListener);
    }

    public void removeItemBlocListener(ItemBlocListener itemBlocListener) {
        this.listItemBlocListener.remove(itemBlocListener);
    }

    // --------------------------------------- implementing ItemblocListener
    @Override
    public void onItemBlocSelected(ItemBloc itemBloc, int treeRowOfBloc, String path) {
        for (ItemBlocListener itemBlocListener : listItemBlocListener) {
            itemBlocListener.onItemBlocSelected(itemBloc, treeRowOfBloc, path);
        }
    }

    @Override
    public void onItemRubriqueSelected(ItemRubrique itemRubrique, int treeRowOfBloc, String path) {

        for (ItemBlocListener itemBlocListener : listItemBlocListener) {
            itemBlocListener.onItemRubriqueSelected(itemRubrique, treeRowOfBloc, path);
        }

    }

    @Override
    public void onItemBlocModified(ItemBloc itemBloc, int treeRowOfBloc, ModifiedState state, boolean refresh) {
        for (ItemBlocListener itemBlocListener : listItemBlocListener) {
            itemBlocListener.onItemBlocModified(itemBloc, treeRowOfBloc, state, refresh);
        }
    }

}
