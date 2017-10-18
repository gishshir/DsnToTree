package fr.tsadeo.app.dsntotree.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.tsadeo.app.dsntotree.gui.ItemBlocListener;
import fr.tsadeo.app.dsntotree.model.BlocTree;
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
    public void onItemBlocSelected(ItemBloc itemBloc) {
        for (ItemBlocListener itemBlocListener : listItemBlocListener) {
            itemBlocListener.onItemBlocSelected(itemBloc);
        }
    }

    @Override
    public void onItemRubriqueSelected(ItemRubrique itemRubrique) {

        for (ItemBlocListener itemBlocListener : listItemBlocListener) {
            itemBlocListener.onItemRubriqueSelected(itemRubrique);
        }

    }

    @Override
    public void onItemBlocModified(ItemBloc itemBloc, ModifiedState state, boolean refresh) {
        for (ItemBlocListener itemBlocListener : listItemBlocListener) {
            itemBlocListener.onItemBlocModified(itemBloc, state, refresh);
        }
    }

    @Override
    public BlocTree getTreeRoot() {
        // nothing
        return null;

    }

    @Override
    public void onItemBlocDragStarted() {
        for (ItemBlocListener itemBlocListener : listItemBlocListener) {
            itemBlocListener.onItemBlocDragStarted();
        }
    }

    @Override
    public void onItemBlocDropEnded() {
        for (ItemBlocListener itemBlocListener : listItemBlocListener) {
            itemBlocListener.onItemBlocDropEnded();
        }
    }

}
