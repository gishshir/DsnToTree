package fr.tsadeo.app.dsntotree.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.tsadeo.app.dsntotree.util.IConstants;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public class Dsn implements IConstants {

    private final DsnState dsnState = new DsnState();

    private File file;

    private final PhaseNatureType phaseNatureType = new PhaseNatureType();

    private String phase;

    private List<Declaration> listDeclarations = new ArrayList<>();

    // private String nature;
    //
    // private String type;

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
        this.phaseNatureType.setPhase(PhaseDsn.getPhaseDsnFromPhase(phase));
    }

    public PhaseNatureType getPhaseNatureType() {
        return phaseNatureType;
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


    public void addDeclaration(String nature, String type) {

        this.listDeclarations.add(new Declaration(nature, type));
        this.phaseNatureType.setNature(NatureDsn.getNatureDsn(nature));
        this.phaseNatureType.setType(TypeDsn.getNatureDsn(type));
    }

    public String getNature() {
        if (this.listDeclarations == null || listDeclarations.isEmpty()) {
            return null;
        }
        return this.listDeclarations.get(0).getNature();
    }


    public String getType() {
        if (this.listDeclarations == null || listDeclarations.isEmpty()) {
            return null;
        }
        return this.listDeclarations.get(0).getType();
    }


    private String displayPhase() {
        if (this.phaseNatureType.getPhase() != null) {
            return this.phaseNatureType.getPhase().toString();
        }
        return StringUtils.concat("Phase: NON CONNUE (", this.phase, ")");
    }

    private String displayNature() {
        if (this.phaseNatureType.getNature() != null) {
            return this.phaseNatureType.getNature().toString();
        }
        return StringUtils.concat("nature: NON CONNUE (", this.getNature(), ")");
    }

    private String displayType() {
        if (this.phaseNatureType.getType() != null) {
            return this.phaseNatureType.getType().toString();
        }
        return StringUtils.concat("type: NON CONNU (", this.getType(), ")");
    }

    @Override
    public String toString() {
        String declarations = this.listDeclarations.size() > 1 ? "- Nbr déclarations: " + this.listDeclarations.size()
                : "";
        return StringUtils.concat(this.displayPhase(), " - ", this.displayNature(), " ", this.displayType(), " ",
                declarations);

    }

    // ================================= INNER CLASS
    private class Declaration {

        private final String nature;

        private final String type;

        private Declaration(String nature, String type) {
            this.nature = nature;
            this.type = type;
        }

        public String getNature() {
            return nature;
        }


        public String getType() {
            return type;
        }


    }

}
