package fr.tsadeo.app.dsntotree.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import fr.tsadeo.app.dsntotree.dto.LinkedPropertiesDto;
import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ErrorMessage;
import fr.tsadeo.app.dsntotree.model.ItemBloc;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;

public class ReadDsnFromFileService extends AbstractReadDsn {

    public static void main(String[] args) {

        if (args == null || args.length == 0) {
            System.out.println("manque le nom du fichier dsn à analyser...");
        }

        File file = new File(args[0]);
        new ReadDsnFromFileService().buildTreeFromFile(file);
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

        // si bloc d'erreur on l'affiche en premier
        if (dsn.getItemBlocError() != null) {
            root.addFirstChild(dsn.getItemBlocError());
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

}
