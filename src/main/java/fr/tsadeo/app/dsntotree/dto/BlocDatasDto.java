package fr.tsadeo.app.dsntotree.dto;

import java.util.ArrayList;
import java.util.List;

import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;
import fr.tsadeo.app.dsntotree.util.IConstants;

/**
 * Dto utilsé pour la reconstruction de l'arborescence d'une DSN à partir d'une
 * liste de DataDsn issu de la table DSDO
 * 
 * @author sfauche
 *
 */
public class BlocDatasDto implements IConstants {

    private final int sequence;
    private final int sequenceSup;
    private final String bloc;

    private boolean used = false;

    // champ calculé d'une clé unique pour un bloc de data
    private final String key;

    public static String getKeyBlocFromData(DataDsn dataDsn) {
        return dataDsn.getNumSequenceBloc() + US + dataDsn.getNumSequenceBlocSup() + US + dataDsn.getBloc();
    }

    private final List<DataDsn> listDatas = new ArrayList<DataDsn>();

    // ------------------------------------------ constructor
    public BlocDatasDto(int seq, int seqsup, String bloc) {
        this.sequence = seq;
        this.sequenceSup = seqsup;
        this.bloc = bloc;

        this.key = this.getKeyBloc();
    }

    // ---------------------------- public methods
    public String getIntervalleLines() {
        StringBuilder sb = new StringBuilder();
        if (!this.listDatas.isEmpty()) {
            if (this.listDatas.size() == 1) {
                sb.append("ligne ").append(this.listDatas.get(0).getNumLine());
            } else {
                sb.append("lignes: [");
                sb.append(this.listDatas.get(0).getNumLine());
                sb.append(" , ");
                sb.append(this.listDatas.get(this.listDatas.size() - 1).getNumLine());
                sb.append("]");
            }
        }

        return sb.toString();
    }
    // -------------------------------- accessors

    public void addData(DataDsn data) {
        this.listDatas.add(data);
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public int getSequence() {
        return sequence;
    }

    public int getSequenceSup() {
        return sequenceSup;
    }

    public String getBloc() {
        return bloc;
    }

    public List<DataDsn> getListDatas() {
        return listDatas;
    }

    public String getKey() {
        return this.key;
    }

    // ------------------------------------------ private methods
    private String getKeyBloc() {
        return sequence + "_" + sequenceSup + "_" + bloc;
    }

    @Override
    public String toString() {
        return this.getKey();
    }
}
