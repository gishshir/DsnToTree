package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collections;
import java.util.List;
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
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.MySwingUtilities;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.tsadeo.app.dsntotree.business.SalarieDto;
import fr.tsadeo.app.dsntotree.gui.action.CancelSearchAction;
import fr.tsadeo.app.dsntotree.gui.action.FocusSearchAction;
import fr.tsadeo.app.dsntotree.gui.action.NextSearchAction;
import fr.tsadeo.app.dsntotree.gui.action.SaveDsnAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowErrorAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowJdbcFrameAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowOpenDialogAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowSalariesFrameAction;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;
import fr.tsadeo.app.dsntotree.gui.component.StateTextField;
import fr.tsadeo.app.dsntotree.gui.salarie.SalariesFrame;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ErrorMessage;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.util.ListDsnListenerManager;
import fr.tsadeo.app.dsntotree.util.ListItemBlocListenerManager;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class MyFrame extends AbstractFrame implements DocumentListener, ItemBlocListener, IMainActionListener {

    private static final Logger LOG = Logger.getLogger(MyFrame.class.getName());

    private static final long serialVersionUID = 1L;

    private Dsn dsn;

    private MyTree myTree;

    private FilterPanel filterPanel;

    private MyPanelBloc myPanelBloc;
    private JdbcFrame jdbcFrame;
    private SalariesFrame salariesFrame;

    private StateButton btOpen, btSave, btShowErrors, btShowJdbc, btShowSalaries;
    private StateTextField tfSearch;
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

    private void fireFileDroppedWithConfirmation(File file) {

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

    private void fireFileDropped(File file) {

        this.currentDirectory = file.getParentFile();
        processTextArea.setText("DSN: ".concat(file.getName()).concat(POINT).concat(SAUT_LIGNE).concat(SAUT_LIGNE));
        this.actionReadFileAndShowTree(file, false, "DSN déposée!");

    }

    private JComponent createPanelTree() {

        this.myTree = new MyTree(this, this);
        new DropTarget(this.myTree, new FileDropper());

        JScrollPane scrollPane = new JScrollPane(this.myTree);
        return scrollPane;
    }

    private void createFilterPanel(Container container, String layout) {
        this.filterPanel = new FilterPanel(this.buildFilterActionListener());
        JScrollPane scrollPane = new JScrollPane(this.filterPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        container.add(scrollPane, layout);
    }

    private ActionListener buildFilterActionListener() {
        ActionListener al = new ActionListener() {

            // @Override
            public void actionPerformed(final ActionEvent e) {

                waitEndAction();
                MySwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        if (e.getSource() instanceof JCheckBox) {
                            JCheckBox cb = (JCheckBox) e.getSource();
                            LOG.config(cb.getText() + " - " + cb.isSelected());
                            MyFrame.this.myTree.expandBloc(cb.getText().substring(0, 2), cb.isSelected());
                        } else if (e.getActionCommand().equals(ALL) && e.getSource() instanceof JToggleButton) {
                            JToggleButton tb = (JToggleButton) e.getSource();
                            MyFrame.this.myTree.expandBloc(ALL, tb.isSelected());
                        }
                        setFocusOnSearch();
                        currentActionEnded();
                    }
                });

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
        this.createButtonShowSalaries(panelButton, BorderLayout.CENTER);

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

        btShowJdbc = new StateButton();
        GuiUtils.createButton(btShowJdbc, new ShowJdbcFrameAction(this), SHOW_JDBC_ACTION, KeyEvent.VK_B, PATH_BDD_ICO,
                "Accéder BDD", "Récupérer un message depuis la base", active, container, layout);
    }

    private void createButtonShowSalaries(Container container, String layout) {

        this.btShowSalaries = new StateButton();
        GuiUtils.createButton(this.btShowSalaries, new ShowSalariesFrameAction(this), SHOW_SALARIES_ACTION,
                KeyEvent.VK_A, PATH_OPEN_ICO, "Salariés", "Voir la liste des salariés", true, container, layout);
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

    }

    @Override
    public void setFocusOnSearch() {
        if (this.tfSearch != null) {
            this.tfSearch.requestFocusInWindow();
        }
    }

    @Override
    public void actionShowBlocItem(ItemBloc itemBloc, String pathParent) {

        String pathItemBloc = pathParent.concat(" - ").concat(itemBloc.toString());
        BlocTreeFrame blocTreeFrame = new BlocTreeFrame(this.dsn.getFile().getName(), pathItemBloc, this);
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

                        filterPanel.buildListBlocCheckbox(dsn);

                    } else {
                        myTree.showListRubriques(dsn.getRoot(), dsn.getRubriques());
                    }
                    myPanelBloc.setTreeRoot(dsn.getTreeRoot());

                    String phase = dsn.getPhase() == null ? "NA" : dsn.getPhase();
                    String nature = dsn.getNature() == null ? "NA" : dsn.getNature();
                    String type = dsn.getType() == null ? "NA" : dsn.getType();
                    processTextArea.append("Phase: ".concat(phase).concat(" - Nature: ").concat(nature)
                            .concat(" - Type: ").concat(type).concat(SAUT_LIGNE));

                    btSave.setEnabled(dsn.getDsnState().isModified());
                    btShowErrors.setEnabled(dsn.getDsnState().isError());
                    currentActionEnded();

                    setFocusOnSearch();

                    if (messageDialog != null) {
                        JOptionPane.showMessageDialog(MyFrame.this, messageDialog, "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (Exception e) {
                    currentActionEnded();
                    processTextArea.setText("ERROR: " + e.getMessage());
                    myTree.createNodes(null, true);
                }
            }
        });

    }

    @Override
    public void actionCancelSearch() {
        tfSearch.setText("");
        tfSearch.setBackground(tfSearchBg);
        searchNoResult = Integer.MAX_VALUE;
        MyFrame.this.myTree.cancelSearch();
        ListDsnListenerManager.get().onSearchCanceled();
    }

    @Override
    public void searchNext() {
        if (this.myTree.search(this.tfSearch.getText(), true)) {
            ListDsnListenerManager.get().onSearch(this.tfSearch.getText(), true);
        }
    }

    private void search() {
        String search = tfSearch.getText();
        int searchLenght = search != null ? search.length() : 0;
        if (searchLenght > 3 && searchLenght < this.searchNoResult) {
            if (this.myTree.search(this.tfSearch.getText(), false)) {
                this.searchNoResult = Integer.MAX_VALUE;
                this.tfSearch.setBackground(this.tfSearchBg);

                ListDsnListenerManager.get().onSearch(search, false);
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

                    if (currentDirectory == null || !dsn.getFile().exists()) {
                        File fileToSave = MyFrame.this
                                .showSaveFileDialog(dsn.getFile() == null ? "dsn.txt" : dsn.getFile().getName());
                        if (fileToSave != null) {
                            dsn.setFile(fileToSave);
                            file = ServiceFactory.getWriteDsnService().write(dsn);
                        }

                    }

                } catch (Exception ex) {
                    exception = ex;
                }
                return file;
            }

            @Override
            protected void done() {

                currentActionEnded();

                if (file != null) {
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
            this.actionReadFileAndShowTree(file, false, null);
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

    private void waitEndAction() {

        this.setCursor(WaitingCursor);

        this.btOpen.waitEndAction();
        this.btSave.waitEndAction();
        this.btShowErrors.waitEndAction();
        this.btShowJdbc.waitEndAction();
        this.tfSearch.waitEndAction();

        this.myPanelBloc.waitEndAction();
        this.filterPanel.waitEndAction();

    }

    private void currentActionEnded() {

        this.btOpen.actionEnded();
        this.btSave.actionEnded();
        this.btShowErrors.actionEnded();
        this.btShowJdbc.actionEnded();
        this.tfSearch.actionEnded();

        this.myPanelBloc.currentActionEnded();
        this.filterPanel.currentActionEnded();

        this.setCursor(Cursor.getDefaultCursor());
    }

    // ======================================== INNER CLASS
    private class FileDropper extends DropTargetAdapter {

        @Override
        public void drop(DropTargetDropEvent dtde) {

            try {
                DropTargetContext context = dtde.getDropTargetContext();
                dtde.acceptDrop(DnDConstants.ACTION_COPY);

                Transferable trans = dtde.getTransferable();
                File file;
                Object obj = trans.getTransferData(DataFlavor.javaFileListFlavor);
                if (obj instanceof List) {
                    List<?> list = (List<?>) obj;
                    for (Object item : list) {
                        if (item instanceof File) {
                            file = (File) item;
                            LOG.config("Drop: " + file.getAbsolutePath());
                            context.dropComplete(true);
                            fireFileDroppedWithConfirmation(file);
                        }
                    }
                }

            } catch (Exception e) {
                processTextArea.setText("ERROR: " + e.getMessage());
            }

        }

    }
}
