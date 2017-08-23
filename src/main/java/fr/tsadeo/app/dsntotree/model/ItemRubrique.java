package fr.tsadeo.app.dsntotree.model;

import fr.tsadeo.app.dsntotree.util.IConstants;

public class ItemRubrique extends AbstractItemTree implements Comparable<ItemRubrique> {

    private ItemBloc blocContainer;

    // ligne d'origine
    private final String line;

    // ex S21.G00.30.001
    private String key;
    // 2750564102107
    private String value;

    // ex S21.G00
    private String prefix;
    // ex 30
    private String blocLabel;
    // ex 001
    private String rubriqueLabel;

    // ---------------------------------- constructor
    public ItemRubrique() {
        this(-1, null);
    }

    public ItemRubrique(int numLine, String line) {
        this.setNumLine(numLine);
        this.line = line;
    }

    // ---------------------------------- implementing IItemTree
    @Override
    public boolean isBloc() {
        return false;
    }

    @Override
    public boolean isRubrique() {
        return true;
    }

    // --------------------------------------- accessor
    public ItemBloc getBlocContainer() {
        return blocContainer;
    }

    public void setBlocContainer(ItemBloc blocContainer) {
        this.blocContainer = blocContainer;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getLine() {
        return line;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getBlocAndRubriqueLabel() {
        return this.blocLabel.concat(IConstants.POINT).concat(this.rubriqueLabel);
    }

    public String getBlocLabel() {
        return blocLabel;
    }

    public void setBlocLabel(String blocLabel) {
        this.blocLabel = blocLabel;
    }

    public String getRubriqueLabel() {
        return rubriqueLabel;
    }

    public void setRubriqueLabel(String rubriqueLabel) {
        this.rubriqueLabel = rubriqueLabel;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getRubriqueLabelAsInt() {

        int numRubrique = Integer.MIN_VALUE;

        try {
            numRubrique = Integer.parseInt(this.rubriqueLabel);
        } catch (NumberFormatException e) {
            // Nothing
        }

        return numRubrique;
    }

    // ---------------------------------------- overriding Object
    @Override
    public String toString() {

        return (this.isError()) ? "ligne: ".concat(Integer.toString(this.getNumLine())).concat(" - ").concat(this.line)
                : this.getBlocAndRubriqueLabel() + " : " + this.value;
    }

    // --------------------------------------------- implements Comparable
    @Override
    public int compareTo(ItemRubrique other) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (other == null) {
            return BEFORE;
        }
        if (this == other) {
            return EQUAL;
        }

        return (this.getRubriqueLabelAsInt() < other.getRubriqueLabelAsInt()) ? BEFORE : AFTER;
    }

}
