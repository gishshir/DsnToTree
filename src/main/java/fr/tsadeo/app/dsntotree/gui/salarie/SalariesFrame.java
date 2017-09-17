package fr.tsadeo.app.dsntotree.gui.salarie;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.tsadeo.app.dsntotree.business.SalarieDto;
import fr.tsadeo.app.dsntotree.gui.AbstractFrame;
import fr.tsadeo.app.dsntotree.gui.GuiUtils;
import fr.tsadeo.app.dsntotree.gui.IMainActionListener;
import fr.tsadeo.app.dsntotree.gui.action.ShowSalarieAction;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;

public class SalariesFrame extends AbstractFrame implements ISalarieListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private IMainActionListener mainActionListener;
    private TableSalaries tableSalaries;
    private SalarieStateButton btShowRubriques, btDeleteSalarie, btEditSalarie;
    

    //------------------------------------- implementing ISalarieListener
	@Override
	public void onSalarieSelected(SalarieDto salarie) {
		this.btShowRubriques.setEnabled(true);
		this.btShowRubriques.setToolTipText("voir les rubriques du salarie " + salarie.getNom());
		this.btShowRubriques.setSalarie(salarie);
	}

	//----------------------------------- constructor
    public SalariesFrame(IMainActionListener mainActionListener) {
        super("Liste des salaries", JFrame.DISPOSE_ON_CLOSE);
        this.mainActionListener = mainActionListener;

        // Set up the content pane.
        addComponentsToPane(this.getContentPane());
    }

    // ------------------------------- public methods
    public void setDatas(List<SalarieDto> listSalaries) {

        this.tableSalaries.setDatas(listSalaries);
    }
    // ------------------------------------ private methods

    private void addComponentsToPane(Container pane) {
        pane.setLayout(new BorderLayout());

        createPanelTop(pane, BorderLayout.PAGE_START);
        createPanelMiddle(pane, BorderLayout.CENTER);

    }

    private void createPanelMiddle(Container container, String layout) {
        tableSalaries = new TableSalaries(this);
        JScrollPane scrollPanel = new JScrollPane(this.tableSalaries);

        container.add(scrollPanel, layout);
    }

    private void createPanelTop(Container container, String layout) {

    	JPanel panelTop = new JPanel();
    	panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.Y_AXIS));
    	panelTop.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    	
    	this.createPanelSearch(panelTop, BorderLayout.CENTER);
    	this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
    	this.createPanelButtons(panelTop, BorderLayout.CENTER);
    	
    	container.add(panelTop, layout);
    }
    
    private void createPanelButtons(Container container, String layout) {
    	JPanel panelButton = new JPanel();
    	panelButton.setLayout(new BoxLayout(panelButton, BoxLayout.X_AXIS));
    	
    	panelButton.add(Box.createHorizontalGlue());
    	this.createButtonShowSalaries(container, BorderLayout.CENTER);
    	
    	container.add(panelButton, layout);
    }
    private void createPanelSearch(Container container, String layout)  {
    	
    }
    private void createButtonShowSalaries(Container container, String layout) {

        this.btShowRubriques = new SalarieStateButton();
        GuiUtils.createButton(this.btShowRubriques, new ShowSalarieAction(this.mainActionListener), SHOW_SALARIE_ACTION,
                KeyEvent.VK_R, PATH_SHOW_BLOC_ICO, null, "Voir les rubriques du salarié", false, container, layout);
    }


    //======================================== INNER CLASS
    public static class SalarieStateButton extends StateButton {
    	
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private SalarieDto salarie;

		public SalarieDto getSalarie() {
			return salarie;
		}

		public void setSalarie(SalarieDto salarie) {
			this.salarie = salarie;
		}
    	
    	
    }

}
