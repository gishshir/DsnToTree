package fr.tsadeo.app.dsntotree.bdd.model;

import fr.tsadeo.app.dsntotree.util.StringUtils;

public class DataDsn extends EntiteBase {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** Bloc de rattachement exemple '73'. */
    private String bloc;

    /**
     * <bloc>.<rubrique> exemple '73.005'
     */
    private String codeRubrique;

    /**
     * La valeur de la donnée DSN.
     */
    private String value;

    /** N° séquence bloc. */
    private Integer numSequenceBloc;
    /** N° séquence bloc bloc superieur. */
    private Integer numSequenceBlocSup;

    // -------------------------------------------- accessors

    public String getCodeRubrique() {
        return codeRubrique;
    }

    public String getBloc() {
        return bloc;
    }

    public void setBloc(String bloc) {
        this.bloc = bloc;
    }

    public void setCodeRubrique(String codeRubrique) {
        this.codeRubrique = codeRubrique;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getNumSequenceBloc() {
        return numSequenceBloc;
    }

    public void setNumSequenceBloc(Integer numSequenceBloc) {
        this.numSequenceBloc = numSequenceBloc;
    }

    public Integer getNumSequenceBlocSup() {
        return numSequenceBlocSup;
    }

    public void setNumSequenceBlocSup(Integer numSequenceBlocSup) {
        this.numSequenceBlocSup = numSequenceBlocSup;
    }

    @Override
    public String toString() {

        return StringUtils.concat("id: ", this.getId(), " - bloc:", this.getBloc(), " - seq: ",
                this.getNumSequenceBloc(), " - ssup: ", this.getNumSequenceBlocSup(), " - rub: ",
                this.getCodeRubrique(), " = ", this.getValue());
    }
}
