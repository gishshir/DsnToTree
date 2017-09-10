package fr.tsadeo.app.dsntotree.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.tsadeo.app.dsntotree.gui.MyTree;

/**
 * The Class BlocDependencies.
 */
public class BlocTree {
	
	private static final Logger LOG = Logger.getLogger(BlocTree.class.getName());

    /** Father block name property. */
    private String blocLabel;

    private CardinaliteEnum cardinalite;

    private boolean actif;

    private BlocTree parent;
    private List<BlocTree> childrens;
    
    //map child.blocLabel to child order
    private Map<String, Integer> mapChildLabelToOrder = new HashMap<String, Integer>();

    /**
     * Instantiates a new bloc dependencies.
     */
    public BlocTree() {
        // Constructor
    }

    /**
     * Instantiates a new bloc dependencies.
     *
     * @param blocLabel
     *            the bloc rattachement
     */
    public BlocTree(String blocLabel) {
        this.blocLabel = blocLabel;
    }

    public void setBlocLabel(String blocLabel) {
        this.blocLabel = blocLabel;
    }

    public BlocTree getParent() {
        return parent;
    }

    public void setParent(BlocTree parent) {
        this.parent = parent;
    }

    public List<BlocTree> getChildrens() {
        return childrens;
    }

    public int getChildOrder(String childLabel) {
    	Integer order = this.mapChildLabelToOrder == null?null:this.mapChildLabelToOrder.get(childLabel);
    	return order == null?0:order;
    }
    public void addChild(BlocTree child) {
        if (child != null) {
        	if (this.childrens == null) {
        		this.childrens = new ArrayList<BlocTree>();
        		this.mapChildLabelToOrder = new HashMap<String, Integer>();
        	}
            this.childrens.add(child);
            this.mapChildLabelToOrder.put(child.getBlocLabel(), this.childrens.size() - 1);
            child.setParent(this);
        }
    }

    public void showChildrens(String prefix) {

    	LOG.config(prefix + this);
        if (this.hasChildrens()) {

            for (BlocTree child : childrens) {
                child.showChildrens("-" + prefix);
            }

        }
    }

    public boolean hasChildrens() {
    	
    	return this.childrens != null && !this.childrens.isEmpty();
    }
    public BlocTree findChild(String blocLabel, boolean recursif) {

        if (this.hasChildrens()) {

            for (BlocTree child : childrens) {
                if (child.getBlocLabel().equals(blocLabel)) {
                    return child;
                }
            }
            if (recursif) {
                for (BlocTree child : childrens) {
                    BlocTree result = child.findChild(blocLabel, true);
                    if (result != null) {
                        return result;
                    }
                }
            }

        }

        return null;
    }


	/**
     * Gets the bloc rattachement.
     *
     * @return the bloc rattachement
     */
    public String getBlocLabel() {
        return blocLabel;
    }

    public CardinaliteEnum getCardinalite() {
        return cardinalite;
    }

    public void setCardinalite(CardinaliteEnum cardinalite) {
        this.cardinalite = cardinalite;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return blocLabel.concat(" (").concat(cardinalite.getMin() + "").concat(",")
        		.concat(cardinalite.getMax()== Integer.MAX_VALUE?"n":cardinalite.getMax()+"").concat(")");
    }

}
