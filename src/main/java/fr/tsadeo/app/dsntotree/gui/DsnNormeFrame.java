package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.tsadeo.app.dsntotree.model.BlocTree;

/**
 * Frame présentant l'arborescence des blocs avec les libelles issus de la norme
 * DSN pour la DSN en cours
 * 
 * @author sfauche
 *
 */
public class DsnNormeFrame extends AbstractFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private DsnNormeTree dsnNormeTree;

    private final IMainActionListener mainActionListener;
    private final String description;
    private BlocTree blocTreeRoot;

    private JPanel panelTop;

    // ---------------------------------------------- constructor
    protected DsnNormeFrame(String description, IMainActionListener listener) {
        super("Norme DSN", JFrame.DISPOSE_ON_CLOSE);
        this.mainActionListener = listener;
        this.description = description;

        // Set up the content pane.
        addComponentsToPane(this.getContentPane());

    }

    void setBlocTree(BlocTree blocTree) {
        this.blocTreeRoot = blocTree;
        this.dsnNormeTree.createNodes(blocTree, false);
        this.dsnNormeTree.expandBloc(BLOC_11, true);

        this.buildDescription();
    }

    // ------------------------------------ private methode

    private void addComponentsToPane(Container pane) {
        pane.setLayout(new BorderLayout());

        createPanelTop(pane, BorderLayout.PAGE_START);
        createPanelMiddle(pane, BorderLayout.CENTER);
    }

    private void createPanelMiddle(Container pane, String layout) {
        this.dsnNormeTree = new DsnNormeTree("Norme");

        JScrollPane scrollPane = new JScrollPane(this.dsnNormeTree);
        pane.add(scrollPane, layout);

    }

    private void buildDescription() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(this.panelTop.getBackground());
        panel.setAlignmentX(CENTER_ALIGNMENT);
        this.panelTop.add(panel);

        JLabel labelLibelle = new JLabel(this.description);
        labelLibelle.setForeground(TREE_NORMAL_COLOR);
        panel.add(labelLibelle);

    }

    private void createPanelTop(Container container, String layout) {

        this.panelTop = new JPanel();
        panelTop.setMinimumSize(container.getSize());
        panelTop.setBackground(TREE_BACKGROUND_COLOR);
        panelTop.setForeground(TREE_NORMAL_COLOR);

        panelTop.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(Color.BLUE)));
        container.add(panelTop, layout);

    }

}
