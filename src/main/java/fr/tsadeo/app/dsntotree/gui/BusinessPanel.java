package fr.tsadeo.app.dsntotree.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.tsadeo.app.dsntotree.gui.action.ShowDsnNormeAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowErrorAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowEtablissementsFrameAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowSalariesFrameAction;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;

public class BusinessPanel extends JPanel implements IGuiConstants {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    private final IMainActionListener mainActionListener;
    private StateButton btShowEtabs, btShowSalaries, btShowDsnNorme, btShowErrors;
    
    private JPanel panelDrop;

    private final GridBagLayout layout;

    // ---------------------------------------- constructor
    public BusinessPanel(IMainActionListener mainActionListener) {
        this.mainActionListener = mainActionListener;
        this.layout = new GridBagLayout();
        this.setLayout(this.layout);
        this.setBorder(BorderFactory.createEmptyBorder(20, 5, 10, 5));

        this.createButtonPanel();
    }

    // --------------------------------------- package methods
    void waitEndAction() {

        this.btShowSalaries.waitEndAction();
        this.btShowEtabs.waitEndAction();
        this.btShowDsnNorme.waitEndAction();
        this.btShowErrors.waitEndAction();
    }

    void currentActionEnded() {

        this.btShowSalaries.actionEnded();
        this.btShowEtabs.actionEnded();
        this.btShowDsnNorme.actionEnded();
        this.btShowErrors.actionEnded();
    }
    
    void activeErrorButton(boolean active) {
    	this.btShowErrors.setEnabled(active);
    }

    void activeNormeButton(boolean active) {
        this.btShowDsnNorme.setEnabled(active);
    }

    void activeButtons(boolean active) {
        this.btShowSalaries.setEnabled(active);
        this.btShowEtabs.setEnabled(active);
    }
    
    void enterDropPanel() {
    	this.panelDrop.setBackground(TREE_BACKGROUND_DROPPABLE_COLOR);
    	this.labelDrop.setForeground(DRAG_START_COLOR);
    }
    
    void exitDropPanel() {
    	this.panelDrop.setBackground(null);
    	this.labelDrop.setForeground(this.getBackground());
    }

    // --------------------------------------- private methods
    private void createButtonPanel() {

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(10, 5, 10, 5);

        this.createButtonShowEtablissements(this, constraints);
        this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_15));
        this.createButtonShowSalaries(this, constraints);
        this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_15));
        this.createButtonShowNorme(this, constraints);
        this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_15));
        this.createButtonShowErrors(this, constraints);
        this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_15));
        this.createPanelDrop(this, constraints);
        this.add(Box.createVerticalGlue());
    }
    
    private JLabel labelDrop;
    private void createPanelDrop(Container container, GridBagConstraints constraints) {
    	
    	this.panelDrop = new JPanel();
    	this.labelDrop = new JLabel("drop file");
    	this.labelDrop.setSize(60,  20);
    	
    	this.panelDrop.setLayout(new BoxLayout(this.panelDrop, BoxLayout.Y_AXIS));
//      	this.panelDrop.setPreferredSize(new Dimension(60, 300));
      	
    	this.labelDrop.setAlignmentX(JComponent.CENTER_ALIGNMENT);

    	this.panelDrop.add(Box.createVerticalGlue());
    	this.panelDrop.add(this.labelDrop);
    	this.panelDrop.add(Box.createVerticalGlue());


    	layout.setConstraints(this.panelDrop, constraints);
    	container.add(this.panelDrop);
    	
    	this.exitDropPanel();
    }
    
    private void createButtonShowErrors(Container container, GridBagConstraints constraints) {

        btShowErrors = new StateButton();
        GuiUtils.createButton(btShowErrors, new ShowErrorAction(this.mainActionListener), SHOW_ERROR_DIALOG_ACTION, KeyEvent.VK_R,
                PATH_ERROR_ICO, "erreurs", "Voir la liste des erreurs", false, container, constraints, layout);
    }

    private void createButtonShowNorme(Container container, GridBagConstraints constraints) {

        this.btShowDsnNorme = new StateButton("Norme");
        GuiUtils.createButton(this.btShowDsnNorme, new ShowDsnNormeAction(this.mainActionListener), SHOW_NORME_ACTION,
                KeyEvent.VK_N, PATH_SHOW_NORME_ICO, "Norme", "Voir la norme DSN", false, container, constraints,
                layout);

    }

    private void createButtonShowSalaries(Container container, GridBagConstraints constraints) {

        this.btShowSalaries = new StateButton("Salariés");
        GuiUtils.createButton(this.btShowSalaries, new ShowSalariesFrameAction(this.mainActionListener),
                SHOW_SALARIES_ACTION, KeyEvent.VK_A, PATH_SALARIES_ICO, "Salariés", "Voir la liste des salariés", false,
                container, constraints, layout);
    }
    
    private void createButtonShowEtablissements(Container container, GridBagConstraints constraints) {

        this.btShowEtabs = new StateButton("Etabliss.");
        GuiUtils.createButton(this.btShowEtabs, new ShowEtablissementsFrameAction(this.mainActionListener),
                SHOW_ETABS_ACTION, KeyEvent.VK_A, PATH_ETABS_ICO, "Etabliss.", "Voir la liste des établissements", false,
                container, constraints, layout);
    }

}
