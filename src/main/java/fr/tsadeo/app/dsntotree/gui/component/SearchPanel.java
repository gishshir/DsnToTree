package fr.tsadeo.app.dsntotree.gui.component;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.tsadeo.app.dsntotree.gui.GuiUtils;
import fr.tsadeo.app.dsntotree.gui.IGuiConstants;
import fr.tsadeo.app.dsntotree.gui.ISearchActionListener;
import fr.tsadeo.app.dsntotree.gui.action.CancelSearchAction;
import fr.tsadeo.app.dsntotree.gui.action.FocusSearchAction;
import fr.tsadeo.app.dsntotree.gui.action.NextSearchAction;

public class SearchPanel extends JPanel implements IGuiConstants,  IStateComponent,
         DocumentListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final ISearchActionListener searchListener;
	private StateTextField tfSearch;
	private Color tfSearchBg;
//------------------------------------ implementing IStateComposant
	@Override
	public void waitEndAction() {
		this.tfSearch.waitEndAction();

	}

	@Override
	public void actionEnded() {
		this.tfSearch.actionEnded();
	}
    // -------------------------------------- implementing DocumentListener

    @Override
    public void insertUpdate(DocumentEvent e) {
       this.searchListener.search();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    	this.searchListener.search();
    }
    @Override
	public void changedUpdate(DocumentEvent e) {
		// Nothing
	}

	//---------------------------- public methods
	public SearchPanel(ISearchActionListener searchListener) {
		this.searchListener = searchListener;
		this.buildMainPanel();
		
	}
	public boolean hasSearchFocus() {
		return this.tfSearch.hasFocus();
	}
	public void requestFocusOnSearch() {
		 this.tfSearch.requestFocusInWindow();
	}
	public String getSearchText() {
		return this.tfSearch.getText();
	}
	public void setSearchColor(Color color) {
		this.tfSearch.setBackground(color);
	}
	public void setDefaultBackground() {
		this.setSearchColor(this.tfSearchBg);
	}
	public void cancelSearch() {
		this.tfSearch.setText("");
		this.setDefaultBackground();
	}
	private void buildMainPanel() {
		
		JLabel labelRechercher = new JLabel("rechercher...");
        labelRechercher.setIcon(GuiUtils.createImageIcon(PATH_FIND_ICO));
        this.add(labelRechercher);
        this.tfSearch = new StateTextField(15);
        this.tfSearch.setFont(FONT);
        this.tfSearchBg = this.tfSearch.getBackground();
        this.tfSearch.getDocument().addDocumentListener(this);

        InputMap im = this.tfSearch.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.tfSearch.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_SEARCH_ACTION);
        am.put(CANCEL_SEARCH_ACTION, new CancelSearchAction(this.searchListener));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), NEXT_SEARCH_ACTION);
        am.put(NEXT_SEARCH_ACTION, new NextSearchAction(this.searchListener));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), FOCUS_SEARCH_ACTION);
        am.put(FOCUS_SEARCH_ACTION, new FocusSearchAction(this.searchListener));

        this.add(this.tfSearch);

	}

}
