package fr.tsadeo.app.dsntotree.gui.table.common;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.tsadeo.app.dsntotree.gui.AbstractFrame;
import fr.tsadeo.app.dsntotree.gui.GuiUtils;
import fr.tsadeo.app.dsntotree.gui.IMainActionListener;
import fr.tsadeo.app.dsntotree.gui.ISearchActionListener;
import fr.tsadeo.app.dsntotree.gui.action.EditTableItemAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowTableItemAction;
import fr.tsadeo.app.dsntotree.gui.component.SearchPanel;
import fr.tsadeo.app.dsntotree.gui.table.dto.ITableItemDto;

public abstract class AbstractItemFrame<T extends ITableItemDto> extends AbstractFrame
        implements IItemListener<T>, ISearchActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // private IMainActionHandler mainActionHandler;
    private AbstractMyTable<T> tableItems;
    private ItemStateButton<T> btShowRubriques, btEditItem;

    private int searchNoResult = Integer.MAX_VALUE;

    protected SearchPanel searchPanel;
    private IMainActionListener mainActionListener;

    // -------------------------------------- constructor
    protected AbstractItemFrame(String title,IMainActionListener mainActionListener) {
        super(title, JFrame.DISPOSE_ON_CLOSE);
         this.mainActionListener = mainActionListener;

        // Set up the content pane.
        addComponentsToPane(this.getContentPane());
    }

    // ------------------------------------- implementing ISalarieListener
    @Override
    public void onItemSelected(T item) {

        if (item != null) {
            this.btShowRubriques.setEnabled(true);
            this.btEditItem.setEnabled(true);
            this.btShowRubriques.setToolTipText("voir les rubriques de l'item " + item.getNom());
            this.btShowRubriques.setItem(item);
            this.btEditItem.setItem(item);
        } else {
            this.btShowRubriques.setEnabled(false);
            this.btEditItem.setEnabled(false);
            this.btShowRubriques.setToolTipText("");
            this.btShowRubriques.setItem(null);
            this.btEditItem.setItem(null);
        }
    }

    @Override
    public void setFocusOnSearch() {
        if (this.searchPanel != null) {
            this.searchPanel.requestFocusOnSearch();
        }
    }

    // ------------------------------------- implementing ISearchActionListener
    @Override
    public void actionCancelSearch() {
        this.searchPanel.cancelSearch();
        searchNoResult = Integer.MAX_VALUE;
    }

    @Override
    public void search() {
        String search = this.searchPanel.getSearchText();
        int searchLenght = search != null ? search.length() : 0;
        if (searchLenght > 3 && searchLenght < this.searchNoResult) {
            if (this.tableItems.filter(search)) {
                this.searchNoResult = Integer.MAX_VALUE;
                this.searchPanel.setSearchColor(SEARCH_SUCCESS_COLOR);

            } else {
            	this.searchPanel.setSearchColor(ERROR_COLOR);
                this.searchNoResult = search.length();
            }
        } else {
            if (searchLenght <= 3) {
            	this.searchPanel.setDefaultBackground();
                this.searchNoResult = Integer.MAX_VALUE;
                this.tableItems.reinitSearch();
            }
        }
    }

    /*
     * @Override public void filter() { String search =
     * this.searchPanel.getSearchText(); int searchLenght = search != null ?
     * search.length() : 0; if (searchLenght > 3 && searchLenght <
     * this.searchNoResult) { if (this.tableItems.filter(search)) {
     * this.searchNoResult = Integer.MAX_VALUE;
     * this.searchPanel.setSearchColor(SEARCH_SUCCESS_COLOR);
     * 
     * } else { this.searchPanel.setSearchColor(SEARCH_ERROR_COLOR);
     * this.searchNoResult = search.length(); } } else { if (searchLenght <= 3)
     * { this.searchPanel.setDefaultBackground(); this.searchNoResult =
     * Integer.MAX_VALUE; this.tableItems.reinitSearch(); } } }
     */

    // ------------------------------- public methods
    public void setDatas(List<T> listSalaries) {

        this.tableItems.setDatas(listSalaries);
    }

    // ---------------------------------------- private methods
    private void addComponentsToPane(Container contentPane) {
        contentPane.setLayout(new BorderLayout());

        createPanelTop(contentPane, BorderLayout.PAGE_START);
        createPanelMiddle(contentPane, BorderLayout.CENTER);
    }

    protected abstract AbstractMyTable<T> createTable(IItemListener<T> itemListener);

    private void createPanelMiddle(Container container, String layout) {
        tableItems = this.createTable(this);
        JScrollPane scrollPanel = new JScrollPane(this.tableItems);

        container.add(scrollPanel, layout);
    }

    private void createPanelTop(Container container, String layout) {

        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.Y_AXIS));
        panelTop.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.createPanelSearch(panelTop, BorderLayout.CENTER);
        this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
        this.createPanelButtons(panelTop, BorderLayout.CENTER);

        container.add(panelTop, layout);
    }
//    protected abstract void createButtonShowItem(Container container, String layout);
//    protected abstract void createButtonEditItem(Container container, String layout);
    private void createPanelButtons(Container container, String layout) {
        JPanel panelButton = new JPanel();
        panelButton.setLayout(new BoxLayout(panelButton, BoxLayout.X_AXIS));

        panelButton.add(Box.createHorizontalGlue());
        this.createButtonShowItem(panelButton, BorderLayout.CENTER);
        panelButton.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));
        this.createButtonEditItem(panelButton, BorderLayout.CENTER);
        panelButton.add(Box.createHorizontalGlue());

        container.add(panelButton, layout);
    }
    private void createPanelSearch(Container container, String layout) {

        this.searchPanel = new SearchPanel(this);
        container.add(this.searchPanel, layout);
    }
    protected void createButtonShowItem(Container container, String layout) {

        this.btShowRubriques = new ItemStateButton<T>();
        GuiUtils.createButton(this.btShowRubriques, new ShowTableItemAction<T>(this.mainActionListener), SHOW_SALARIE_ACTION,
                KeyEvent.VK_R, PATH_SHOW_BLOC_ICO, null, "Voir les rubriques du salarié", false, container, layout);
    }

    protected void createButtonEditItem(Container container, String layout) {

        this.btEditItem = new ItemStateButton<T>();
        GuiUtils.createButton(this.btEditItem, new EditTableItemAction<T>(this.mainActionListener), EDIT_SALARIE_ACTION,
                KeyEvent.VK_E, PATH_EDIT_ITEM_ICO, null, "Editer les rubriques du salarié", false, container,
                layout);
    }

}
