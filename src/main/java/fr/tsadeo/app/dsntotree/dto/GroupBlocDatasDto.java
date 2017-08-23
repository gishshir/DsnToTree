package fr.tsadeo.app.dsntotree.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;
import fr.tsadeo.app.dsntotree.util.IConstants;

public class GroupBlocDatasDto implements IConstants {

    private Map<String, List<BlocDatasDto>> mapBlocLabelToListBlocDatas = new HashMap<String, List<BlocDatasDto>>();

    public void addBloc(BlocDatasDto bloc) {

        if (!this.mapBlocLabelToListBlocDatas.containsKey(bloc.getBloc())) {
            List<BlocDatasDto> listBlocs = new ArrayList<BlocDatasDto>();
            this.mapBlocLabelToListBlocDatas.put(bloc.getBloc(), listBlocs);
        }
        List<BlocDatasDto> listBlocs = this.mapBlocLabelToListBlocDatas.get(bloc.getBloc());
        listBlocs.add(bloc);
    }

    public boolean isEmpty() {
        return this.mapBlocLabelToListBlocDatas.isEmpty();
    }

    public String extractDsnPhase() {
        DataDsn dataDsn = this.extractRubrique(BLOC_00, RUB_006);
        return dataDsn == null ? null : dataDsn.getValue();
    }

    public String extractDsnNature() {
        DataDsn dataDsn = this.extractRubrique(BLOC_05, RUB_001);
        return dataDsn == null ? null : dataDsn.getValue();
    }

    /**
     * Retourne si existent les blocs non intégrés dans l'arboresence
     * 
     * @return
     */
    public List<BlocDatasDto> getListNotUsedBlocs() {

        List<BlocDatasDto> listNotUsedBlocs = new ArrayList<BlocDatasDto>();

        for (String blocLabel : this.mapBlocLabelToListBlocDatas.keySet()) {
            List<BlocDatasDto> list = this.mapBlocLabelToListBlocDatas.get(blocLabel);
            if (list != null) {
                for (BlocDatasDto blocDatasDto : list) {
                    if (!blocDatasDto.isUsed()) {
                        listNotUsedBlocs.add(blocDatasDto);
                    }
                }
            }
        }
        return listNotUsedBlocs;

    }

    /**
     * Retourne liste de BlocDatas avec label donné et un même numéro de
     * séquence sup
     */
    public List<BlocDatasDto> getListBlocsForLabelAndSeqSup(String blocLabel, int seqSup) {
        List<BlocDatasDto> listAll = this.mapBlocLabelToListBlocDatas.get(blocLabel);
        if (listAll == null) {
            return null;
        }
        List<BlocDatasDto> listBlocForSeqSup = new ArrayList<BlocDatasDto>();

        for (BlocDatasDto blocDatasDto : listAll) {
            if (blocDatasDto.getSequenceSup() == seqSup) {
                listBlocForSeqSup.add(blocDatasDto);
            }
        }

        return listBlocForSeqSup;
    }

    // ---------------------------------------- private methods
    private DataDsn extractRubrique(String blocLabel, String codeRubrique) {

        List<BlocDatasDto> listBlocs = this.mapBlocLabelToListBlocDatas.get(blocLabel);
        if (listBlocs != null) {

            for (BlocDatasDto blocDatasDto : listBlocs) {
                DataDsn dataDsn = this.findDataInBloc(blocDatasDto, codeRubrique);
                if (dataDsn != null) {
                    return dataDsn;
                }
            }
        }
        return null;
    }

    private DataDsn findDataInBloc(BlocDatasDto bloc, String codeRubrique) {
        if (bloc == null || codeRubrique == null) {
            return null;
        }
        String rubrique = bloc.getBloc().concat(POINT).concat(codeRubrique);
        for (DataDsn dataDsn : bloc.getListDatas()) {
            if (dataDsn.getCodeRubrique().equals(rubrique)) {
                return dataDsn;
            }
        }
        return null;
    }
}
