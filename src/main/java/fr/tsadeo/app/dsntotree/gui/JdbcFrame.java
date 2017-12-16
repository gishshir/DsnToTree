package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.MyJOptionPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import org.apache.commons.io.IOUtils;

import fr.tsadeo.app.dsntotree.bdd.dao.BddAccessManagerFactory;
import fr.tsadeo.app.dsntotree.bdd.dao.DatabaseManager;
import fr.tsadeo.app.dsntotree.bdd.dao.IDataDsnDao;
import fr.tsadeo.app.dsntotree.bdd.dao.IMessageDsnDao;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.JdbcDataDsnDao;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.JdbcMessageDsnDao;
import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;
import fr.tsadeo.app.dsntotree.bdd.model.MessageDsn;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.gui.action.EditBddMsgAction;
import fr.tsadeo.app.dsntotree.gui.action.FocusSearchChronoAction;
import fr.tsadeo.app.dsntotree.gui.action.LoadBddMsgAction;
import fr.tsadeo.app.dsntotree.gui.action.LoadSqlFileAction;
import fr.tsadeo.app.dsntotree.gui.action.LoadSqlRequestAction;
import fr.tsadeo.app.dsntotree.gui.action.PatternFilter;
import fr.tsadeo.app.dsntotree.gui.bdd.ConnexionState;
import fr.tsadeo.app.dsntotree.gui.bdd.IBddConnectionListener;
import fr.tsadeo.app.dsntotree.gui.component.LabelAndTextField;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;
import fr.tsadeo.app.dsntotree.gui.component.StateTextField;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.util.ApplicationManager;

public class JdbcFrame extends AbstractFrame implements IBddActionListener,
IBddConnectionListener, DocumentListener {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(JdbcFrame.class.getName());

    private static final Pattern PATTERN_NUM_CHRONO = Pattern.compile("[0-9]{1,10}");
    private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateInstance();

    private static final String KEY_INFO_CHRONO = "keyInfoChrono";
    private static final String KEY_INFO_DATE = "keyInfoDate";
    private static final String KEY_INFO_NAME = "keyInfoName";

    private static final String MESS_MSG_FOUND = "Le message DSN de numéro chrono: %1$s a été trouvé en base de données!";
    private static final String MESS_MSG_NOT_FOUND = "Le message DSN de numéro chrono: %1$s na pas été trouvé en base de données!";

    private final IMessageDsnDao messageDao = new JdbcMessageDsnDao();
    private final IDataDsnDao datasDao = new JdbcDataDsnDao();

    private final Map<String, PanelMessageInfo> mapKeyToMessageInfo = new HashMap<String, PanelMessageInfo>();

    private MyPanelConnexion panelConnexion;
    private StateButton btChargerMsg, btEditerMsg, btSqlFile, btSqlRequest;
    private StateTextField tfSearchChrono;
    private JTextArea tAListRubriques;

    private MessageDsn message = null;
    private List<DataDsn> listDatas = null;

    private final IMainActionListener mainActionListener;
    // -------------------------------------- implementing DocumentListener
	@Override
	public void changedUpdate(DocumentEvent e) {
		// nothing
	}

    @Override
    public void insertUpdate(DocumentEvent e) {
        manageSaisieEnCours();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        manageSaisieEnCours();
    }

    // -------------------------------- implementing IBddConnectionListener
	@Override
	public void connectionState(ConnexionState connectionState) {
		 panelConnexion.setBddConnexionStatus(connectionState);
		 this.tfSearchChrono.setEnabled(connectionState == ConnexionState.Ok);
		 manageSaisieEnCours();
	}

    // -------------------------------- implementing IBddActionListener
    @Override
    public void setFocusOnSearch() {
        if (this.tfSearchChrono != null && this.tfSearchChrono.isEnabled()) {
            this.tfSearchChrono.requestFocusInWindow();
        }
    }

    @Override
    public void actionLoadSqlFileByChrono() {
        this.processTextArea.setText(null);
        try {
            this.processTextArea.setText("Chargement du message DSN depuis un fichier...");

            this.tAListRubriques.setText(null);
            this.waitEndAction();

            boolean success = this.showOpenFileDialogAndExtractDatas();

            this.searchActionEnded(success);
        } catch (Exception e) {
            this.processTextArea.append("Erreur lors du chargement des datas!: ");
            this.processTextArea.append(e.getMessage());
            this.searchActionEnded(false);
        }
    }

    @Override
    public void actionTesterConnexion() {

        processTextArea.setText("Test de connexion à la base de données...");
        panelConnexion.setBddConnexionStatus(ConnexionState.Unknown);
        final BddConnexionDto connexionDto = this.panelConnexion.getBddConnexionDto();
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

            boolean success = false;

            @Override
            protected Boolean doInBackground() throws Exception {

                waitEndAction();
                this.success = DatabaseManager.get().testerConnexion(connexionDto);
                return this.success;
            }

            @Override
            protected void done() {

                BddAccessManagerFactory.get().setCurrentBddConnexionDto(success ? connexionDto : null);
                connectionState(success?ConnexionState.Ok:ConnexionState.Nok);
                processTextArea.append(RC);
                processTextArea.append(success ? "OK" : "Echec");
                processTextArea.append(RC);
                
                if (success) {
                    updateSettings(connexionDto);
                }       
                currentActionEnded();
            }
        };
        worker.execute();
    }

    public void updateSettings(final BddConnexionDto connexionDto) {

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {

                return ApplicationManager.get().updateSettings(connexionDto);
            }

            @Override
            protected void done() {
                try {
                    Boolean result = this.get();
                    if (result) {
                        processTextArea.append(RC);
                        processTextArea.append("Settings mis à jour.");
                        processTextArea.append(RC);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    LOG.severe(e.getMessage());
                }

            }

        };
        worker.execute();
    }

    @Override
    public void actionEditMessageInTree() {

        if (this.message != null && this.listDatas != null) {

            try {

                SwingWorker<Dsn, Void> worker = new SwingWorker<Dsn, Void>() {

                    Dsn dsn;

                    @Override
                    protected Dsn doInBackground() throws Exception {
                        waitEndAction();
                        dsn = ServiceFactory.getReadDsnFromDatasService().buildTreeFromDatas(message.getName(),
                                listDatas);
                        return dsn;
                    }

                    @Override
                    protected void done() {

                        currentActionEnded();
                        if (dsn != null) {
                            mainActionListener.actionShowDsnTreeWithConfirmation(dsn, "Chargement depuis BDD terminé!");
                            JdbcFrame.this.setVisible(false);
                        } else {
                            processTextArea.append("Impossible de construire la DSN!");
                            btEditerMsg.setEnabled(false);
                        }

                    }

                };
                worker.execute();

            } catch (Exception e) {
                this.btEditerMsg.setEnabled(false);
                this.processTextArea.append("Erreur dans la construction de la DSN!");
                this.processTextArea.append(RC);
                this.processTextArea.append(e.getMessage());
            }
        }
    }

    @Override
    public void actionLoadSqlRequestByChrono() {

        this.processTextArea.setText(null);
        this.processTextArea.setText("Chargement des datas à partir des résultats d'une requête SQL...");
        String results = this.askSqlRequestResult();
        if (results != null) {

            File file = this.writeTemporaryFile(results);
            if (file != null && file.exists()) {
                try {
                    this.tAListRubriques.setText(null);
                    this.waitEndAction();

                    boolean success = this.extractDatasFromFile(file);
                    this.searchActionEnded(success);
                    this.processTextArea.setText((success ? "SUCCES" : "ECHEC")
                            .concat(" du chargement des datas à partir des résultats d'une requête SQL."));
                } catch (Exception ex) {
                    this.processTextArea.append("Echec dans la lecture du fichier temporaire");
                    this.processTextArea.append(RC);
                    this.processTextArea.append(ex.getMessage());
                    this.searchActionEnded(false);
                }
            }
        } else {
            this.processTextArea.append(RC);
            this.processTextArea.append("... action annulée par l'utilisateur.");
        }

    }

    private File writeTemporaryFile(String results) {

        if (results != null) {
            // sauvegarder un fichier temporaire
            File file = null;
            FileOutputStream out = null;
            try {
                file = File.createTempFile("dsn", ".tmp");
                out = new FileOutputStream(file);
                IOUtils.write(results, out, UTF8);
            } catch (Exception ex) {

                this.processTextArea.append("Erreur dans la sauvegarde du fichier temporaire");
                this.processTextArea.append(RC);
                this.processTextArea.append(ex.getMessage());

            } finally {
                IOUtils.closeQuietly(out);
            }
            return file;
        } else {
            this.processTextArea.append("Aucune data DSN valide!!");
            return null;
        }

    }

    private String askSqlRequestResult() {

        final PanelSqlRequest panelSqlRequest = new PanelSqlRequest(SQL_CHRONO);

        int option = MyJOptionPane.showOptionDialog(null, panelSqlRequest,
                "Copier/coller le résultat de la requête SQL", MyJOptionPane.OK_CANCEL_OPTION,
                MyJOptionPane.QUESTION_MESSAGE, null, null, panelSqlRequest);
        if (option == MyJOptionPane.OK_OPTION) {
            return panelSqlRequest.getResults();
        }
        return null;
    }

    @Override
    public void actionLoadBddMessageByChrono() {

        this.message = null;
        final String chrono = this.tfSearchChrono.getText();
        this.processTextArea.setText("Recherche du message en base de donnée...");

        this.tAListRubriques.setText(null);
        this.tfSearchChrono.setEnabled(false);
        this.waitEndAction();

        SwingWorker<MessageDsn, Void> worker = new SwingWorker<MessageDsn, Void>() {

            String errorMessage = null;

            @Override
            protected MessageDsn doInBackground() throws Exception {

                try {
                    message = messageDao.getMessageDsn(new Long(chrono));

                } catch (SQLException e) {
                    errorMessage = "Exception: " + e.getMessage();
                }
                return message;

            }

            @Override
            protected void done() {
                processTextArea.append(RC);
                processTextArea.append(String.format(message == null ? MESS_MSG_NOT_FOUND : MESS_MSG_FOUND, chrono));
                processTextArea.append(RC);
                processTextArea.append(errorMessage == null ? "" : errorMessage);
                populateMessageInfos(message);
                poursuivreAction();
            }

            private void poursuivreAction() {

                if (message != null) {
                    loadDatasForMessage(chrono);
                } else {
                    searchActionEnded(false);
                }
            }

        };
        worker.execute();

    }

    private boolean showOpenFileDialogAndExtractDatas() throws Exception {

        if (this.currentDirectory != null) {
            fc.setCurrentDirectory(this.currentDirectory);
        }
        int returnVal = fc.showOpenDialog(this);

        processTextArea.setText(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            this.currentDirectory = file.getParentFile();
            return this.extractDatasFromFile(file);

        } else {

            processTextArea.setText("Open command cancelled by user.".concat(SAUT_LIGNE));
            return false;
        }
    }

    private boolean extractDatasFromFile(File file) throws Exception {

        this.message = new MessageDsn();
        this.message.setName(file.getName());

        this.listDatas = ServiceFactory.getReadDatasFromSqlRequestService().buildListDatasFromSqlRequest(file);
        this.populateMessageInfos(message);
        this.populateMessageDatas();
        return listDatas != null && !listDatas.isEmpty();

    }

    private void loadDatasForMessage(final String chrono) {
        this.listDatas = null;
        SwingWorker<List<DataDsn>, Void> worker = new SwingWorker<List<DataDsn>, Void>() {

            String errorMessage = null;

            @Override
            protected List<DataDsn> doInBackground() throws Exception {

                try {
                    listDatas = datasDao.getListDataDsnForMessage(new Long(chrono));
                } catch (SQLException e) {
                    errorMessage = "Exception: " + e.getMessage();
                }
                return listDatas;
            }

            @Override
            protected void done() {
                processTextArea.append(RC);
                processTextArea.append(errorMessage == null ? "" : errorMessage);
                populateMessageDatas();
                searchActionEnded(listDatas != null);
            }

        };
        worker.execute();

    }

    // -------------------------------------------- constructor
    public JdbcFrame(IMainActionListener listener) {
        super("Ouvrir un message DSN en base de données", JFrame.HIDE_ON_CLOSE);
        this.mainActionListener = listener;

        // Set up the content pane.
        addComponentsToPane(this.getContentPane());
        this.manageSaisieEnCours();
    }

    // -------------------------------------------- private methods
    private void addComponentsToPane(Container pane) {
        this.setSize(300, 300);
        pane.setLayout(new BorderLayout());

        createPanelTop(pane, BorderLayout.PAGE_START);
        createSplitPanel(pane, createPanelMessage(), createPanelConnexion(), BorderLayout.CENTER, 600);
        createTextArea(pane, BorderLayout.PAGE_END);
    }

    private void createPanelTop(Container container, String layout) {
        JPanel panelTop = new JPanel(new BorderLayout());

        this.createPanelSearchChrono(panelTop, BorderLayout.LINE_START);
        this.createPanelButton(panelTop, BorderLayout.CENTER);
        container.add(panelTop, layout);

    }

    private void createPanelSearchChrono(Container container, String layout) {

        JPanel panelSearch = new JPanel();
        panelSearch.setLayout(new BoxLayout(panelSearch, BoxLayout.X_AXIS));
        panelSearch.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panelSearch.add(new JLabel("numéro chrono (num): "));
        panelSearch.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));

        this.createTextFieldChrono(panelSearch, null);
        container.add(panelSearch, layout);
    }

    private void createTextFieldChrono(Container container, String layout) {

        this.tfSearchChrono = new StateTextField(20);
        this.tfSearchChrono.setMaximumSize(new Dimension(200, 20));
        this.tfSearchChrono.getDocument().addDocumentListener(this);
        this.tfSearchChrono.setEnabled(false);
        ((AbstractDocument) this.tfSearchChrono.getDocument()).setDocumentFilter(new PatternFilter(PATTERN_NUM_CHRONO));

        InputMap im = this.tfSearchChrono.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.tfSearchChrono.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), FOCUS_SEARCH_ACTION);
        am.put(FOCUS_SEARCH_ACTION, new FocusSearchChronoAction(this));

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), LIRE_BDD_MSG_ACTION);
        am.put(LIRE_BDD_MSG_ACTION, new LoadBddMsgAction(this));

        container.add(this.tfSearchChrono);
    }

    private void createPanelButton(Container container, String layout) {

        JPanel panelButton = new JPanel();
        this.createButtonChargerMessage(panelButton, BorderLayout.CENTER);
        this.createButtonEditTreeMessage(panelButton, BorderLayout.CENTER);
        this.createButtonLoadSqlFile(panelButton, BorderLayout.CENTER);
        this.createButtonLoadSqlRequest(panelButton, BorderLayout.CENTER);
        container.add(panelButton, layout);
    }

    private JComponent createPanelConnexion() {

        this.panelConnexion = new MyPanelConnexion(this, this);
        return this.panelConnexion;
    }

    private JComponent createPanelMessage() {
        JPanel panelMessage = new JPanel();
        panelMessage.setLayout(new BorderLayout());

        this.createEnteteMessagePanel(panelMessage, BorderLayout.PAGE_START);
        this.createBodyMessagePanel(panelMessage, BorderLayout.CENTER);

        return panelMessage;
    }

    private void createEnteteMessagePanel(Container container, String layout) {
        JPanel panelEnteteMess = new JPanel();
        panelEnteteMess.setLayout(new BoxLayout(panelEnteteMess, BoxLayout.Y_AXIS));
        panelEnteteMess.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.mapKeyToMessageInfo.put(KEY_INFO_CHRONO, new PanelMessageInfo("chrono message"));
        this.mapKeyToMessageInfo.put(KEY_INFO_DATE, new PanelMessageInfo("date de déclaration"));
        this.mapKeyToMessageInfo.put(KEY_INFO_NAME, new PanelMessageInfo("fichier"));

        panelEnteteMess.add(mapKeyToMessageInfo.get(KEY_INFO_CHRONO));
        panelEnteteMess.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
        panelEnteteMess.add(mapKeyToMessageInfo.get(KEY_INFO_DATE));
        panelEnteteMess.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
        panelEnteteMess.add(mapKeyToMessageInfo.get(KEY_INFO_NAME));
        panelEnteteMess.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));

        container.add(panelEnteteMess, layout);
    }

    private void createBodyMessagePanel(Container container, String layout) {
        this.tAListRubriques = new JTextArea(5, 800);
        this.tAListRubriques.setEditable(false);
        this.tAListRubriques.setMargin(new Insets(10, 10, 10, 10));
        this.tAListRubriques.setBackground(TREE_BACKGROUND_COLOR);
        this.tAListRubriques.setForeground(TREE_NORMAL_COLOR);

        JScrollPane scrollPane = new JScrollPane(tAListRubriques);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scrollPane, layout);
    }

    private void createButtonEditTreeMessage(Container container, String layout) {

        this.btEditerMsg = new StateButton();
        GuiUtils.createButton(btEditerMsg, new EditBddMsgAction(this), EDIT_BDD_MSG_ACTION, KeyEvent.VK_E,
                PATH_EDIT_ICO, "Editer le message DSN", "Editer le message DSN sous forme arborescente.", false,
                container, layout);
    }

    private void createButtonLoadSqlFile(Container container, String layout) {

        this.btSqlFile = new StateButton();
        GuiUtils.createButton(btSqlFile, new LoadSqlFileAction(this), LOAD_SQL_FILE_MSG_ACTION, KeyEvent.VK_S,
                PATH_SQL_FILE_ICO, "Charger fichier SQL", "Charger un message depuis une fichier de résultat SQL.",
                true, container, layout);
        this.btSqlFile.setVisible(false);
    }

    private void createButtonLoadSqlRequest(Container container, String layout) {

        this.btSqlRequest = new StateButton();
        GuiUtils.createButton(btSqlRequest, new LoadSqlRequestAction(this), LOAD_SQL_REQUEST_MSG_ACTION, KeyEvent.VK_R,
                PATH_SQL_REQUEST_ICO, "Charger résultats SQL", "Charger un message depuis une requête SQL.", true,
                container, layout);
    }

    private void createButtonChargerMessage(Container container, String layout) {

        btChargerMsg = new StateButton();
        GuiUtils.createButton(btChargerMsg, new LoadBddMsgAction(this), LIRE_BDD_MSG_ACTION, KeyEvent.VK_C,
                PATH_BDD_ICO, "Charger un message DSN", "Charger un message DSN depuis la base de données.", false,
                container, layout);
    }

    private void manageSaisieEnCours() {

        if (this.panelConnexion.getConnexionState() == ConnexionState.Ok) {
            String search = this.tfSearchChrono.getText();
            int searchLenght = search != null ? search.length() : 0;

            this.btChargerMsg.setEnabled(searchLenght > 0);
        } else {
            this.btChargerMsg.setEnabled(false);
        }
    }

    private void waitEndAction() {

        this.setCursor(WaitingCursor);

        this.btChargerMsg.waitEndAction();
        this.btEditerMsg.waitEndAction();
        this.btSqlFile.waitEndAction();
        this.btSqlRequest.waitEndAction();

        this.tfSearchChrono.waitEndAction();
        panelConnexion.waitEndAction();
    }

    private void currentActionEnded() {

        this.btChargerMsg.actionEnded();
        this.btEditerMsg.actionEnded();
        this.btSqlFile.actionEnded();
        this.btSqlRequest.actionEnded();

        this.tfSearchChrono.actionEnded();
        panelConnexion.actionEnded();
        this.setCursor(Cursor.getDefaultCursor());
        this.tfSearchChrono.requestFocus();
    }

    private void searchActionEnded(boolean success) {
        this.currentActionEnded();
        this.tfSearchChrono.setEnabled(true);
        this.btEditerMsg.setEnabled(success);
        this.setFocusOnSearch();

    }

    private void populateMessageInfos(MessageDsn message) {

        if (message != null) {
            this.mapKeyToMessageInfo.get(KEY_INFO_CHRONO)
                    .setValue(message.getNumeroChronoMessage() == null ? null : message.getNumeroChronoMessage() + "");
            this.mapKeyToMessageInfo.get(KEY_INFO_DATE).setValue(message.getDateReferenceDeclaration() == null ? null
                    : DATE_FORMAT.format(message.getDateReferenceDeclaration()));
            this.mapKeyToMessageInfo.get(KEY_INFO_NAME).setValue(message.getName() == null ? null : message.getName());

        }
    }

    private void populateMessageDatas() {

        if (this.listDatas == null || this.listDatas.isEmpty()) {
            this.processTextArea.append("la liste des données est vide!");
            return;
        }
        this.listDatas.stream().forEachOrdered(dataDSN -> {
            this.tAListRubriques.append(RC);
            this.tAListRubriques.append(dataDSN.toString());
        });
    }

    // ============================================== INNER CLASS
    private class PanelMessageInfo extends LabelAndTextField {
        private static final long serialVersionUID = 1L;

        private PanelMessageInfo(String text) {
            super(text, 120, 400, false);
        }
    }

    private static final String SQL_CHRONO = "select\n" +

            " DSDOCHRMSG as chronoMess,\n" + // ...
            " DSDOIDFSYS as id,\n" + // ...
            " DSDOBLCRAT as bloc,\n" + // ...
            " DSDOSEQBLC as seq_bloc,\n" + // ...
            " DSDOBLCSUP as bloc_sup,\n" + // ...
            " DSDOSEQSUP as seq_sup,\n" + // ...
            " DSDOCODRUB as codeRubrique,\n" + // ...
            " DSDOVALRUB as value\n" + // ...

            " from DSDONNEESCL data\n" + // ...

            " where DSDOCHRMSG = <chrono>\n" + // ...
            " order by  bloc, seq_bloc, seq_sup, codeRubrique;"; // ...

    private static final class PanelSqlRequest extends JPanel {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private final JTextArea taSql = new JTextArea(5, 50);
        private final JTextArea taResult = new JTextArea(10, 100);

        // ------------------------------------- constructor
        private PanelSqlRequest(String sql) {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            this.assemblageComposants();

            this.taSql.setText(sql);
            this.taResult.requestFocusInWindow();
        }

        private String getResults() {
            return this.taResult.getText();
        }

        private void assemblageComposants() {

            this.add(new JLabel("Requête SQL à exécuter dans SQL Developper (alimenter le numéro chrono):"));
            this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
            this.createSqlTextArea(this, BorderLayout.CENTER);

            this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
            this.add(new JLabel("Resultat à copier depuis SQL Developper:"));
            this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
            this.createResultTextArea(this, BorderLayout.CENTER);
        }

        private void createSqlTextArea(Container container, String layout) {

            taSql.setEditable(false);
            taSql.setMargin(new Insets(10, 10, 10, 10));
            container.add(taSql, layout);
        }

        public void addNotify() {
            super.addNotify();
            this.taResult.requestFocus();
        }

        private void createResultTextArea(Container container, String layout) {

            taResult.setEditable(true);
            taResult.setMargin(new Insets(10, 10, 10, 10));

            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(taResult);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

            container.add(scrollPane, layout);
        }

    }



}
