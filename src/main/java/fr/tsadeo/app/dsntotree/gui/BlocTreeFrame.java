package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.MySwingUtilities;

import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.util.ListDsnListenerManager;
import fr.tsadeo.app.dsntotree.util.ListItemBlocListenerManager;

public class BlocTreeFrame extends AbstractFrame implements ItemBlocListener, IDsnListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final IMainActionListener mainActionListener;

    private final String pathItemBloc;
    private MySimpleTree simpleTree;
    private ItemBloc itemBloc;
    private JPanel panelBlocPath;

    // ------------------------------------ implementing IDsnListener
    @Override
    public void onDsnReloaded(Dsn dsn) {

        if (this.itemBloc == null) {
            return;
        }
        ItemBloc sameItemBloc = ServiceFactory.getDsnService().findItemBlocEquivalent(dsn, this.itemBloc);
        this.itemBloc = sameItemBloc;
        this.simpleTree.clearTree();
        this.simpleTree.createNodes(itemBloc, false);
        this.simpleTree.expandBloc(ALL, true);
    }

    @Override
    public void onDsnOpened() {
        this.actionClose();
    }

    @Override
    public void onSearch(String search, boolean next) {
        this.simpleTree.search(search, next);
    }

    @Override
    public void onSearchCanceled() {
        this.simpleTree.cancelSearch();
    }

    // ------------------------------------- implementing ItemBlocListener
    @Override
    public void onItemBlocSelected(ItemBloc itemBloc) {
        // TODO Auto-generated method stub

    }

    @Override
    public BlocTree getTreeRoot() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onItemRubriqueSelected(ItemRubrique itemRubrique) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemBlocDragStarted() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemBlocDropEnded() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemBlocModified(ItemBloc modifiedItemBloc, ModifiedState state, boolean refresh) {

        if (this.itemBloc.isDeleted()) {
            this.actionClose();
            return;
        }
        if (this.itemBloc == modifiedItemBloc || this.itemBloc.isAncestorBloc(modifiedItemBloc)
                || this.itemBloc.isDescendentBloc(modifiedItemBloc)) {

            this.simpleTree.clearTree();
            this.simpleTree.createNodes(itemBloc, false);
            this.simpleTree.expandBloc(ALL, true);
        }
    }

    // ---------------------------------------------- constructor
    protected BlocTreeFrame(String title, String pathItemBloc, IMainActionListener listener) {
        super("DSN: " + title, JFrame.DISPOSE_ON_CLOSE);
        this.mainActionListener = listener;
        this.pathItemBloc = pathItemBloc;

        ListDsnListenerManager.get().addDsnListener(this);

        // Set up the content pane.
        addComponentsToPane(this.getContentPane());

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Schedule a job for the event-dispatching thread:
                // creating and showing this application's GUI.
                MySwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ListItemBlocListenerManager.get().removeItemBlocListener((ItemBlocListener) BlocTreeFrame.this);
                        ListDsnListenerManager.get().removeDsnListener((IDsnListener) BlocTreeFrame.this);
                    }
                });
            }
        });

    }

    void setItemBloc(ItemBloc itemBloc) {
        this.itemBloc = itemBloc;
        this.simpleTree.createNodes(itemBloc, false);
        this.simpleTree.expandBloc(ALL, true);
    }

    // -------------------------------------------- private methode

    private void addComponentsToPane(Container pane) {
        pane.setLayout(new BorderLayout());

        createPanelTop(pane, BorderLayout.PAGE_START);
        createPanelMiddle(pane, BorderLayout.CENTER);
    }

    private void createPanelMiddle(Container pane, String layout) {

        this.simpleTree = new MySimpleTree(this.mainActionListener, null, "Bloc");
        JScrollPane scrollPane = new JScrollPane(this.simpleTree);

        pane.add(scrollPane, layout);
    }

    private void createPanelTop(Container container, String layout) {
        this.createBlocPanelTitle(container, layout);
        this.buildBlocPath();
    }

    private void createBlocPanelTitle(Container container, String layout) {

        this.panelBlocPath = new JPanel();
        this.panelBlocPath.setMinimumSize(container.getSize());
        this.panelBlocPath.setBackground(TREE_BACKGROUND_COLOR);
        this.panelBlocPath.setForeground(TREE_NORMAL_COLOR);

        this.panelBlocPath.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(Color.BLUE)));
        container.add(this.panelBlocPath, layout);

    }

    private void buildBlocPath() {

        JLabel labelBloc = new JLabel(this.pathItemBloc);
        labelBloc.setForeground(TREE_NORMAL_COLOR);
        this.panelBlocPath.add(labelBloc);

    }

}
