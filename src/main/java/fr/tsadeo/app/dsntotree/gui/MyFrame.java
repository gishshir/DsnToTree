package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collections;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;

import fr.tsadeo.app.dsntotree.gui.action.CancelSearchAction;
import fr.tsadeo.app.dsntotree.gui.action.FocusSearchAction;
import fr.tsadeo.app.dsntotree.gui.action.NextSearchAction;
import fr.tsadeo.app.dsntotree.gui.action.SaveDsnAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowErrorAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowJdbcFrameAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowOpenDialogAction;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ErrorMessage;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.util.ListItemBlocListenerManager;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class MyFrame extends AbstractFrame implements DocumentListener, ItemBlocListener, IMainActionListener {

	private static final Logger LOG = Logger.getLogger(MyFrame.class.getName());
	
    private static final long serialVersionUID = 1L;

    private Dsn dsn;

    private MyTree myTree;

    private FilterPanel blocsPanel;

    private MyPanelBloc myPanelBloc;
    private JdbcFrame jdbcFrame;

    private StateButton btOpen, btSave, btShowErrors, showJdbc;
    private JTextField tfSearch;
    private int searchNoResult = Integer.MAX_VALUE;
    private Color tfSearchBg;

    private final FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Fichiers texte", "txt");

    public void addComponentsToPane(Container pane) {
        pane.setLayout(new BorderLayout());

        createPanelTop(pane, BorderLayout.PAGE_START);
        createFilterPanel(pane, BorderLayout.LINE_START);
        createSplitPanel(pane, this.createPanelTree(), this.createPanelBloc(), BorderLayout.CENTER, 500);
        createTextArea(pane, BorderLayout.PAGE_END);
    }

    private JComponent createPanelBloc() {

    	ListItemBlocListenerManager.get().addItemBlocListener(this);
        this.myPanelBloc = new MyPanelBloc(this);
        
        return this.myPanelBloc;
    }

    private JComponent createPanelTree() {
        this.myTree = new MyTree(this);
        JScrollPane scrollPane = new JScrollPane(this.myTree);
        return scrollPane;
    }

    private void createFilterPanel(Container container, String layout) {
        this.blocsPanel = new FilterPanel(this.buildFilterActionListener());
        JScrollPane scrollPane = new JScrollPane(this.blocsPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        container.add(scrollPane, layout);
    }

    private ActionListener buildFilterActionListener() {
        ActionListener al = new ActionListener() {

            // @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() instanceof JCheckBox) {
                    JCheckBox cb = (JCheckBox) e.getSource();
                    LOG.config(cb.getText() + " - " + cb.isSelected());
                    MyFrame.this.myTree.expandBloc(cb.getText().substring(0, 2), cb.isSelected());
                } else if (e.getActionCommand().equals(ALL) && e.getSource() instanceof JToggleButton) {
                    JToggleButton tb = (JToggleButton) e.getSource();
                    MyFrame.this.myTree.expandBloc(ALL, tb.isSelected());
                }
                setFocusOnSearch();
            }

        };

        return al;
    }

    private void createPanelButton(Container container, String layout) {

        JPanel panelButton = new JPanel();
        this.createButtonOpen(panelButton, BorderLayout.CENTER);
        this.createButtonSave(panelButton, BorderLayout.CENTER);
        this.createButtonShowErrors(panelButton, BorderLayout.CENTER);
        this.createButtonShowJdbc(panelButton, BorderLayout.CENTER);

        container.add(panelButton, layout);
    }

    private void createButtonOpen(Container container, String layout) {

        btOpen = new StateButton();
        GuiUtils.createButton(btOpen, new ShowOpenDialogAction(this), SHOW_OPEN_DIALOG_ACTION, KeyEvent.VK_O,
                PATH_OPEN_ICO, "Ouvrir une DSN ...", "Ouvrir un fichier DSN", true, container, layout);
    }

    private void createButtonSave(Container container, String layout) {

        btSave = new StateButton();
        GuiUtils.createButton(btSave, new SaveDsnAction(this), SAVE_DSN_ACTION, KeyEvent.VK_S, PATH_SAVE_ICO,
                "sauvegarder", "Sauvegarder le fichier", false, container, layout);
    }

    private void createButtonShowJdbc(Container container, String layout) {
    	
    	boolean active = SettingsUtils.get().hasApplicationSettings();
    	

        showJdbc = new StateButton();
        GuiUtils.createButton(showJdbc, new ShowJdbcFrameAction(this), SHOW_JDBC_ACTION, KeyEvent.VK_B, PATH_BDD_ICO,
                "Accéder BDD", "Récupérer un message depuis la base", active, container, layout);
    }

    private void createButtonShowErrors(Container container, String layout) {

        btShowErrors = new StateButton();
        GuiUtils.createButton(btShowErrors, new ShowErrorAction(this), SHOW_ERROR_DIALOG_ACTION, KeyEvent.VK_R,
                PATH_ERROR_ICO, "erreurs", "Voir la liste des erreurs", false, container, layout);
    }

    private void createSearchPanel(Container container, String layout) {

        JPanel panelSearch = new JPanel();
        JLabel labelRechercher = new JLabel("rechercher...");
        labelRechercher.setIcon(GuiUtils.createImageIcon(PATH_FIND_ICO));
        panelSearch.add(labelRechercher);
        this.tfSearch = new JTextField(15);
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

    private void createPanelTop(Container container, String layout) {

        JPanel panelTop = new JPanel(new BorderLayout());

        this.createPanelButton(panelTop, BorderLayout.CENTER);
        this.createSearchPanel(panelTop, BorderLayout.LINE_END);
        container.add(panelTop, layout);
    }

    public MyFrame() {
        super("Visualisation et édition d'un message DSN sous forme arborescente", JFrame.EXIT_ON_CLOSE);

        // Set up the content pane.
        addComponentsToPane(this.getContentPane());
        this.fc.setFileFilter(this.fileFilter);
        
//    	this.addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent e) {
//				int i = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment quitter l'application?");
//				if (i == 0)
//					System.exit(0);// cierra aplicacion
//			}
//		});

    }

    @Override
    public void setFocusOnSearch() {
        if (this.tfSearch != null) {
            this.tfSearch.requestFocusInWindow();
        }
    }

    @Override
    public void actionShowBlocItem(ItemBloc itemBloc, String pathParent) {
    	
    	String title = pathParent.concat(" - ").concat(itemBloc.toString());
    	BlocTreeFrame blocTreeFrame = new BlocTreeFrame(title, this);
    	ListItemBlocListenerManager.get().addItemBlocListener(blocTreeFrame);
    	GuiApplication.centerFrame(blocTreeFrame, 0.25f, 0.65f);
    	
    	blocTreeFrame.setItemBloc(itemBloc);
    	
    	blocTreeFrame.setVisible(true);
    }
    @Override
    public void actionShowJdbcDialog() {

        if (this.jdbcFrame == null) {
            this.jdbcFrame = new JdbcFrame(this);
        }
        GuiApplication.centerFrame(jdbcFrame, 0.65f, 0.65f);
        jdbcFrame.setVisible(true);
    }

    private void actionReadFileAndShowTree(File file) {

        try {

            this.dsn = ServiceFactory.getReadDsnFromFileService().buildTreeFromFile(file);
            this.actionShowDsnTree(dsn);
        } catch (Exception e) {
            processTextArea.setText("ERROR: " + e.getMessage());
            this.myTree.createNodes(null, true);
        }
    }

    @Override
    public void actionShowDsnTreeWithConfirmation(Dsn dsn) {
        if (dsn == null || dsn.getFile() == null) {
            return;
        }
        this.processTextArea.setText(null);
        if (this.isDsnModified()) {

            int respons = JOptionPane.showConfirmDialog(this,
                    "La DSN a été modifiée. \nVoulez-vous sauvegarder les modifications?", "DSN modifiée",
                    JOptionPane.YES_NO_OPTION);
            if (respons == JOptionPane.YES_OPTION) {
                this.actionSaveDsn(false);
            }
            this.actionShowDsnTree(dsn);

        } else {
            this.actionShowDsnTree(dsn);
        }
    }

    private void actionShowDsnTree(Dsn dsn) {

        this.requestFocus();
        this.actionCancelSearch();
        this.myPanelBloc.clear();
        this.myTree.clearTree();
        this.btShowErrors.setEnabled(false);

        try {

            this.dsn = dsn;
            this.btSave.setEnabled(dsn.getDsnState().isModified());
            this.actionCancelSearch();
            this.btShowErrors.setEnabled(this.dsn.getDsnState().isError());

            if (dsn.getRoot().hasChildren()) {
                this.myTree.createNodes(dsn.getRoot(), true);
                this.myTree.expandPath(new TreePath(this.myTree.getTop()));

                this.blocsPanel.buildListBlocCheckbox(dsn);

            } else {
                this.myTree.showListRubriques(dsn.getRoot(), dsn.getRubriques());
            }
            this.myPanelBloc.setTreeRoot(dsn.getTreeRoot());

            String phase = dsn.getPhase() == null ? "NA" : dsn.getPhase();
            String nature = dsn.getNature() == null ? "NA" : dsn.getNature();
            String type = dsn.getType() == null ? "NA" : dsn.getType();
            processTextArea.append("Phase: ".concat(phase).concat(" - Nature: ").concat(nature).concat(" - Type: ")
                    .concat(type).concat(SAUT_LIGNE));

            this.setFocusOnSearch();
        } catch (Exception e) {
            processTextArea.setText("ERROR: " + e.getMessage());
            this.myTree.createNodes(null, true);
        }
    }

    @Override
    public void actionCancelSearch() {
        tfSearch.setText("");
        tfSearch.setBackground(tfSearchBg);
        searchNoResult = Integer.MAX_VALUE;
        MyFrame.this.myTree.cancelSearch();
    }

    @Override
    public void searchNext() {
        this.myTree.search(this.tfSearch.getText(), true);
    }

    private void search() {
        String search = tfSearch.getText();
        int searchLenght = search != null ? search.length() : 0;
        if (searchLenght > 3 && searchLenght < this.searchNoResult) {
            if (this.myTree.search(this.tfSearch.getText(), false)) {
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
            }
        }
    }

    @Override
    public void actionShowErrors() {
        JPanel panelError = new JPanel();
        panelError.setLayout(new BoxLayout(panelError, BoxLayout.Y_AXIS));
        if (dsn.getDsnState().getListErrorMessage() != null) {
            Collections.sort(dsn.getDsnState().getListErrorMessage());
            for (ErrorMessage errorMessage : dsn.getDsnState().getListErrorMessage()) {
                panelError.add(new JLabel(errorMessage.toString()));
            }
            JOptionPane.showMessageDialog(this, panelError, "Liste des erreurs", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionSaveDsn(boolean reload) {

        if (this.dsn == null || this.dsn.getFile() == null) {
            this.processTextArea.append("Sauvegarde du fichier impossible");
            return;
        }
        try {
            ServiceFactory.getDsnService().updateDsnWithTree(this.dsn);

            if (this.currentDirectory == null || !this.dsn.getFile().exists()) {
                File fileToSave = this
                        .showSaveFileDialog(this.dsn.getFile() == null ? "dsn.txt" : this.dsn.getFile().getName());
                if (fileToSave != null) {
                    this.dsn.setFile(fileToSave);
                } else {
                    this.processTextArea.append("Sauvegarde du fichier annulée");
                    return;
                }
            }

            File file = ServiceFactory.getWriteDsnService().write(this.dsn);
            if (file != null) {
                JOptionPane.showMessageDialog(this, "DSN sauvegardée!", "Information", JOptionPane.INFORMATION_MESSAGE);
                this.processTextArea.append("Sauvegarde du fichier ".concat(file.getAbsolutePath().concat(SAUT_LIGNE)));
                this.btSave.setEnabled(false);
                if (reload) {
                    // on recharge le fichier sauvegardé
                    this.actionReadFileAndShowTree(file);
                }
            }
        } catch (Exception ex) {
            this.processTextArea.setText("Echec de la sauvegarde:");
            this.processTextArea.append(RC);
            this.processTextArea.append(ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
        }
    }

    private boolean isDsnModified() {
        return this.btSave.isEnabled();
    }

    @Override
    public void actionShowOpenDialogWithConfirmation() {

        if (this.isDsnModified()) {

            int respons = JOptionPane.showConfirmDialog(this,
                    "La DSN a été modifiée. \nVoulez-vous sauvegarder les modifications?", "DSN modifiée",
                    JOptionPane.YES_NO_OPTION);
            if (respons == JOptionPane.YES_OPTION) {
                this.actionSaveDsn(false);
            }
            this.actionShowOpenFileDialog();

        } else {
            this.actionShowOpenFileDialog();
        }
    }

    private File showSaveFileDialog(String filename) {

        if (this.currentDirectory != null) {
            fc.setCurrentDirectory(this.currentDirectory);
        }
        fc.setSelectedFile(new File(filename));

        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            return fc.getSelectedFile();
        }
        return null;
    }

    private void actionShowOpenFileDialog() {

        if (this.currentDirectory != null) {
            fc.setCurrentDirectory(this.currentDirectory);
        }
        int returnVal = fc.showOpenDialog(this);

        processTextArea.setText(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            this.currentDirectory = file.getParentFile();

            processTextArea.append("DSN: ".concat(file.getName()).concat(POINT).concat(SAUT_LIGNE).concat(SAUT_LIGNE));
            this.actionReadFileAndShowTree(file);
        } else {

            processTextArea.setText("Open command cancelled by user.".concat(SAUT_LIGNE));
        }
        processTextArea.setCaretPosition(processTextArea.getDocument().getLength());
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

    // ---------------------------------- implements ItemBlocListener
    @Override
    public void onItemBlocSelected(ItemBloc itemBloc, int treeRowOfBloc, String pathParent) {

        this.actionShowBlocToEditWithConfirmation(itemBloc, treeRowOfBloc, pathParent, itemBloc.getFirstRubrique(),
                false);
    }

    @Override
    public void onItemRubriqueSelected(ItemRubrique itemRubrique, int treeRowOfBloc, String pathParent) {

        if (!itemRubrique.isError()) {
            boolean focus = !this.tfSearch.hasFocus();
            ItemBloc itemBloc = itemRubrique.getBlocContainer();
            if (itemBloc != null) {
                this.actionShowBlocToEditWithConfirmation(itemBloc, treeRowOfBloc, pathParent, itemRubrique, focus);
            }
        }
    }

    private void actionShowBlocToEditWithConfirmation(ItemBloc itemBloc, int treeRowOfBloc, String pathParent,
            ItemRubrique itemRubriqueToFocus, boolean focus) {

        ItemBloc editedItemBloc = this.myPanelBloc.getCurrentItemBloc();
        if (editedItemBloc == itemBloc) {
            this.myPanelBloc.selectItemRubrique(itemRubriqueToFocus, focus);
            return;
        }

        if (this.myPanelBloc.isCurrentBlocModified()) {
            int respons = JOptionPane.showConfirmDialog(this,
                    "Le bloc " + this.myPanelBloc.getCurrentItemBloc().toString() + " a été modifié.\n"
                            + "Voulez-vous valider les modifications ?",
                    "Confirmer modifications", JOptionPane.WARNING_MESSAGE);
            if (respons == JOptionPane.OK_OPTION) {
                this.myPanelBloc.validerSaisie(false);
            }
        }
        this.actionShowBlocToEdit(itemBloc, treeRowOfBloc, pathParent, itemRubriqueToFocus, focus);
    }

    private void actionShowBlocToEdit(ItemBloc itemBloc, int treeRowOfBloc, String pathParent,
            ItemRubrique itemRubriqueToFocus, boolean focus) {

        this.myPanelBloc.setItemBloc(itemBloc, treeRowOfBloc, pathParent, itemRubriqueToFocus, focus);
        this.repaint();
        this.revalidate();
    }

    @Override
    public void onItemBlocModified(ItemBloc itemBloc, int treeRowOfBloc, ModifiedState state, boolean refresh) {

        switch (state) {
        case annuler:

            break;
        case valider:
            this.validerBlocModification(itemBloc, treeRowOfBloc, true);
            break;
        }

    }
    
    


    private void validerBlocModification(ItemBloc itemBloc, int treeRowOfBloc, boolean refresh) {

        ServiceFactory.getDsnService().updateDsnListBloc(this.dsn, itemBloc);
        this.myTree.refreshBloc(treeRowOfBloc, itemBloc);
        this.btSave.setEnabled(true);
        if (refresh) {
            this.myTree.expandRow(treeRowOfBloc);
            this.myTree.setSelectionRow(treeRowOfBloc);
        }
        this.repaint();
        this.revalidate();
    }

}
