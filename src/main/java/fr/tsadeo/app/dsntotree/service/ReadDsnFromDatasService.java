package fr.tsadeo.app.dsntotree.service;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;
import fr.tsadeo.app.dsntotree.dto.BlocDatasDto;
import fr.tsadeo.app.dsntotree.dto.GroupBlocDatasDto;
import fr.tsadeo.app.dsntotree.gui.MyFrame;
import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ErrorMessage;
import fr.tsadeo.app.dsntotree.model.ItemBloc;

public class ReadDsnFromDatasService extends AbstractReadDsn {

	private static final Logger LOG = Logger.getLogger(ReadDsnFromDatasService.class.getName());
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
                dsn.setType(groupBlocs.extractDsnType());

                // contruire l'arbre des ItemBloc avec leur rubriques
                this.buildDsnTreeFromDatas(dsn, groupBlocs);

                // reconstruire la liste linéaire des blocs
                ServiceFactory.getDsnService().updateDsnAllListBlocs(dsn);

                // on numerote les blocs enfant
                for (ItemBloc itemBloc : dsn.getBlocs()) {
                    ServiceFactory.getDsnService().numeroterIndexChildren(itemBloc);
                }

                dsn.getDsnState().setModified(true);
            } else {
                throw new RuntimeException("Phase ou nature de la DSN inconnue!");
            }
        }

        return dsn;
    }
	@Override
	protected Logger getLog() {
		return LOG;
	}

    private void buildDsnTreeFromDatas(Dsn dsn, GroupBlocDatasDto groupBlocs) {

        // organisation des blocs en arborescence
        ItemBloc itemRoot = this.buildItemTreeFromDatas(dsn, groupBlocs);

        StringBuffer sb = new StringBuffer();
        itemRoot.showChildrens("", sb);
        LOG.config(sb.toString());

        dsn.setRoot(itemRoot);

        if (dsn.getDsnState().isError()) {
            LOG.config("\nDSN en erreur");
            itemRoot.setErrorMessage(new ErrorMessage("DSN en erreur"));
            for (ErrorMessage error : dsn.getDsnState().getListErrorMessage()) {
                LOG.severe(error.toString());
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

            // Gerer les blocs orphelins
            this.gererBlocOrphelins(dsn, groupBlocs);

            // si bloc d'erreur on l'affiche en premier
            if (dsn.getItemBlocError() != null) {
                root.addFirstChild(dsn.getItemBlocError());
            }

        }

        return root;

    }

    // On cherche les BlocDataDtos orphelins
    // (label bloc inconnu dans le json)
    private void gererBlocOrphelins(Dsn dsn, GroupBlocDatasDto groupBlocs) {

        List<BlocDatasDto> listNotUsedBlocs = groupBlocs.getListNotUsedBlocs();
        if (!listNotUsedBlocs.isEmpty()) {
            ItemBloc itemBlocError = super.getOrBuildItemBlocErreur(dsn);
            for (BlocDatasDto blocDatasDto : listNotUsedBlocs) {

                ItemBloc itemBloc = ServiceFactory.getDsnService().createNewBloc(blocDatasDto.getBloc());
                ErrorMessage errorMessage = new ErrorMessage(-1, blocDatasDto.getIntervalleLines().concat(" - Bloc ")
                        .concat(blocDatasDto.getBloc()).concat(" inconnu dans l'arborescence!"));
                itemBloc.setErrorMessage(errorMessage);
                dsn.getDsnState().addErrorMessage(errorMessage);
                itemBlocError.addChild(itemBloc);

                for (DataDsn dataDsn : blocDatasDto.getListDatas()) {
                    itemBloc.addRubrique(ServiceFactory.getDsnService().createNewRubrique(itemBloc, dataDsn));
                }
            }
        }
    }

    // Fonction recursive
    private void buildItemBlocFromDatas(ItemBloc blocParent, BlocTree blocTree, BlocDatasDto blocDatasDto,
            GroupBlocDatasDto groupBlocs) {

        ItemBloc blocChild = ServiceFactory.getDsnService().createNewChild(blocParent, blocTree.getBlocLabel());
        blocChild.setIndex(blocDatasDto.getSequence() + 1);
        blocChild.setOrder(blocDatasDto.getSequence());
        blocDatasDto.setUsed(true);

        // on construit la liste des ItemRubriques
        for (DataDsn dataDsn : blocDatasDto.getListDatas()) {

            blocChild.addRubrique(ServiceFactory.getDsnService().createNewRubrique(blocChild, dataDsn));
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
                blocParent.getOrder());
        if (listBlocDatas != null) {

            // pour chaque blocDatas on construit un ItemBloc avec ses rubriques
            for (BlocDatasDto blocDatasDto : listBlocDatas) {
                if (!blocDatasDto.isUsed()) {
                    this.buildItemBlocFromDatas(blocParent, blocTree, blocDatasDto, groupBlocs);
                }
            }

        }

    }


}
