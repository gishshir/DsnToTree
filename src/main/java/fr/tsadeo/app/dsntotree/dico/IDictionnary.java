package fr.tsadeo.app.dsntotree.dico;

import java.util.List;

public interface IDictionnary {

    public String getLibelle(String key);

    public String getLibelle(String key, String subkey);

    public List<KeyAndLibelle> getOrderedListOfSubItem(String key);
}
