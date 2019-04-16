package fr.tsadeo.app.dsntotree.util;

/**
 * Classe pour gérer les constantes liées au batch d'intégration.
 * 
 * @author pjourdan
 * 
 */
public interface IJsonConstants {

    /** Format de date DSN. */
    public static final String DSN_DATE_FORMAT = "ddMMyyyy";

    /** The Constant CODE_PAS_DE_PERSONNEL_COUVERT. */
    public static final String CODE_PERSONNEL_COUVERT = "01";

    /** The Constant CODE_PAS_DE_PERSONNEL_COUVERT. */
    public static final String CODE_PAS_DE_PERSONNEL_COUVERT = "02";

    /** The Constant JSON_BLOC. */
    public static final String JSON_BLOC = "bloc";

    /** The Constant JSON_CARDINALITE. */
    public static final String JSON_CARDINALITE = "cardinalite";

    /** The Constant JSON_SOUS_BLOCS. */
    public static final String JSON_SOUS_BLOCS = "sousBlocs";

    /** The Constant JSON_SOUS_BLOCS. */
    public static final String JSON_ACTIF = "actif";

    /** The Constant JSON_FOLDER_PHASE_03. */
    public static final String JSON_FOLDER_PHASE_03 = "json/phase3/";

    /** The Constant JSON_FOLDER_PHASE_02. */
    public static final String JSON_FOLDER_PHASE_02 = "json/phase2/";
    
    /** The Constant JSON_FOLDER_PHASE_19. */
    public static final String JSON_FOLDER_PHASE_19 = "json/phase19/";
    
    /** The Constant JSON_FOLDER_PHASE_20. */
    public static final String JSON_FOLDER_PHASE_20 = "json/phase20/";

    /** The Constant JSON_FILE_DSN_ENTETE. */
    public static final String JSON_FILE_DSN_ENTETE = "entete.json";

    /** The Constant JSON_FILE_DSN_MENSUELLE. */
    public static final String JSON_FILE_DSN_MENSUELLE = "mensuelle.json";

    /** The Constant JSON_FILE_DSN_SIGNAL_ARRET_TRAVAIL. */
    public static final String JSON_FILE_DSN_SIGNAL_ARRET_TRAVAIL = "signal_arret_travail.json";

    /** The Constant JSON_FILE_DSN_SIGNAL_FIN_CONTRAT. */
    public static final String JSON_FILE_DSN_SIGNAL_FIN_CONTRAT = "signal_fin_contrat.json";

    /**
     * The Constant JSON_FILE_DSN_REPRISE_SUITE_ARRET_TRAVAIL (Meme structure
     * que pour arret de travail).
     */
    public static final String JSON_FILE_DSN_REPRISE_SUITE_ARRET_TRAVAIL = "signal_arret_travail.json";

    /** The Constant JSON_FILE_DSN_REPRISE_HISTORIQUE. */
    public static final String JSON_FILE_DSN_REPRISE_HISTORIQUE = "reprise_historique.json";
}
