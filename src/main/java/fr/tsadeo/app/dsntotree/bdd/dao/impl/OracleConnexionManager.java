package fr.tsadeo.app.dsntotree.bdd.dao.impl;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.tsadeo.app.dsntotree.bdd.dao.IConnectionManager;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.model.xml.OracleConnexion;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class OracleConnexionManager implements IConnectionManager {

    private static final String ORACLE_JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String ORACLE_DB_URL = "jdbc:oracle:thin:@%1$s:%2$d/%3$s";
    
  private static final Pattern PATTERN_DB_URL = Pattern
  .compile("^jdbc:oracle:thin:@([\\w.]*):([\\d]{1,4})\\/([\\w]{1,10})");

    @Override
    public Type getType() {
        return Type.Oracle;
    }

    @Override
    public String getDriver() {
        return ORACLE_JDBC_DRIVER;
    }

    @Override
    public BddConnexionDto getDefaultConnexion() {

        return this.mapOracleConnextionToDto(SettingsUtils.get().getDefaultOracleConnexion());
    }

    // ------------------------------------------------------------- private
    // methods
    private BddConnexionDto mapOracleConnextionToDto(OracleConnexion oracleConnexion) {

        if (oracleConnexion == null) {
            return null;
        }

        return new BddConnexionDto(ORACLE_JDBC_DRIVER,
                getUrl(oracleConnexion.getHost(), oracleConnexion.getPort(), oracleConnexion.getInstance()),
                oracleConnexion.getUser(), oracleConnexion.getPassword());

    }

    // ---------------------------------------- protected methods
    public String getUrl(String host, Integer port, String instance) {
        return String.format(ORACLE_DB_URL, host, port, instance);
    }
    
    public UrlParametersDto getUrlParameters(String url) {
    	
    	UrlParametersDto dto = new UrlParametersDto();
        Matcher m = PATTERN_DB_URL.matcher(url);
        if (m.matches()) {
            int count = m.groupCount();
            if (count == 3) {
            	dto.setHost(m.group(1));
                dto.setPort(m.group(2));
                dto.setInstance(m.group(3));
            }
        }
        return dto;
    }
    
    
    //========================================= INNER CLASS
    public static class UrlParametersDto {
    	private String host;
    	private String port;
    	private String instance;
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public String getPort() {
			return port;
		}
		public void setPort(String port) {
			this.port = port;
		}
		public String getInstance() {
			return instance;
		}
		public void setInstance(String instance) {
			this.instance = instance;
		}
    	
    	
    }

}
