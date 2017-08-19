package fr.tsadeo.app.dsntotree.gui.bdd;

import javax.swing.event.DocumentListener;

import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;

public interface IConnectComponent {

    public enum Type {
        Oracle, Mysql
    }

    public boolean isUrlEmpty();

    public Type getType();

    public String getDriver();

    public String getUrl();

    public void setDocumentListener(DocumentListener documentListener);

    public void setBddConnexionDto(BddConnexionDto connexionDto);
}
