package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.tsadeo.app.dsntotree.bdd.dao.DatabaseManager;
import fr.tsadeo.app.dsntotree.dico.KeyAndLibelle;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.gui.action.FocusSearchInstanceAction;
import fr.tsadeo.app.dsntotree.gui.action.TesterBddAction;
import fr.tsadeo.app.dsntotree.gui.bdd.ConnexionState;
import fr.tsadeo.app.dsntotree.gui.bdd.IBddConnectionListener;
import fr.tsadeo.app.dsntotree.gui.bdd.OracleConnectComponent;
import fr.tsadeo.app.dsntotree.gui.component.IStateComponent;
import fr.tsadeo.app.dsntotree.gui.component.LabelAndTextField;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;
import fr.tsadeo.app.dsntotree.gui.component.StateListBox;

public class MyPanelConnexion extends JPanel implements IGuiConstants, DocumentListener, IStateComponent,
IBddInstanceListener{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private OracleConnectComponent oracleConnectComponent;
    private LabelAndTextField ltfUser, ltfPwd;
    private JScrollPane spUsers;
    private StateListBox lUsers;
    private StateButton btTester;
    private JLabel labTestOk, labTestNok, labNoTest;

    private final IBddActionListener listener;
    private final IBddConnectionListener bddConnectionListener;
    private ConnexionState connexionState = ConnexionState.Unknown;


    // ----------------------------------------------- constructor
    MyPanelConnexion(IBddActionListener listener, IBddConnectionListener bddConnectionListener) {

        this.listener = listener;
        this.bddConnectionListener = bddConnectionListener;
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.createTitlePanel(this, BorderLayout.PAGE_START);
        this.createCenterPanel(this, BorderLayout.CENTER);
        
        this.loadDefaultBddConnexion();

    }

    // ----------------------------------- implementing IBddInstanceListener
	@Override
	public void userChanged(String instance, String user, String pwd) {
		 this.bddConnectionListener.connectionState(ConnexionState.Unknown);
		 
		 this.ltfUser.setValue(user);
         this.ltfPwd.setValue(pwd);
	}
	@Override
	public void setFocusOnSearch() {
		 if (this.lUsers != null) {
	            this.lUsers.requestFocusInWindow();
	        }	
		 }


    @Override
    public void instanceChanged(String instance) {

    	this.bddConnectionListener.connectionState(ConnexionState.Unknown);

        if (this.oracleConnectComponent != null && this.oracleConnectComponent.getConnexionManager() != null) {
            List<BddConnexionDto> listdto = this.oracleConnectComponent.getConnexionManager()
                    .getListBddConnexionDto(instance);

            if (listdto != null && !listdto.isEmpty()) {
                this.ltfUser.setValue(listdto.get(0).getUser());
                this.ltfPwd.setValue(listdto.get(0).getPwd());
                this.lUsers.populateListBox(this.mapToListKeyAndLibelle(listdto));
                this.lUsers.setSelectedIndex(0);
                this.lUsers.setVisible(true);
            } else {
            	this.lUsers.setVisible(false);
            	this.lUsers.removeAllElements();
                this.ltfUser.setValue(null);
                this.ltfPwd.setValue(null);
                
            }
        }
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
        this.lUsers.waitEndAction();
    }

    @Override
    public void actionEnded() {
        this.oracleConnectComponent.actionEnded();
        this.ltfPwd.actionEnded();
        this.ltfUser.actionEnded();
        this.btTester.actionEnded();
        this.lUsers.actionEnded();
    }

    // ----------------------------------------------- private methods
    private List<KeyAndLibelle> mapToListKeyAndLibelle(List<BddConnexionDto> listDto) {
    	List<KeyAndLibelle>  listUserAndPwd = new ArrayList<>(listDto == null?0:listDto.size());
    	if (listDto != null) {
    		for (BddConnexionDto dto : listDto) {
				listUserAndPwd.add(new KeyAndLibelle(dto.getUser(), dto.getPwd(), false));
			}
    	}
    	return listUserAndPwd;
    }
    private void loadDefaultBddConnexion() {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				oracleConnectComponent.activateSearchComboBox();

				BddConnexionDto bddConnexionDto = DatabaseManager.get().getDefaultBddConnexionDto();
				if (bddConnexionDto != null) {
					oracleConnectComponent.setBddConnexionDto(bddConnexionDto);

					ltfUser.setValue(bddConnexionDto.getUser());
					ltfPwd.setValue(bddConnexionDto.getPwd());
				}
			}

		});

    }
    private ListSelectionListener listSelectionListener;
    private ListSelectionListener buildListSelectionListener() {
    	
    	if (this.listSelectionListener == null) {
    		this.listSelectionListener = new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					// TODO Auto-generated method stub
					if (e.getValueIsAdjusting() == false) {

				        if (lUsers.getSelectedIndex() > -1) {
				        	KeyAndLibelle selectedUser = lUsers.getSelectedValue();
				        	MyPanelConnexion.this.userChanged(null, selectedUser.getKey(), selectedUser.getLibelle());
				        }
					}
				}
			};
    	}
    	return this.listSelectionListener;
    }

    private void controleSaisieEnCours() {

        boolean enable = false;
        this.bddConnectionListener.connectionState(ConnexionState.Unknown);
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
        panelCenter.setAlignmentX(LEFT_ALIGNMENT);
        panelCenter.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
        this.createButtonPanel(panelCenter, null);
        panelCenter.add(Box.createVerticalGlue());

        container.add(panelCenter, layout);
    }

    private void createUrlConnexionComponent(Container container, String layout) {

        this.oracleConnectComponent = new OracleConnectComponent(this);
        this.oracleConnectComponent.setDocumentListener(this);
        container.add(this.oracleConnectComponent, layout);
    }

    private void createCredentialPanel(Container container, String layout) {

        JPanel panelCredential = new JPanel();
        panelCredential.setLayout(new GridBagLayout());
        panelCredential.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));


        // user and password
        this.ltfUser = new LabelAndTextField("User:", 50, 100);
        this.ltfUser.setDocumentListener(this);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        panelCredential.add(this.ltfUser, c);
        
        this.ltfPwd = new LabelAndTextField("Pwd:", 50, 100);
        this.ltfPwd.setDocumentListener(this);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        panelCredential.add(this.ltfPwd, c);

        // list user possible
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 20, 0, 20);
        panelCredential.add(this.createListUsers(), c);

        container.add(panelCredential, layout);

    }
    
    private JComponent createListUsers() {
    	
    	// list des users references pour l'instance en cours
    	this.lUsers = new StateListBox();
        this.lUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.lUsers.setLayoutOrientation(JList.VERTICAL);
        this.lUsers.setVisibleRowCount(5);
        this.lUsers.addListSelectionListener(this.buildListSelectionListener());
        
        InputMap im = this.lUsers.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.lUsers.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK), FOCUS_SEARCH_INSTANCE);
        am.put(FOCUS_SEARCH_INSTANCE, new FocusSearchInstanceAction(this));
        
        this.spUsers = new JScrollPane(this.lUsers);
        spUsers.setPreferredSize(new Dimension(150, 100));
        
        return this.spUsers;
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
