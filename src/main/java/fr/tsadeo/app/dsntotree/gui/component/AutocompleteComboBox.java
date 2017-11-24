package fr.tsadeo.app.dsntotree.gui.component;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import fr.tsadeo.app.dsntotree.dico.KeyAndLibelle;

public class AutocompleteComboBox extends StateComboBox<KeyAndLibelle> implements DocumentListener, FocusListener {

    public interface Searchable {

        public List<KeyAndLibelle> search(String search);
    }

    private final Searchable searchable;
    private String currentSearch;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JTextComponent tc;

    public AutocompleteComboBox(Searchable searchable) {
        this.searchable = searchable;
        this.setEditable(false);
        this.setMaximumRowCount(10);
        this.setListeners(this, this);
    }

    private void setListeners(DocumentListener documentListener, FocusListener focusListener) {

        Component c = this.getEditor().getEditorComponent();
        if (c instanceof JTextComponent) {
            this.tc = (JTextComponent) c;
            this.tc.getDocument().addDocumentListener(documentListener);
            this.tc.addFocusListener(focusListener);
        }
    }


    @Override
    protected void fireItemStateChanged(ItemEvent e) {
        System.out.println("fireItemStateChanged");
        super.fireItemStateChanged(e);
    }

    @Override
    protected void fireActionEvent() {
        System.out.println("fireActionEvent");
        super.fireActionEvent();
    }


    // ------------------------ implementing DocumentListener
    @Override
    public void insertUpdate(DocumentEvent e) {
        this.update();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.update();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    // ------------------------ implementing focusListener
    @Override
    public void focusGained(FocusEvent e) {
        this.setPopupVisible(true);
    }

    @Override
    public void focusLost(FocusEvent e) {
    }


    // ------------------------------- private method
    private static int MIN_CAR = 3;
    private boolean searchInProgress = false;
    private long resultCount = 0L;
    /*
     * 1 - modification champ de recherche
     * Si aboutit alors
     * 2 - text vide
     * 3 - text avec selectedItem
     */
    private void update() {

        System.out.println("update");
        if (this.tc != null) {
            String search = tc.getText().trim();
            System.out.println("text: " + search);
            if (search != null && (searchInProgress || resultCount > 0 ||
            		search.length() >= MIN_CAR)) {

                if (this.currentSearch == null || !search.equals(this.currentSearch)) {

                    System.out.println("searchInProgress: " + searchInProgress);
                    //utilisateur a modifie la recherche
                    if (!searchInProgress) {
                    	searchInProgress = true;
                        this.search();
                    } else {
                    	// le resultat de la recherche modifie le model et 
                    	// vide le champs de recherche
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override

                            public void run() {
                                System.out.println("currentSearch: " + currentSearch);
                                setSelectedIndex(-1);
                                tc.setText(currentSearch == null ? "" : currentSearch);
                                searchInProgress = false;
                            }
                        });
                    }
                }
            } 

        }
    }

    private DefaultComboBoxModel<KeyAndLibelle> getDefaultComboboxModel() {
        return (DefaultComboBoxModel<KeyAndLibelle>) this.getModel();
    }

    private void search() {

        SwingUtilities.invokeLater(new Runnable() {

            @Override

            public void run() {

                String search = tc == null ? null : tc.getText().trim();
                System.out.println("search: " + search);

                List<KeyAndLibelle> listInstances = search.length() < MIN_CAR?Collections.emptyList():
                		searchable.search(search);

                resultCount = listInstances == null?0:listInstances.size();
                if (listInstances != null) {
                	
                    setEditable(false);
                    currentSearch = search;
                    System.out.println("nbr instances: " + resultCount);

                    DefaultComboBoxModel<KeyAndLibelle> model = getDefaultComboboxModel();
                    model.removeAllElements();

                    for (KeyAndLibelle keyAndLibelle : listInstances) {
                        model.addElement(keyAndLibelle);
                    }

                    System.out.println("model rempli!");
                    setPopupVisible(true);
                    setEditable(true);
                    requestFocusInWindow();
                }

            }
        });

    }

}
