package fr.tsadeo.app.dsntotree.gui.bdd;

import java.awt.Color;
import java.awt.Container;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.DocumentListener;

import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.gui.IGuiConstants;
import fr.tsadeo.app.dsntotree.gui.component.IStateComponent;
import fr.tsadeo.app.dsntotree.gui.component.LabelAndTextField;

public class OracleConnectComponent extends JPanel implements IConnectComponent, IGuiConstants, IStateComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String ORACLE_JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String ORACLE_DB_URL = "jdbc:oracle:thin:@%1$s:%2$d/%3$s";

    private static final Pattern PATTERN_PORT = Pattern.compile("[0-9]{1,4}");
    private static final Pattern PATTERN_DB_URL = Pattern
            .compile("^jdbc:oracle:thin:@([\\w.]*):([\\d]{1,4})\\/([\\w]{1,10})");

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

        Matcher m = PATTERN_DB_URL.matcher(connexionDto.getUrl());
        if (m.matches()) {
            int count = m.groupCount();
            if (count == 3) {
                this.pfHost.setValue(m.group(1));
                this.pfPort.setValue(m.group(2));
                this.pfInstance.setValue(m.group(3));

            }
        }

    }

    @Override
    public Type getType() {
        return Type.Oracle;
    }

    @Override
    public String getDriver() {
        return ORACLE_JDBC_DRIVER;
    }

    @Override
    public String getUrl() {
        if (this.isUrlEmpty()) {
            return null;
        }
        return this.getUrl(this.pfHost.getValue(), this.pfPort.getIntValue(), this.pfInstance.getValue());
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
    private void createPanelTextField(Container container) {

        this.pfHost = new LabelAndTextField("Host:", 100, 150);
        container.add(this.pfHost);
        container.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));

        this.pfPort = new LabelAndTextField("Port:", 100, 50, true, PATTERN_PORT);
        container.add(this.pfPort);
        container.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));

        this.pfInstance = new LabelAndTextField("Instance:", 100, 100);
        container.add(this.pfInstance);

    }

    // ---------------------------------------- protected methods
    protected String getUrl(String host, Integer port, String instance) {
        return String.format(ORACLE_DB_URL, host, port, instance);
    }

}
