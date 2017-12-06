package fr.tsadeo.app.dsntotree.gui;

public interface IBddInstanceListener {

	public void instanceChanged(String instance);
	
	public void userChanged(String instance, String user, String pwd);
	
	public void setFocusOnSearch();

}
