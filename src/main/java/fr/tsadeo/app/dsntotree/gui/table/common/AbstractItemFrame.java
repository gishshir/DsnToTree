package fr.tsadeo.app.dsntotree.gui.table.common;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.tsadeo.app.dsntotree.gui.AbstractFrame;
import fr.tsadeo.app.dsntotree.gui.ISearchActionListener;
import fr.tsadeo.app.dsntotree.gui.component.SearchPanel;

public abstract class AbstractItemFrame<T extends ITableItemDto> extends AbstractFrame
        implements IItemListener<T>, ISearchActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // private IMainActionHandler mainActionHandler;
    private AbstractMyTable<T> tableItems;

    private int searchNoResult = Integer.MAX_VALUE;

    protected SearchPanel searchPanel;


    // -------------------------------------- constructor
    protected AbstractItemFrame(String title) {
        super(title, JFrame.DISPOSE_ON_CLOSE);
        // this.mainActionHandler = mainActionHandler;

        // Set up the content pane.
        addComponentsToPane(this.getContentPane());
    }

    // ------------------------------------- implementing IItemListener
    @Override
    public void onItemSelected(T item) {

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

        container.add(panelTop, layout);
    }

    private void createPanelSearch(Container container, String layout) {

        this.searchPanel = new SearchPanel(this);
        container.add(this.searchPanel, layout);
    }


}
