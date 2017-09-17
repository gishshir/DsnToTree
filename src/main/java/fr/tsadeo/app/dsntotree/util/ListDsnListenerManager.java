package fr.tsadeo.app.dsntotree.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.tsadeo.app.dsntotree.gui.IDsnListener;
import fr.tsadeo.app.dsntotree.model.Dsn;

public class ListDsnListenerManager implements IDsnListener {

    private static ListDsnListenerManager instance;

    public static ListDsnListenerManager get() {
        if (instance == null) {
            instance = new ListDsnListenerManager();
        }
        return instance;
    }

    private ListDsnListenerManager() {
    }

    private final List<IDsnListener> listDsnListener = Collections.synchronizedList(new ArrayList<IDsnListener>());

    // -------------------------------------- public methods
    public void addDsnListener(IDsnListener dsnListener) {
        this.listDsnListener.add(dsnListener);
    }

    public void removeDsnListener(IDsnListener dsnBlocListener) {
        this.listDsnListener.remove(dsnBlocListener);
    }

    // ----------------------------------------- implementing IDsnListener
    @Override
    public void onDsnReloaded(Dsn dsn) {
        for (IDsnListener dsnListener : listDsnListener) {
            dsnListener.onDsnReloaded(dsn);
        }
    }

    @Override
    public void onDsnOpened() {
        for (IDsnListener dsnListener : listDsnListener) {
            dsnListener.onDsnOpened();
        }
    }

    @Override
    public void onSearch(String search, boolean next) {
        for (IDsnListener dsnListener : listDsnListener) {
            dsnListener.onSearch(search, next);
        }
    }

    @Override
    public void onSearchCanceled() {
        for (IDsnListener dsnListener : listDsnListener) {
            dsnListener.onSearchCanceled();
        }
    }

}
