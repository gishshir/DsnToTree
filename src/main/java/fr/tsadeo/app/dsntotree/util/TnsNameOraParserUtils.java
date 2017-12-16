package fr.tsadeo.app.dsntotree.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.tsadeo.app.dsntotree.dto.TnsOracleInstanceDto;
import fr.tsadeo.app.dsntotree.util.RegexUtils.CapturingGroups;

public class TnsNameOraParserUtils implements IRegexConstants, IConstants {

    private static final Logger LOG = Logger.getLogger(TnsNameOraParserUtils.class.getName());

    private static final String REGEX_EQUAL = "=";
    private static final String REGEX_START_LINE = REGEX_BEGIN + "\\(DESCRIPTION" + REGEX_EQUAL + REGEX_ANYTHING + "*";


    private static final String REGEX_PROPERTY = "[\\w_\\(\\)=]*";
    private static final String REGEX_SERVICE = REGEX_BEGIN + "(" + REGEX_ALPHANUMERIQUE + "{4,50})" + REGEX_EQUAL +

            "\\(DESCRIPTION" + REGEX_EQUAL // ...
            + "(\\(ADDRESS_LIST=)?" + "\\(ADDRESS" + REGEX_EQUAL // ...
            + "\\(PROTOCOL" + REGEX_EQUAL + "(" + REGEX_ALPHANUMERIQUE + "{1,5})\\)" // ...
            + "\\(HOST" + REGEX_EQUAL + "(" + REGEX_ALPHANUMERIQUE + "*" + REGEX_POINT + REGEX_TREE_CHAR + REGEX_POINT
            + REGEX_TREE_CHAR + ")\\)" // ...
            + "\\(PORT" + REGEX_EQUAL + "(" + REGEX_FOUR_DIGIT + ")[\\)]{2,3}" // ...
            + "\\(CONNECT_DATA" + REGEX_EQUAL // ...
            + REGEX_PROPERTY // ...
            + "\\(SERVICE_NAME" + REGEX_EQUAL + "(" + REGEX_ALPHANUMERIQUE + "*)[\\)]"
            + REGEX_PROPERTY;

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
            String previous = null;
            
            for (String line : listLines) {

                line = line.trim().replace(" ", "").toUpperCase();
                // LOG.config(line);

                // debut
                if (!lineInProcess && RegexUtils.get().matches(line, PATTERN_START_LINE)) {
                    lineInProcess = true;
                    sb = new StringBuilder(previous);
                    sb.append(line);
                }
                // fin
                else if (lineInProcess && line.isEmpty()) {
                    lineInProcess = false;

                    listLineAPlat.add(sb.toString());

                }
                // milieu
                else {
                    if (sb != null) {
                        sb.append(line);
                    }
                }
                previous = line;
            }
        }

        return listLineAPlat;
    }

    private List<TnsOracleInstanceDto> buildListTnsOracleInstance(List<String> listLines) {
        LOG.info("Pattern: " + PATTERN_SERVICE);
        if (listLines == null) {
            return null;
        }
        List<TnsOracleInstanceDto> listInstances = new ArrayList<>();
        
        listLines.stream()
          .forEachOrdered(line -> {
        	  TnsOracleInstanceDto instance = this.buildTsnOracleInstanceDto(line);
              if (instance != null) {
                  listInstances.add(instance);
              } 
          });

        return listInstances;
    }

    private TnsOracleInstanceDto buildTsnOracleInstanceDto(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }

        TnsOracleInstanceDto instance = new TnsOracleInstanceDto();
        CapturingGroups capturingGroups = new CapturingGroups(1, 3, 4, 5, 6);
        RegexUtils.get().extractsGroups(line, PATTERN_SERVICE, capturingGroups);

        if (capturingGroups.isSuccess()) {
            LOG.fine("OK: " + line);
            int i = 1;
            instance.setTnsname(capturingGroups.valueOf(i++));
            i++;
            instance.setProtocole(capturingGroups.valueOf(i++));
            instance.setHost(capturingGroups.valueOf(i++).toLowerCase());
            instance.setPort(NumberUtils.createInteger(capturingGroups.valueOf(i++)));

            instance.setService(capturingGroups.valueOf(i++));
            return instance;
        } else {
            LOG.fine("KO: " + line);
        }

        return null;

    }
}
