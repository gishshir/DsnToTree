package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import fr.tsadeo.app.dsntotree.gui.action.ShowSalariesFrameAction;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;

public class BusinessPanel extends JPanel implements IGuiConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final IMainActionListener mainActionListener;
	private StateButton btShowSalaries;
	
	//---------------------------------------- constructor
	public BusinessPanel(IMainActionListener mainActionListener) {
        this.mainActionListener = mainActionListener;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(20, 5, 10, 5));
        
        this.createButtonPanel();
	}
	
	  // --------------------------------------- package methods
    void waitEndAction() {

        this.btShowSalaries.waitEndAction();
    }

    void currentActionEnded() {

        this.btShowSalaries.actionEnded();
    }
    
    void activeButtons (boolean active) {
    	this.btShowSalaries.setEnabled(true);
    }

 // --------------------------------------- private methods
	private void createButtonPanel() {
		
		this.createButtonShowSalaries(this, BorderLayout.CENTER);
		this.add( Box.createRigidArea(DIM_VER_RIGID_AREA_15));
		this.add(Box.createVerticalGlue());
	}
	
	 private void createButtonShowSalaries(Container container, String layout) {

	        this.btShowSalaries = new StateButton();
	        GuiUtils.createButton(this.btShowSalaries, new ShowSalariesFrameAction(this.mainActionListener), SHOW_SALARIES_ACTION,
	                KeyEvent.VK_A, PATH_SALARIES_ICO, "Salariés", "Voir la liste des salariés", false, container, layout);
	    }


}
