package fr.tsadeo.app.dsntotree.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;
import fr.tsadeo.app.dsntotree.dto.BlocChildDto;
import fr.tsadeo.app.dsntotree.dto.BlocChildrenDto;
import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ErrorMessage;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;
import fr.tsadeo.app.dsntotree.util.IConstants;
import fr.tsadeo.app.dsntotree.util.IJsonConstants;

public class DsnService implements IConstants, IJsonConstants {

    private static final String NA = "UNKNOWN";

    public List<String> buildListBlocChildEnabled(BlocTree treeRoot, ItemBloc itemBloc) {

        List<String> listChildName = new ArrayList<String>();
        if (treeRoot == null) {
            return listChildName;
        }
        BlocTree blocTree = treeRoot.findChild(itemBloc.getBlocLabel(), true);
        if (blocTree != null && blocTree.hasChildrens()) {
            for (BlocTree childTree : blocTree.getChildrens()) {
                listChildName.add(childTree.getBlocLabel());
            }
        }

        return listChildName;
    }

    public String getDefaultRubriquePrefix() {
        return RUBRIQUE_PREFIX;
    }

    public void numeroterIndexChildren(ItemBloc itemBloc) {

        List<String> listChildLabels = itemBloc.getListLabelChildrens();
        if (listChildLabels != null) {

            for (String childLabel : listChildLabels) {

                List<ItemBloc> listChildrenSameBloc = itemBloc.getChildrens(childLabel);
                if (listChildrenSameBloc != null && !listChildrenSameBloc.isEmpty()
                        && listChildrenSameBloc.size() > 1) {

                    for (int i = 0; i < listChildrenSameBloc.size(); i++) {
                        listChildrenSameBloc.get(i).setIndex(i + 1);
                    }
                }
            }
        }
    }

    /**
     * si un bloc enfant a été ajouté il faut reordonner la liste des blocs
     * enfants correctement en fonction de l'ordre défini dans le fichier json
     */
    public void reorderListChildBloc(BlocTree treeRoot, ItemBloc itemBloc) {

        if (!itemBloc.isChildrenModified()) {
            return;
        }
        if (itemBloc != null && itemBloc.hasChildren()) {

            BlocTree blocTree = treeRoot.findChild(itemBloc.getBlocLabel(), true);
            if (blocTree != null && blocTree.hasChildrens()) {

                // associer chaque blocLabel a un numero d'ordre
                // Map<String, Integer> mapBlocLabelToOrder = blocTree.
                // for (int i = 0; i < blocTree.getChildrens().size(); i++) {
                // BlocTree childTree = blocTree.getChildrens().get(i);
                // mapBlocLabelToOrder.put(childTree.getBlocLabel(), i);
                // }

                // associe chaque BlocItem avec le numero d'ordre de son label
                for (ItemBloc childBloc : itemBloc.getChildrens()) {
                    Integer order = blocTree.getChildOrder(childBloc.getBlocLabel());
                    childBloc.setOrder(order != null ? order : Integer.MAX_VALUE);
                }

                this.numeroterIndexChildren(itemBloc);
                Collections.sort(itemBloc.getChildrens());
            }

        }
    }

    /**
     * determiner si les blocs enfants peuvent etre ajoutés ou supprimes
     */
    public BlocChildrenDto determineActionSurListBlocChild(BlocTree treeRoot, ItemBloc itemBloc,
            Collection<ItemBloc> listBlocChild) {

        BlocChildrenDto blocChildrenDto = new BlocChildrenDto();
        if (listBlocChild == null) {
            return null;
        }

        // Groupement des blocs enfant par blocLabel
        Map<String, List<BlocChildDto>> mapBlocLabelToListDto = new HashMap<String, List<BlocChildDto>>();
        for (ItemBloc blocChild : listBlocChild) {
            BlocChildDto blocDto = new BlocChildDto(blocChild);

            List<BlocChildDto> listBlocLabelToDto = mapBlocLabelToListDto.get(blocChild.getBlocLabel());
            if (listBlocLabelToDto == null) {
                listBlocLabelToDto = new ArrayList<BlocChildDto>();
                mapBlocLabelToListDto.put(blocChild.getBlocLabel(), listBlocLabelToDto);
            }
            listBlocLabelToDto.add(blocDto);
        }

        // list blocs enfant avec droit de suppression ou d'ajout
        List<BlocChildDto> listBlocChildDto = blocChildrenDto.getListBlocChildDto();
        for (String childLabel : mapBlocLabelToListDto.keySet()) {

            List<BlocChildDto> listDtoForBlocLabel = mapBlocLabelToListDto.get(childLabel);
            BlocTree childTree = treeRoot.findChild(childLabel, true);

            if (childTree != null) {

                int nbBlocs = listDtoForBlocLabel.size();
                int min = childTree.getCardinalite().getMin();
                int max = childTree.getCardinalite().getMax();

                for (int i = 0; i < listDtoForBlocLabel.size(); i++) {
                    BlocChildDto blocDto = listDtoForBlocLabel.get(i);

                    listBlocChildDto.add(blocDto);
                    blocDto.setAdd(nbBlocs < max);
                    blocDto.setDel(nbBlocs > min);
                    blocDto.setDuplicate(blocDto.isAdd() && !blocDto.getBlocChild().isCreated());
                }
            }
        }

        // list bloc enfants potentiels
        List<String> listOtherBlocLabel = blocChildrenDto.getListOtherBlocLabel();
        BlocTree blocTree = treeRoot == null ? null : treeRoot.findChild(itemBloc.getBlocLabel(), true);

        if (blocTree != null && blocTree.hasChildrens()) {
            for (BlocTree childTree : blocTree.getChildrens()) {
                if (!mapBlocLabelToListDto.keySet().contains(childTree.getBlocLabel())) {
                    listOtherBlocLabel.add(childTree.getBlocLabel());
                }
            }
        }

        return blocChildrenDto;
    }

    /*
     * Creation d'un nouveau bloc enfant à partir d'un bloc frère
     */
    public ItemBloc createNewChild(ItemBloc blocSibling) {
        if (blocSibling == null) {
            return null;
        }
        ItemBloc blocChild = new ItemBloc(-1, blocSibling.getPrefix(), blocSibling.getBlocLabel());
        blocChild.setCreated(true);
        return blocChild;
    }

    public ItemBloc createNewChild(ItemBloc blocParent, String blocLabel) {
        if (blocParent == null || blocLabel == null) {
            return null;
        }
        ItemBloc blocChild = new ItemBloc(-1, blocParent.getPrefix(), blocLabel);
        blocChild.setCreated(true);
        return blocChild;
    }

    public ItemBloc createNewBloc(String blocLabel) {
        return new ItemBloc(-1, RUBRIQUE_PREFIX, blocLabel);
    }

    public ItemRubrique createNewRubrique(ItemBloc itemBloc, DataDsn dataDsn) {

        BlocAndRubriqueLabel blocAndLabel = this
                .getBlocAndRubriqueLabelFromCodeLabelAndRubrique(dataDsn.getCodeRubrique());
        String labelRubrique = blocAndLabel == null ? dataDsn.getCodeRubrique() : blocAndLabel.rubriqueLabel;
        ItemRubrique itemRubrique = this.createNewRubrique(itemBloc, labelRubrique);
        itemRubrique.setValue(dataDsn.getValue());
        itemRubrique.setNumLine(dataDsn.getNumLine());
        itemRubrique.setCreated(true);
        return itemRubrique;
    }

    /*
     * Creation d'une nouvelle rubrique pour un itemBloc
     */
    public ItemRubrique createNewRubrique(ItemBloc itemBloc, String labelRubrique) {

        if (itemBloc == null || labelRubrique == null) {
            return null;
        }
        ItemRubrique itemRubrique = new ItemRubrique();
        itemRubrique.setCreated(true);
        // construit la clé ( S21.G00.30.001)
        itemRubrique.setKey(
                itemBloc.getPrefix().concat(POINT).concat(itemBloc.getBlocLabel()).concat(POINT).concat(labelRubrique));
        itemRubrique.setPrefix(itemBloc.getPrefix());
        itemRubrique.setBlocLabel(itemBloc.getBlocLabel());
        itemRubrique.setRubriqueLabel(labelRubrique);
        itemRubrique.setValue("");

        return itemRubrique;
    }

    /**
     * Mise à jour de la liste des rubriques à partir de liste linéaire des
     * blocs qui contiennent des rubriques modifiées, supprimées ou ajoutées
     * 
     * @param dsn
     */
    public void updateDsnWithTree(Dsn dsn) {

        dsn.clearListRubriques();
        if (dsn.getBlocs() != null) {
            for (ItemBloc itemBloc : dsn.getBlocs()) {
                dsn.addAllRubriques(itemBloc.getListRubriques());
            }
        }
    }

    // si des blocs fils sont ajoutés ou supprimés il faut mettre à jour la Dsn
    public void updateDsnListBloc(Dsn dsn, ItemBloc itemBloc) {

        if (!itemBloc.isChildrenModified()) {
            return;
        }

        // supprimer le bloc et sa descendance existants dans la liste des blocs
        // de la dsn
        int index = 0;
        int indexBloc = Integer.MIN_VALUE;
        Iterator<ItemBloc> iter = dsn.getBlocs().iterator();
        while (iter.hasNext()) {
            ItemBloc item = iter.next();
            // index du bloc en cours de traitement
            if (item == itemBloc) {
                indexBloc = index;
                iter.remove();
            } else if (item.isAncestorBloc(itemBloc)) {
                iter.remove();
            }
            index++;
        }

        // Remplacer avec les nouveaux blocs enfants
        this.addBlocItemToList(dsn, itemBloc, indexBloc);
    }

    /*
     * Reconstruit entièrement la liste des blocs à partir de l'arborescence de
     * root.
     */
    public void updateDsnAllListBlocs(Dsn dsn) {
        dsn.clearListBlocs();
        this.addBlocItemToList(dsn, dsn.getRoot(), 0);
    }

    private void addBlocItemToList(Dsn dsn, ItemBloc itemBloc, int index) {

        dsn.addBloc(index++, itemBloc);
        if (itemBloc.hasChildren()) {
            for (ItemBloc childBloc : itemBloc.getChildrens()) {
                this.addBlocItemToList(dsn, childBloc, index++);
            }
        }

    }

    String getRubriqueLine(ItemRubrique itemRubrique) {

        if (itemRubrique == null) {
            return "";
        }
        if (itemRubrique.isCreated() || itemRubrique.isModified()) {
            // reconstruire la ligne si la valeur a été modifiée ou la rubrique
            // cree
            return buildLineFromKeyValue(itemRubrique.getKey(), itemRubrique.getValue());
        }
        return itemRubrique.getLine();

    }

    String buildLineFromKeyValue(String key, String value) {

        if (key == null || value == null) {
            return "";
        }
        return key.concat(VIRGULE).concat(COTE).concat(value).concat(COTE);
    }

    ItemRubrique buildRubrique(String line, int numLine) {

        // S21.G00.30.001,'2750564102107'
        ItemRubrique itemRubrique = new ItemRubrique(numLine, line);

        KeyAndValue keyAndValue = this.getKeyAndValue(line);
        if (!keyAndValue.error) {

            BlocAndRubriqueLabel blocAndRubriqueLabel = this.getBlocAndRubriqueLabelFromKey(keyAndValue.key);
            itemRubrique.setKey(keyAndValue.key);
            itemRubrique.setValue(keyAndValue.value);
            itemRubrique.setPrefix(blocAndRubriqueLabel.prefix);
            itemRubrique.setBlocLabel(blocAndRubriqueLabel.blocLabel);
            itemRubrique.setRubriqueLabel(blocAndRubriqueLabel.rubriqueLabel);
            if (blocAndRubriqueLabel.error) {
                itemRubrique.setErrorMessage(new ErrorMessage(numLine, "Impossible de déterminer bloc et rubrique!"));
            }

        } else {

            // impossible de déterminer key et value
            itemRubrique.setErrorMessage(new ErrorMessage(numLine, "Impossible de déterminer key et value!"));
            itemRubrique.setKey(null);
            itemRubrique.setValue(line);
            itemRubrique.setBlocLabel("");
            itemRubrique.setRubriqueLabel("");
        }
        return itemRubrique;
    }

    ItemRubrique findOneRubrique(List<ItemRubrique> listRubriques, String blocLabel, String rubriqueLabel) {

        List<ItemRubrique> listRub = this.findRubriques(listRubriques, blocLabel, rubriqueLabel);
        if (listRub != null && listRub.size() == 1) {
            return listRub.get(0);
        }
        return null;
    }

    List<ItemRubrique> findRubriques(List<ItemRubrique> listRubriques, String blocLabel, String rubriqueLabel) {

        List<ItemRubrique> result = new ArrayList<ItemRubrique>();

        if (listRubriques != null && blocLabel != null && rubriqueLabel != null) {

            for (ItemRubrique itemRubrique : listRubriques) {
                if (blocLabel.equals(itemRubrique.getBlocLabel())
                        && rubriqueLabel.equals(itemRubrique.getRubriqueLabel())) {

                    result.add(itemRubrique);
                }
            }
        }

        return result;
    }

    private KeyAndValue getKeyAndValue(String line) {

        KeyAndValue keyAndValue = null;
        Matcher m = PATTERN_KEY_VALUE.matcher(line);
        if (m.matches()) {
            int count = m.groupCount();
            if (count == 2) {

                // scinder key/value (key: S21.G00.30.001 / value:
                // 2750564102107)
                keyAndValue = new KeyAndValue(m.group(1), m.group(2));

            }
        }
        if (keyAndValue == null) {
            keyAndValue = new KeyAndValue(NA, line);
            keyAndValue.error = true;
        }

        return keyAndValue;
    }

    private BlocAndRubriqueLabel getBlocAndRubriqueLabelFromCodeLabelAndRubrique(String codeLabelAndRubrique) {

        BlocAndRubriqueLabel blocAndRubriqueLabel = null;

        // déterminer bloc et rubrique (bloc: 30 / rubrique 001)
        Matcher m = PATTERN_BLOC_RUBRIQUE.matcher(codeLabelAndRubrique);
        if (m.matches()) {

            int count = m.groupCount();
            if (count == 2) {

                blocAndRubriqueLabel = new BlocAndRubriqueLabel(null, m.group(1), m.group(2));
            }
        } else {
            // Impossible de déterminer bloc et rubrique
            blocAndRubriqueLabel = new BlocAndRubriqueLabel(NA, "", codeLabelAndRubrique);
            blocAndRubriqueLabel.error = true;
        }

        return blocAndRubriqueLabel;
    }

    private BlocAndRubriqueLabel getBlocAndRubriqueLabelFromKey(String key) {

        BlocAndRubriqueLabel blocAndRubriqueLabel = null;

        // déterminer bloc et rubrique (bloc: 30 / rubrique 001)
        Matcher m = PATTERN_PREF_BLOC_RUBRIQUE.matcher(key);
        if (m.matches()) {

            int count = m.groupCount();
            if (count == 3) {

                blocAndRubriqueLabel = new BlocAndRubriqueLabel(m.group(1), m.group(2), m.group(3));
            }
        } else {
            // Impossible de déterminer bloc et rubrique
            blocAndRubriqueLabel = new BlocAndRubriqueLabel(NA, "", key);
            blocAndRubriqueLabel.error = true;
        }

        return blocAndRubriqueLabel;
    }

    // =======================================================
    // key: S21.G00.30.001
    // value: 2750564102107
    private static class KeyAndValue {

        private boolean error = false;
        private final String key;
        private final String value;

        private KeyAndValue(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    // prefix: S21.G00
    // bloc: 30
    // rubrique: 001
    private static class BlocAndRubriqueLabel {

        private boolean error = false;
        private final String prefix;
        private final String blocLabel;
        private final String rubriqueLabel;

        private BlocAndRubriqueLabel(String prefix, String blocLabel, String rubriqueLabel) {
            this.prefix = prefix;
            this.blocLabel = blocLabel;
            this.rubriqueLabel = rubriqueLabel;
        }
    }
}
