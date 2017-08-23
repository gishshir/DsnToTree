package fr.tsadeo.app.dsntotree.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBloc extends AbstractItemTree implements Comparable<ItemBloc> {

    private boolean root = false;

    private final String prefix;
    private final String blocLabel;
    private ItemBloc parent;

    private boolean childrenModified = false;
    // si plusieurs blocs enfant de meme label, index
    private int index = 0;
    // ordre des blocLabel entre eux
    private int order = 0;

    // groupement des blocs enfants par nom de bloc
    private Map<String, List<ItemBloc>> mapBlocLabelToListChildren;

    private List<ItemRubrique> listRubriques;
    private List<ItemBloc> childrens;

    // ------------------------------- constructor

    public ItemBloc(int numLine, String prefix, String blocLabel) {
        this.setNumLine(numLine);
        this.prefix = prefix;
        this.blocLabel = blocLabel;
    }

    // ---------------------------------- implementing IItemTree
    @Override
    public boolean isBloc() {
        return true;
    }

    @Override
    public boolean isRubrique() {
        return false;
    }

    // --------------------------------- public methods

    // -------------------------------- accessor

    public boolean isRoot() {
        return root;
    }

    public boolean isChildrenModified() {
        return childrenModified;
    }

    public void setChildrenModified(boolean childrenModified) {
        this.childrenModified = childrenModified;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public String getPrefix() {
        return prefix;
    }

    /*
     * Fonction recursive Determine si itemBloc est un ancetre
     */
    public boolean isAncestorBloc(ItemBloc itemBloc) {

        if (this.parent == null) {
            return false;
        }
        if (parent == itemBloc) {
            return true;
        } else {
            return this.parent.isAncestorBloc(itemBloc);
        }
    }

    public ItemRubrique getFirstRubrique() {
        if (this.listRubriques == null || this.listRubriques.isEmpty()) {
            return null;
        }
        return this.listRubriques.get(0);
    }

    public ItemRubrique getLastRubrique() {
        if (this.listRubriques == null || this.listRubriques.isEmpty()) {
            return null;
        }
        return this.listRubriques.get(this.listRubriques.size() - 1);
    }

    public ItemBloc getParent() {
        return parent;
    }

    public void addRubrique(ItemRubrique itemRubrique) {
        if (this.listRubriques == null) {
            this.listRubriques = new ArrayList<ItemRubrique>();
        }
        this.listRubriques.add(itemRubrique);
        itemRubrique.setBlocContainer(this);
    }

    public List<ItemRubrique> getListRubriques() {
        if (this.listRubriques == null) {
            this.listRubriques = new ArrayList<ItemRubrique>();
        }
        return listRubriques;
    }

    public boolean hasRubriques() {
        return this.listRubriques != null && !this.listRubriques.isEmpty();
    }

    public void clearChildrens() {
        if (this.hasChildren()) {
            this.getChildrens().clear();
            this.mapBlocLabelToListChildren = null;
        }
    }

    public List<String> getListLabelChildrens() {
        return this.mapBlocLabelToListChildren == null ? null
                : new ArrayList<String>(this.mapBlocLabelToListChildren.keySet());
    }

    public List<ItemBloc> getChildrens(String blocLabel) {
        return mapBlocLabelToListChildren == null ? null : this.mapBlocLabelToListChildren.get(blocLabel);
    }

    public List<ItemBloc> getChildrens() {
        if (this.childrens == null) {
            this.childrens = new ArrayList<ItemBloc>();
        }
        return childrens;
    }

    public boolean hasChildren() {
        return this.childrens != null && !this.childrens.isEmpty();
    }

    public void addFirstChild(ItemBloc child) {
        this.addChild(child, 0);
    }

    public void addChild(ItemBloc child) {
        this.addChild(child, -1);

    }

    private void addChild(ItemBloc child, int index) {
        if (this.childrens == null) {
            this.childrens = new ArrayList<ItemBloc>();
        }
        if (index >= 0) {
            this.childrens.add(index, child);
        } else {
            this.childrens.add(child);
        }
        child.parent = this;

        if (this.mapBlocLabelToListChildren == null) {
            this.mapBlocLabelToListChildren = new HashMap<String, List<ItemBloc>>();
        }
        List<ItemBloc> listChildrenSameBloc = this.mapBlocLabelToListChildren.get(child.getBlocLabel());
        if (listChildrenSameBloc == null) {
            listChildrenSameBloc = new ArrayList<ItemBloc>();
            this.mapBlocLabelToListChildren.put(child.getBlocLabel(), listChildrenSameBloc);
        }
        listChildrenSameBloc.add(child);

    }

    public String getBlocLabel() {
        return blocLabel;
    }

    public void showChildrens(String prefix, StringBuffer sb) {
        sb.append("\n");
        sb.append(prefix).append("Bloc ").append(this.toString()).append("\n");
        if (this.listRubriques != null && !this.listRubriques.isEmpty()) {
            for (ItemRubrique itemRubrique : listRubriques) {
                sb.append(prefix).append(itemRubrique).append("\n");
            }
        }
        if (this.childrens != null && !this.childrens.isEmpty()) {

            for (ItemBloc child : childrens) {
                child.showChildrens("\t" + prefix, sb);
            }

        }
    }

    private String showIndex() {
        return this.index > 0 ? " [" + this.index + "]" : "";
    }

    private String showNumLine() {
        return (this.getNumLine() > 0) ? "ligne: ".concat(Integer.toString(this.getNumLine())).concat(" - ") : "";
    }

    private String showError() {
        return (this.isError()) ? this.showNumLine().concat(this.blocLabel) : "";
    }

    private String showLabel() {
        return (this.isError()) ? "" : this.blocLabel.concat(this.showIndex());
    }

    // ----------------------------- overriding Object
    @Override
    public String toString() {

        return this.showError().concat(this.showLabel());
    }

    // ------------------------------ Implementing Comparable
    @Override
    public int compareTo(ItemBloc o) {

        if (o == null) {
            return -1;
        }
        if (this == o) {
            return 0;
        }
        if (this.getBlocLabel().equals(o.getBlocLabel())) {
            return Integer.compare(this.index, o.index);
        } else {
            return Integer.compare(this.order, o.order);
        }
    }

}
