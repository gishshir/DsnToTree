package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.tsadeo.app.dsntotree.dico.KeyAndLibelle;
import fr.tsadeo.app.dsntotree.gui.action.CancelSearchAction;
import fr.tsadeo.app.dsntotree.gui.action.FocusSearchAction;
import fr.tsadeo.app.dsntotree.gui.action.NextSearchAction;
import fr.tsadeo.app.dsntotree.gui.component.StateTextField;
import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.NatureDsn;
import fr.tsadeo.app.dsntotree.model.PhaseDsn;
import fr.tsadeo.app.dsntotree.model.PhaseNatureType;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.util.ListDsnListenerManager;

/**
 * Frame présentant l'arborescence des blocs avec les libelles issus de la norme
 * DSN pour la DSN en cours
 * 
 * @author sfauche
 *
 */
public class DsnNormeFrame extends AbstractFrame implements ActionListener, ISearchActionListener,
DocumentListener{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String COMMENT = "Le choix de la phase et de la nature de la DSN determine l'arborescence des blocs."
            + "\nLa liste des rubriques est exhaustive et correspond au document PDF de norme en cours.";

    private DsnNormeTree dsnNormeTree;

    private PhaseNatureType phaseNatureType;

    private JComboBox<KeyAndLibelle> cbListPhases, cbListNatures;
    private JPanel panelTop;

    private JTextArea taComment;
    private StateTextField tfSearch;
    private Color tfSearchBg;
    private int searchNoResult = Integer.MAX_VALUE;
    private boolean searchInNode = true;

    // ---------------------------------------- implementing ActionListener
    @Override
    public void actionPerformed(ActionEvent e) {
        this.updateTree();
    }

    // ---------------------------------------------- constructor
    protected DsnNormeFrame(String title, IMainActionListener listener) {
        super("Norme DSN: ".concat(title), JFrame.DISPOSE_ON_CLOSE);

        // Set up the content pane.
        addComponentsToPane(this.getContentPane());

    }

    void setPhaseNaturePhase(PhaseNatureType phaseNatureType) {

        if (phaseNatureType != null) {

            this.cbListPhases.setSelectedIndex(phaseNatureType.getPhase() == null ? PhaseDsn.PHASE_3.ordinal()
                    : phaseNatureType.getPhase().ordinal());
            this.cbListNatures.setSelectedIndex(phaseNatureType.getNature() == null ? NatureDsn.DSN_MENSUELLE.ordinal()
                    : phaseNatureType.getNature().ordinal());
        }
        // this.updateTree();
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
    //------------------------------------- implementing ISearchListner
	@Override
	public void actionCancelSearch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocusOnSearch() {
		if (this.tfSearch != null) {
            this.tfSearch.requestFocusInWindow();
        }
	}

	@Override
	public void searchNext() {
		if (this.dsnNormeTree.search(this.tfSearch.getText(), this.searchInNode,  true)) {
            ListDsnListenerManager.get().onSearch(this.tfSearch.getText(), true);
        }
	}

    // ------------------------------------ private methode
	 private void search() {
	        String search = tfSearch.getText();
	        int searchLenght = search != null ? search.length() : 0;
	        if (searchLenght > 1 && searchLenght < this.searchNoResult) {
	        	
	            if (this.dsnNormeTree.search(this.tfSearch.getText(), true, false)) {
	            	this.searchInNode = true;
	                this.searchNoResult = Integer.MAX_VALUE;
	                this.tfSearch.setBackground(SEARCH_SUCCESS_COLOR);

	                ListDsnListenerManager.get().onSearch(search, false);
	            }
	            else if (this.dsnNormeTree.search(this.tfSearch.getText(), false, false)) {
	            	this.searchInNode = false;
	                this.searchNoResult = Integer.MAX_VALUE;
	                this.tfSearch.setBackground(SEARCH_SUCCESS_COLOR);

	                ListDsnListenerManager.get().onSearch(search, false);
	            }
	            else {
	                this.tfSearch.setBackground(ERROR_COLOR);
	                this.searchNoResult = search.length();
	            }
	        } else {
	            if (searchLenght <= 3) {
	                this.tfSearch.setBackground(this.tfSearchBg);
	                this.searchNoResult = Integer.MAX_VALUE;
	                this.searchInNode = true;
	            }
	        }
	    }


    private void createSearchPanel(Container container, String layout) {

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
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_SEARCH_ACTION);
        am.put(CANCEL_SEARCH_ACTION, new CancelSearchAction(this));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), NEXT_SEARCH_ACTION);
        am.put(NEXT_SEARCH_ACTION, new NextSearchAction(this));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), FOCUS_SEARCH_ACTION);
        am.put(FOCUS_SEARCH_ACTION, new FocusSearchAction(this));

        panelSearch.add(this.tfSearch);
        container.add(panelSearch, layout);
    }

    private void updateTree() {

        if (this.dsnNormeTree != null) {

            PhaseNatureType phaseNatureTypeSaisie = this.getPhaseNatureTypeFromSaisie();

            if (phaseNatureTypeSaisie == null) {
                return;
            }
            if (this.phaseNatureType == null || this.phaseNatureType.getPhase() != phaseNatureTypeSaisie.getPhase()
                    || this.phaseNatureType.getNature() != phaseNatureTypeSaisie.getNature()) {

            BlocTree blocTree = ServiceFactory.getBlocTreeService().buildRootTree(phaseNatureTypeSaisie);
            if (blocTree != null) {

                    this.dsnNormeTree.clearTree();
                    this.setBlocTree(blocTree);
                    this.phaseNatureType = phaseNatureTypeSaisie;
            }
            }
        }
        this.setFocusOnSearch();
    }

    private void setBlocTree(BlocTree blocTree) {
        this.dsnNormeTree.createNodes(blocTree, false);
        this.dsnNormeTree.expandBloc(BLOC_11, true);

    }

    private PhaseNatureType getPhaseNatureTypeFromSaisie() {

        KeyAndLibelle itemPhase = (KeyAndLibelle) this.cbListPhases.getSelectedItem();
        KeyAndLibelle itemNature = (KeyAndLibelle) this.cbListNatures.getSelectedItem();

        if (itemPhase == null || itemNature == null) {
            return null;
        }

        PhaseDsn phase = PhaseDsn.getPhaseDsnFromPrefix(itemPhase.getKey());
        NatureDsn nature = NatureDsn.getNatureDsn(itemNature.getKey());

        return new PhaseNatureType(phase, nature, null);
    }



    private void addComponentsToPane(Container pane) {
        pane.setLayout(new BorderLayout());

        createPanelTop(pane, BorderLayout.PAGE_START);
        createPanelMiddle(pane, BorderLayout.CENTER);
        createPanelComment(pane, BorderLayout.PAGE_END);
    }

    private void populatePhaseNatureType() {

        DefaultComboBoxModel<KeyAndLibelle> model = (DefaultComboBoxModel<KeyAndLibelle>) this.cbListPhases.getModel();
        for (PhaseDsn phaseDsn : PhaseDsn.values()) {
            model.addElement(phaseDsn.getKeyAndLibelle());
        }

        model = (DefaultComboBoxModel<KeyAndLibelle>) this.cbListNatures.getModel();
        for (NatureDsn natureDsn : NatureDsn.values()) {
            model.addElement(natureDsn.getKeyAndLibelle());
        }

    }

    private void createPanelPhaseNatureType(Container container, String layout) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        this.cbListPhases = new JComboBox<>();
        this.formatComboBox(this.cbListPhases);
        panel.add(this.cbListPhases);
        panel.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));

        this.cbListNatures = new JComboBox<>();
        this.formatComboBox(this.cbListNatures);
        panel.add(this.cbListNatures);

        container.add(panel, layout);

        this.populatePhaseNatureType();
    }

    private void formatComboBox(JComboBox<KeyAndLibelle> comboBox) {
        comboBox.setModel(new DefaultComboBoxModel<KeyAndLibelle>());
        Dimension size = new Dimension(250, 20);
        comboBox.setPreferredSize(size);
        comboBox.setMaximumSize(size);
        comboBox.addActionListener(this);
    }

    private void createPanelMiddle(Container pane, String layout) {
        this.dsnNormeTree = new DsnNormeTree("Norme");

        JScrollPane scrollPane = new JScrollPane(this.dsnNormeTree);
        pane.add(scrollPane, layout);

    }

    private void createPanelComment(Container pane, String layout) {

        JPanel panelComment = new JPanel();
        panelComment.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.taComment = new JTextArea();
        this.taComment.setText(COMMENT);
        this.taComment.setMargin(new Insets(10, 10, 10, 10));
        panelComment.add(this.taComment);

        pane.add(panelComment, layout);
    }


    private void createPanelTop(Container container, String layout) {

        this.panelTop = new JPanel();
        this.panelTop.setLayout(new BoxLayout(this.panelTop, BoxLayout.Y_AXIS));
        this.createSearchPanel(this.panelTop, null);
        this.panelTop.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
        this.createPanelPhaseNatureType(this.panelTop, null);
        this.panelTop.add(Box.createRigidArea(DIM_VER_RIGID_AREA_15));
        container.add(panelTop, layout);

    }



}
