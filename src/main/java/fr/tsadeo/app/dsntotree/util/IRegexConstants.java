package fr.tsadeo.app.dsntotree.util;

import java.util.regex.Pattern;

public interface IRegexConstants {
	
    public static final String REGEX_BEGIN = "^";
    public static final String REGEX_ANYTHING = "[.]";
    public static final String REGEX_POINT = "[\\.]";
    public static final String REGEX_SPACE = "[\\s]";
	public static final String REGEX_TWO_DIGIT = "[\\d]{2}";
	public static final String REGEX_TREE_DIGIT = "[\\d]{3}";
    public static final String REGEX_FOUR_DIGIT = "[\\d]{4}";
	public static final String REGEX_NO_NUMERIQUE = "[^\\d]";
    public static final String REGEX_ALPHANUMERIQUE = "[\\w_]";
    public static final String REGEX_PARENTHESES = "[\\(\\)]";
    public static final String REGEX_TREE_CHAR = "[\\w]{3}";
	
    public static final Pattern PATTERN_KEY_VALUE = Pattern
            .compile("(S[\\d]{2}.G[\\d]{2}.[\\d]{2}.[\\d]{3}),[\\s]{0,5}'(.*)'");
    
    public static final Pattern PATTERN_NUM_RUBRIQUE = Pattern.compile("[0-9]{1,3}");
    
    public static final Pattern PATTERN_SEARCH_BLOC_OR_RUBRIQUE =
    		Pattern.compile(REGEX_TWO_DIGIT +  REGEX_POINT + "?" + "[\\d]{0,3}");

    public static final String BLOC = "(" + REGEX_TWO_DIGIT + ")";
    public static final Pattern PATTERN_BLOC = Pattern.compile(BLOC);
    
    public static final String BLOC_RUBRIQUE = BLOC + ".(" + REGEX_TREE_DIGIT + ")";
    public static final Pattern PATTERN_BLOC_RUBRIQUE = Pattern.compile(BLOC_RUBRIQUE);
    
    public static final Pattern PATTERN_PREF_BLOC_RUBRIQUE = Pattern
            .compile("(S" + REGEX_TWO_DIGIT + REGEX_POINT + "G" + REGEX_TWO_DIGIT + ")" + REGEX_POINT + BLOC_RUBRIQUE);

}
