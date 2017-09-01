package fr.tsadeo.app.dsntotree.gui.bdd;

import javax.swing.event.DocumentListener;

import fr.tsadeo.app.dsntotree.bdd.dao.IConnectionManager;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;

public interface IConnectComponent {

    public boolean isUrlEmpty();

    public IConnectionManager getConnexionManager();

    public String getUrl();

    public void setDocumentListener(DocumentListener documentListener);

    public void setBddConnexionDto(BddConnexionDto connexionDto);
}
