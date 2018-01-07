package fr.tsadeo.app.dsntotree.gui.etabliss;

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
import fr.tsadeo.app.dsntotree.gui.action.EditEtablissementAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowEtablissementAction;
import fr.tsadeo.app.dsntotree.gui.component.SearchPanel;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;
import fr.tsadeo.app.dsntotree.gui.table.common.IItemListener;
import fr.tsadeo.app.dsntotree.gui.table.dto.EtablissementDto;

public class EtablissementFrame extends AbstractFrame
        implements IItemListener<EtablissementDto>, ISearchActionListener {
	  /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private IMainActionListener mainActionListener;
    private TableEtablissement tableEtablissement;
    private EtablissementStateButton btShowRubriques, btEditEtablissement;

//    private StateTextField tfSearch;
    private int searchNoResult = Integer.MAX_VALUE;
//    private Color tfSearchBg;
    
    private SearchPanel searchPanel;

    // ------------------------------------- implementing ISalarieListener
    @Override
    public void onItemSelected(EtablissementDto etablissement) {

        if (etablissement != null) {
            this.btShowRubriques.setEnabled(true);
            this.btEditEtablissement.setEnabled(true);
            this.btShowRubriques.setToolTipText("voir les rubriques de l'établissement " + etablissement.toString());
            this.btShowRubriques.setEtablissement(etablissement);
            this.btEditEtablissement.setEtablissement(etablissement);
        } else {
            this.btShowRubriques.setEnabled(false);
            this.btEditEtablissement.setEnabled(false);
            this.btShowRubriques.setToolTipText("");
            this.btShowRubriques.setEtablissement(null);
            this.btEditEtablissement.setEtablissement(null);
        }
    }

    @Override
    public void setFocusOnSearch() {
        if (this.searchPanel != null) {
            this.searchPanel.requestFocusOnSearch();
        }
    }


    // ----------------------------------- constructor
    public EtablissementFrame(IMainActionListener mainActionListener) {
        super("Liste des établissements", JFrame.DISPOSE_ON_CLOSE);
        this.mainActionListener = mainActionListener;

        // Set up the content pane.
        addComponentsToPane(this.getContentPane());
    }

    // ------------------------------- public methods
    public void setDatas(List<EtablissementDto> listEtablissements) {

        this.tableEtablissement.setDatas(listEtablissements);
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
            if (this.tableEtablissement.search(search)) {
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
                this.tableEtablissement.reinitSearch();
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
    	tableEtablissement = new TableEtablissement(this);
        JScrollPane scrollPanel = new JScrollPane(this.tableEtablissement);

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
        this.createButtonShowEtablissement(panelButton, BorderLayout.CENTER);
        panelButton.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));
        this.createButtonEditEtablissement(panelButton, BorderLayout.CENTER);
        panelButton.add(Box.createHorizontalGlue());

        container.add(panelButton, layout);
    }

    private void createButtonShowEtablissement(Container container, String layout) {

        this.btShowRubriques = new EtablissementStateButton();
        GuiUtils.createButton(this.btShowRubriques, new ShowEtablissementAction(this.mainActionListener), SHOW_ETAB_ACTION,
                KeyEvent.VK_R, PATH_SHOW_BLOC_ICO, null, "Voir les rubriques de l'établissement", false, container, layout);
    }

    private void createButtonEditEtablissement(Container container, String layout) {

        this.btEditEtablissement = new EtablissementStateButton();
        GuiUtils.createButton(this.btEditEtablissement, new EditEtablissementAction(this.mainActionListener), EDIT_SALARIE_ACTION,
                KeyEvent.VK_E, PATH_EDIT_ITEM_ICO, null, "Editer les rubriques du salarié", false, container,
                layout);
    }

    private void createPanelSearch(Container container, String layout) {

        this.searchPanel = new SearchPanel(this);
        container.add(this.searchPanel, layout);
    }

    // ======================================== INNER CLASS
    public static class EtablissementStateButton extends StateButton {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private EtablissementDto etablissement;

        public EtablissementDto  getEtablissement() {
            return this.etablissement;
        }

        public void setEtablissement(EtablissementDto etablissement) {
            this.etablissement = etablissement;
        }

    }

}
