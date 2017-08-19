package fr.tsadeo.app.dsntotree.gui;

import fr.tsadeo.app.dsntotree.gui.MyPanelBloc.PanelChild;
import fr.tsadeo.app.dsntotree.gui.MyPanelBloc.PanelRubrique;

public interface IBlocActionListener {
	
	
	public void actionAddOtherChild();
	public void actionAddChild(PanelChild panelChild);
	public void actionDuplicateChildWithContirmation(PanelChild panelChild);
	public void actionDeleteChildWithConfirmation(PanelChild panelChild);
	
	
	public void actionDeleteRubrique(PanelRubrique panelRubrique);	
	public void actionAddRubrique();
	
	public void actionValiderSaisie();
	public void actionAnnulerSaisie();
	
	public void actionNextRubrique(PanelRubrique panelRubrique);
	
	public boolean isSourceBtAddBloc(Object obj);
	

}
