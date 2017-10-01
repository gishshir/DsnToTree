package fr.tsadeo.app.dsntotree.gui.salarie;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.tsadeo.app.dsntotree.business.SalarieDto;
import fr.tsadeo.app.dsntotree.gui.AbstractFrame;
import fr.tsadeo.app.dsntotree.gui.GuiUtils;
import fr.tsadeo.app.dsntotree.gui.IMainActionListener;
import fr.tsadeo.app.dsntotree.gui.action.EditSalarieAction;
import fr.tsadeo.app.dsntotree.gui.action.FocusSearchSalarieAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowSalarieAction;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;
import fr.tsadeo.app.dsntotree.gui.component.StateTextField;

public class SalariesFrame extends AbstractFrame implements ISalarieListener, DocumentListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private IMainActionListener mainActionListener;
    private TableSalaries tableSalaries;
    private SalarieStateButton btShowRubriques, btDeleteSalarie, btEditSalarie;

    private StateTextField tfSearch;
    private int searchNoResult = Integer.MAX_VALUE;
    private Color tfSearchBg;

    // ------------------------------------- implementing ISalarieListener
    @Override
    public void onSalarieSelected(SalarieDto salarie) {

        if (salarie != null) {
            this.btShowRubriques.setEnabled(true);
            this.btEditSalarie.setEnabled(true);
            this.btShowRubriques.setToolTipText("voir les rubriques du salarie " + salarie.getNom());
            this.btShowRubriques.setSalarie(salarie);
            this.btEditSalarie.setSalarie(salarie);
        } else {
            this.btShowRubriques.setEnabled(false);
            this.btEditSalarie.setEnabled(false);
            this.btShowRubriques.setToolTipText("");
            this.btShowRubriques.setSalarie(null);
            this.btEditSalarie.setSalarie(null);
        }
    }

    @Override
    public void setFocusOnSearch() {
        if (this.tfSearch != null) {
            this.tfSearch.requestFocusInWindow();
        }
    }

    // -------------------------------------- implementing DocumentListener

    @Override
    public void insertUpdate(DocumentEvent e) {
        search();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        search();
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
    // ------------------------------------ private methods

    private void search() {
        String search = tfSearch.getText();
        int searchLenght = search != null ? search.length() : 0;
        if (searchLenght > 3 && searchLenght < this.searchNoResult) {
            if (this.tableSalaries.search(this.tfSearch.getText())) {
                this.searchNoResult = Integer.MAX_VALUE;
                this.tfSearch.setBackground(this.tfSearchBg);

            } else {
                this.tfSearch.setBackground(ERROR_COLOR);
                this.searchNoResult = search.length();
            }
        } else {
            if (searchLenght <= 3) {
                this.tfSearch.setBackground(this.tfSearchBg);
                this.searchNoResult = Integer.MAX_VALUE;
                this.tableSalaries.reinitSearch();
            }
        }
    }

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

        this.btShowRubriques = new SalarieStateButton();
        GuiUtils.createButton(this.btShowRubriques, new ShowSalarieAction(this.mainActionListener), SHOW_SALARIE_ACTION,
                KeyEvent.VK_R, PATH_SHOW_BLOC_ICO, null, "Voir les rubriques du salarié", false, container, layout);
    }

    private void createButtonEditSalaries(Container container, String layout) {

        this.btEditSalarie = new SalarieStateButton();
        GuiUtils.createButton(this.btEditSalarie, new EditSalarieAction(this.mainActionListener), EDIT_SALARIE_ACTION,
                KeyEvent.VK_E, PATH_EDIT_SALARIE_ICO, null, "Editer les rubriques du salarié", false, container, layout);
    }

    private void createPanelSearch(Container container, String layout) {

        JPanel panelSearch = new JPanel();
        JLabel labelRechercher = new JLabel("rechercher...");
        labelRechercher.setIcon(GuiUtils.createImageIcon(PATH_FIND_ICO));
        panelSearch.add(labelRechercher);
        this.tfSearch = new StateTextField(15);
        this.tfSearch.setFont(FONT);
        this.tfSearchBg = this.tfSearch.getBackground();
        this.tfSearch.getDocument().addDocumentListener(this);

        InputMap im = this.tfSearch.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.tfSearch.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), FOCUS_SEARCH_ACTION);
        am.put(FOCUS_SEARCH_ACTION, new FocusSearchSalarieAction(this));

        panelSearch.add(this.tfSearch);
        container.add(panelSearch, layout);
    }

    // ======================================== INNER CLASS
    public static class SalarieStateButton extends StateButton {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private SalarieDto salarie;

		public SalarieDto getSalarie() {
            return salarie;
        }

        public void setSalarie(SalarieDto salarie) {
            this.salarie = salarie;
        }

    }

}
