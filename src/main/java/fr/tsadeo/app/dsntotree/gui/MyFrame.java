package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.dnd.DropTarget;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.MySwingUtilities;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;

import fr.tsadeo.app.dsntotree.business.SalarieDto;
import fr.tsadeo.app.dsntotree.gui.action.SaveDsnAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowErrorAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowJdbcFrameAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowOpenDialogAction;
import fr.tsadeo.app.dsntotree.gui.component.SearchPanel;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;
import fr.tsadeo.app.dsntotree.gui.salarie.SalariesFrame;
import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;
import fr.tsadeo.app.dsntotree.model.NatureDsn;
import fr.tsadeo.app.dsntotree.model.PhaseDsn;
import fr.tsadeo.app.dsntotree.model.PhaseNatureType;
import fr.tsadeo.app.dsntotree.model.TypeDsn;
import fr.tsadeo.app.dsntotree.service.IDictionnaryListener;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.util.DragAndDropUtil.FileDropper;
import fr.tsadeo.app.dsntotree.util.ListDsnListenerManager;
import fr.tsadeo.app.dsntotree.util.ListItemBlocListenerManager;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;
import fr.tsadeo.app.dsntotree.util.SettingsUtils.ISettingsListener;

public class MyFrame extends AbstractFrame
        implements ItemBlocListener, ISearchActionListener, IMainActionListener, IDictionnaryListener,
        ISettingsListener {

    private static final Logger LOG = Logger.getLogger(MyFrame.class.getName());

    private static final long serialVersionUID = 1L;

    private Dsn dsn;
    private boolean dnsSavedOneTime = false;

    private MyTree myTree;

    private BusinessPanel businessPanel;

    private MyPanelBloc myPanelBloc;
    private JdbcFrame jdbcFrame;
    private SalariesFrame salariesFrame;

    private StateButton btOpen, btSave, btShowErrors, btShowJdbc;
    private int searchNoResult = Integer.MAX_VALUE;
    
    private SearchPanel searchPanel;
    private boolean searchInNode = true;

    private boolean blocDragStarted = false;

    private final FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Fichiers texte", "txt");

    public void addComponentsToPane(Container pane) {
        pane.setLayout(new BorderLayout());

        createPanelTop(pane, BorderLayout.PAGE_START);
        createBusinessPanel(pane, BorderLayout.LINE_START);
        createSplitPanel(pane, this.createPanelTree(), this.createPanelBloc(), BorderLayout.CENTER, 500);
        createBottomPanel(pane, BorderLayout.PAGE_END);
    }

    private void createBottomPanel(Container container, String layout) {

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        createTextArea(bottomPanel, BorderLayout.NORTH);
        bottomPanel.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));

        JPanel versionPanel = new JPanel();
        versionPanel.setLayout(new BoxLayout(versionPanel, BoxLayout.X_AXIS));
        versionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
        versionPanel.add(Box.createHorizontalGlue());

        JLabel labVersion = new JLabel("mise à jour le 11 décembre 2017");
        labVersion.setForeground(Color.gray);
        versionPanel.add(labVersion);

        versionPanel.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));

        bottomPanel.add(versionPanel, BorderLayout.PAGE_END);

        container.add(bottomPanel, layout);

    }

    private JComponent createPanelBloc() {

        ListItemBlocListenerManager.get().addItemBlocListener(this);
        this.myPanelBloc = new MyPanelBloc(this);

        return this.myPanelBloc;
    }

    private void fireFileDropped(File file) {

        this.currentDirectory = file.getParentFile();
        processTextArea.setText("DSN: ".concat(file.getName()).concat(POINT).concat(SAUT_LIGNE).concat(SAUT_LIGNE));
        this.actionReadFileAndShowTree(file, false, "DSN déposée!");

    }

    private JComponent createPanelTree() {

        this.myTree = new MyTree(this, this);

        JScrollPane scrollPane = new JScrollPane(this.myTree);
        return scrollPane;
    }

    private void createBusinessPanel(Container container, String layout) {
        this.businessPanel = new BusinessPanel(this);

        JScrollPane scrollPane = new JScrollPane(this.businessPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        container.add(scrollPane, layout);
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

        boolean active = false;

        btShowJdbc = new StateButton();
        GuiUtils.createButton(btShowJdbc, new ShowJdbcFrameAction(this), SHOW_JDBC_ACTION, KeyEvent.VK_B, PATH_BDD_ICO,
                "Accéder BDD", "Récupérer un message depuis la base", active, container, layout);
    }

    private void createButtonShowErrors(Container container, String layout) {

        btShowErrors = new StateButton();
        GuiUtils.createButton(btShowErrors, new ShowErrorAction(this), SHOW_ERROR_DIALOG_ACTION, KeyEvent.VK_R,
                PATH_ERROR_ICO, "erreurs", "Voir la liste des erreurs", false, container, layout);
    }

    private void createSearchPanel(Container container, String layout) {
    	
    	this.searchPanel = new SearchPanel(this);
        container.add(this.searchPanel, layout);
    }

    private void createPanelTop(Container container, String layout) {

        JPanel panelTop = new JPanel(new BorderLayout());

        this.createPanelButton(panelTop, BorderLayout.CENTER);
        this.createSearchPanel(panelTop, BorderLayout.LINE_END);
        container.add(panelTop, layout);
    }

    public MyFrame() {
        super("Visualisation et édition d'un message DSN sous forme arborescente", JFrame.EXIT_ON_CLOSE);

        ServiceFactory.getDictionnaryService().addListener(this);

        // Set up the content pane.
        addComponentsToPane(this.getContentPane());
        this.fc.setFileFilter(this.fileFilter);

        new DropTarget(this, new FileDropper(this));
    }

    @Override
    public void setFocusOnSearch() {
        if (this.searchPanel != null) {
            this.searchPanel.requestFocusOnSearch();
        }
    }

    // ----------------------------------- implementing ISettingsListener
    @Override
    public void settingsLoaded() {

        this.btShowJdbc.setEnabled(SettingsUtils.get().hasApplicationSettings());
    }

    @Override
    public void settingsUpdated() {
        LOG.warning("settings updated!!!!!!!!!");
    }

    // ---------------------------------------- implementing IMainActionListener

    @Override
    public void actionItemBlocDroppedWithConfirmation(ItemBloc parentTarget, ItemBloc blocToDrop) {

        String message = "Voulez-vous copier le bloc ".concat(blocToDrop.toString())
                .concat("\ndans le bloc parent ".concat(parentTarget.toString()).concat(" ?"));
        int respons = JOptionPane.showConfirmDialog(this, message, "Copie d'un bloc ", JOptionPane.YES_NO_OPTION);
        if (respons == JOptionPane.YES_OPTION) {

            LOG.config(" to ItemBloc " + parentTarget.toString());
            ItemBloc newBloc = ServiceFactory.getDsnService().createNewChild(blocToDrop, true, true);
            parentTarget.addChild(newBloc);
            parentTarget.setChildrenModified(true);
            ServiceFactory.getDsnService().reorderListChildBloc(this.getTreeRoot(), parentTarget);
            this.validerBlocModification(parentTarget, true);

            ListItemBlocListenerManager.get().onItemBlocModified(parentTarget, ModifiedState.valider, true);
        }
    }

    @Override
    public void actionFileDroppedWithConfirmation(File file) {

        LOG.info("actionFileDroppedWithConfirmation(): ".concat(file.getName()));
        if (this.isDsnModified()) {

            int respons = JOptionPane.showConfirmDialog(this,
                    "La DSN a été modifiée. \nVoulez-vous sauvegarder les modifications?", "DSN modifiée",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (respons == JOptionPane.YES_OPTION) {
                this.actionSaveDsn(false);
            } else if (respons == JOptionPane.CANCEL_OPTION) {
                processTextArea.append("Drag and drop annulé par l'utilisateur!");
            } else {
                this.fireFileDropped(file);
            }

        } else {
            this.fireFileDropped(file);
        }

    }

    @Override
    public void actionDisplayProcessMessage(String message, boolean append) {
        if (append) {
            this.processTextArea.append(SAUT_LIGNE);
            this.processTextArea.append(message);
        } else {
            this.processTextArea.setText(message);
        }
    }

    @Override
    public void actionEditBlocItem(ItemBloc itemBloc, boolean selectInTree) {
        this.requestFocus();
        if (selectInTree) {
            TreePath treePath = this.myTree.getPath(itemBloc);
            this.myTree.expandPath(treePath);
            this.myTree.setSelectionPath(treePath);
        }
        this.actionShowBlocToEditWithConfirmation(itemBloc, itemBloc.getFirstRubrique(), false);
    }

    @Override
    public void actionShowDnsNormeFrame() {

        DsnNormeFrame dnsNormeFrame = new DsnNormeFrame(SettingsUtils.get().getNormeDsnFile().getName(), this);

        GuiApplication.centerFrame(dnsNormeFrame, 0.35f, 0.65f);

        PhaseNatureType phaseNatureType = this.dsn != null ? this.dsn.getPhaseNatureType()
                : new PhaseNatureType(PhaseDsn.PHASE_3, NatureDsn.DSN_MENSUELLE, TypeDsn.NORMALE);
        dnsNormeFrame.setPhaseNaturePhase(phaseNatureType);
        dnsNormeFrame.setVisible(true);
    }

    @Override
    public void actionShowBlocFrame(ItemBloc itemBloc) {

        String pathItemBloc = this.myTree.getPathAsString(itemBloc);
        String description = itemBloc == null ? null
                : ServiceFactory.getDsnService().getBlocLibelle(itemBloc.getBlocLabel());
        ShowBlocFrame blocTreeFrame = new ShowBlocFrame(this.dsn.getFile().getName(), pathItemBloc, description, this);
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

    @Override
    public void actionShowSalarieDialog() {

        if (this.salariesFrame == null) {
            this.salariesFrame = new SalariesFrame(this);
        }
        GuiApplication.centerFrame(this.salariesFrame, 0.35f, 0.35f);

        List<SalarieDto> listSalaries = ServiceFactory.getDsnService().buildListSalarieDtos(this.dsn);
        this.salariesFrame.setDatas(listSalaries);

        this.salariesFrame.setVisible(true);

    }

    private void actionReadFileAndShowTree(File file, boolean reload, String messageDialog) {

        try {
            if (!reload) {
                ListDsnListenerManager.get().onDsnOpened();
            }
            this.dsn = ServiceFactory.getReadDsnFromFileService().buildTreeFromFile(file);
            this.actionShowDsnTree(dsn, messageDialog);
        } catch (Exception e) {
            processTextArea.setText("ERROR: " + e.getMessage());
            this.myTree.createNodes(null, true);
        }
    }

    @Override
    public void actionShowDsnTreeWithConfirmation(Dsn dsn, String messageDialog) {
        if (dsn == null || dsn.getFile() == null) {
            return;
        }
        this.processTextArea.setText(null);
        this.dnsSavedOneTime = false;
        if (this.isDsnModified()) {

            int respons = JOptionPane.showConfirmDialog(this,
                    "La DSN a été modifiée. \nVoulez-vous sauvegarder les modifications?", "DSN modifiée",
                    JOptionPane.YES_NO_OPTION);
            if (respons == JOptionPane.YES_OPTION) {
                this.actionSaveDsn(false);
            }
            this.actionShowDsnTree(dsn, messageDialog);

        } else {
            this.actionShowDsnTree(dsn, messageDialog);
        }
    }

    private void actionShowDsnTree(final Dsn dsn, final String messageDialog) {

        this.requestFocus();
        this.actionCancelSearch();
        this.myPanelBloc.clear();
        this.myTree.clearTree();
        this.btShowErrors.setEnabled(false);

        this.dsn = dsn;
        this.actionCancelSearch();

        this.waitEndAction();

        MySwingUtilities.invokeLater(new Runnable() {
            public void run() {

                try {

                    if (dsn.getRoot().hasChildren()) {
                        myTree.createNodes(dsn.getRoot(), true);
                        myTree.expandBloc(BLOC_11, true);

                        // filterPanel.buildListBlocCheckbox(dsn);

                    } else {
                        myTree.showListRubriques(dsn.getRoot(), dsn.getRubriques());
                    }
                    myPanelBloc.setTreeRoot(dsn.getTreeRoot());
                    businessPanel.activeButtons(true);

                    processTextArea.append(getPhaseNatureType().concat(SAUT_LIGNE));

                    btSave.setEnabled(dsn.getDsnState().isModified());
                    btShowErrors.setEnabled(dsn.getDsnState().isError());
                    currentActionEnded();

                    setFocusOnSearch();

                    if (messageDialog != null) {
                        JOptionPane.showMessageDialog(MyFrame.this, messageDialog, "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (Throwable e) {
                    currentActionEnded();
                    processTextArea.setText("ERROR: " + e.getMessage());
                    myTree.createNodes(null, true);
                }
            }
        });

    }

    private String getPhaseNatureType() {

        return dsn.toString();
    }

    @Override
    public void actionCancelSearch() {
        this.searchPanel.cancelSearch();
        searchNoResult = Integer.MAX_VALUE;
        MyFrame.this.myTree.cancelSearch();
        ListDsnListenerManager.get().onSearchCanceled();
    }

    @Override
    public void searchNext() {
    	String search = this.searchPanel.getSearchText();
        if (this.myTree.search(search, this.searchInNode, true)) {
            ListDsnListenerManager.get().onSearch(search, true);
        }
    }

    @Override
    public void search() {
    	String search = this.searchPanel.getSearchText();
        int searchLenght = search != null ? search.length() : 0;
        boolean blocOrRubrique = searchLenght >= 2 && ServiceFactory.getDsnService().isBlocOrRubriquePattern(search);

        if (blocOrRubrique) {

            if (this.myTree.search(search, true, false)) {
                this.searchInNode = true;
                this.searchNoResult = Integer.MAX_VALUE;
                this.searchPanel.setSearchColor(SEARCH_SUCCESS_COLOR);
                return;
            }
        }

        if (searchLenght > 3 && searchLenght < this.searchNoResult) {

            if (this.myTree.search(search, false, false)) {
                this.searchInNode = false;
                this.searchNoResult = Integer.MAX_VALUE;
                this.searchPanel.setSearchColor(SEARCH_SUCCESS_COLOR);

                ListDsnListenerManager.get().onSearch(search, false);
            } else {
                this.searchPanel.setSearchColor(ERROR_COLOR);
                this.searchNoResult = search.length();
            }
        } else {
            if (searchLenght <= 3) {
                this.searchPanel.setDefaultBackground();
                this.searchNoResult = Integer.MAX_VALUE;
                this.searchInNode = true;
            }
        }
    }

    @Override
    public void actionShowErrors() {
        JPanel panelError = new JPanel();
        panelError.setLayout(new BoxLayout(panelError, BoxLayout.Y_AXIS));
        if (dsn.getDsnState().getListErrorMessage() != null) {

            dsn.getDsnState().getListErrorMessage().stream().sorted()
                    .forEach(errorMessage -> panelError.add(new JLabel(errorMessage.toString())));

            JOptionPane.showMessageDialog(this, panelError, "Liste des erreurs", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionSaveDsn(final boolean reload) {

        if (this.dsn == null || this.dsn.getFile() == null) {
            this.processTextArea.append("Sauvegarde du fichier impossible".concat(SAUT_LIGNE));
            return;
        }
        this.processTextArea.append("Sauvegarde en cours...".concat(SAUT_LIGNE));

        SwingWorker<File, Void> worker = new SwingWorker<File, Void>() {

            File file;
            Exception exception;

            @Override
            protected File doInBackground() throws Exception {

                waitEndAction();
                file = null;
                try {
                    ServiceFactory.getDsnService().updateDsnWithTree(dsn);

                    if (!MyFrame.this.dnsSavedOneTime || currentDirectory == null || dsn.getFile() == null) {
                        File fileToSave = MyFrame.this
                                .showSaveFileDialog(dsn.getFile() == null ? "dsn.txt" : dsn.getFile().getName());
                        if (fileToSave != null) {
                            dsn.setFile(fileToSave);
                            file = ServiceFactory.getWriteDsnService().write(dsn);
                        }
                    } else {
                        file = ServiceFactory.getWriteDsnService().write(dsn);
                    }

                } catch (Exception ex) {
                    exception = ex;
                    file = null;
                }
                return file;
            }

            @Override
            protected void done() {

                currentActionEnded();

                if (file != null) {
                    MyFrame.this.dnsSavedOneTime = true;
                    JOptionPane.showMessageDialog(MyFrame.this, "DSN sauvegardée!", "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                    processTextArea.append("Sauvegarde du fichier ".concat(file.getAbsolutePath().concat(SAUT_LIGNE)));
                    btSave.setEnabled(false);
                    if (reload) {
                        // on recharge le fichier sauvegardé
                        actionReadFileAndShowTree(file, true, null);
                        ListDsnListenerManager.get().onDsnReloaded(dsn);
                    }
                } else {
                    if (exception != null) {
                        processTextArea.setText("Echec de la sauvegarde:");
                        processTextArea.append(RC);
                        processTextArea.append(exception.getMessage() == null ? exception.getClass().getName()
                                : exception.getMessage());
                    } else {
                        processTextArea.append("Sauvegarde du fichier annulée".concat(SAUT_LIGNE));
                    }
                }
            }

        };
       worker.execute();

    }

    private boolean isDsnModified() {
        return this.btSave.isEnabled();
    }

    @Override
    public void actionShowOpenDialogWithConfirmation() {

        if (this.isDsnModified()) {

            int respons = JOptionPane.showConfirmDialog(this,
                    "La DSN a été modifiée. \nVoulez-vous sauvegarder les modifications?", "DSN modifiée",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (respons == JOptionPane.YES_OPTION) {
                this.actionSaveDsn(false);
                this.actionShowOpenFileDialog();
            } else if (respons == JOptionPane.NO_OPTION) {
                this.actionShowOpenFileDialog();
            }

        } else {
            this.actionShowOpenFileDialog();
        }
    }

    /**
     * retourne le fichier à sauvegarder ou null si annulation
     */
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
            this.dnsSavedOneTime = false;

            processTextArea.append("DSN: ".concat(file.getName()).concat(POINT).concat(SAUT_LIGNE).concat(SAUT_LIGNE));
            this.actionReadFileAndShowTree(file, false, null);
        } else {

            processTextArea.setText("Open command cancelled by user.".concat(SAUT_LIGNE));
        }
        processTextArea.setCaretPosition(processTextArea.getDocument().getLength());
    }

    // -------------------------------------- implementing IDictionnaryListener
    @Override
    public void dsnDictionnaryReady() {
        this.businessPanel.activeNormeButton(true);
    }


    // ---------------------------------- implements ItemBlocListener
    @Override
    public BlocTree getTreeRoot() {
        return this.dsn == null ? null : this.dsn.getTreeRoot();
    }

    @Override
    public void onItemBlocSelected(ItemBloc itemBloc) {

        if (blocDragStarted) {
            return;
        }
        this.actionShowBlocToEditWithConfirmation(itemBloc, itemBloc.getFirstRubrique(), false);
    }

    @Override
    public void onItemRubriqueSelected(ItemRubrique itemRubrique) {

        if (blocDragStarted) {
            return;
        }
        if (!itemRubrique.isError()) {
            boolean focus = !this.searchPanel.hasSearchFocus();
            ItemBloc itemBloc = itemRubrique.getBlocContainer();
            if (itemBloc != null) {
                this.actionShowBlocToEditWithConfirmation(itemBloc, itemRubrique, focus);
            }
        }
    }

    @Override
    public void onItemBlocDragStarted() {
        blocDragStarted = true;
    }

    @Override
    public void onItemBlocDropEnded() {
        blocDragStarted = false;
    }

    private void actionShowBlocToEditWithConfirmation(ItemBloc itemBloc, ItemRubrique itemRubriqueToFocus,
            boolean focus) {

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
        this.actionShowBlocToEdit(itemBloc, itemRubriqueToFocus, focus);
    }

    private void actionShowBlocToEdit(ItemBloc itemBloc, ItemRubrique itemRubriqueToFocus, boolean focus) {

        String pathParent = this.myTree.getPathAsString(itemBloc);
        this.myPanelBloc.setItemBloc(itemBloc, pathParent, itemRubriqueToFocus, focus);
        this.repaint();
        this.revalidate();
    }

    @Override
    public void onItemBlocModified(ItemBloc itemBloc, ModifiedState state, boolean refresh) {

        switch (state) {
        case annuler:

            break;
        case valider:
            this.validerBlocModification(itemBloc, true);
            break;
        }

    }

    private void validerBlocModification(ItemBloc itemBloc, boolean refresh) {

        ServiceFactory.getDsnService().updateDsnListBloc(this.dsn, itemBloc);
        TreePath treePath = this.myTree.refreshBloc(itemBloc);
        this.btSave.setEnabled(true);
        if (refresh) {
            this.myTree.expandPath(treePath);
            this.myTree.setSelectionPath(treePath);
        }
        this.repaint();
        this.revalidate();
    }

    private void waitEndAction() {

        this.setCursor(WaitingCursor);

        this.btOpen.waitEndAction();
        this.btSave.waitEndAction();
        this.btShowErrors.waitEndAction();
        this.btShowJdbc.waitEndAction();
        this.searchPanel.waitEndAction();

        this.myPanelBloc.waitEndAction();
        // this.filterPanel.waitEndAction();
        this.businessPanel.waitEndAction();

    }

    private void currentActionEnded() {

        this.btOpen.actionEnded();
        this.btSave.actionEnded();
        this.btShowErrors.actionEnded();
        this.btShowJdbc.actionEnded();
        this.searchPanel.actionEnded();

        this.myPanelBloc.currentActionEnded();
        // this.filterPanel.currentActionEnded();
        this.businessPanel.currentActionEnded();

        this.setCursor(Cursor.getDefaultCursor());
    }


    // ======================================== INNER CLASS

}
