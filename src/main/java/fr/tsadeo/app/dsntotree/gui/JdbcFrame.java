package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import com.sun.glass.events.KeyEvent;

import fr.tsadeo.app.dsntotree.bdd.dao.DatabaseManager;
import fr.tsadeo.app.dsntotree.bdd.dao.IDataDsnDao;
import fr.tsadeo.app.dsntotree.bdd.dao.IMessageDsnDao;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.JdbcDataDsnDao;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.JdbcMessageDsnDao;
import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;
import fr.tsadeo.app.dsntotree.bdd.model.MessageDsn;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.dto.LinkedPropertiesDto;
import fr.tsadeo.app.dsntotree.gui.action.EditBddMsgAction;
import fr.tsadeo.app.dsntotree.gui.action.FocusSearchChronoAction;
import fr.tsadeo.app.dsntotree.gui.action.LireBddMsgAction;
import fr.tsadeo.app.dsntotree.gui.action.PatternFilter;
import fr.tsadeo.app.dsntotree.gui.bdd.ConnexionState;
import fr.tsadeo.app.dsntotree.gui.component.LabelAndTextField;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;
import fr.tsadeo.app.dsntotree.gui.component.StateTextField;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;

public class JdbcFrame extends AbstractFrame implements IBddActionListener, DocumentListener {

    private static final long serialVersionUID = 1L;

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
    private StateButton btChargerMsg, btEditerMsg;
    private StateTextField tfSearchChrono;
    private JTextArea tAListRubriques;

    private MessageDsn message = null;
    private List<DataDsn> listDatas = null;

    private final IMainActionListener mainActionListener;
    // -------------------------------------- implementing DocumentListener

    @Override
    public void insertUpdate(DocumentEvent e) {
        saisieEnCours();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        saisieEnCours();
    }

    // -------------------------------- implementing IBddActionListener
    @Override
    public void setFocusOnSearch() {
        if (this.tfSearchChrono != null) {
            this.tfSearchChrono.requestFocusInWindow();
        }
    }

    @Override
    public void actionTesterConnexion() {

        textArea.setText("Test de connexion à la base de données...");
        panelConnexion.setBddConnexionStatus(ConnexionState.Unknown);
        final BddConnexionDto connexionDto = this.panelConnexion.getBddConnexionDto();
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

            boolean test = false;

            @Override
            protected Boolean doInBackground() throws Exception {

                waitEndAction();
                this.test = DatabaseManager.get().testerConnexion(connexionDto);
                return this.test;
            }

            @Override
            protected void done() {
                panelConnexion.setBddConnexionStatus(test ? ConnexionState.Ok : ConnexionState.Nok);
                textArea.append(RC);
                textArea.append(test ? "OK" : "Echec");
                textArea.append(RC);
                saisieEnCours();
                currentActionEnded();
            }
        };
        worker.execute();
    }

    @Override
    public void actionEditMessageInTree() {

        if (this.message != null && this.listDatas != null) {

            Dsn dsn = ServiceFactory.getReadDsnService().buildTreeFromDatas(this.message.getName(), listDatas);

            this.mainActionListener.actionShowDsnTreeWithConfirmation(dsn);
        }
    }

    private String PREFIX = "S21.G00.";

    private LinkedPropertiesDto buildPropertiesFromDatas() {

        LinkedPropertiesDto properties = new LinkedPropertiesDto();
        if (this.listDatas != null) {
            for (DataDsn dataDsn : listDatas) {
                properties.setProperty(PREFIX.concat(dataDsn.getCodeRubrique()), dataDsn.getValue());
            }
        }
        return properties;
    }

    @Override
    public void actionLoadMessageByChrono() {

        this.message = null;
        final String chrono = this.tfSearchChrono.getText();
        this.textArea.setText("Recherche du message en base de donnée...");

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
                textArea.append(RC);
                textArea.append(String.format(message == null ? MESS_MSG_NOT_FOUND : MESS_MSG_FOUND, chrono));
                textArea.append(RC);
                textArea.append(errorMessage == null ? "" : errorMessage);
                populateMessageInfos(message);
                poursuivreAction();
            }

            private void poursuivreAction() {

                if (message != null) {
                    actionLoadDatasForMessage(chrono);
                } else {
                    searchActionEnded(false);
                }
            }

        };
        worker.execute();

    }

    private void actionLoadDatasForMessage(final String chrono) {
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
                textArea.append(RC);
                textArea.append(String.format(listDatas == null ? "datas not found" : "datas found", chrono));
                textArea.append(RC);
                textArea.append(errorMessage == null ? "" : errorMessage);
                populateMessageDatas(listDatas);
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

        this.tfSearchChrono = new StateTextField(20);
        this.tfSearchChrono.setMaximumSize(new Dimension(200, 20));
        this.tfSearchChrono.getDocument().addDocumentListener(this);
        ((AbstractDocument) this.tfSearchChrono.getDocument()).setDocumentFilter(new PatternFilter(PATTERN_NUM_CHRONO));

        InputMap im = this.tfSearchChrono.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.tfSearchChrono.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), FOCUS_SEARCH_ACTION);
        am.put(FOCUS_SEARCH_ACTION, new FocusSearchChronoAction(this));

        panelSearch.add(this.tfSearchChrono);
        container.add(panelSearch, layout);
    }

    private void createPanelButton(Container container, String layout) {

        JPanel panelButton = new JPanel();
        this.createButtonChargerMessage(panelButton, BorderLayout.CENTER);
        this.createButtonEditerMessage(panelButton, BorderLayout.CENTER);

        container.add(panelButton, layout);
    }

    private JComponent createPanelConnexion() {

        this.panelConnexion = new MyPanelConnexion(this);
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

        JScrollPane scrollPane = new JScrollPane(tAListRubriques);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scrollPane, layout);
    }

    private void createButtonEditerMessage(Container container, String layout) {

        this.btEditerMsg = new StateButton();
        GuiUtils.createButton(btEditerMsg, new EditBddMsgAction(this), EDIT_BDD_MSG_ACTION, KeyEvent.VK_E,
                PATH_EDIT_ICO, "Editer le message DSN", "Editer le message DSN sous forme arborescente.", false,
                container, layout);
    }

    private void createButtonChargerMessage(Container container, String layout) {

        btChargerMsg = new StateButton();
        GuiUtils.createButton(btChargerMsg, new LireBddMsgAction(this), LIRE_BDD_MSG_ACTION, KeyEvent.VK_C,
                PATH_BDD_ICO, "Charger un message DSN", "Charger un message DSN depuis la base de données.", false,
                container, layout);
    }

    private void saisieEnCours() {

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

        this.tfSearchChrono.waitEndAction();
        panelConnexion.waitEndAction();
    }

    private void currentActionEnded() {

        this.btChargerMsg.actionEnded();
        this.btEditerMsg.actionEnded();

        this.tfSearchChrono.actionEnded();
        panelConnexion.actionEnded();
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void searchActionEnded(boolean success) {
        this.currentActionEnded();
        this.tfSearchChrono.setEnabled(true);
        this.btEditerMsg.setEnabled(success);
        this.setFocusOnSearch();

    }

    private void populateMessageInfos(MessageDsn message) {

        this.mapKeyToMessageInfo.get(KEY_INFO_CHRONO)
                .setValue(message == null ? null : message.getNumeroChronoMessage() + "");
        this.mapKeyToMessageInfo.get(KEY_INFO_DATE)
                .setValue(message == null ? null : DATE_FORMAT.format(message.getDateReferenceDeclaration()));
        this.mapKeyToMessageInfo.get(KEY_INFO_NAME).setValue(message == null ? null : message.getName());
    }

    private void populateMessageDatas(List<DataDsn> listDatas) {
        this.tAListRubriques.setText("liste des rubriques.....");

        for (DataDsn dataDSN : listDatas) {
            this.tAListRubriques.append(RC);
            this.tAListRubriques.append(dataDSN.getCodeRubrique());
            this.tAListRubriques.append(TAB);
            this.tAListRubriques.append(SEP);
            this.tAListRubriques.append(TAB);
            this.tAListRubriques.append(dataDSN.getValue());
        }
    }

    // ============================================== INNER CLASS
    private class PanelMessageInfo extends LabelAndTextField {
        private static final long serialVersionUID = 1L;

        private PanelMessageInfo(String text) {
            super(text, 120, 300, false);
        }
    }

}
