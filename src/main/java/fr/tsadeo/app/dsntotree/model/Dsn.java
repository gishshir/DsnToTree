package fr.tsadeo.app.dsntotree.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.tsadeo.app.dsntotree.util.IConstants;

public class Dsn implements IConstants {

    private final DsnState dsnState = new DsnState();

    private File file;

    private String phase;
    private String nature;
    private String type;

    private ItemBloc itemRoot;
    private BlocTree treeRoot;

    private ItemBloc itemBlocError;

    public Dsn(File file) {
        this.file = file;
    }

    public DsnState getDsnState() {
        return dsnState;
    }

    public BlocTree getTreeRoot() {
        return treeRoot;
    }

    public void setTreeRoot(BlocTree treeRoot) {
        this.treeRoot = treeRoot;
    }

    public ItemBloc getItemBlocError() {
        return itemBlocError;
    }

    public void setItemBlocError(ItemBloc itemBlocError) {
        this.itemBlocError = itemBlocError;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public ItemBloc getRoot() {
        return this.itemRoot;
    }

    public void setRoot(ItemBloc itemRoot) {
        this.itemRoot = itemRoot;
    }

    private List<ItemBloc> blocs;

    private List<ItemRubrique> rubriques;

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public List<ItemRubrique> getRubriques() {
        return rubriques;
    }

    public void addRubrique(ItemRubrique itemRubrique) {
        if (this.rubriques == null) {
            this.rubriques = new ArrayList<ItemRubrique>();
        }
        this.rubriques.add(itemRubrique);
        if (itemRubrique.isError()) {
            this.dsnState.addErrorMessage(itemRubrique.getErrorMessage());
        }
    }

    public void clearListBlocs() {
        if (this.blocs != null) {
            this.blocs.clear();
        }
    }
    
    public void clearListRubriques() {
    	if(this.rubriques != null) {
    		this.rubriques.clear();
    	}
    }
    public void addAllRubriques(List<ItemRubrique> listRubriques) {
    	if (listRubriques == null) {
    		return;
    	}
    	if (this.rubriques == null) {
    		this.rubriques = new ArrayList<ItemRubrique>();
    	}
    	this.rubriques.addAll(listRubriques);
    }

    public void addBloc(ItemBloc itemBloc) {
        this.addBloc(-1, itemBloc);
    }

    public void addBloc(int index, ItemBloc itemBloc) {
        if (this.blocs == null) {
            this.blocs = new ArrayList<ItemBloc>();
        }
        if (index < 0) {
            this.blocs.add(itemBloc);
        } else {
            this.blocs.add(index, itemBloc);
        }
    }

    public List<ItemBloc> getBlocs() {
        return blocs;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Phase: " + this.phase + " - nature: " + this.nature + " - type: " + type;
    }

}
