package fr.tsadeo.app.dsntotree.gui.bdd;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import fr.tsadeo.app.dsntotree.gui.component.AutocompleteComboBox;
import fr.tsadeo.app.dsntotree.gui.component.AutocompleteComboBox.Searchable;
import fr.tsadeo.app.dsntotree.gui.component.IStateComponent;
import fr.tsadeo.app.dsntotree.gui.component.LabelAndTextField;
import fr.tsadeo.app.dsntotree.gui.component.StateComponentEnum;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.service.TnsNameOraService;
import fr.tsadeo.app.dsntotree.util.IConstants;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public class OracleConnectComponent extends JPanel 
        implements IConnectComponent, IGuiConstants, IConstants, IStateComponent, ActionListener
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private TnsNameOraService service = ServiceFactory.getTnsNameOraService();
    private final IBddInstanceListener listener;

    private LabelAndTextField pfHost, pfPort, pfInstance;
    private AutocompleteComboBox cbSearchInstance;
    private StateComponentEnum state = StateComponentEnum.actif;

    //---------------------------------------------------- implementing ActionLIstener
	@Override
	public void actionPerformed(ActionEvent e) {

			Object item = this.cbSearchInstance.getSelectedItem();
            if (item instanceof KeyAndLibelle) {
            	KeyAndLibelle keyAndLibelle = (KeyAndLibelle) item;
            TnsOracleInstanceDto dto = keyAndLibelle == null || this.mapKeyAndLibelleToDto == null ? null
                    : this.mapKeyAndLibelleToDto.get(keyAndLibelle);
            if (dto != null) {
                this.populateWithSelectedInstance(dto);
                this.listener.instanceChanged(dto.getService());
            }
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

		if (connexionDto != null) {
			UrlParametersDto dto = this.getOracleConnectionManager().getUrlParameters(connexionDto.getUrl());
			this.setValues(dto == null ? null : dto.getHost(), dto == null ? null : dto.getPort(),
					dto == null ? null : dto.getInstance());
		}
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

    private void populateWithSelectedInstance(TnsOracleInstanceDto dto) {
    	 	 

    	if (dto != null) {
    		this.setValues(dto.getHost(), Integer.toString(dto.getPort()), dto.getService());	
    	} else {
    		this.clearValues();
    	}
    	
    }

    public void activateSearchComboBox() {
    	
         boolean activate = service.hasTnsNameInstances();
         System.out.println("activate searchInstance: " + activate);

        List<KeyAndLibelle> listInstances = this.getSearchable().search(null);
        this.cbSearchInstance.populateComboBox(listInstances);
        this.cbSearchInstance.setEnabled(activate);

    	
    }

    private Map<KeyAndLibelle, TnsOracleInstanceDto> mapKeyAndLibelleToDto = null;
    private List<KeyAndLibelle> mapToListKeyAndLibelle(List<TnsOracleInstanceDto> listDto) {

        List<KeyAndLibelle> list = null;
        if (listDto != null) {

            this.mapKeyAndLibelleToDto = listDto.stream().collect(Collectors.toMap(dto -> mapFromDto(dto), key -> key));
            list = this.mapKeyAndLibelleToDto.keySet().stream()
                    .sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
                    .collect(Collectors.toList());

        }
        return list == null ? Collections.emptyList()
                : list;
    }

    private KeyAndLibelle mapFromDto(TnsOracleInstanceDto dto) {
        return new KeyAndLibelle(dto.getTnsname(), StringUtils.concat(dto.getService(), SPACE, dto.getHost()));
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
        panelSearch.add(new JLabel("tns names:"));
    	panelSearch.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));

    	this.createSearchComboBox();
    	panelSearch.add(this.cbSearchInstance);
    	
    	container.add(panelSearch);
    }

    private Searchable getSearchable() {

        return search -> {
            List<TnsOracleInstanceDto> listDto = service.filterInstances(search);
            List<KeyAndLibelle> list = new ArrayList<>();
            list.add(new KeyAndLibelle(null, ""));
            list.addAll(mapToListKeyAndLibelle(listDto));
            return list;
        };
    }
    private void createSearchComboBox() {
        this.cbSearchInstance = new AutocompleteComboBox(this.getSearchable());
    	this.cbSearchInstance.setModel(new DefaultComboBoxModel<KeyAndLibelle>());
        Dimension size = new Dimension(350, 20);
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
