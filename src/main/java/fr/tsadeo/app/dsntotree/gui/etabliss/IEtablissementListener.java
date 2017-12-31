package fr.tsadeo.app.dsntotree.gui.etabliss;

import fr.tsadeo.app.dsntotree.business.EtablissementDto;

public interface IEtablissementListener {

    public void onEtablissementSelected(EtablissementDto etablissement);

    public void setFocusOnSearch();
}
