package fr.tsadeo.app.dsntotree.gui.component;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
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
        this.setEditable(true);
        this.setMaximumRowCount(10);
        // this.setListeners(this, this);
    }

    private void activateListeners() {

        Component c = this.getEditor().getEditorComponent();
        if (c instanceof JTextComponent) {
            this.tc = (JTextComponent) c;
            this.tc.getDocument().addDocumentListener(this);
            this.tc.addFocusListener(this);
        }
    }


    @Override
    protected void fireItemStateChanged(ItemEvent e) {
        System.out.println("fireItemStateChanged");
        super.fireItemStateChanged(e);
    }

	@Override
	protected void fireActionEvent() {
		if (!this.searchInProgress) {
			System.out.println("fireActionEvent");
			super.fireActionEvent();
		}
	}


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            this.activateListeners();
        }
    }
    // ------------------------ implementing DocumentListener
    @Override
    public void insertUpdate(DocumentEvent e) {
        this.update();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        System.out.println("removeUpdate");
        if (!tc.getText().isEmpty()) {
            reinitGUISelectedItem();
        }
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

		if (this.tc == null) {
			return;
		}
		String search = tc.getText().trim();
		System.out.println("\ntext: " + search + " - searchInProgress: " + searchInProgress);
		if (search != null 
				&& (searchInProgress || resultCount > 0 || search.length() >= MIN_CAR)) {

			if (this.currentSearch == null || !search.equals(this.currentSearch)) {

				// utilisateur a modifie la recherche
				if (!searchInProgress) {
					this.search();
				} else {
					
					// le resultat de la recherche modifie le model et
					// vide le champs de recherche
					if (search.isEmpty()) {
						
						System.out.println("currentSearch: " + currentSearch);
						reinitGUISearchText(currentSearch);
					}
				}
			}
		}

	}
	private void reinitGUISelectedItem() {
		
		if (this.getSelectedItem() != null) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override

				public void run() {
					setSelectedIndex(-1);
				}
			});
		}
	}

    private void reinitGUISearchText(final String text) {

		
		SwingUtilities.invokeLater(new Runnable() {

			@Override

			public void run() {
				setSelectedIndex(-1);
				tc.setText(text == null ? "" : text);
				searchInProgress = false;
			}
		});

	}

    private DefaultComboBoxModel<KeyAndLibelle> getDefaultComboboxModel() {
        return (DefaultComboBoxModel<KeyAndLibelle>) this.getModel();
    }

    private void search() {

    	if (this.searchInProgress) {
    		return;
    	}
    	
        SwingUtilities.invokeLater(new Runnable() {

            @Override

            public void run() {

                String search = tc == null ? null : tc.getText().trim();
                System.out.println("search: " + search);
                if (search == null || search.isEmpty() || getSelectedItem() != null) {
                	return;
                }
                
                searchInProgress = true;

                List<KeyAndLibelle> listInstances = search.length() < MIN_CAR
                        ? new ArrayList<KeyAndLibelle>(0) :
                		searchable.search(search);


                if (listInstances != null) {
                	
                    setEditable(false);
                    currentSearch = search;
                    System.out.println("nbr instances: " + resultCount);

                    populateComboBox(listInstances);
                    setPopupVisible(true);
                    setEditable(true);
                    requestFocusInWindow();
                }

            }
        });

    }

    public void populateComboBox(List<KeyAndLibelle> listItems) {

        resultCount = listItems == null ? 0 : listItems.size();
        if (listItems != null) {

            System.out.println("nbr instances: " + resultCount);

            DefaultComboBoxModel<KeyAndLibelle> model = getDefaultComboboxModel();
            model.removeAllElements();

            for (KeyAndLibelle keyAndLibelle : listItems) {
                model.addElement(keyAndLibelle);
            }

            System.out.println("model rempli!");
        }
    }

}
