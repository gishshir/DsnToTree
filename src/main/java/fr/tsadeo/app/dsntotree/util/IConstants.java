package fr.tsadeo.app.dsntotree.util;

import java.util.regex.Pattern;

public interface IConstants {

    public static final String SETTINGS_XML = "settings.xml";

    public static final String US = "_";
    public static final String VIRGULE = ",";
    public static final String COTE = "'";
    public static final String POINT = ".";
    public static final String SAUT_LIGNE = "\n";
    public static final String LIGNE = "Ligne: ";
    public static final String TIRET_WITH_SPACE = " - ";

    public static final String RUBRIQUE_PREFIX = "S21.G00";

    public static final String UTF8 = "UTF-8";
    public static final String BLOC_00 = "00";
    public static final String BLOC_05 = "05";
    public static final String BLOC_11 = "11";
    public static final String BLOC_30 = "30";
    public static final String BLOC_90 = "90";

    public static final String RUB_001 = "001";
    public static final String RUB_002 = "002";
    public static final String RUB_003 = "003";
    public static final String RUB_004 = "004";
    public static final String RUB_006 = "006";

    public static final String NATURE_MENSUELLE = "01";
    public static final String NATURE_SIGNAL_FIN_CONTRAT = "02";
    public static final String NATURE_SIGNAL_ARRET_TRAVAIL = "04";
    public static final String NATURE_SIGNAL_REPRISE_SUITE_ARRET_TRAVAIL = "05";

    public static final String PREFIX_PHASE_2 = "P02";
    public static final String PREFIX_PHASE_3 = "P03";

    public static final String JSON_PHASE2_PATH = "phase2";
    public static final String JSON_PHASE3_PATH = "phase3";

    public static final Pattern PATTERN_KEY_VALUE = Pattern
            .compile("(S[\\d]{2}.G[\\d]{2}.[\\d]{2}.[\\d]{3}),[\\s]{0,5}'(.*)'");

    public static final String BLOC_RUBRIQUE = "([\\d]{2}).([\\d]{3})";
    public static final Pattern PATTERN_BLOC_RUBRIQUE = Pattern.compile(BLOC_RUBRIQUE);
    public static final Pattern PATTERN_PREF_BLOC_RUBRIQUE = Pattern
            .compile("(S[\\d]{2}.G[\\d]{2}).".concat(BLOC_RUBRIQUE));

}
