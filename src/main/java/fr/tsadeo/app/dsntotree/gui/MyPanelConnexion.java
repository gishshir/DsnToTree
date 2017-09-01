package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.tsadeo.app.dsntotree.bdd.dao.DatabaseManager;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.gui.action.TesterBddAction;
import fr.tsadeo.app.dsntotree.gui.bdd.ConnexionState;
import fr.tsadeo.app.dsntotree.gui.bdd.OracleConnectComponent;
import fr.tsadeo.app.dsntotree.gui.component.IStateComponent;
import fr.tsadeo.app.dsntotree.gui.component.LabelAndTextField;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;

public class MyPanelConnexion extends JPanel implements IGuiConstants, DocumentListener, IStateComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private OracleConnectComponent oracleConnectComponent;
    private LabelAndTextField ltfUser, ltfPwd;
    private StateButton btTester;
    private JLabel labTestOk, labTestNok, labNoTest;

    private final IBddActionListener listener;
    private ConnexionState connexionState = ConnexionState.Unknown;

    // ----------------------------------------------- constructor
    MyPanelConnexion(IBddActionListener listener) {

        this.listener = listener;
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.createTitlePanel(this, BorderLayout.PAGE_START);
        this.createCenterPanel(this, BorderLayout.CENTER);

        this.loadDefaultBddConnexion();
    }

    // ----------------------------------- implementing DocumentListener
    @Override
    public void insertUpdate(DocumentEvent e) {
        this.controleSaisieEnCours();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.controleSaisieEnCours();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    // ----------------------------------------------- package methods
    ConnexionState getConnexionState() {
        return this.connexionState;
    }

    BddConnexionDto getBddConnexionDto() {

        return new BddConnexionDto(this.oracleConnectComponent.getConnexionManager().getDriver(), 
        		this.oracleConnectComponent.getUrl(),
                this.ltfUser.getValue(), this.ltfPwd.getValue());
    }

    void setBddConnexionStatus(ConnexionState connexionState) {

        this.labTestNok.setVisible(connexionState == ConnexionState.Nok);
        this.labTestOk.setVisible(connexionState == ConnexionState.Ok);
        this.labNoTest.setVisible(connexionState == ConnexionState.Unknown);
        this.connexionState = connexionState;
    }

    // -------------------------------------- implementing IStateComponent
    @Override
    public void waitEndAction() {

        this.oracleConnectComponent.waitEndAction();
        this.ltfPwd.waitEndAction();
        this.ltfUser.waitEndAction();
        this.btTester.waitEndAction();
    }

    @Override
    public void actionEnded() {
        this.oracleConnectComponent.actionEnded();
        this.ltfPwd.actionEnded();
        this.ltfUser.actionEnded();
        this.btTester.actionEnded();
    }

    // ----------------------------------------------- private methods
    private void loadDefaultBddConnexion() {

        BddConnexionDto bddConnexionDto = DatabaseManager.get().getDefaultBddConnexion();
        this.oracleConnectComponent.setBddConnexionDto(bddConnexionDto);

        this.ltfUser.setValue(bddConnexionDto.getUser());
        this.ltfPwd.setValue(bddConnexionDto.getPwd());

    }

    private void controleSaisieEnCours() {

        boolean enable = false;
        if (!this.oracleConnectComponent.isUrlEmpty() && !this.ltfUser.isEmpty() && !this.ltfPwd.isEmpty()) {
            enable = true;
        }
        this.btTester.setEnabled(enable);
    }

    private void createTitlePanel(Container container, String layout) {

        JPanel panelTitle = new JPanel();
        panelTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelTitle.setLayout(new BoxLayout(panelTitle, BoxLayout.X_AXIS));

        this.labTestOk = this.createLabelMark(panelTitle, PATH_CONNEXION_OK_ICO, false);
        this.labTestNok = this.createLabelMark(panelTitle, PATH_CONNEXION_NOK_ICO, false);
        this.labNoTest = this.createLabelMark(panelTitle, PATH_CONNEXION_UNKNONW_ICO, true);

        panelTitle.add(new JLabel("BDD connexion"));
        panelTitle.add(Box.createRigidArea(DIM_VER_RIGID_AREA_15));

        container.add(panelTitle, layout);
    }

    private JLabel createLabelMark(Container container, String iconPath, boolean visible) {

        JLabel labMark = new JLabel();
        labMark.setVisible(visible);
        labMark.setIcon(GuiUtils.createImageIcon(iconPath));
        container.add(labMark);
        container.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));
        return labMark;
    }

    private void createCenterPanel(Container container, String layout) {

        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.Y_AXIS));

        this.createUrlConnexionComponent(panelCenter, null);
        panelCenter.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
        this.createCredentialPanel(panelCenter, null);
        panelCenter.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
        this.createButtonPanel(panelCenter, null);
        panelCenter.add(Box.createVerticalGlue());

        container.add(panelCenter, layout);
    }

    private void createUrlConnexionComponent(Container container, String layout) {

        this.oracleConnectComponent = new OracleConnectComponent();
        this.oracleConnectComponent.setDocumentListener(this);
        container.add(this.oracleConnectComponent, layout);
    }

    private void createCredentialPanel(Container container, String layout) {

        JPanel panelCredential = new JPanel();
        panelCredential.setLayout(new BoxLayout(panelCredential, BoxLayout.Y_AXIS));
        panelCredential.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        this.ltfUser = new LabelAndTextField("User:", 100, 100);
        this.ltfUser.setDocumentListener(this);
        panelCredential.add(this.ltfUser);
        panelCredential.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));

        this.ltfPwd = new LabelAndTextField("Pwd:", 100, 100);
        this.ltfPwd.setDocumentListener(this);
        panelCredential.add(this.ltfPwd);

        container.add(panelCredential, layout);

    }

    private void createButtonPanel(Container container, String layout) {

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));

        panelButtons.add(Box.createHorizontalGlue());

        this.btTester = new StateButton();
        GuiUtils.createButton(this.btTester, new TesterBddAction(this.listener), TESTER_BDD_ACTION, KeyEvent.VK_T,
                PATH_TEST_CONNEXION_ICO, "Tester la connexion", "Tester la connexion à la base de données.", false,
                panelButtons, null);

        container.add(panelButtons, layout);
    }

}
