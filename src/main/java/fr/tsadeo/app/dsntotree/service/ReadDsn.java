package fr.tsadeo.app.dsntotree.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;
import fr.tsadeo.app.dsntotree.dto.BlocDatasDto;
import fr.tsadeo.app.dsntotree.dto.GroupBlocDatasDto;
import fr.tsadeo.app.dsntotree.dto.LinkedPropertiesDto;
import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.CardinaliteEnum;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ErrorMessage;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;
import fr.tsadeo.app.dsntotree.util.IConstants;
import fr.tsadeo.app.dsntotree.util.IJsonConstants;
import fr.tsadeo.app.dsntotree.util.JsonUtils;

public class ReadDsn implements IConstants, IJsonConstants {

    private final JsonUtils jsonUtils = new JsonUtils();
    private final DsnService dsnService = new DsnService();

    public static void main(String[] args) {

        if (args == null || args.length == 0) {
            System.out.println("manque le nom du fichier dsn à analyser...");
        }

        File file = new File(args[0]);
        new ReadDsn().buildTreeFromFile(file);
    }

    /**
     * Creation d'un objet Dsn à partir d'un objet Properties
     * 
     * @param file
     * @param properties
     * @return
     */
    public Dsn buildTreeFromProperties(File file, LinkedPropertiesDto properties) {

        Dsn dsn = null;
        if (file != null && properties != null) {

            // constituer la liste linéaire des rubriques et des blocs
            dsn = this.createDsnFromProperties(file, properties);
            // construire la structure arborescente
            this.buildDsnTree(dsn);
        }

        return dsn;

    }

    /**
     * Prerequis la liste des datas est triée par bloc, seq_bloc, seq_sup,
     * codeRubrique
     * 
     * @param listDatas
     * @return
     */
    public Dsn buildTreeFromDatas(String dsnName, List<DataDsn> listDatas) {

        Dsn dsn = null;

        if (listDatas != null) {

            // on constitue une liste de bloc de datas cohérents
            GroupBlocDatasDto groupBlocs = new GroupBlocDatasDto();
            BlocDatasDto currentBloc = null;
            for (DataDsn dataDsn : listDatas) {

                // nouveau bloc
                if (currentBloc == null || !currentBloc.getKey().equals(BlocDatasDto.getKeyBlocFromData(dataDsn))) {
                    currentBloc = new BlocDatasDto(dataDsn.getNumSequenceBloc(), dataDsn.getNumSequenceBlocSup(),
                            dataDsn.getBloc());
                    groupBlocs.addBloc(currentBloc);
                }
                currentBloc.addData(dataDsn);
            }
            String phase = groupBlocs.extractDsnPhase();
            String nature = groupBlocs.extractDsnNature();
            if (phase != null && nature != null) {
                dsn = new Dsn(new File(dsnName));
                dsn.setPhase(phase);
                dsn.setNature(nature);

                // contruire l'arbre des ItemBloc avec leur rubriques
                this.buildDsnTreeFromDatas(dsn, groupBlocs);

                // reconstruire la liste linéaire des blocs
                ServiceFactory.getDsnService().updateDsnAllListBlocs(dsn);

                dsn.getDsnState().setModified(true);
            }
        }

        return dsn;
    }

    /**
     * Creation d'un objet Dsn à partir d'un fichier
     * 
     * @param dsnFile
     * @return
     */
    public Dsn buildTreeFromFile(File dsnFile) {
        Dsn dsn = null;
        if (dsnFile != null) {

            // constituer la liste linéaire des rubriques et des blocs
            dsn = this.createDsnFromFile(dsnFile);
            System.out.println(dsn.toString());
            // construire la structure arborescente
            this.buildDsnTree(dsn);

        }

        return dsn;
    }

    private void buildDsnTreeFromDatas(Dsn dsn, GroupBlocDatasDto groupBlocs) {

        // organisation des blocs en arborescence
        ItemBloc itemRoot = this.buildItemTreeFromDatas(dsn, groupBlocs);

        StringBuffer sb = new StringBuffer();
        itemRoot.showChildrens("", sb);
        System.out.println(sb.toString());

        dsn.setRoot(itemRoot);

        if (dsn.getDsnState().isError()) {
            System.out.println("\nDSN en erreur");
            itemRoot.setErrorMessage(new ErrorMessage("DSN en erreur"));
            for (ErrorMessage error : dsn.getDsnState().getListErrorMessage()) {
                System.out.println(error);
            }
        }

    }

    private void buildDsnTree(Dsn dsn) {

        // organisation des blocs en arborescence
        ItemBloc itemRoot = this.buildItemTree(dsn);

        StringBuffer sb = new StringBuffer();
        itemRoot.showChildrens("", sb);
        System.out.println(sb.toString());

        dsn.setRoot(itemRoot);

        if (dsn.getDsnState().isError()) {
            System.out.println("\nDSN en erreur");
            itemRoot.setErrorMessage(new ErrorMessage("DSN en erreur"));
            for (ErrorMessage error : dsn.getDsnState().getListErrorMessage()) {
                System.out.println(error);
            }
        }

    }

    private ItemBloc buildItemTreeFromDatas(Dsn dsn, GroupBlocDatasDto groupBlocs) {

        ItemBloc root = new ItemBloc(0, RUBRIQUE_PREFIX, "DSN");
        root.setRoot(true);

        // description de l'arborscence en fonction de la phase et de la nature
        BlocTree treeRoot = this.buildRootTree(this.jsonUtils.getJsonEnteteForDsnAsStream(dsn),
                this.jsonUtils.getJsonForDsnAsStream(dsn));

        dsn.setTreeRoot(treeRoot);
        if (treeRoot == null) {
            dsn.getDsnState().setStructured(false);
            dsn.getDsnState().addErrorMessage(new ErrorMessage(
                    "Impossible de déterminer la structure de la dsn à partir de son type et de sa nature!"));
        } else {
            dsn.getDsnState().setStructured(true);
        }

        // pas de bloc ou Dsn non struturée
        if (groupBlocs.isEmpty()) {
            dsn.getDsnState().addErrorMessage(new ErrorMessage("Aucun bloc identifié!"));
            return root;
        } else if (!dsn.getDsnState().isStructured()) {
            for (ItemBloc itemBloc : dsn.getBlocs()) {
                root.addChild(itemBloc);
            }
            return root;
        }

        if (dsn.getDsnState().isStructured()) {
            // fonction recursive de construction de l'arborescence des ItemBloc
            // avec leur rubriques
            if (treeRoot.hasChildrens()) {
                for (BlocTree blocTree : treeRoot.getChildrens()) {
                    this.buildItemBlocsFromDatas(root, blocTree, groupBlocs);
                }

            }
        }

        return root;

    }

    /**
     * Fonction recursive Par ex,ItemBloc est le 11 '(blocParent) et je cherche
     * tous les blocs 15 (childTree) associés
     * 
     * @param blocParent
     *            ItemBloc auquel on veut ajouter des fils
     * @param childTree
     *            décrit l'élement de bloc fils qu'on cherche à constituer
     * @param groupBlocs
     *            : BlocDatasDto et DataDsn
     */
    private void buildItemBlocsFromDatas(ItemBloc blocParent, BlocTree blocTree, GroupBlocDatasDto groupBlocs) {

        if (blocTree == null || groupBlocs == null || blocParent == null) {
            return;
        }
        // on cherche une liste de bloc correspondant au nom du bloc et au
        // numero de seq du bloc parent
        List<BlocDatasDto> listBlocDatas = groupBlocs.getListBlocsForLabelAndSeqSup(blocTree.getBlocLabel(),
                blocParent.getIndex());
        if (listBlocDatas != null) {

            // pour chaque blocDatas on construit un ItemBloc avec ses rubriques
            for (BlocDatasDto blocDatasDto : listBlocDatas) {
                this.buildItemBlocFromDatas(blocParent, blocTree, blocDatasDto, groupBlocs);
            }

        }

    }

    // Fonction recursive
    private void buildItemBlocFromDatas(ItemBloc blocParent, BlocTree blocTree, BlocDatasDto blocDatasDto,
            GroupBlocDatasDto groupBlocs) {

        ItemBloc blocChild = ServiceFactory.getDsnService().createNewChild(blocParent, blocTree.getBlocLabel());
        blocChild.setIndex(blocDatasDto.getSequence());

        // on construit la liste des ItemRubriques
        for (DataDsn dataDsn : blocDatasDto.getListDatas()) {
            ItemRubrique itemRubrique = ServiceFactory.getDsnService().createNewRubrique(blocChild, dataDsn);
            itemRubrique.setValue(dataDsn.getValue());
            blocChild.addRubrique(itemRubrique);
        }
        blocParent.addChild(blocChild);

        // on cherche les enfants potentiels
        if (blocTree.hasChildrens()) {
            for (BlocTree childTree : blocTree.getChildrens()) {

                // appel recursif
                this.buildItemBlocsFromDatas(blocChild, childTree, groupBlocs);
            }
        }
    }

    /*
     * Construction de l'arborescence des blocs (ItemBloc) en s'appuyant sur la
     * structure (BlocTree)
     */
    private ItemBloc buildItemTree(Dsn dsn) {

        ItemBloc root = new ItemBloc(0, "", "DSN");
        root.setRoot(true);

        BlocTree treeRoot = this.buildRootTree(this.jsonUtils.getJsonEnteteForDsnAsStream(dsn),
                this.jsonUtils.getJsonForDsnAsStream(dsn));

        dsn.setTreeRoot(treeRoot);
        if (treeRoot == null) {
            dsn.getDsnState().setStructured(false);
            dsn.getDsnState().addErrorMessage(new ErrorMessage(
                    "Impossible de déterminer la structure de la dsn à partir de son type et de sa nature!"));
        } else {
            dsn.getDsnState().setStructured(true);
        }

        // pas de bloc ou Dsn non struturée
        if (dsn.getBlocs() == null || dsn.getBlocs().isEmpty()) {
            dsn.getDsnState().addErrorMessage(new ErrorMessage("Aucun bloc identifié!"));
            return root;
        } else if (!dsn.getDsnState().isStructured()) {
            for (ItemBloc itemBloc : dsn.getBlocs()) {
                root.addChild(itemBloc);
            }
            return root;
        }

        Map<String, BlocTree> mapBlocLabel2BlocTree = new HashMap<String, BlocTree>();
        Map<String, ItemBloc> mapBlocLabel2LastBlocItem = new HashMap<String, ItemBloc>();

        mapBlocLabel2LastBlocItem.put("", root);

        // si bloc d'erreur on l'affiche en premier
        if (dsn.getItemBlocError() != null) {
            root.addChild(dsn.getItemBlocError());
        }

        for (ItemBloc bloc : dsn.getBlocs()) {
            String blocLabel = bloc.getBlocLabel();
            mapBlocLabel2LastBlocItem.put(blocLabel, bloc);

            // on recherche le bloc de description de structure
            BlocTree blocTree = mapBlocLabel2BlocTree.get(blocLabel);
            if (blocTree == null) {
                blocTree = treeRoot.findChild(blocLabel, true);
                if (blocTree != null) {
                    mapBlocLabel2BlocTree.put(blocLabel, blocTree);
                }
            }

            // on cherche le nom du bloc parent
            if (blocTree != null) {
                String parentBlocLabel = blocTree.getParent() == null ? null : blocTree.getParent().getBlocLabel();
                if (parentBlocLabel != null) {
                    ItemBloc parentBloc = mapBlocLabel2LastBlocItem.get(parentBlocLabel);
                    if (parentBloc == null) {
                        ErrorMessage errorMessage = new ErrorMessage(bloc.getNumLine(),
                                "Bloc parent ".concat(parentBlocLabel).concat(" inconnu dans l'arborescence!"));
                        bloc.setErrorMessage(errorMessage);
                        dsn.getDsnState().addErrorMessage(errorMessage);
                        this.getOrBuildItemBlocErreur(dsn).addChild(bloc);
                    } else {
                        parentBloc.addChild(bloc);
                    }
                }

            } else {
                // Probleme : bloc non decrit dans l'arborescence.
                // on le rattache au bloc d'erreur
                ErrorMessage errorMessage = new ErrorMessage(bloc.getNumLine(),
                        "Bloc ".concat(blocLabel).concat(" inconnu dans l'arborescence!"));
                bloc.setErrorMessage(errorMessage);
                dsn.getDsnState().addErrorMessage(errorMessage);
                this.getOrBuildItemBlocErreur(dsn).addChild(bloc);
            }
        }

        // on numerote les blocs enfant
        for (ItemBloc itemBloc : dsn.getBlocs()) {
            ServiceFactory.getDsnService().numeroterIndexChildren(itemBloc);
        }
        return root;
    }

    private Dsn readDsnListLines(File file, List<String> listLignes) {

        Dsn dsn = new Dsn(file);

        // liste linéaire des rubriques et bloc
        ItemBloc currentBloc = null;
        ItemRubrique lastRubrique = null;
        for (int i = 0; i < listLignes.size(); i++) {

            String line = listLignes.get(i);
            ItemRubrique itemRubrique = this.dsnService.buildRubrique(line, i + 1);
            dsn.addRubrique(itemRubrique);

            // gestion des rubriques en erreur
            if (itemRubrique.isError()) {
                ItemBloc itemBlocError = this.getOrBuildItemBlocErreur(dsn);
                itemBlocError.addRubrique(itemRubrique);
                continue; // next rubrique
            }

            // rupture de bloc
            // - si on change de numero de bloc
            // ou
            // - si pour un meme bloc on trouve une rubrique de numero inf à la
            // dernière lue
            if (currentBloc == null ||
            // nom de bloc différent
                    !itemRubrique.getBlocLabel().equals(currentBloc.getBlocLabel())
                    // meme bloc, rubrique inférieure à la dernière du bloc
                    || (lastRubrique != null
                            && lastRubrique.getRubriqueLabelAsInt() >= itemRubrique.getRubriqueLabelAsInt())) {

                ItemBloc itemBloc = new ItemBloc(itemRubrique.getNumLine(), itemRubrique.getPrefix(),
                        itemRubrique.getBlocLabel());
                dsn.addBloc(itemBloc);

                currentBloc = itemBloc;
            }
            currentBloc.addRubrique(itemRubrique);
            lastRubrique = itemRubrique;
        }

        return dsn;

    }

    /*
     * constituer la liste linéaire des rubriques et des blocs
     */
    private Dsn createDsnFromProperties(File dsnFile, LinkedPropertiesDto properties) {

        Dsn dsn = null;

        List<String> lines = new ArrayList<String>(properties.size());
        for (String key : properties.listPropertyNames()) {
            lines.add(dsnService.buildLineFromKeyValue(key, properties.getProperty(key)));
        }
        try {
            dsn = this.readDsnListLines(dsnFile, lines);

            this.extractDsnPhase(dsn);
            this.extractNatureTypeDsn(dsn);

        } catch (Exception ex) {
            dsn.getDsnState().addErrorMessage(
                    new ErrorMessage("Impossible de parcourir le fichier: ".concat(dsnFile.getAbsolutePath())));
        }
        return dsn;
    }

    /*
     * constituer la liste linéaire des rubriques et des blocs
     */
    private Dsn createDsnFromFile(File dsnFile) {

        Dsn dsn = null;

        if (dsnFile.exists() && dsnFile.isFile() && dsnFile.canRead()) {

            InputStream in = null;
            try {
                in = new FileInputStream(dsnFile);
                dsn = this.readDsnListLines(dsnFile, IOUtils.readLines(in, UTF8));

                this.extractDsnPhase(dsn);
                this.extractNatureTypeDsn(dsn);

            } catch (Exception ex) {
                dsn.getDsnState().addErrorMessage(
                        new ErrorMessage("Impossible de parcourir le fichier: ".concat(dsnFile.getAbsolutePath())));
            } finally {
                IOUtils.closeQuietly(in);
            }

        } else {
            String errorMessage = "Fichier: " + (dsnFile == null ? "null" : dsnFile.getAbsolutePath())
                    + " n'existe pas ou n'est pas un fichier!";
            throw new RuntimeException(errorMessage);
        }

        return dsn;
    }

    private ItemBloc getOrBuildItemBlocErreur(Dsn dsn) {

        ItemBloc itemBlocError = dsn.getItemBlocError();
        if (itemBlocError == null) {
            itemBlocError = new ItemBloc(0, null, "ERROR");
            itemBlocError.setErrorMessage(new ErrorMessage("Bloc d'erreurs"));
            dsn.setItemBlocError(itemBlocError);
        }
        return itemBlocError;
    }

    private void extractDsnPhase(Dsn dsn) {

        ItemRubrique rubPhase = this.dsnService.findOneRubrique(dsn.getRubriques(), BLOC_00, RUB_006);
        if (rubPhase != null) {
            dsn.setPhase(rubPhase.getValue());
        }
    }

    private void extractNatureTypeDsn(Dsn dsn) {
        ItemRubrique rubNature = this.dsnService.findOneRubrique(dsn.getRubriques(), BLOC_05, RUB_001);
        if (rubNature != null) {
            dsn.setNature(rubNature.getValue());
        } else {
            dsn.getDsnState().addErrorMessage(new ErrorMessage("Impossible de déterminer la nature de la DSN!"));
        }
        ItemRubrique rubType = this.dsnService.findOneRubrique(dsn.getRubriques(), BLOC_05, RUB_002);
        if (rubType != null) {
            dsn.setType(rubType.getValue());
        } else {
            dsn.getDsnState().addErrorMessage(new ErrorMessage("Impossible de déterminer le type de la DSN!"));
        }
    }

    /*
     * Description de la structure arborescente de la DSN (phase/nature)
     */
    private BlocTree buildRootTree(InputStream enteteInputStream, InputStream jsonInputStream) {

        if (enteteInputStream == null || jsonInputStream == null) {
            return null;
        }
        BlocTree treeBlocs = new BlocTree();

        try {
            JSONObject json = new JSONObject(IOUtils.toString(enteteInputStream, Charset.forName(UTF8)));
            enteteInputStream.close();
            jsonToBlocTree(treeBlocs, json);

            BlocTree bloc05 = treeBlocs.findChild(BLOC_05, true);
            if (bloc05 != null) {
                json = new JSONObject(IOUtils.toString(jsonInputStream, Charset.forName(UTF8)));
                jsonInputStream.close();
                jsonToBlocTree(bloc05, json);

            }
        } catch (Exception ex) {
            System.out.println("Echec lors de la lecture du fichier json");
            throw new RuntimeException(ex.getMessage());
        }
        return treeBlocs;
    }

    /*
     * Methode recursive Construction d'un bloc fils à partir de jsonObject et
     * rattachement à son parent
     */
    private void jsonToBlocTree(BlocTree blocTreeParent, JSONObject jsonObject) throws JSONException {

        if (jsonObject != null) {

            String blocLabel = null;
            if (jsonObject.has(IJsonConstants.JSON_BLOC) && !jsonObject.isNull(JSON_BLOC)) {
                blocLabel = jsonObject.getString(JSON_BLOC);
            }
            String cardinalite = null;
            if (jsonObject.has(IJsonConstants.JSON_CARDINALITE)
                    && !jsonObject.isNull(IJsonConstants.JSON_CARDINALITE)) {
                cardinalite = jsonObject.getString(JSON_CARDINALITE);
            }

            boolean actif = false;
            if (jsonObject.has(IJsonConstants.JSON_ACTIF) && !jsonObject.isNull(JSON_ACTIF)) {
                actif = jsonObject.getBoolean(JSON_ACTIF);
            }

            JSONArray sousBlocs = null;
            if (jsonObject.has(IJsonConstants.JSON_SOUS_BLOCS)) {
                sousBlocs = jsonObject.getJSONArray(JSON_SOUS_BLOCS);
            }

            BlocTree blocDependencies = null;

            if (blocTreeParent.getBlocLabel() == null) {

                blocDependencies = blocTreeParent;
                blocDependencies.setBlocLabel(blocLabel);
                blocDependencies.setActif(actif);
                blocDependencies.setCardinalite(this.getCardinalite(cardinalite));
            }

            else if (blocLabel != null && blocLabel.isEmpty()) {
                blocDependencies = blocTreeParent;
            } else {
                blocDependencies = new BlocTree();
                blocDependencies.setBlocLabel(blocLabel);
                blocDependencies.setActif(actif);
                blocDependencies.setCardinalite(this.getCardinalite(cardinalite));
            }

            if (blocTreeParent != blocDependencies) {
                blocTreeParent.addChild(blocDependencies);
            }

            if (sousBlocs != null) {

                for (int i = 0; i < sousBlocs.length(); i++) {
                    JSONObject sousBloc = sousBlocs.getJSONObject(i);
                    jsonToBlocTree(blocDependencies, sousBloc);
                }

            }
        }
    }

    private CardinaliteEnum getCardinalite(String cardinalite) {

        CardinaliteEnum result;
        try {

            result = CardinaliteEnum.valueOf(cardinalite);
        } catch (Exception e) {
            result = CardinaliteEnum.UN;
        }

        return result;
    }

}
