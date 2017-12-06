package fr.tsadeo.app.dsntotree.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;

import fr.tsadeo.app.dsntotree.gui.action.ShowDsnNormeAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowSalariesFrameAction;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;

public class BusinessPanel extends JPanel implements IGuiConstants {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    private final IMainActionListener mainActionListener;
    private StateButton btShowSalaries, btShowDsnNorme;

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
        this.btShowDsnNorme.waitEndAction();
    }

    void currentActionEnded() {

        this.btShowSalaries.actionEnded();
        this.btShowDsnNorme.actionEnded();
    }

    void activeNormeButton(boolean active) {
        this.btShowDsnNorme.setEnabled(active);
    }

    void activeButtons(boolean active) {
        this.btShowSalaries.setEnabled(active);
//        this.btShowDsnNorme.setEnabled(active);
    }

    // --------------------------------------- private methods
    private void createButtonPanel() {

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(10, 5, 10, 5);

        this.createButtonShowSalaries(this, constraints);
        this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_15));
        this.createButtonShowNorme(this, constraints);
        this.add(Box.createVerticalGlue());
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

}
