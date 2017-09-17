package fr.tsadeo.app.dsntotree.gui.salarie;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import fr.tsadeo.app.dsntotree.business.SalarieDto;
import fr.tsadeo.app.dsntotree.gui.AbstractFrame;
import fr.tsadeo.app.dsntotree.gui.IMainActionListener;

public class SalariesFrame extends AbstractFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private IMainActionListener mainActionListener;
    private TableSalaries tableSalaries;

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
        tableSalaries = new TableSalaries();
        JScrollPane scrollPanel = new JScrollPane(this.tableSalaries);

        container.add(scrollPanel, layout);
    }

    private void createPanelTop(Container container, String layout) {

    }

}
