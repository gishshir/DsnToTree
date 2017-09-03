package fr.tsadeo.app.dsntotree.gui.bdd;

import java.awt.Color;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.DocumentListener;

import fr.tsadeo.app.dsntotree.bdd.dao.BddAccessManagerFactory;
import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager;
import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager.Type;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.OracleBddAccessManager;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.OracleBddAccessManager.UrlParametersDto;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.gui.IGuiConstants;
import fr.tsadeo.app.dsntotree.gui.component.IStateComponent;
import fr.tsadeo.app.dsntotree.gui.component.LabelAndTextField;

public class OracleConnectComponent extends JPanel implements IConnectComponent, IGuiConstants, IStateComponent
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    private LabelAndTextField pfHost, pfPort, pfInstance;

    // ------------------------------------- Overriding IStateComponent
    @Override
    public void waitEndAction() {
        this.pfHost.waitEndAction();
        this.pfPort.waitEndAction();
        this.pfInstance.waitEndAction();
    }

    @Override
    public void actionEnded() {
        this.pfHost.actionEnded();
        this.pfPort.actionEnded();
        this.pfInstance.actionEnded();
    }

    // ----------------------------------------- overriding IConnectComponent
    @Override
    public void setBddConnexionDto(BddConnexionDto connexionDto) {

    	UrlParametersDto dto = this.getOracleConnectionManager().getUrlParameters(connexionDto.getUrl());
        this.pfHost.setValue(dto.getHost());
        this.pfPort.setValue(dto.getPort());
        this.pfInstance.setValue(dto.getInstance());
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
    public OracleConnectComponent() {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        this.createPanelTextField(this);
    }

    // ---------------------------------------- private methods
    private OracleBddAccessManager getOracleConnectionManager() {
    	return (OracleBddAccessManager)BddAccessManagerFactory.get(Type.Oracle);
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
