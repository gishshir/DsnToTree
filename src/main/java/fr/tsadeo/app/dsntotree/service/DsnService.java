package fr.tsadeo.app.dsntotree.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;
import fr.tsadeo.app.dsntotree.dico.KeyAndLibelle;
import fr.tsadeo.app.dsntotree.dto.BlocChildDto;
import fr.tsadeo.app.dsntotree.dto.BlocChildrenDto;
import fr.tsadeo.app.dsntotree.gui.table.dto.EtablissementTableDto;
import fr.tsadeo.app.dsntotree.gui.table.dto.SalarieTableDto;
import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ErrorMessage;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;
import fr.tsadeo.app.dsntotree.util.IConstants;
import fr.tsadeo.app.dsntotree.util.IJsonConstants;
import fr.tsadeo.app.dsntotree.util.IRegexConstants;
import fr.tsadeo.app.dsntotree.util.RegexUtils;
import fr.tsadeo.app.dsntotree.util.RegexUtils.CapturingGroups;

public class DsnService implements IConstants, IJsonConstants, IRegexConstants {

    private static final Logger LOG = Logger.getLogger(DsnService.class.getName());
    private static final String NA = "UNKNOWN";

    public String getBlocLibelle(String blocLabel) {
        String libelle = blocLabel == null ? null
                : ServiceFactory.getDictionnaryService().getDsnDictionnary().getLibelle(blocLabel);
        return libelle == null ? "non connu..." : libelle;
    }

    public String getRubriqueLibelle(ItemRubrique itemRubrique) {
        String libelle = itemRubrique == null ? null
                : ServiceFactory.getDictionnaryService().getDsnDictionnary().getLibelle(itemRubrique.getBlocLabel(),
                        itemRubrique.getRubriqueLabel());

        return libelle == null ? "non connu..." : libelle;
    }

    /**
     * Comptabilise le nombre de déclaration dans le fichier DSN Nombre de
     * 05.001
     */
    public int countDeclarations(Dsn dsn) {

        List<ItemBloc> listBloc05 = this.findListItemBlocByBlocLabel(dsn, BLOC_05);
        return listBloc05 == null ? 0 : listBloc05.size();
    }

    public List<EtablissementTableDto> buildListEtablissementDtos(Dsn dsn) {
    	
    	List<EtablissementTableDto> listEtablissements = new ArrayList<>();
    	
    	List<ItemBloc> listItemBlocs = this.findListItemBlocByBlocLabel(dsn, BLOC_11);
    	if (!Objects.isNull(listItemBlocs)) {
    		
    		listEtablissements = IntStream.range(0, listItemBlocs.size())
            		.mapToObj(index -> this.createEtablissementDto(index, listItemBlocs.get(index)))
            		.collect(Collectors.toList());
    	}
    	
    	return listEtablissements;
    }
    public List<SalarieTableDto> buildListSalarieDtos(Dsn dsn) {

        List<SalarieTableDto> listSalaries = new ArrayList<>();

        List<ItemBloc> listItemBlocs = this.findListItemBlocByBlocLabel(dsn, BLOC_30);
        if (listItemBlocs != null) {
        	
        	listSalaries = IntStream.range(0, listItemBlocs.size())
        		.mapToObj(index -> this.createSalarieDto(index, listItemBlocs.get(index)))
        		.collect(Collectors.toList());
        		
        }

        return listSalaries;
    }

    public boolean isBlocOrRubriquePattern(String value) {
        return PATTERN_SEARCH_BLOC_OR_RUBRIQUE.matcher(value).matches();
    }

    /**
     * Determine si il est possible de copier 'blocToDrop' dans le bloc parent
     * 'parentTarget'
     * 
     * @param dsn
     * @param parentTarget
     * @return
     */
    public boolean canDropItemBloc(BlocTree treeRoot, ItemBloc parentTarget, ItemBloc blocToDrop) {

        // si meme parent drop refuse
        if (blocToDrop == null || parentTarget == null || blocToDrop.getParent() == parentTarget) {
            return false;
        }
        // si parent de label different drop refuse
        if (!parentTarget.getBlocLabel().equals(blocToDrop.getParent().getBlocLabel())) {
            return false;
        }

        BlocTree treeBlocParent = treeRoot.findChild(parentTarget.getBlocLabel(), true);
        if (treeBlocParent == null) {
            return false;
        }
        if (treeBlocParent.hasChildrens()) {

            BlocTree childTree = treeBlocParent.findChild(blocToDrop.getBlocLabel(), false);
            if (childTree != null) {

                int max = childTree.getCardinalite().getMax();

                // compter le nombre de child de meme label deja existant dans
                // le parent
                int nbBlocs = 0;
                if (parentTarget.hasChildren()) {
                    for (ItemBloc blocChild : parentTarget.getChildrens()) {
                        if (blocChild.getBlocLabel().equals(blocToDrop.getBlocLabel())) {
                            nbBlocs++;
                        }
                    }
                }
                return nbBlocs < max;
            }
        }

        return false;
    }

    private EtablissementTableDto createEtablissementDto(int index, ItemBloc blocEtablissement) {
    	EtablissementTableDto etablissementDto = new EtablissementTableDto(index, blocEtablissement);
    	
    	ItemRubrique nicEtabRubrique = 
        		this.findOneRubrique(blocEtablissement.getListRubriques(), blocEtablissement.getBlocLabel(), RUB_001);
    	etablissementDto.setNicEtab(nicEtabRubrique == null?null:nicEtabRubrique.getValue());
    	
    	ItemRubrique nicLocaliteRubrique = 
        		this.findOneRubrique(blocEtablissement.getListRubriques(), blocEtablissement.getBlocLabel(), RUB_005);
    	etablissementDto.setLocaliteEtab(nicLocaliteRubrique == null?null:nicLocaliteRubrique.getValue());

    	ItemBloc blocEntreprise = blocEtablissement.getParent();
    	if (!Objects.isNull(blocEntreprise)) {
    		
    		ItemRubrique sirenSiegeRubrique = 
            		this.findOneRubrique(blocEntreprise.getListRubriques(), blocEntreprise.getBlocLabel(), RUB_001);
        	etablissementDto.setSirenSiege(sirenSiegeRubrique== null?null:sirenSiegeRubrique.getValue());
        	
    		ItemRubrique nicSiegeRubrique = 
            		this.findOneRubrique(blocEntreprise.getListRubriques(), blocEntreprise.getBlocLabel(), RUB_002);
        	etablissementDto.setNicSiege(nicSiegeRubrique == null?null:nicSiegeRubrique.getValue());
    	}
    	
    	return etablissementDto;
    }
    private SalarieTableDto createSalarieDto(int index, ItemBloc blocSalarie) {
        SalarieTableDto salarieDto = new SalarieTableDto(index, blocSalarie);

        if (blocSalarie.hasRubriques()) {
            for (ItemRubrique itemRubrique : blocSalarie.getListRubriques()) {
                if (itemRubrique.getRubriqueLabel().equals(RUB_001)) {
                    salarieDto.setNir(itemRubrique.getValue());
                } else if (itemRubrique.getRubriqueLabel().equals(RUB_002)) {
                    salarieDto.setNom(itemRubrique.getValue());
                } else if (itemRubrique.getRubriqueLabel().equals(RUB_003)) {
                    salarieDto.setNom(itemRubrique.getValue());
                } else if (itemRubrique.getRubriqueLabel().equals(RUB_004)) {
                    salarieDto.setPrenom(itemRubrique.getValue());
                }
            }
        }
        ItemBloc blocEtablissement = blocSalarie.getParent();
        if (!Objects.isNull(blocEtablissement)) {
        	
            ItemRubrique nicEtabRubrique = 
            		this.findOneRubrique(blocEtablissement.getListRubriques(), blocEtablissement.getBlocLabel(), RUB_001);
            salarieDto.setNic(nicEtabRubrique == null?null:nicEtabRubrique.getValue());
            
            ItemBloc blocEntreprise = blocEtablissement.getParent();
            if (!Objects.isNull(blocEntreprise)) {
            	ItemRubrique sirenRubrique = this.findOneRubrique(blocEntreprise.getListRubriques(),
            			blocEntreprise.getBlocLabel(), RUB_001);
            	salarieDto.setSiren(sirenRubrique == null?null:sirenRubrique.getValue());
            }
        }

        return salarieDto;
    }

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
     * Trouver dans la Dsn une liste de blocs de même label
     * 
     * @param dsn
     * @param blocLabel
     * @return
     */
    private List<ItemBloc> findListItemBlocByBlocLabel(Dsn dsn, String blocLabel) {
        List<ItemBloc> listItemBlocs = new ArrayList<>();

        if (dsn != null && blocLabel != null) {
        	
        	listItemBlocs = dsn.getBlocs().stream()
        	  .filter(itemBloc -> itemBloc.getBlocLabel().equals(blocLabel))
        	  .collect(Collectors.toList());
        }

        return listItemBlocs;
    }

    /**
     * Trouver dans la Dsn un ItemBloc équivalent (equals) à celui fourni en
     * argument
     * 
     * @param dsn
     * @param itemBloc
     * @return
     */
    public ItemBloc findItemBlocEquivalent(Dsn dsn, ItemBloc itemBlocToFind) {

        if (dsn != null && itemBlocToFind != null) {
        	
        	return dsn.getBlocs().stream()
        		.filter(itemBloc -> {
        			 if (itemBloc == itemBlocToFind) {
                         return true;
                     }
                     if (itemBloc.hashCode() == itemBlocToFind.hashCode()) {
                         return true;
                     }	
                     return false;
        		})
        		.findFirst().orElseGet(null);
        	
        }
        return null;
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
    
    public boolean hasRubriqueAAJouter(ItemBloc itemBloc, Collection<ItemRubrique> listRubriqueExistantes) {

        // liste des rubriques d'un bloc
        List<KeyAndLibelle> listAllRubriques = ServiceFactory.getDictionnaryService().getDsnDictionnary()
                .getOrderedListOfSubItem(itemBloc.getBlocLabel());

        if (Objects.nonNull(listRubriqueExistantes)) {
            // enlever celles qui existent déja
            List<String> listRubriqueLabelExistantes = listRubriqueExistantes.stream()
                    .map(itemRubrique -> itemRubrique.getRubriqueLabel()).collect(Collectors.toList());

            return (listAllRubriques.stream()
                    .filter(keyAndLibelle -> !listRubriqueLabelExistantes.contains(keyAndLibelle.getKey())).findAny()
                    .orElse(null)) != null;

        }
        return Objects.nonNull(listAllRubriques) && !listAllRubriques.isEmpty();

    }
    public List<KeyAndLibelle> determineListRubriqueAAjouter (ItemBloc itemBloc,
    Collection<ItemRubrique> listRubriqueExistantes) {

    	//liste des rubriques d'un bloc
    	List<KeyAndLibelle> listAllRubriques = ServiceFactory.getDictionnaryService().getDsnDictionnary().getOrderedListOfSubItem(itemBloc.getBlocLabel());
    	
    	if (Objects.nonNull(listRubriqueExistantes)) {
    		// enlever celles qui existent déja
    		List<String> listRubriqueLabelExistantes =
    			listRubriqueExistantes.stream()
                            .map(itemRubrique -> itemRubrique.getRubriqueLabel())
    			.collect(Collectors.toList());
    		
            return listAllRubriques.stream()
                    .filter(keyAndLibelle -> !listRubriqueLabelExistantes.contains(keyAndLibelle.getKey()))
                    .collect(Collectors.toList());
    	
    	}
    	
    	return listAllRubriques;
    }

    /**
     * determiner si les blocs enfants peuvent etre ajoutés ou supprimes
     */
    public BlocChildrenDto determineActionSurListBlocChild(BlocTree treeRoot, ItemBloc itemBloc,
            Collection<ItemBloc> listBlocChild) {

        BlocChildrenDto blocChildrenDto = new BlocChildrenDto();
        if (Objects.isNull(listBlocChild)) {
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

                listBlocChildDto.addAll(
                IntStream.range(0, listDtoForBlocLabel.size())
                	.mapToObj(i -> {
                		
                		BlocChildDto blocDto = listDtoForBlocLabel.get(i);
                		 blocDto.setShow(blocDto.getBlocChild().hasRubriques() || blocDto.getBlocChild().hasChildren());
                         blocDto.setDel(nbBlocs > min);
                         blocDto.setDuplicate(nbBlocs < max);
                         return blocDto;
                	})
                	.collect(Collectors.toList()));
                
            }
        }

        // list bloc enfants potentiels
        List<KeyAndLibelle> listOtherBlocLabel = blocChildrenDto.getListOtherBlocLabel();
        BlocTree blocTree = treeRoot == null ? null : treeRoot.findChild(itemBloc.getBlocLabel(), true);

        if (Objects.nonNull(blocTree) && blocTree.hasChildrens()) {
        	
        	listOtherBlocLabel.addAll(
        	blocTree.getChildrens().stream()
        		.filter(childTree -> !mapBlocLabelToListDto.keySet().contains(childTree.getBlocLabel()))
        		.map(childTree -> {
        			String blocLibelle = this.getBlocLibelle(childTree.getBlocLabel());
        			return new KeyAndLibelle(childTree.getBlocLabel(), blocLibelle);
        		})
        		.collect(Collectors.toList()));
        	
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

    /*
     * Creation d'un nouveau bloc enfant à partir d'un bloc frère
     */
    public ItemBloc createNewChild(ItemBloc blocSibling, boolean withRubriques, boolean withChildrens) {

        ItemBloc blocChild = this.createNewChild(blocSibling);
        if (blocChild != null) {

            if (withRubriques) {
                if (blocSibling.hasRubriques()) {

                    blocSibling.getListRubriques().stream().forEach(itemRubrique -> {
                        ItemRubrique newRubrique = this.createNewRubrique(blocChild, itemRubrique.getRubriqueLabel());
                        newRubrique.setValue(itemRubrique.getValue());
                        blocChild.addRubrique(newRubrique);
                    });
                }
            }
            if (withChildrens) {

                if (blocSibling.hasChildren()) {

                    blocSibling.getChildrens().stream().forEach(childOfSibling -> {
                        ItemBloc newChildForChild = this.createNewChild(childOfSibling, withRubriques, withChildrens);
                        blocChild.addChild(newChildForChild);
                    });
                }
            }
        }

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

            dsn.getBlocs().stream().forEach(itemBloc -> dsn.addAllRubriques(itemBloc.getListRubriques()));
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
        LOG.config("Remplacer avec les nouveaux blocs enfants");
        this.addBlocItemToList(dsn, itemBloc, new AtomicInteger(indexBloc));
    }

    private void addBlocItemToList(Dsn dsn, ItemBloc itemBloc, AtomicInteger compteur) {

        LOG.config("bloc " + itemBloc.getBlocLabel() + " - index: " + compteur.get());
        dsn.addBloc(compteur.getAndIncrement(), itemBloc);
        if (itemBloc.hasChildren()) {

            itemBloc.getChildrens().stream().forEach(childBloc -> this.addBlocItemToList(dsn, childBloc, compteur));
        }

    }

    /*
     * Reconstruit entièrement la liste des blocs à partir de l'arborescence de
     * root.
     */
    public void updateDsnAllListBlocs(Dsn dsn) {
        dsn.clearListBlocs();
        this.addBlocItemToList(dsn, dsn.getRoot(), new AtomicInteger(0));
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

    ItemRubrique findOneRubrique(List<ItemRubrique> listRubriques, String blocLabel, final String rubriqueLabel) {

        List<ItemRubrique> listRub = this.findRubriques(listRubriques, blocLabel, rubriqueLabel);
        if (listRub != null && listRub.size() == 1) {
            return listRub.get(0);
        }
        return null;
    }

    List<ItemRubrique> findRubriques(List<ItemRubrique> listRubriques, String blocLabel, String rubriqueLabel) {

        List<ItemRubrique> result = new ArrayList<ItemRubrique>();

        if (listRubriques != null && blocLabel != null && rubriqueLabel != null) {

            result = listRubriques.stream().filter(itemRubrique -> blocLabel.equals(itemRubrique.getBlocLabel())
                    && rubriqueLabel.equals(itemRubrique.getRubriqueLabel())).collect(Collectors.toList());

        }

        return result;
    }

    private KeyAndValue getKeyAndValue(String line) {

        KeyAndValue keyAndValue = null;
        CapturingGroups capturingGroups = new CapturingGroups(1, 2);
        RegexUtils.get().extractsGroups(line, PATTERN_KEY_VALUE, capturingGroups);
        if (capturingGroups.isSuccess()) {

            // scinder key/value (key: S21.G00.30.001 / value:
            // 2750564102107)
            keyAndValue = new KeyAndValue(capturingGroups.valueOf(1), capturingGroups.valueOf(2));

        }
        else {
            keyAndValue = new KeyAndValue(NA, line);
            keyAndValue.error = true;
        }

        return keyAndValue;
    }

    private BlocAndRubriqueLabel getBlocAndRubriqueLabelFromCodeLabelAndRubrique(String codeLabelAndRubrique) {

        BlocAndRubriqueLabel blocAndRubriqueLabel = null;

        // déterminer bloc et rubrique (bloc: 30 / rubrique 001)
        CapturingGroups capturingGroups = new CapturingGroups(1, 2);
        RegexUtils.get().extractsGroups(codeLabelAndRubrique, PATTERN_BLOC_RUBRIQUE, capturingGroups);
        if (capturingGroups.isSuccess()) {
            blocAndRubriqueLabel = new BlocAndRubriqueLabel(null, capturingGroups.valueOf(1),
                    capturingGroups.valueOf(2));
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
        CapturingGroups capturingGroups = new CapturingGroups(1, 2, 3);
        RegexUtils.get().extractsGroups(key, PATTERN_PREF_BLOC_RUBRIQUE, capturingGroups);
        if (capturingGroups.isSuccess()) {

            blocAndRubriqueLabel = new BlocAndRubriqueLabel(capturingGroups.valueOf(1), capturingGroups.valueOf(2),
                    capturingGroups.valueOf(3));
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
