package fr.tsadeo.app.dsntotree.gui.bdd;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentListener;

import fr.tsadeo.app.dsntotree.bdd.dao.BddAccessManagerFactory;
import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager;
import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager.Type;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.OracleBddAccessManager;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.OracleBddAccessManager.UrlParametersDto;
import fr.tsadeo.app.dsntotree.dico.KeyAndLibelle;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.dto.TnsOracleInstanceDto;
import fr.tsadeo.app.dsntotree.gui.IBddInstanceListener;
import fr.tsadeo.app.dsntotree.gui.IGuiConstants;
import fr.tsadeo.app.dsntotree.gui.component.IStateComponent;
import fr.tsadeo.app.dsntotree.gui.component.LabelAndTextField;
import fr.tsadeo.app.dsntotree.gui.component.StateComboBox;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.service.TnsNameOraService;

public class OracleConnectComponent extends JPanel 
implements IConnectComponent, IGuiConstants, IStateComponent, ActionListener
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private TnsNameOraService service = ServiceFactory.getTnsNameOraService();
    private final IBddInstanceListener listener;

    private LabelAndTextField pfHost, pfPort, pfInstance;
    private StateComboBox<KeyAndLibelle> cbSearchInstance;
    private boolean listInstanceReady = false;


    //---------------------------------------------------- implementing ActionLIstener
	@Override
	public void actionPerformed(ActionEvent e) {
		if (listInstanceReady) {

			Object item = this.cbSearchInstance.getSelectedItem();

				this.populateWithSelectedInstance((KeyAndLibelle) item);
				this.listener.instanceChanged();
		} else {
			this.listener.listInstanceReady();
			listInstanceReady = true;
		}
	}

    // ------------------------------------- Overriding IStateComponent
    @Override
    public void waitEndAction() {
        this.pfHost.waitEndAction();
        this.pfPort.waitEndAction();
        this.pfInstance.waitEndAction();
        this.cbSearchInstance.waitEndAction();
    }

    @Override
    public void actionEnded() {
        this.pfHost.actionEnded();
        this.pfPort.actionEnded();
        this.pfInstance.actionEnded();
        this.cbSearchInstance.actionEnded();
    }

    // ----------------------------------------- overriding IConnectComponent
    @Override
    public void setBddConnexionDto(BddConnexionDto connexionDto) {

    	UrlParametersDto dto = this.getOracleConnectionManager().getUrlParameters(connexionDto.getUrl());
        this.setValues(dto.getHost(), dto.getPort(), dto.getInstance());
    }

    
    @Override
    public IBddAccessManager getConnexionManager() {
    	return this.getOracleConnectionManager();
    }

    @Override
    public String getUrl() {
        if (this.isUrlEmpty()) {
            return null;
        }
        
        return this.getOracleConnectionManager().getUrl(this.pfHost.getValue(), this.pfPort.getIntValue(), this.pfInstance.getValue());
    }

    @Override
    public boolean isUrlEmpty() {

        return (this.pfHost.isEmpty() || this.pfPort.isEmpty() || this.pfInstance.isEmpty());
    }

    @Override
    public void setDocumentListener(DocumentListener documentListener) {

        if (this.pfHost != null) {
            this.pfHost.setDocumentListener(documentListener);
        }
        if (this.pfPort != null) {
            this.pfPort.setDocumentListener(documentListener);
        }
        if (this.pfInstance != null) {
            this.pfInstance.setDocumentListener(documentListener);
        }
    }

    // ---------------------------------------- constructor
    public OracleConnectComponent(IBddInstanceListener listener) {

    	this.listener = listener;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        this.createSearchInstancePanel(this);
        this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
        this.createPanelTextField(this);
        
        this.populateSearchComboBox();
       
    }

    // ---------------------------------------- private methods
    
    private void setValues(String host, String port, String instance) {
    	this.pfHost.setValue(host);
        this.pfPort.setValue(port);
        this.pfInstance.setValue(instance);
    }
    
    private void clearValues() {
    	this.setValues(null, null, null);
    }

    private void filterListOfInstance(String search) {
    	System.out.println("search " + search);
//    	this.cbS
    	
    	
    	cbSearchInstance.showPopup();
    }
    private void populateWithSelectedInstance(KeyAndLibelle keyAndLibelle) {
    	
    	 
    	TnsOracleInstanceDto dto = keyAndLibelle == null?null: service.getInstance(keyAndLibelle.getKey());
    	if (dto != null) {
    		this.setValues(dto.getHost(), Integer.toString(dto.getPort()), dto.getService());	
    	} else {
    		this.clearValues();
    	}
    	
    }
    private void populateSearchComboBox() {
    	
    	SwingWorker<List<TnsOracleInstanceDto>, Void> worker =
    			new SwingWorker<List<TnsOracleInstanceDto>, Void>() {
    		
    		List<TnsOracleInstanceDto> listInstances;

					@Override
					protected List<TnsOracleInstanceDto> doInBackground() throws Exception {
						listInstances = service.getListInstances();
						return listInstances;
					}
					
					@Override
		            protected void done() {
						
				    	if (this.listInstances != null) {
				    		
				    		DefaultComboBoxModel<KeyAndLibelle> model = 
				    				(DefaultComboBoxModel<KeyAndLibelle>) cbSearchInstance.getModel();
				    		
				    		for (TnsOracleInstanceDto tnsOracleInstanceDto : listInstances) {
								KeyAndLibelle keyAndLibelle = new KeyAndLibelle(tnsOracleInstanceDto.getService(),
										tnsOracleInstanceDto.getHost());
								model.addElement(keyAndLibelle);
							}
				    	}
					}
    		
    	};
    	 worker.execute();
    	
    }
    private OracleBddAccessManager getOracleConnectionManager() {
    	return (OracleBddAccessManager)BddAccessManagerFactory.get(Type.Oracle);
    }
    private void createSearchInstancePanel(Container container) {
    	
    	JPanel panelSearch = new JPanel();
    	panelSearch.setLayout(new BoxLayout(panelSearch, BoxLayout.X_AXIS));
    	panelSearch.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    	panelSearch.setBackground(Color.white);
    	
    	panelSearch.add(Box.createHorizontalGlue());
    	panelSearch.add(new JLabel("search:"));
    	panelSearch.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));

    	this.createSearchComboBox();
    	panelSearch.add(this.cbSearchInstance);
    	
    	container.add(panelSearch);
    }
    
    private void createSearchComboBox() {
    	this.cbSearchInstance = new StateComboBox<>();
    	this.cbSearchInstance.setModel(new DefaultComboBoxModel<KeyAndLibelle>());
          Dimension size = new Dimension(250, 20);
          this.cbSearchInstance.setPreferredSize(size);
          this.cbSearchInstance.setMaximumSize(size);
          this.cbSearchInstance.addActionListener(this);
    }
    private void createPanelTextField(Container container) {

        this.pfHost = new LabelAndTextField("Host:", 100, 150);
        container.add(this.pfHost);
        container.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));

        this.pfPort = new LabelAndTextField("Port:", 100, 50, true, IBddAccessManager.PATTERN_PORT);
        container.add(this.pfPort);
        container.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));

        this.pfInstance = new LabelAndTextField("Instance:", 100, 100);
        container.add(this.pfInstance);

    }



}
