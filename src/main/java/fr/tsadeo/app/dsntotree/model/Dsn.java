package fr.tsadeo.app.dsntotree.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.tsadeo.app.dsntotree.util.IConstants;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public class Dsn implements IConstants {

    private final DsnState dsnState = new DsnState();

    private File file;

    private String phase;
    private PhaseDsn phaseDsn;

    private String nature;
    private NatureDsn natureDsn;

    private String type;
    private TypeDsn typeDsn;

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

    public PhaseDsn getPhaseDsn() {
        return this.phaseDsn;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
        this.phaseDsn = PhaseDsn.getPhaseDsnFromPhase(phase);
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
        if (this.rubriques != null) {
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

    public NatureDsn getNatureDsn() {
        return this.natureDsn;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
        this.natureDsn = NatureDsn.getNatureDsn(nature);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        this.typeDsn = TypeDsn.getNatureDsn(type);
    }

    private String displayPhase() {
        if (this.phaseDsn != null) {
            return this.phaseDsn.toString();
        }
        return StringUtils.concat("Phase: NON CONNUE (", this.phase, ")");
    }

    private String displayNature() {
        if (this.natureDsn != null) {
            return this.natureDsn.toString();
        }
        return StringUtils.concat("nature: NON CONNUE (", this.nature, ")");
    }

    private String displayType() {
        if (this.typeDsn != null) {
            return this.typeDsn.toString();
        }
        return StringUtils.concat("type: NON CONNU (", this.type, ")");
    }

    @Override
    public String toString() {
        return StringUtils.concat(this.displayPhase(), " - ", this.displayNature(), " ", this.displayType());

    }

}
