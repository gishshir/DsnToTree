package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.MyJOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import fr.tsadeo.app.dsntotree.dto.BlocChildDto;
import fr.tsadeo.app.dsntotree.dto.BlocChildrenDto;
import fr.tsadeo.app.dsntotree.gui.ItemBlocListener.ModifiedState;
import fr.tsadeo.app.dsntotree.gui.action.AddChildAction;
import fr.tsadeo.app.dsntotree.gui.action.AddRubriquedAction;
import fr.tsadeo.app.dsntotree.gui.action.CancelAction;
import fr.tsadeo.app.dsntotree.gui.action.DelChildAction;
import fr.tsadeo.app.dsntotree.gui.action.DelRubriquedAction;
import fr.tsadeo.app.dsntotree.gui.action.DuplicateChildAction;
import fr.tsadeo.app.dsntotree.gui.action.NextRubriquedAction;
import fr.tsadeo.app.dsntotree.gui.action.PatternFilter;
import fr.tsadeo.app.dsntotree.gui.action.ShowChildAction;
import fr.tsadeo.app.dsntotree.gui.action.ValiderAction;
import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;

public class MyPanelBloc extends JPanel implements IGuiConstants, IBlocActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Pattern PATTERN_NUM_RUBRIQUE = Pattern.compile("[0-9]{1,3}");

    private static final Dimension DIM_BUTTON_SMALL = new Dimension(16, 20);

    private BlocTree treeRoot;
    
    private JTabbedPane tabbedPane;

    private JPanel panelBloc;
    private JPanel panelChildrens;
    private JPanel panelListRubriques;
    private JPanel panelListChildrens;
    private JPanel panelOtherChildrens;

    private Component childrenVerticalGlue = Box.createVerticalGlue();

    private JButton btValider;
    private JButton btAnnuler;
    private JButton btAddRubrique;
    private JButton btAddBloc;

    private JComboBox<String> cbOtherChildLabel;

    private ItemBloc currentItemBloc;
    private ItemRubrique currentItemRubrique;
    private int currentTreeRowBloc;

    private Action nextRubriqueAction;
    private Action delRubriqueAction;

    private Action showChildAction;
    private Action addChildAction;
    private Action duplicateChildAction;
    private Action delChildAction;

    private final ItemBlocListener itemBlocListener;
    private DocumentListener documentListener;

    public MyPanelBloc(ItemBlocListener itemBlocListener) {
        this.itemBlocListener = itemBlocListener;
        this.setLayout(new BorderLayout());
        this.setBackground(TREE_BACKGROUND_COLOR);

        this.createTopPanel(this, BorderLayout.PAGE_START);
        this.createMiddlePanel(this, BorderLayout.CENTER);
        this.createBottomPanel(this, BorderLayout.PAGE_END);
    }

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
        
        JScrollPane scrollPanelBloc = new JScrollPane(middlePanel);
        container.add(scrollPanelBloc, layout);
    }
    
    private void createTabbedPane (Container container, String layout) {
    	
    	this.tabbedPane = new JTabbedPane();
        container.add(this.tabbedPane, layout);
        
        this.tabbedPane.addTab("rubriques", this.createListRubriquePanel());
        this.tabbedPane.addTab("blocs enfants", this.createChildrenPanel());

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
        this.btValider = new JButton();
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

        this.cbOtherChildLabel = new JComboBox<String>();
        this.cbOtherChildLabel.setModel(new DefaultComboBoxModel<String>());
        Dimension size = new Dimension(100, 20);
        this.cbOtherChildLabel.setPreferredSize(size);
        this.cbOtherChildLabel.setMaximumSize(size);

        this.cbOtherChildLabel.setBackground(Color.white);
        container.add(this.cbOtherChildLabel);
    }

    private void createButtonNewBloc(Container container) {
        this.btAddBloc = new JButton();

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
        this.btAddRubrique = new JButton();
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
        this.btAnnuler = new JButton();
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

        this.panelBloc = new JPanel();
        this.panelBloc.setMinimumSize(container.getSize());
        this.panelBloc.setBackground(TREE_BACKGROUND_COLOR);
        this.panelBloc.setForeground(TREE_NORMAL_COLOR);

        this.panelBloc.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(Color.BLUE)));
        container.add(this.panelBloc, layout);
    }

    private JPanel createListRubriquePanel() {

        this.panelListRubriques = new JPanel();
        this.panelListRubriques.setLayout(new BoxLayout(this.panelListRubriques, BoxLayout.Y_AXIS));
        this.panelListRubriques.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.panelListRubriques.setBackground(TREE_BACKGROUND_COLOR);

        return this.panelListRubriques;
    }

    private void createOtherChildPanel(Container container, String layout) {

        this.panelOtherChildrens = new JPanel();
        container.add(this.panelOtherChildrens, layout);

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

    }

    private JPanel createChildrenPanel() {

        this.panelChildrens = new JPanel();
        this.panelChildrens.setLayout(new BorderLayout());
        this.panelChildrens.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.panelChildrens.add(new MyLabel("Liste des blocs enfants..."), BorderLayout.PAGE_START);

        this.createListChildrenPanel(this.panelChildrens, BorderLayout.LINE_START);
        this.createOtherChildPanel(this.panelChildrens, BorderLayout.CENTER);

        this.panelChildrens.setVisible(false);
        
        return this.panelChildrens;

    }

    private void createListChildrenPanel(Container container, String layout) {

        this.panelListChildrens = new JPanel();
        this.panelListChildrens.setLayout(new BoxLayout(this.panelListChildrens, BoxLayout.Y_AXIS));
        this.panelListChildrens.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        container.add(this.panelListChildrens, layout);
    }

    private void enableButtons(boolean enabled) {
        this.btAnnuler.setEnabled(enabled);
        this.btValider.setEnabled(enabled);
        this.btAddRubrique.setEnabled(enabled);
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

            for (int i = 0; i < this.panelListRubriques.getComponentCount(); i++) {

                if (this.panelListRubriques.getComponent(i) instanceof PanelRubrique) {
                    listPanelRubriques.add((PanelRubrique) this.panelListRubriques.getComponent(i));
                }
            }
        }
        return listPanelRubriques;
    }

    private void cancelModification() {
        if (this.currentItemBloc == null) {
            return;
        }
        ItemBloc itemBloc = this.currentItemBloc;
        int treeRowBloc = this.currentTreeRowBloc;
        ItemRubrique itemRubrique = this.currentItemRubrique;
        this.clear();

        this.setItemBloc(itemBloc, treeRowBloc, "", itemRubrique, true);

        this.revalidate();
        this.repaint();
    }

    void clear() {

        this.currentItemBloc = null;
        this.currentTreeRowBloc = -1;

        this.panelBloc.removeAll();
        this.panelChildrens.setVisible(false);
        this.panelListRubriques.removeAll();
        this.panelListChildrens.removeAll();

        this.clearListOtherChild();

        this.nextRubriqueAction = null;
        this.delRubriqueAction = null;

    }

    private void clearListOtherChild() {
        ((DefaultComboBoxModel<String>) this.cbOtherChildLabel.getModel()).removeAllElements();
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

    void setItemBloc(ItemBloc itemBloc, int treeRowBloc, String pathParent, ItemRubrique itemRubriqueToSelect,
            boolean focus) {

        this.clear();

        this.enableButtons(false);
        this.btAddRubrique.setEnabled(!itemBloc.isError());
        this.currentItemBloc = itemBloc;
        this.currentItemRubrique = itemRubriqueToSelect;
        this.currentTreeRowBloc = treeRowBloc;

        JLabel labelTitle = new JLabel(pathParent);
        labelTitle.setForeground(TREE_NORMAL_COLOR);
        this.panelBloc.add(labelTitle);

        JLabel labelBloc = new JLabel("Bloc " + itemBloc.toString());
        labelBloc.setForeground(TREE_NORMAL_COLOR);
        this.panelBloc.add(labelBloc);

        // liste des rubriques
        if (itemBloc.hasRubriques()) {

            for (int i = 0; i < itemBloc.getListRubriques().size(); i++) {

                ItemRubrique itemRubrique = itemBloc.getListRubriques().get(i);
                this.addPanelRubriqueToList(itemRubrique, itemRubrique == itemRubriqueToSelect, focus);
            }
        }

        // liste des blocs fils
        if (itemBloc.hasChildren()) {

            for (int i = 0; i < itemBloc.getChildrens().size(); i++) {

                ItemBloc child = itemBloc.getChildrens().get(i);
                this.addPanelChildToList(child);
            }
            this.addVerticalGlueToPanelChildList();
        }

        // Etat des boutons Add et Del des blocs enfants
        if (this.updateListChildrenActions()) {
            this.panelChildrens.setVisible(!itemBloc.isError());
        }
    }

    private List<PanelChild> buildListPanelChildren() {

        List<PanelChild> list = new ArrayList<MyPanelBloc.PanelChild>();
        if (this.panelListChildrens.getComponentCount() > 0) {

            for (int i = 0; i < this.panelListChildrens.getComponentCount(); i++) {

                if (this.panelListChildrens.getComponent(i) instanceof PanelChild) {

                    list.add((PanelChild) this.panelListChildrens.getComponent(i));
                }
            }
        }

        return list;
    }

    private Map<ItemBloc, PanelChild> buildMapPanelChildrens() {

        List<PanelChild> list = this.buildListPanelChildren();
        Map<ItemBloc, PanelChild> mapPanelChildrens = new LinkedHashMap<ItemBloc, PanelChild>();

        for (PanelChild panelChild : list) {
            if (panelChild.deleted) {
                continue; // next
            }
            mapPanelChildrens.put(panelChild.child, panelChild);
        }
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
        if (blocChildrenDto.getListBlocChildDto() != null) {
            for (BlocChildDto blocChildDto : blocChildrenDto.getListBlocChildDto()) {
                PanelChild panelChild = mapPanelChildrens.get(blocChildDto.getBlocChild());
                panelChild.enableButtons(blocChildDto.isAdd(), blocChildDto.isDel(), blocChildDto.isDuplicate());
            }

        }
        this.clearListOtherChild();
        for (String otherChildLabel : blocChildrenDto.getListOtherBlocLabel()) {
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) this.cbOtherChildLabel.getModel();
            model.addElement(otherChildLabel);
        }
        this.btAddBloc.setEnabled(blocChildrenDto.hasOtherChild());
        this.cbOtherChildLabel.setEnabled(blocChildrenDto.hasOtherChild());

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

    void validerSaisie(boolean refresh) {
        populateItemBlocFromSaisie();
        itemBlocListener.onItemBlocModified(currentItemBloc, currentTreeRowBloc, ModifiedState.valider, refresh);
        enableButtons(false);
    }

    @Override
    // ajout d'un bloc enfant par son label à partir de la selection dans la
    // combo box
    public void actionAddOtherChild() {

        String childLabel = this.cbOtherChildLabel.getSelectedItem().toString();
        ItemBloc newChild = ServiceFactory.getDsnService().createNewChild(this.currentItemBloc, childLabel);
        this.addChildBloc(newChild, -1, "(new)");
    }

    // voir un bloc enfant dans une fenêtre secondaire
    @Override
    public void actionShowChild(PanelChild panelChild) {
    	
    	//TODO
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
        this.revalidate();
        this.repaint();
    }

    @Override
    public void actionDuplicateChildWithContirmation(PanelChild panelChild) {

        OptionDuplicateChild respons = this.askOptionForDuplicateChild(panelChild.child);
        if (respons != null) {

            System.out.println("do duplicate...");
            System.out.println("with rubriques: " + respons.withRubriques);
            System.out.println("with childrens: " + respons.withChildrens);

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
        PanelRubrique panelRubrique = new PanelRubrique(itemRubrique, this.getDocumentListener());
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
        PanelChild panelChild = new PanelChild(itemChild);
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
        itemBlocListener.onItemBlocModified(currentItemBloc, currentTreeRowBloc, ModifiedState.annuler, true);
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

        final JTextField tf = new JTextField(5) {

            private static final long serialVersionUID = 1L;

            public void addNotify() {
                super.addNotify();
                requestFocus();
            }
        };
        ((AbstractDocument) tf.getDocument()).setDocumentFilter(new PatternFilter(PATTERN_NUM_RUBRIQUE));

        JPanel panel = new JPanel();
        panel.add(new JLabel("Num: "));
        panel.add(tf);

        int option = MyJOptionPane.showOptionDialog(null, panel, "Numéro de rubrique", MyJOptionPane.OK_CANCEL_OPTION,
                MyJOptionPane.QUESTION_MESSAGE, null, null, tf);
        if (option == MyJOptionPane.OK_OPTION) {
            return String.format("%03d", Integer.parseInt(tf.getText()));
        }
        return null;

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
        private JLabel labBlocLabel;
        private JLabel commentLabel;

        private final JButton btDelBloc = new JButton();
        private final JButton btDuplicateBloc = new JButton();
      private final JButton btShowBloc = new JButton();

        private final ItemBloc child;
        private boolean deleted = false;

        private boolean isDeleted() {
            return this.deleted;
        }

        private boolean isCreated() {
            return this.child.isCreated();
        }

        private PanelChild(ItemBloc child) {
            this.child = child;
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            Dimension size = new Dimension(250, 40);
            this.setMinimumSize(size);
            this.setPreferredSize(size);
            this.setMaximumSize(size);
            this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.cyan, 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));

            this.addBlocLabel(this);
            this.addButtons(this);
            this.addCommentLabel(this);
            this.add(Box.createGlue());
        }

        private void enableButtons(boolean showEnabled, boolean delEnabled, boolean duplicateEnabled) {
//            this.btShowBloc.setEnabled(showEnabled);
            this.btDuplicateBloc.setEnabled(duplicateEnabled);
            this.btDelBloc.setEnabled(delEnabled);
        }

        private void addButtons(Container container) {

            container.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));
            this.addButton(this.btDelBloc, container, getDelChildAction(),
                    "supprimer ce bloc enfant ".concat(child.toString()), PATH_DEL_ICO);
            container.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));
            
            
            this.addButton(this.btDuplicateBloc, container, getDuplicateChildAction(),
                    "dupliquer le bloc ".concat(child.toString()).concat(" ..."), PATH_DUPLICATE_ICO);
            container.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));
            
//            this.addButton(this.btShowBloc, container, getShowChildAction(),
//                    "Voir le bloc ".concat(child.toString()), PATH_SHOW_BLOC_ICO);

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
        private JTextField tfRubriqueValue;
        private Color originForeground;
        private Color originBackground;

        private final JButton btDelRubrique = new JButton();

        private final ItemRubrique itemRubrique;
        private boolean deleted = false;

        // --------------------------- constructor
        private PanelRubrique(ItemRubrique itemRubrique, DocumentListener documentListener) {
            this.itemRubrique = itemRubrique;
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            this.addLabelAndTextfield(this, documentListener);
            this.addButtons(this);
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

        private void addLabelAndTextfield(Container container, DocumentListener documentListener) {

            this.labRubriqueLabel = new JLabel(itemRubrique.getBlocAndRubriqueLabel());
            this.tfRubriqueValue = new JTextField(20);
            this.tfRubriqueValue.setMaximumSize(new Dimension(400, 20));
            this.tfRubriqueValue.setMinimumSize(new Dimension(100, 20));
            this.originForeground = this.tfRubriqueValue.getForeground();
            this.originBackground = this.tfRubriqueValue.getBackground();

            this.setValue(itemRubrique.getValue());
            this.tfRubriqueValue.getDocument().addDocumentListener(documentListener);

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
