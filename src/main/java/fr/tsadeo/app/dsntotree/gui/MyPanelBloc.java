package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.MyJOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.tsadeo.app.dsntotree.dico.KeyAndLibelle;
import fr.tsadeo.app.dsntotree.dto.BlocChildrenDto;
import fr.tsadeo.app.dsntotree.gui.ItemBlocListener.ModifiedState;
import fr.tsadeo.app.dsntotree.gui.action.AddChildAction;
import fr.tsadeo.app.dsntotree.gui.action.AddRubriquedAction;
import fr.tsadeo.app.dsntotree.gui.action.CancelAction;
import fr.tsadeo.app.dsntotree.gui.action.DelChildAction;
import fr.tsadeo.app.dsntotree.gui.action.DelRubriquedAction;
import fr.tsadeo.app.dsntotree.gui.action.DuplicateChildAction;
import fr.tsadeo.app.dsntotree.gui.action.NextRubriquedAction;
import fr.tsadeo.app.dsntotree.gui.action.ShowChildAction;
import fr.tsadeo.app.dsntotree.gui.action.ValiderAction;
import fr.tsadeo.app.dsntotree.gui.component.StateButton;
import fr.tsadeo.app.dsntotree.gui.component.StateTextField;
import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.util.DragAndDropUtil;
import fr.tsadeo.app.dsntotree.util.IRegexConstants;
import fr.tsadeo.app.dsntotree.util.ListItemBlocListenerManager;

public class MyPanelBloc extends JPanel implements IGuiConstants, IBlocActionListener, IRegexConstants {

    private static final Logger LOG = Logger.getLogger(MyPanelBloc.class.getName());
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    

    private static final Dimension DIM_BUTTON_SMALL = new Dimension(16, 20);

    private static final int TAB_RUBRIQUES = 0;
    private static final int TAB_BLOCS = 1;
    private static final int TAB_OTHER_BLOCS = 2;

    private BlocTree treeRoot;

    private JTabbedPane tabbedPane;

    private JPanel panelTitle;
    private JPanel panelChildrens;
    private JPanel panelListRubriques;
    private JPanel panelListChildrens;
    private JPanel panelOtherChildrens;
    private JScrollPane scrollPanelBloc;

    private Component childrenVerticalGlue = Box.createVerticalGlue();

    private StateButton btValider;
    private StateButton btAnnuler;
    private StateButton btAddRubrique;
    private StateButton btAddBloc;

    private JComboBox<KeyAndLibelle> cbOtherChildComboBox;

    private ItemBloc currentItemBloc;
    private ItemRubrique currentItemRubrique;

    private Action nextRubriqueAction;
    private Action delRubriqueAction;

    private Action showChildAction;
    private Action addChildAction;
    private Action duplicateChildAction;
    private Action delChildAction;

    private final IMainActionListener mainActionListener;
    private DocumentListener documentListener;

    public MyPanelBloc(IMainActionListener mainActionListener) {

        this.mainActionListener = mainActionListener;
        this.setLayout(new BorderLayout());
        this.setBackground(TREE_BACKGROUND_COLOR);

        this.createTopPanel(this, BorderLayout.PAGE_START);
        this.createMiddlePanel(this, BorderLayout.CENTER);
        this.createBottomPanel(this, BorderLayout.PAGE_END);

    }

    // ------------------------------------------------------- package methods
    void waitEndAction() {

        this.btAddBloc.waitEndAction();
        this.btAddRubrique.waitEndAction();
        this.btAnnuler.waitEndAction();
        this.btValider.waitEndAction();

        List<PanelRubrique> listPanelRubriques = this.buildListPanelRubriques();
        if (Objects.nonNull(listPanelRubriques)) {
            listPanelRubriques.stream().forEach(panelRubrique -> panelRubrique.waitEndAction());
        }

        List<PanelChild> listPanelChildrens = this.buildListPanelChildren();
        if (Objects.nonNull(listPanelChildrens)) {
            listPanelChildrens.stream().forEach(panelChild -> panelChild.waitEndAction());
        }
    }

    void currentActionEnded() {

        this.btAddBloc.actionEnded();
        this.btAddRubrique.actionEnded();
        this.btAnnuler.actionEnded();
        this.btValider.actionEnded();

        List<PanelRubrique> listPanelRubriques = this.buildListPanelRubriques();
        if (Objects.nonNull(listPanelRubriques)) {
            listPanelRubriques.stream().forEach(panelRubrique -> panelRubrique.currentActionEnded());
        }

        List<PanelChild> listPanelChildrens = this.buildListPanelChildren();
        if (Objects.nonNull(listPanelChildrens)) {
            listPanelChildrens.stream().forEach(panelChild -> panelChild.currentActionEnded());
        }
    }

    void clear() {

        this.currentItemBloc = null;

        this.panelTitle.removeAll();
        this.panelChildrens.setVisible(false);
        this.panelListChildrens.setVisible(false);
        this.panelOtherChildrens.setVisible(false);

        this.panelListRubriques.removeAll();
        this.panelListChildrens.removeAll();

        this.clearListOtherChild();

        this.nextRubriqueAction = null;
        this.delRubriqueAction = null;

        this.tabbedPane.setSelectedIndex(TAB_RUBRIQUES);

    }

    void validerSaisie(boolean refresh) {
        populateItemBlocFromSaisie();
        ListItemBlocListenerManager.get().onItemBlocModified(currentItemBloc, ModifiedState.valider, refresh);
        enableButtons(false);
    }

    // ------------------------------------------------------- private methods
    /*
     * Panel supérieur contenant le chemin complet du bloc et les boutons de
     * validation/anulation
     */
    private void createTopPanel(Container container, String layout) {

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        this.createBlocPanelTitle(topPanel, null);
        this.createPanelButton(topPanel, null);

        container.add(topPanel, layout);

    }

    /*
     * Panel du centre contenant la liste des rubriques et des blocs enfants
     * dans un scroll panel
     */
    private void createMiddlePanel(Container container, String layout) {

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setBackground(TREE_BACKGROUND_COLOR);
        middlePanel.add(Box.createRigidArea(DIM_VER_RIGID_AREA_15));

        this.createTabbedPane(middlePanel, BorderLayout.CENTER);

        this.scrollPanelBloc = new JScrollPane(middlePanel);
        container.add(this.scrollPanelBloc, layout);
    }

    private void scrollToBottom() {
        if (this.scrollPanelBloc == null) {
            return;
        }
        JViewport viewPort = this.scrollPanelBloc.getViewport();
        Point pt = viewPort.getViewPosition();
        System.out.println("Point: " + pt.toString());

        pt.y = 300; // this.getMaxYExtent();
        System.out.println("Point: " + pt.toString());
        viewPort.setViewPosition(pt);
    }

    private int getMaxYExtent() {
        JViewport viewPort = this.scrollPanelBloc.getViewport();
        System.out.println("getView().getHeight(): " + viewPort.getView().getHeight());
        System.out.println("getHeight(): " + viewPort.getHeight());
        return viewPort.getView().getHeight() - viewPort.getHeight();
    }

    private void createTabbedPane(Container container, String layout) {

        this.tabbedPane = new JTabbedPane();
        container.add(this.tabbedPane, layout);

        this.tabbedPane.addTab("rubriques", this.createListRubriquePanel());
        this.tabbedPane.addTab("blocs enfants", this.createChildrenPanel());
        this.tabbedPane.addTab("autres blocs", this.createOtherChildPanel());

        this.tabbedPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (e.getSource() == tabbedPane) {
                    int index = tabbedPane.getSelectedIndex();
                    panelListChildrens.setVisible(index == TAB_BLOCS);
                }
            }
        });

    }

    private void createBottomPanel(Container container, String layout) {

        JPanel bottomPanel = new JPanel();
        container.add(bottomPanel, layout);
    }

    private DocumentListener getDocumentListener() {

        if (this.documentListener == null) {
            this.documentListener = new DocumentListener() {

                @Override
                public void removeUpdate(DocumentEvent e) {
                    enableButtons(true);
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    enableButtons(true);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    enableButtons(true);
                }
            };
        }
        return this.documentListener;
    }

    private void createPanelButton(Container container, String layout) {

        JPanel panelButton = new JPanel();
        this.createButtonValider(panelButton);
        panelButton.add(Box.createRigidArea(new Dimension(5, 0)));
        this.createButtonAnnuler(panelButton);
        panelButton.add(Box.createRigidArea(new Dimension(5, 0)));
        this.createButtonNewRubrique(panelButton);

        container.add(panelButton, layout);

        this.enableButtons(false);
    }

    private void createButtonValider(Container container) {
        Action action = new ValiderAction(this);
        this.btValider = new StateButton();
        this.btValider.setAction(action);
        this.btValider.setText("Valider");
        this.btValider.setToolTipText("Intégrer les modifications dans l'arborescence de la DSN.");
        this.btValider.setMnemonic(KeyEvent.VK_V);

        InputMap im = this.btValider.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.btValider.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.ALT_DOWN_MASK), VALIDER_SAISIE_ACTION);
        am.put(VALIDER_SAISIE_ACTION, action);

        container.add(this.btValider);
    }

    private void createListNewBloc(Container container) {

        this.cbOtherChildComboBox = new JComboBox<KeyAndLibelle>();
        this.cbOtherChildComboBox.setModel(new DefaultComboBoxModel<KeyAndLibelle>());
        Dimension size = new Dimension(250, 20);
        this.cbOtherChildComboBox.setPreferredSize(size);
        this.cbOtherChildComboBox.setMaximumSize(size);

        this.cbOtherChildComboBox.setBackground(Color.white);
        container.add(this.cbOtherChildComboBox);
    }

    private void createButtonNewBloc(Container container) {
        this.btAddBloc = new StateButton();

        this.btAddBloc.setMaximumSize(new Dimension(130, 20));
        Action action = this.getAddChildAction();
        this.btAddBloc.setAction(action);
        this.btAddBloc.setIcon(GuiUtils.createImageIcon(PATH_ADD_RUB_ICO));
        this.btAddBloc.setToolTipText("Ajouter un bloc enfant.");

        InputMap im = this.btAddBloc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.btAddBloc.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.ALT_DOWN_MASK), ADD_BLOC_ACTION);
        am.put(ADD_BLOC_ACTION, action);

        this.btAddBloc.setText("Ajouter");
        this.btAddBloc.setMnemonic(KeyEvent.VK_J);

        this.btAddBloc.setVerticalTextPosition(AbstractButton.CENTER);
        this.btAddBloc.setHorizontalTextPosition(AbstractButton.RIGHT);
        this.btAddBloc.setEnabled(false);

        container.add(this.btAddBloc);

    }

    private void createButtonNewRubrique(Container container) {

        Action action = new AddRubriquedAction(this);
        this.btAddRubrique = new StateButton();
        this.btAddRubrique.setMaximumSize(new Dimension(16, 20));
        this.btAddRubrique.setAction(action);
        this.btAddRubrique.setIcon(GuiUtils.createImageIcon(PATH_ADD_RUB_ICO));
        this.btAddRubrique.setToolTipText("Ajouter une nouvelle rubrique.");

        InputMap im = this.btAddRubrique.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.btAddRubrique.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK), ADD_RUBRIQUE_ACTION);
        am.put(ADD_RUBRIQUE_ACTION, action);

        container.add(this.btAddRubrique);
    }

    private void createButtonAnnuler(Container container) {
        Action action = new CancelAction(this);
        this.btAnnuler = new StateButton();
        this.btAnnuler.setAction(action);
        this.btAnnuler.setText("Annuler");
        this.btAnnuler.setToolTipText("Annuler les modifications.");
        this.btAnnuler.setMnemonic(KeyEvent.VK_N);

        InputMap im = this.btAnnuler.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.btAnnuler.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK), ANNULER_SAISIE_ACTION);
        am.put(ANNULER_SAISIE_ACTION, action);

        container.add(this.btAnnuler);

    }

    private void createBlocPanelTitle(Container container, String layout) {

        this.panelTitle = new JPanel();

        this.panelTitle.setMinimumSize(container.getSize());
        this.panelTitle.setBackground(TREE_BACKGROUND_COLOR);
        this.panelTitle.setForeground(TREE_NORMAL_COLOR);

        this.panelTitle.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(Color.BLUE)));
        container.add(this.panelTitle, layout);
    }

    private JPanel createListRubriquePanel() {

        this.panelListRubriques = new JPanel();
        this.panelListRubriques.setOpaque(true);
        this.panelListRubriques.setLayout(new BoxLayout(this.panelListRubriques, BoxLayout.Y_AXIS));
        this.panelListRubriques.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.panelListRubriques.setBackground(TREE_BACKGROUND_COLOR);

        return this.panelListRubriques;
    }

    private JPanel createOtherChildPanel() {

        this.panelOtherChildrens = new JPanel();
        this.panelOtherChildrens.setOpaque(true);
        this.panelOtherChildrens.setVisible(false);

        this.panelOtherChildrens.setLayout(new BoxLayout(this.panelOtherChildrens, BoxLayout.Y_AXIS));
        this.panelOtherChildrens.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(15, 5, 5, 5)));
        this.panelOtherChildrens.setAlignmentY(Box.TOP_ALIGNMENT);
        this.panelOtherChildrens.setAlignmentX(Box.LEFT_ALIGNMENT);

        this.panelOtherChildrens.add(new MyLabel("autres blocs possibles..."));
        this.panelOtherChildrens.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));

        // Bouton ajouter other child
        this.createButtonNewBloc(this.panelOtherChildrens);
        this.panelOtherChildrens.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));

        // panel contenant la liste des blocs
        JPanel panelHor = new JPanel();
        panelHor.setLayout(new BoxLayout(panelHor, BoxLayout.X_AXIS));
        panelHor.setAlignmentX(Box.LEFT_ALIGNMENT);
        this.createListNewBloc(panelHor);
        panelHor.add(Box.createHorizontalGlue());
        this.panelOtherChildrens.add(panelHor);

        this.panelOtherChildrens.add(Box.createVerticalGlue());
        return this.panelOtherChildrens;

    }

    private JPanel createChildrenPanel() {

        this.panelChildrens = new JPanel();
        this.panelChildrens.setOpaque(true);
        this.panelChildrens.setLayout(new BorderLayout());
        this.panelChildrens.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.panelChildrens.add(new MyLabel("Liste des blocs enfants..."), BorderLayout.PAGE_START);
        this.createListChildrenPanel(this.panelChildrens, BorderLayout.CENTER);

        this.panelChildrens.setVisible(false);

        return this.panelChildrens;

    }

    private void createListChildrenPanel(Container container, String layout) {

        this.panelListChildrens = new JPanel();
        this.panelListChildrens.setBackground(TREE_BACKGROUND_COLOR);
        this.panelListChildrens.setOpaque(true);
        this.panelListChildrens.setVisible(false);
        this.panelListChildrens.setLayout(new BoxLayout(this.panelListChildrens, BoxLayout.Y_AXIS));
        this.panelListChildrens.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        container.add(this.panelListChildrens, layout);
    }

    private void enableButtons(boolean enabled) {
        this.btAnnuler.setEnabled(enabled);
        this.btValider.setEnabled(enabled);

        if (enabled) {
            this.updateEtatBoutonAddRubriques();
        } else {
            this.btAddRubrique.setEnabled(false);
        }

    }

    private List<ItemRubrique> getListItemRubriquesFromPanel() {

        List<PanelRubrique> listPanelRubriques = this.buildListPanelRubriques();
        return listPanelRubriques.stream().filter(panelRubrique -> !panelRubrique.isDeleted())
                .map(panelRubrique -> panelRubrique.itemRubrique)
                .collect(Collectors.toList());
    }

    /**
     * Alimente la liste des ItemRubrique du bloc en cours avec les valeurs
     * saisies. La liste est ordonnée par rubriqueLabel croissant (valeur
     * numériques)
     */
    private void populateItemBlocFromSaisie() {

        // mise à jour de la liste des rubriques
        List<PanelRubrique> listPanelRubriques = this.buildListPanelRubriques();
        List<ItemRubrique> listItemRubriques = this.currentItemBloc.getListRubriques();
        listItemRubriques.clear();

        for (PanelRubrique panelRubrique : listPanelRubriques) {
            if (panelRubrique.isDeleted()) {
                panelRubrique.itemRubrique.setDeleted(true);
                continue; // next
            }

            listItemRubriques.add(panelRubrique.itemRubrique);
            if (panelRubrique.isCreated()) {
                panelRubrique.itemRubrique.setValue(panelRubrique.getValue());
            } else if (panelRubrique.isModified()) {
                panelRubrique.itemRubrique.setValue(panelRubrique.getValue());
                panelRubrique.itemRubrique.setModified(true);
            }
        }
        Collections.sort(listItemRubriques);

        // mise à jour de la liste des blocs enfants
        List<PanelChild> listPanelChildren = this.buildListPanelChildren();
        this.currentItemBloc.clearChildrens();

        for (PanelChild panelChild : listPanelChildren) {
            if (panelChild.isDeleted()) {
                panelChild.child.setDeleted(true);
                this.currentItemBloc.setChildrenModified(true);
                continue; // next
            }

            if (panelChild.child.isCreated()) {
                this.currentItemBloc.setChildrenModified(true);
            }

            this.currentItemBloc.addChild(panelChild.child);
        }
        ServiceFactory.getDsnService().reorderListChildBloc(this.treeRoot, this.currentItemBloc);

    }

    // construit la liste des PanelRubrique
    private List<PanelRubrique> buildListPanelRubriques() {

        List<PanelRubrique> listPanelRubriques = new ArrayList<MyPanelBloc.PanelRubrique>();
        if (this.currentItemBloc == null) {
            return listPanelRubriques;
        }

        if (this.panelListRubriques.getComponentCount() > 0) {

            IntStream.range(0, this.panelListRubriques.getComponentCount())
                    .filter(i -> this.panelListRubriques.getComponent(i) instanceof PanelRubrique).forEachOrdered(
                            i -> listPanelRubriques.add((PanelRubrique) this.panelListRubriques.getComponent(i)));
        }
        return listPanelRubriques;
    }

    private void cancelModification() {
        if (this.currentItemBloc == null) {
            return;
        }
        ItemBloc itemBloc = this.currentItemBloc;
        ItemRubrique itemRubrique = this.currentItemRubrique;
        this.clear();

        this.setItemBloc(itemBloc, "", itemRubrique, true);

        this.revalidate();
        this.repaint();
    }

    private void clearListOtherChild() {
        ((DefaultComboBoxModel<KeyAndLibelle>) this.cbOtherChildComboBox.getModel()).removeAllElements();
    }

    void setTreeRoot(BlocTree treeRoot) {
        this.treeRoot = treeRoot;
    }

    /*
     * Return true si une rubrique ou un bloc fils a été modifié ou créé ou
     * supprimé
     */
    boolean isCurrentBlocModified() {

        return this.currentItemBloc != null && this.btValider.isEnabled();
    }

    ItemBloc getCurrentItemBloc() {
        return this.currentItemBloc;
    }

    private void buildTitle(ItemBloc itemBloc, String path) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(this.panelTitle.getBackground());
        panel.setAlignmentX(CENTER_ALIGNMENT);
        this.panelTitle.add(panel);

        JLabel labelTitle = new JLabel(path);
        labelTitle.setForeground(TREE_NORMAL_COLOR);
        panel.add(labelTitle);

        if (itemBloc != null) {
            JLabel labelLibelle = new JLabel(ServiceFactory.getDsnService().getBlocLibelle(itemBloc.getBlocLabel()));
            labelLibelle.setForeground(TREE_NORMAL_COLOR);
            panel.add(labelLibelle);
        }
    }

    /*
     * Point d'entrée principal pour setter un ItemBloc dans le paneau d'edition
     */
    void setItemBloc(ItemBloc itemBloc, String path, ItemRubrique itemRubriqueToSelect, boolean focus) {

        this.clear();

        this.enableButtons(false);
        this.btAddRubrique.setEnabled(!itemBloc.isError());
        this.currentItemBloc = itemBloc;
        this.currentItemRubrique = itemRubriqueToSelect;

        this.buildTitle(itemBloc, path);

        // liste des rubriques
        if (itemBloc.hasRubriques()) {

            IntStream.range(0, itemBloc.getListRubriques().size()).forEachOrdered(i -> {
                ItemRubrique itemRubrique = itemBloc.getListRubriques().get(i);
                this.addPanelRubriqueToList(itemRubrique, itemRubrique == itemRubriqueToSelect, focus);
            });
        }

        // liste des blocs fils
        if (itemBloc.hasChildren()) {

            IntStream.range(0, itemBloc.getChildrens().size()).forEachOrdered(i -> {
                ItemBloc child = itemBloc.getChildrens().get(i);
                this.addPanelChildToList(child);
            });
            this.addVerticalGlueToPanelChildList();
        }

        // Etat des boutons Add et Del des blocs enfants
        if (this.updateListChildrenActions()) {
            this.panelChildrens.setVisible(!itemBloc.isError());
        }

        // Etat du bouton ajouter
        this.btAddRubrique
                .setEnabled(ServiceFactory.getDsnService().hasRubriqueAAJouter(itemBloc, itemBloc.getListRubriques()));

    }



    private List<PanelChild> buildListPanelChildren() {

        List<PanelChild> list = new ArrayList<MyPanelBloc.PanelChild>();
        if (this.panelListChildrens.getComponentCount() > 0) {

            IntStream.range(0, this.panelListChildrens.getComponentCount())
                    .filter(i -> this.panelListChildrens.getComponent(i) instanceof PanelChild)
                    .forEachOrdered(i -> list.add((PanelChild) this.panelListChildrens.getComponent(i)));
        }

        return list;
    }

    private Map<ItemBloc, PanelChild> buildMapPanelChildrens() {

        List<PanelChild> list = this.buildListPanelChildren();
        Map<ItemBloc, PanelChild> mapPanelChildrens = new LinkedHashMap<ItemBloc, PanelChild>();

        list.stream().filter(panelChild -> !panelChild.deleted)
                .forEachOrdered(panelChild -> mapPanelChildrens.put(panelChild.child, panelChild));
        return mapPanelChildrens;
    }

    /**
     * Definit les actions possibles sur les blocs enfants et établit la liste
     * des blocs possible.
     * 
     * @return true si le bloc parent est susceptible d'avoir des bloc enfants
     */
    private boolean updateListChildrenActions() {

        Map<ItemBloc, PanelChild> mapPanelChildrens = this.buildMapPanelChildrens();
        BlocChildrenDto blocChildrenDto = ServiceFactory.getDsnService().determineActionSurListBlocChild(this.treeRoot,
                this.currentItemBloc, mapPanelChildrens.keySet());
        if (Objects.nonNull(blocChildrenDto.getListBlocChildDto())) {

            blocChildrenDto.getListBlocChildDto().stream().forEachOrdered(blocChildDto -> {
                PanelChild panelChild = mapPanelChildrens.get(blocChildDto.getBlocChild());
                panelChild.enableButtons(blocChildDto.isShow(), blocChildDto.isDel(), blocChildDto.isDuplicate());
            });

        }
        this.clearListOtherChild();

        blocChildrenDto.getListOtherBlocLabel().stream().forEachOrdered(otherChildLabel -> {
            DefaultComboBoxModel<KeyAndLibelle> model = (DefaultComboBoxModel<KeyAndLibelle>) this.cbOtherChildComboBox
                    .getModel();
            model.addElement(otherChildLabel);
        });
        this.btAddBloc.setEnabled(blocChildrenDto.hasOtherChild());
        this.cbOtherChildComboBox.setEnabled(blocChildrenDto.hasOtherChild());

        return blocChildrenDto.canHaveChildren();
    }

    private Action getNextRubriqueAction() {

        if (this.nextRubriqueAction == null) {
            this.nextRubriqueAction = new NextRubriquedAction(this);
        }
        return this.nextRubriqueAction;
    }

    private Action getDelChildAction() {
        if (this.delChildAction == null) {
            this.delChildAction = new DelChildAction(this);
        }
        return this.delChildAction;
    }

    private Action getAddChildAction() {
        if (this.addChildAction == null) {
            this.addChildAction = new AddChildAction(this);
        }
        return this.addChildAction;
    }

    private Action getShowChildAction() {
        if (this.showChildAction == null) {
            this.showChildAction = new ShowChildAction(this);
        }
        return this.showChildAction;
    }

    private Action getDuplicateChildAction() {
        if (this.duplicateChildAction == null) {
            this.duplicateChildAction = new DuplicateChildAction(this);
        }
        return this.duplicateChildAction;
    }

    private Action getDelRubriqueAction() {

        if (this.delRubriqueAction == null) {
            this.delRubriqueAction = new DelRubriquedAction(this);
        }
        return this.delRubriqueAction;
    }

    @Override
    public boolean isSourceBtAddBloc(Object obj) {
        return obj == this.btAddBloc;
    }

    @Override
    public void actionValiderSaisie() {
        this.validerSaisie(true);
    }

    @Override
    // ajout d'un bloc enfant par son label à partir de la selection dans la
    // combo box
    public void actionAddOtherChild() {

        KeyAndLibelle childToAdd = (KeyAndLibelle) this.cbOtherChildComboBox.getSelectedItem();
        ItemBloc newChild = ServiceFactory.getDsnService().createNewChild(this.currentItemBloc, childToAdd.getKey());
        this.addChildBloc(newChild, -1, "(new)");
        this.scrollToBottom();
    }

    // voir un bloc enfant dans une fenêtre secondaire
    @Override
    public void actionShowChild(PanelChild panelChild) {

        if (panelChild.child != null) {
            this.mainActionListener.actionShowBlocFrame(panelChild.child);
        }
    }

    @Override
    // ajout d'un bloc enfant de meme label
    public void actionAddChild(PanelChild panelChild) {

        if (panelChild.child != null) {

            int index = this.getIndexOfPanelChild(panelChild);

            ItemBloc newChild = ServiceFactory.getDsnService().createNewChild(panelChild.child);
            this.addChildBloc(newChild, index + 1, "(new)");
        }
    }

    // ajout d'un bloc
    private void addChildBloc(ItemBloc newChild, int index, String comment) {

        this.addPanelChildToList(newChild, index, comment);
        this.enableButtons(true);

        this.updateListChildrenActions();
        this.tabbedPane.setSelectedIndex(TAB_BLOCS);

        this.revalidate();
        this.repaint();
    }

    @Override
    public void actionDuplicateChildWithContirmation(PanelChild panelChild) {

        OptionDuplicateChild respons = this.askOptionForDuplicateChild(panelChild.child);
        if (respons != null) {

            if (panelChild.child != null) {

                int index = this.getIndexOfPanelChild(panelChild);

                ItemBloc newChild = ServiceFactory.getDsnService().createNewChild(panelChild.child,
                        respons.withRubriques, respons.withChildrens);
                this.addChildBloc(newChild, index + 1, "(dupl.)");
            }
        }
    }

    @Override
    public void actionAddRubrique() {

        if (this.currentItemBloc == null) {
            return;
        }

        String rubrique = this.askNumRubrique();

        if (rubrique != null) {

            ItemRubrique itemRubrique = ServiceFactory.getDsnService().createNewRubrique(this.currentItemBloc,
                    rubrique);
            this.addPanelRubriqueToList(itemRubrique, false, true);
            this.selectItemRubrique(itemRubrique, true);
            this.enableButtons(true);
            this.revalidate();
            this.repaint();
        }
    }

    private void addPanelRubriqueToList(ItemRubrique itemRubrique, boolean toBeSelected, boolean toBeFocused) {
        PanelRubrique panelRubrique = new PanelRubrique(itemRubrique, this.getDocumentListener(),
                ServiceFactory.getDsnService().getRubriqueLibelle(itemRubrique));
        this.panelListRubriques.add(panelRubrique);
        panelRubrique.selectRubrique(toBeSelected, toBeFocused);

        if (this.currentItemBloc.isError()) {
            panelRubrique.setReadOnly();
        }

        this.panelListRubriques.add(Box.createRigidArea(new Dimension(0, 5)));

    }

    // ajout d'un PanelChild en fin de liste
    private void addPanelChildToList(ItemBloc itemChild) {
        this.addPanelChildToList(itemChild, -1, null);
    }

    // ajout d'un PanelChild à la position indiquée par l'index
    private void addPanelChildToList(ItemBloc itemChild, int index, String comment) {
        PanelChild panelChild = new PanelChild(itemChild,
                ServiceFactory.getDsnService().getBlocLibelle(itemChild.getBlocLabel()));
        // fonctionnalité de drag and drop (source)
        DragAndDropUtil.get().createDefaultDragGestureRecognizer(panelChild);

        if (comment != null) {
            panelChild.commentLabel.setText(comment);
        }
        index = index < 0 ? this.getIndexOfFinalGlue() - 1 : index;

        if (index > -1) {
            this.panelListChildrens.add(panelChild, index + 1);
            this.panelListChildrens.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5), index + 2);
        } else {

            this.panelListChildrens.add(panelChild);
            this.panelListChildrens.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
        }
    }

    private int getIndexOfFinalGlue() {
        if (this.panelListChildrens == null) {
            return -1;
        }
        for (int i = 0; i < this.panelListChildrens.getComponentCount(); i++) {
            if (this.panelListChildrens.getComponent(i) == this.childrenVerticalGlue) {
                return i;
            }

        }
        return -1;
    }

    private void addVerticalGlueToPanelChildList() {
        this.panelListChildrens.add(this.childrenVerticalGlue);
    }

    @Override
    public void actionDeleteRubrique(PanelRubrique panelRubrique) {

        int index = this.getIndexOfPanelRubrique(panelRubrique);
        if (index != -1) {

            panelRubrique.deleted = true;
            this.panelListRubriques.getComponent(index).setVisible(false);
            this.panelListRubriques.getComponent(index + 1).setVisible(false);
            this.enableButtons(true);

            this.revalidate();
            this.repaint();

        }
    }

    @Override
    public void actionDeleteChildWithConfirmation(PanelChild panelChild) {

        int index = this.getIndexOfPanelChild(panelChild);
        if (index != -1) {

            if (!panelChild.child.isCreated()) {

                int respons = JOptionPane.showConfirmDialog(this,
                        "Voulez-vous supprimer le bloc " + panelChild.child.toString()
                                + "\nainsi que toutes ses rubriques et ses blocs enfants ?",
                        "Suppression du bloc enfant", JOptionPane.WARNING_MESSAGE);
                if (respons == JOptionPane.OK_OPTION) {
                    this.actionDeleteChild(panelChild, index);
                }
            } else {
                this.actionDeleteChild(panelChild, index);
            }
        }
    }

    private void actionDeleteChild(PanelChild panelChild, int index) {

        panelChild.deleted = true;
        this.panelListChildrens.getComponent(index).setVisible(false);
        this.panelListChildrens.getComponent(index + 1).setVisible(false);
        this.enableButtons(true);

        this.updateListChildrenActions();

        this.revalidate();
        this.repaint();
    }

    private int getIndexOfPanelChild(PanelChild panelChild) {

        if (this.panelListChildrens.getComponentCount() > 0) {
            for (int i = 0; i < this.panelListChildrens.getComponentCount(); i++) {

                if (this.panelListChildrens.getComponent(i) == panelChild) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int getIndexOfPanelRubrique(PanelRubrique panelRubrique) {

        if (this.panelListRubriques.getComponentCount() > 0) {
            for (int i = 0; i < this.panelListRubriques.getComponentCount(); i++) {

                if (this.panelListRubriques.getComponent(i) == panelRubrique) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void actionAnnulerSaisie() {
        cancelModification();
        ListItemBlocListenerManager.get().onItemBlocModified(currentItemBloc, ModifiedState.annuler, true);
        enableButtons(false);
    }

    @Override
    public void actionNextRubrique(PanelRubrique panelRubrique) {
        panelRubrique.selectRubrique(false, false);
        if (this.panelListRubriques.getComponentCount() > 0) {
            boolean selectNext = false;
            for (int i = 0; i < this.panelListRubriques.getComponentCount(); i++) {

                Component component = this.panelListRubriques.getComponent(i);
                if (!(component instanceof PanelRubrique)) {
                    continue;
                }

                if (selectNext) {
                    ((PanelRubrique) this.panelListRubriques.getComponent(i)).selectRubrique(true, true);
                    selectNext = false;
                } else if (this.panelListRubriques.getComponent(i) == panelRubrique) {

                    selectNext = true;
                }

            }
            if (selectNext) {
                ((PanelRubrique) this.panelListRubriques.getComponent(0)).selectRubrique(true, true);
            }
        }
    }

    void selectItemRubrique(ItemRubrique itemRubrique, boolean focus) {

        List<PanelRubrique> list = this.buildListPanelRubriques();
        for (PanelRubrique panelRubrique : list) {
            if (panelRubrique.itemRubrique == itemRubrique) {
                panelRubrique.selectRubrique(true, focus);
            } else {
                panelRubrique.selectRubrique(false, false);
            }
        }
    }

    //
    private String askNumRubrique() {

        final JComboBox<KeyAndLibelle> cb = new JComboBox<>();
        List<KeyAndLibelle> listRubriqueAAjouter = ServiceFactory.getDsnService()
                .determineListRubriqueAAjouter(this.getCurrentItemBloc(), this.getListItemRubriquesFromPanel());
        DefaultComboBoxModel<KeyAndLibelle> model = (DefaultComboBoxModel<KeyAndLibelle>) cb.getModel();
        listRubriqueAAjouter.stream().forEachOrdered(keyAndLibelle -> model.addElement(keyAndLibelle));

        JPanel panel = new JPanel();
        panel.add(new JLabel("Num: "));
        panel.add(cb);

        int option = MyJOptionPane.showOptionDialog(null, panel, "Numéro de rubrique", MyJOptionPane.OK_CANCEL_OPTION,
                MyJOptionPane.QUESTION_MESSAGE, null, null, cb);
        if (option == MyJOptionPane.OK_OPTION) {
            KeyAndLibelle rubToAdd = (KeyAndLibelle) cb.getSelectedItem();
            return rubToAdd.getKey();
        }
        return null;

    }

    private void updateEtatBoutonAddRubriques() {

        boolean rubAAjouter = ServiceFactory.getDsnService().hasRubriqueAAJouter(this.getCurrentItemBloc(),
                this.getListItemRubriquesFromPanel());
        this.btAddRubrique.setEnabled(rubAAjouter);
    }

    private OptionDuplicateChild askOptionForDuplicateChild(ItemBloc childBloc) {

        JLabel label = new JLabel("Dupliquer le bloc " + childBloc.toString());
        JCheckBox cbWithRubriques = new JCheckBox("Avec rubriques");
        cbWithRubriques.setSelected(true);
        JCheckBox cbWithChildrens = new JCheckBox("Avec blocs enfants");
        cbWithChildrens.setSelected(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(cbWithRubriques);
        panel.add(Box.createVerticalStrut(5));
        panel.add(cbWithChildrens);

        int option = MyJOptionPane.showOptionDialog(this, panel, "Duplication bloc enfant",
                MyJOptionPane.OK_CANCEL_OPTION, MyJOptionPane.QUESTION_MESSAGE, null, null, null);
        if (option == MyJOptionPane.OK_OPTION) {
            return new OptionDuplicateChild(childBloc, cbWithRubriques.isSelected(), cbWithChildrens.isSelected());
        }
        return null;
    }

    // ================================================= INNER CLASS
    private final class OptionDuplicateChild {
        private final ItemBloc childBloc;
        private final boolean withRubriques;
        private final boolean withChildrens;

        private OptionDuplicateChild(ItemBloc childBlocToDuplicate, boolean withRubriques, boolean withChildrens) {
            this.childBloc = childBlocToDuplicate;
            this.withRubriques = withRubriques;
            this.withChildrens = withChildrens;
        }
    }

    private final class MyLabel extends JLabel {
        private MyLabel(String label) {
            super(label);
            this.setForeground(Color.BLUE);
        }
    }

    public class PanelChild extends JPanel {

        private static final long serialVersionUID = 1L;

        private JPanel mainPanel;

        private JLabel labBlocLabel;
        private JLabel commentLabel;

        private final StateButton btDelBloc = new StateButton();
        private final StateButton btDuplicateBloc = new StateButton();
        private final StateButton btShowBloc = new StateButton();

        private Color originalBackgroundColor = Color.white;

        private final ItemBloc child;
        private boolean deleted = false;

        private boolean isDeleted() {
            return this.deleted;
        }

        private boolean isCreated() {
            return this.child.isCreated();
        }

        private PanelChild(ItemBloc child, String tooltipText) {
            this.child = child;

            this.setBackground(TREE_BACKGROUND_COLOR);
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.createLabelDescription(tooltipText, this, Component.LEFT_ALIGNMENT);
            this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
            this.createMainPanel(this, Component.LEFT_ALIGNMENT);
        }

        private void createLabelDescription(String tooltipText, Container container, float alignment) {
            if (tooltipText != null) {
                JLabel labDescription = new JLabel(tooltipText);
                labDescription.setForeground(Color.lightGray);
                container.add(labDescription);
                labDescription.setAlignmentX(alignment);
            }
        }
        private void createMainPanel(Container container, float alignment) {

            this.mainPanel = new JPanel();
            this.originalBackgroundColor = this.mainPanel.getBackground();

            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
            Dimension size = new Dimension(300, 40);
            mainPanel.setMinimumSize(size);
            mainPanel.setPreferredSize(size);
            mainPanel.setMaximumSize(size);
            mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.cyan, 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));

            this.addBlocLabel(mainPanel);
            this.addButtons(mainPanel);
            this.addCommentLabel(mainPanel);
            mainPanel.add(Box.createGlue());

            container.add(mainPanel);
            mainPanel.setAlignmentX(alignment);
        }

        public void restoreOriginalBackground() {
            this.mainPanel.setBackground(originalBackgroundColor);
        }

        @Override
        public void setBackground(Color bg) {
            if (this.mainPanel != null) {
                this.mainPanel.setBackground(bg);
            }
        }

        public ItemBloc getItemBloc() {
            return this.child;
        }

        private void waitEndAction() {
            this.btDelBloc.waitEndAction();
            this.btDuplicateBloc.waitEndAction();
            this.btShowBloc.waitEndAction();
        }

        private void currentActionEnded() {
            this.btDelBloc.actionEnded();
            this.btDuplicateBloc.actionEnded();
            this.btShowBloc.actionEnded();
        }

        private void enableButtons(boolean showEnabled, boolean delEnabled, boolean duplicateEnabled) {
            this.btShowBloc.setEnabled(showEnabled);
            this.btDuplicateBloc.setEnabled(duplicateEnabled);
            this.btDelBloc.setEnabled(delEnabled);
        }

        private void addButtons(Container container) {

            this.btDelBloc.setFunctionnalContainer(this);
            container.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));
            this.addButton(this.btDelBloc, container, getDelChildAction(),
                    "supprimer ce bloc enfant ".concat(child.toString()), PATH_DEL_ICO);
            container.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));

            this.btDuplicateBloc.setFunctionnalContainer(this);
            this.addButton(this.btDuplicateBloc, container, getDuplicateChildAction(),
                    "dupliquer le bloc ".concat(child.toString()).concat(" ..."), PATH_DUPLICATE_ICO);
            container.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));

            this.btShowBloc.setFunctionnalContainer(this);
            this.addButton(this.btShowBloc, container, getShowChildAction(), "Voir le bloc ".concat(child.toString()),
                    PATH_SHOW_BLOC_ICO);

        }

        private void addButton(JButton button, Container container, Action action, String tooltip, String iconPath) {

            button.setAction(action);
            button.setMaximumSize(DIM_BUTTON_SMALL);
            button.setIcon(GuiUtils.createImageIcon(iconPath));
            button.setToolTipText(tooltip);
            button.setEnabled(false);
            container.add(button);
        }

        private void addBlocLabel(Container container) {
            this.labBlocLabel = new JLabel(this.child.toString());
            this.labBlocLabel.setPreferredSize(new Dimension(40, 20));
            container.add(this.labBlocLabel);
        }

        private void addCommentLabel(Container container) {
            container.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));
            this.commentLabel = new JLabel();
            container.add(this.commentLabel);
        }

    }

    public class PanelRubrique extends JPanel {

        private static final long serialVersionUID = 1L;
        private JLabel labRubriqueLabel;
        private StateTextField tfRubriqueValue;
        private Color originForeground;
        private Color originBackground;

        private final StateButton btDelRubrique = new StateButton();

        private final ItemRubrique itemRubrique;
        private final DocumentListener mydocumentListener;
        private boolean deleted = false;

        // --------------------------- constructor
        private PanelRubrique(ItemRubrique itemRubrique, DocumentListener documentListener, String tooltipText) {
            this.itemRubrique = itemRubrique;
            this.mydocumentListener = documentListener;
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBackground(TREE_BACKGROUND_COLOR);
            this.createLabelDescription(tooltipText, this, Component.LEFT_ALIGNMENT);
            this.add(Box.createRigidArea(DIM_VER_RIGID_AREA_5));
            this.createPanelLabelTexfieldAndButton(this, Component.LEFT_ALIGNMENT);
        }

        private void waitEndAction() {
            this.btDelRubrique.waitEndAction();
            this.tfRubriqueValue.waitEndAction();
        }

        private void currentActionEnded() {
            this.btDelRubrique.actionEnded();
            this.tfRubriqueValue.actionEnded();
        }

        private boolean isDeleted() {
            return this.deleted;
        }

        private boolean isModified() {
            return !this.itemRubrique.getValue().equals(this.getValue());
        }

        private boolean isCreated() {
            return this.itemRubrique.isCreated();
        }

        private void createLabelDescription(String tooltipText, Container container, float alignment) {
            if (tooltipText != null) {
                JLabel labDescription = new JLabel(tooltipText);
                labDescription.setForeground(Color.lightGray);
                container.add(labDescription);
                labDescription.setAlignmentX(alignment);
            }
        }

        private void createPanelLabelTexfieldAndButton(Container container, float alignment) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            this.addLabelAndTextfield(panel);
            this.addButtons(panel);
            this.add(Box.createHorizontalGlue());

            container.add(panel);
            panel.setAlignmentX(alignment);
        }

        private void addLabelAndTextfield(Container container) {

            this.labRubriqueLabel = new JLabel(itemRubrique.getBlocAndRubriqueLabel());

            this.tfRubriqueValue = new StateTextField(20);
            this.tfRubriqueValue.setFunctionnalContainer(this);
            this.tfRubriqueValue.setMaximumSize(new Dimension(400, 20));
            this.tfRubriqueValue.setMinimumSize(new Dimension(100, 20));

            this.originForeground = this.tfRubriqueValue.getForeground();
            this.originBackground = this.tfRubriqueValue.getBackground();

            this.setValue(itemRubrique.getValue());
            this.tfRubriqueValue.getDocument().addDocumentListener(this.mydocumentListener);

            container.add(this.labRubriqueLabel);
            container.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));
            container.add(this.tfRubriqueValue);

            InputMap im = this.tfRubriqueValue.getInputMap(JComponent.WHEN_FOCUSED);
            ActionMap am = this.tfRubriqueValue.getActionMap();
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_RUBRIQUE_ACTION);
            am.put(NEXT_RUBRIQUE_ACTION, MyPanelBloc.this.getNextRubriqueAction());
        }

        private void addButtons(Container container) {

            container.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));

            this.btDelRubrique.setAction(getDelRubriqueAction());
            this.btDelRubrique.setMaximumSize(DIM_BUTTON_SMALL);
            this.btDelRubrique.setIcon(GuiUtils.createImageIcon(PATH_DEL_ICO));
            this.btDelRubrique.setFunctionnalContainer(this);
            container.add(this.btDelRubrique);
        }

        private void selectRubrique(boolean select, boolean focus) {
            if (select && focus) {
                this.tfRubriqueValue.requestFocusInWindow();
            }

            if (select) {
                this.tfRubriqueValue.setBackground(EDIT_COLOR);
                this.tfRubriqueValue.setForeground(Color.WHITE);
                this.tfRubriqueValue.setCaretColor(Color.WHITE);
            } else {
                this.tfRubriqueValue.setBackground(this.originBackground);
                this.tfRubriqueValue.setForeground(this.originForeground);
                this.tfRubriqueValue.setCaretColor(this.originForeground);
            }
        }

        private void setReadOnly() {
            this.tfRubriqueValue.setEnabled(false);
            this.btDelRubrique.setEnabled(false);

        }

        private String getValue() {
            return this.tfRubriqueValue.getText();
        }

        private void setValue(String value) {
            this.tfRubriqueValue.setText(value);
        }
    }

}
