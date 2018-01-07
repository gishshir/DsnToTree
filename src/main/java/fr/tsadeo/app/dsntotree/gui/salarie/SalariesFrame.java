package fr.tsadeo.app.dsntotree.gui.salarie;

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
import fr.tsadeo.app.dsntotree.gui.action.EditItemAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowItemAction;
import fr.tsadeo.app.dsntotree.gui.component.SearchPanel;
import fr.tsadeo.app.dsntotree.gui.table.common.IItemListener;
import fr.tsadeo.app.dsntotree.gui.table.common.ItemStateButton;
import fr.tsadeo.app.dsntotree.gui.table.dto.SalarieDto;

public class SalariesFrame extends AbstractFrame implements IItemListener<SalarieDto>,
  ISearchActionListener{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private IMainActionListener mainActionListener;
    private TableSalaries tableSalaries;
    private ItemStateButton<SalarieDto> btShowRubriques, btEditSalarie;

    private int searchNoResult = Integer.MAX_VALUE;
    
    private SearchPanel searchPanel;

    // ------------------------------------- implementing ISalarieListener
    @Override
    public void onItemSelected(SalarieDto salarie) {

        if (salarie != null) {
            this.btShowRubriques.setEnabled(true);
            this.btEditSalarie.setEnabled(true);
            this.btShowRubriques.setToolTipText("voir les rubriques du salarie " + salarie.getNom());
            this.btShowRubriques.setItem(salarie);
            this.btEditSalarie.setItem(salarie);
        } else {
            this.btShowRubriques.setEnabled(false);
            this.btEditSalarie.setEnabled(false);
            this.btShowRubriques.setToolTipText("");
            this.btShowRubriques.setItem(null);
            this.btEditSalarie.setItem(null);
        }
    }

    @Override
    public void setFocusOnSearch() {
        if (this.searchPanel != null) {
            this.searchPanel.requestFocusOnSearch();
        }
    }


    // ----------------------------------- constructor
    public SalariesFrame(IMainActionListener mainActionListener) {
        super("Liste des salaries", JFrame.DISPOSE_ON_CLOSE);
        this.mainActionListener = mainActionListener;

        // Set up the content pane.
        addComponentsToPane(this.getContentPane());
    }

    // ------------------------------- public methods
    public void setDatas(List<SalarieDto> listSalaries) {

        this.tableSalaries.setDatas(listSalaries);
    }

    //------------------------------------- implementing ISearchActionListener
    @Override
	public void actionCancelSearch() {
    	this.searchPanel.cancelSearch();
        searchNoResult = Integer.MAX_VALUE;
	}

	@Override
	public void searchNext() {
		// nothing
	}
    @Override
    public void search() {
        String search = this.searchPanel.getSearchText();
        int searchLenght = search != null ? search.length() : 0;
        if (searchLenght > 3 && searchLenght < this.searchNoResult) {
            if (this.tableSalaries.filter(search)) {
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
                this.tableSalaries.reinitSearch();
            }
        }
    }

    // ------------------------------------ private methods

    private void addComponentsToPane(Container pane) {
        pane.setLayout(new BorderLayout());

        createPanelTop(pane, BorderLayout.PAGE_START);
        createPanelMiddle(pane, BorderLayout.CENTER);

    }

    private void createPanelMiddle(Container container, String layout) {
        tableSalaries = new TableSalaries(this);
        JScrollPane scrollPanel = new JScrollPane(this.tableSalaries);

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

    private void createPanelButtons(Container container, String layout) {
        JPanel panelButton = new JPanel();
        panelButton.setLayout(new BoxLayout(panelButton, BoxLayout.X_AXIS));

        panelButton.add(Box.createHorizontalGlue());
        this.createButtonShowSalaries(panelButton, BorderLayout.CENTER);
        panelButton.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));
        this.createButtonEditSalaries(panelButton, BorderLayout.CENTER);
        panelButton.add(Box.createHorizontalGlue());

        container.add(panelButton, layout);
    }

    private void createButtonShowSalaries(Container container, String layout) {

        this.btShowRubriques = new ItemStateButton<SalarieDto>();
        GuiUtils.createButton(this.btShowRubriques, new ShowItemAction<SalarieDto>(this.mainActionListener), SHOW_SALARIE_ACTION,
                KeyEvent.VK_R, PATH_SHOW_BLOC_ICO, null, "Voir les rubriques du salarié", false, container, layout);
    }

    private void createButtonEditSalaries(Container container, String layout) {

        this.btEditSalarie = new ItemStateButton<SalarieDto>();
        GuiUtils.createButton(this.btEditSalarie, new EditItemAction<SalarieDto>(this.mainActionListener), EDIT_SALARIE_ACTION,
                KeyEvent.VK_E, PATH_EDIT_ITEM_ICO, null, "Editer les rubriques du salarié", false, container,
                layout);
    }

    private void createPanelSearch(Container container, String layout) {

        this.searchPanel = new SearchPanel(this);
        container.add(this.searchPanel, layout);
    }

	
}
