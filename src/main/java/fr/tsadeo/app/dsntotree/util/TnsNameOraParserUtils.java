package fr.tsadeo.app.dsntotree.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.tsadeo.app.dsntotree.dto.TnsOracleInstanceDto;
import fr.tsadeo.app.dsntotree.util.RegexUtils.CapturingGroups;

public class TnsNameOraParserUtils implements IRegexConstants, IConstants {

    private static final Logger LOG = Logger.getLogger(TnsNameOraParserUtils.class.getName());

    private static final String REGEX_START_LINE = "^" + REGEX_SPACE + "{1,5}\\(DESCRIPTION =" + REGEX_ANYTHING + "*";

    private static final String REGEX_SERVICE =

    		REGEX_SPACE + "{1,4}\\(DESCRIPTION =" +  REGEX_SPACE + "{1,4}\\(ADDRESS = " // ...
                    + "\\(PROTOCOL = (\\w{1,5})" // ...
                    + REGEX_SPACE_OR_PARENTHESES + "*" // ...
                    + "HOST = (" + REGEX_ALPHANUMERIQUE + "*" + REGEX_POINT + REGEX_TREE_CHAR + REGEX_POINT
                    + REGEX_TREE_CHAR + ")" // ...
                    + REGEX_SPACE_OR_PARENTHESES // ...
                    + "*PORT = (" + REGEX_FOUR_DIGIT + ")" // ...
                    + "[\\s\\(\\)\\w=]*" // ...
                    + "SERVICE_NAME = (" + REGEX_ALPHANUMERIQUE + "*)[\\)]{3}";

    private static final Pattern PATTERN_START_LINE = Pattern.compile(REGEX_START_LINE);
    private static final Pattern PATTERN_SERVICE = Pattern.compile(REGEX_SERVICE);

	  private static TnsNameOraParserUtils instance;

	    public static TnsNameOraParserUtils get() {
	        if (instance == null) {
	            instance = new TnsNameOraParserUtils();
	        }
	        return instance;
	    }


	    private TnsNameOraParserUtils() {
	    }

    public List<TnsOracleInstanceDto> loadTnsOracleFile(File file) {

        List<String> listLines = this.mettreAPlat(file);
        return this.buildListTnsOracleInstance(listLines);
    }

     private List<String> mettreAPlat(File file) {
        if (file == null || !file.exists() || !file.canRead()) {
            return null;
        }

        List<String> listLineAPlat = null;
        List<String> listLines = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            String encoding = SettingsUtils.get().getDsnEncoding();
            listLines = IOUtils.readLines(in, encoding == null ? ISO_8859_1 : encoding);

        } catch (Exception ex) {
            LOG.warning("Impossible de charger le fichier: ".concat(file.getAbsolutePath()));
        } finally {
            IOUtils.closeQuietly(in);
        }

        if (listLines != null) {

            listLineAPlat = new ArrayList<>();
            StringBuilder sb = null;
            boolean lineInProcess = false;
            for (String line : listLines) {
                // debut
                if (!lineInProcess && RegexUtils.get().matches(line, PATTERN_START_LINE)) {
                    lineInProcess = true;
                    sb = new StringBuilder(line);
                }
                // fin
                else if (lineInProcess && line.trim().isEmpty()) {
                        lineInProcess = false;
                        listLineAPlat.add(sb.toString());

                }
                // milieu
                else {
                    if (sb != null) {
                    	sb.append(SPACE).append(line);
                    }
                }
            }
        }

        return listLineAPlat;
    }

    private List<TnsOracleInstanceDto> buildListTnsOracleInstance(List<String> listLines) {
        if (listLines == null) {
            return null;
        }
        List<TnsOracleInstanceDto> listInstances = new ArrayList<>();
        for (String line : listLines) {
            TnsOracleInstanceDto instance = this.buildTsnOracleInstanceDto(line);
            if (instance != null) {
                listInstances.add(instance);
            }
        }
        
   
        return listInstances;
    }

    private TnsOracleInstanceDto buildTsnOracleInstanceDto(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }

        TnsOracleInstanceDto instance = new TnsOracleInstanceDto();
        CapturingGroups capturingGroups = new CapturingGroups(1, 2, 3, 4);
        RegexUtils.get().extractsGroups(line, PATTERN_SERVICE, capturingGroups);
        
        if (capturingGroups.isSuccess()){
          instance.setProtocole(capturingGroups.valueOf(1));
          instance.setHost(capturingGroups.valueOf(2));
          instance.setPort(NumberUtils.createInteger(capturingGroups.valueOf(3)));
           
           instance.setService(capturingGroups.valueOf(4));
           return instance;
        }
        
        return null;
        
    }
}
