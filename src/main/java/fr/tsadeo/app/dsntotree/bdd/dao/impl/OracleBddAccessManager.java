package fr.tsadeo.app.dsntotree.bdd.dao.impl;


import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.model.xml.OracleBddAccess;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public class OracleBddAccessManager implements IBddAccessManager {

    private static final String ORACLE_JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String ORACLE_DB_URL = "jdbc:oracle:thin:@%1$s:%2$d/%3$s";
    
  private static final Pattern PATTERN_DB_URL = Pattern
  .compile("^jdbc:oracle:thin:@([\\w.]*):([\\d]{1,4})\\/([\\w]{1,10})");

  
    private BddConnexionDto bddConnexionDto;
    
    @Override
    public Type getType() {
        return Type.Oracle;
    }

    @Override
    public String getDriver() {
        return ORACLE_JDBC_DRIVER;
    }

    @Override
    public BddConnexionDto getDefaultBddConnexionDto() {

    	  List<OracleBddAccess> list = SettingsUtils.get().getListOracleBddAccess();
          if (list != null) {
              for (OracleBddAccess oracleBddAccess : list) {
                  if (oracleBddAccess.getDefaut()) {
                      return this.mapOracleBddAccessToDto(oracleBddAccess);
                  }
              }
          }
          return null;
    }
    @Override
    public BddConnexionDto getBddConnexionDto(String instance) {
    	
    	List<OracleBddAccess> list = SettingsUtils.get().getListOracleBddAccess();
        if (list != null) {
            for (OracleBddAccess oracleBddAccess : list) {
                if (oracleBddAccess.getInstance().equals(instance)) {
                    return this.mapOracleBddAccessToDto(oracleBddAccess);
                }
            }
        }
        return null;
    }
    
    @Override
    public void createOrUpdateBddConnexion(BddConnexionDto bddConnexionDto) {
    	
    	OracleBddAccess oracleBddAccess = this.mapDtoToOracleDbbAccess(bddConnexionDto);
    	if (oracleBddAccess != null) {
    		
    		List<OracleBddAccess> list = SettingsUtils.get().getListOracleBddAccess();
            if (list != null) {
            	
        		BddConnexionDto bddConnexionExistante = this.getBddConnexionDto(oracleBddAccess.getInstance());
        		if (bddConnexionExistante != null) {
        		   this.removeBddAccess(oracleBddAccess.getInstance(), list);
        		}
            	
            	list.add(oracleBddAccess);
            }
    	}
    }
 
    
	@Override
	public BddConnexionDto getCurrentBddConnexionDto() {
		return this.bddConnexionDto != null ? this.bddConnexionDto:this.getDefaultBddConnexionDto();
	}
	@Override
	public void setCurrentBddConnexionDto(BddConnexionDto bddConnexionDto) {
		this.bddConnexionDto = bddConnexionDto;
	}


    // ----------------------------------------------- private methods
    private BddConnexionDto mapOracleBddAccessToDto(OracleBddAccess oracleBddAccess) {

        if (oracleBddAccess == null) {
            return null;
        }

        return new BddConnexionDto(ORACLE_JDBC_DRIVER,
                getUrl(oracleBddAccess.getHost(), oracleBddAccess.getPort(), oracleBddAccess.getInstance()),
                oracleBddAccess.getUser(), oracleBddAccess.getPassword());

    }
    private OracleBddAccess mapDtoToOracleDbbAccess(BddConnexionDto bddConnexionDto) {
    	
    	if (bddConnexionDto == null) {
    		return null;
    	}
    	
    	UrlParametersDto urlParametersDto = this.getUrlParameters(bddConnexionDto.getUrl());
    	
    	OracleBddAccess bddAccess = new OracleBddAccess();
    	bddAccess.setHost(urlParametersDto.getHost());
    	bddAccess.setInstance(urlParametersDto.getInstance());
    	bddAccess.setPort(StringUtils.convertToInt(urlParametersDto.getPort(), 1521));
    	bddAccess.setUser(bddConnexionDto.getUser());
    	bddAccess.setPassword(bddConnexionDto.getPwd());
    	
    	return bddAccess;
    }
    
    private boolean removeBddAccess (String instance, List<OracleBddAccess> list ) {
    	
    	Iterator<OracleBddAccess> iter = list.iterator();
    	while (iter.hasNext()) {
			OracleBddAccess oracleBddAccess = iter.next();
			if (oracleBddAccess.getInstance().equals(instance)) {
				iter.remove();
				return true;
			}
		}
    	return false;
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
