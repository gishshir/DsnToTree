package fr.tsadeo.app.dsntotree.bdd.dao.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.model.xml.Credential;
import fr.tsadeo.app.dsntotree.model.xml.Credentials;
import fr.tsadeo.app.dsntotree.model.xml.OracleBddAccess;
import fr.tsadeo.app.dsntotree.util.RegexUtils;
import fr.tsadeo.app.dsntotree.util.RegexUtils.CapturingGroups;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public class OracleBddAccessManager implements IBddAccessManager {

    private static final String ORACLE_JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String ORACLE_DB_URL = "jdbc:oracle:thin:@%1$s:%2$d/%3$s";
    
  private static final Pattern PATTERN_DB_URL = Pattern
            .compile("^jdbc:oracle:thin:@([\\w.]*):([\\d]{1,4})\\/([\\w_]{1,20})");

  
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
                  if (oracleBddAccess.isDefaut()) {
                    return this.mapOracleBddAccessToDtoWithFirstCredential(oracleBddAccess);
                  }
              }
          }
          return null;
    }
    @Override
    public List<BddConnexionDto> getListBddConnexionDto(String instance) {
    	
        OracleBddAccess oracleBddAccessForInstance = this.getOracleBddAccessByInstance(instance);

        if (oracleBddAccessForInstance != null) {

            return this.mapOracleBddAccessToDto(oracleBddAccessForInstance);
        }
        return null;
    }
    
    private List<Credential> getListCredential(Credentials credentials) {

        List<Credential> list = new ArrayList<>();
        if (credentials != null && credentials.getCredential() != null) {
            list.addAll(credentials.getCredential());
        }
        return list;
    }

    @Override
    public boolean createOrUpdateBddConnexion(BddConnexionDto bddConnexionToUpdate) {

        if (bddConnexionToUpdate != null) {

            OracleBddAccess oracleBddAccessToUpdate = this.mapDtoToOracleDbbAccess(bddConnexionToUpdate);
            if (oracleBddAccessToUpdate == null) {
                return false;
            }
    		
            OracleBddAccess oracleBddAccessInSettings = this
                    .getOracleBddAccessByInstance(oracleBddAccessToUpdate.getInstance());

            // cette instance existe déjà dans les Settings
            if (oracleBddAccessInSettings != null) {
            	
                // instance exists!
                boolean conExists = false;
                boolean conEquals = false;

                Credential credentialInSettings = this.findCredentialWithUser(
                        this.getListCredential(oracleBddAccessInSettings.getCredentials()),
                        bddConnexionToUpdate.getUser());
                conExists = credentialInSettings != null;
                conEquals = conExists ? credentialInSettings.getPassword().equals(bddConnexionToUpdate.getPwd())
                        : false;

                if (conExists && !conEquals) {
                    // update
                    credentialInSettings.setPassword(bddConnexionToUpdate.getPwd());
                    return true;
                }
                if (!conExists) {
                    oracleBddAccessInSettings.getCredentials().getCredential()
                            .add(this.mapToCredential(bddConnexionToUpdate));
                    return true;
        		}
            } else {
                // TODO n'existe pas >>> creation
                SettingsUtils.get().getListOracleBddAccess().add(oracleBddAccessToUpdate);
                return true;
            }
    	}
        return false;
    }

    @Override
    public BddConnexionDto getCurrentBddConnexionDto() {
        return this.bddConnexionDto != null ? this.bddConnexionDto : this.getDefaultBddConnexionDto();
    }

    @Override
    public void setCurrentBddConnexionDto(BddConnexionDto bddConnexionDto) {
        this.bddConnexionDto = bddConnexionDto;
    }

    @Override
	public BddConnexionDto getBddConnectionByName(String name) {

		List<OracleBddAccess> list =  SettingsUtils.get().getListOracleBddAccess();
		
		if (list != null) {

			for (OracleBddAccess oracleBddAccess : list) {

				List<BddConnexionDto> listBddConnection = this.mapOracleBddAccessToDto(oracleBddAccess);
				if (listBddConnection != null && !listBddConnection.isEmpty()) {

					for (BddConnexionDto bddConnexionDto : listBddConnection) {
						if (bddConnexionDto.getUser().startsWith(name)) {
							return bddConnexionDto;
						}
					}
				}
			}
		}
		return null;
	}


    // ----------------------------------------------- private methods

    private Credential mapToCredential(BddConnexionDto bddConnexionDto) {
        Credential credential = new Credential();
        credential.setUser(bddConnexionDto == null ? "" : bddConnexionDto.getUser());
        credential.setPassword(bddConnexionDto == null ? "" : bddConnexionDto.getPwd());
        return credential;
    }
    private Credential findCredentialWithUser(List<Credential> listCredential, String user) {
        if (listCredential == null || user == null) {
            return null;
        }
        for (Credential credential : listCredential) {
            if (credential.getUser().equals(user)) {
                return credential;
            }
        }
        return null;
    }
    private OracleBddAccess getOracleBddAccessByInstance(String instance) {

        List<OracleBddAccess> list = instance == null ? null : SettingsUtils.get().getListOracleBddAccess();
        if (list != null) {

            for (OracleBddAccess oracleBddAccess : list) {
                if (oracleBddAccess.getInstance().equals(instance)) {
                    return oracleBddAccess;
                }
            }
        }

        return null;
    }

    private List<BddConnexionDto> mapOracleBddAccessToDto(OracleBddAccess oracleBddAccess) {

        if (oracleBddAccess == null) {
            return null;
        }

        List<BddConnexionDto> list = new ArrayList<>();
        for (Credential credential : getListCredential(oracleBddAccess.getCredentials())) {
            list.add(new BddConnexionDto(ORACLE_JDBC_DRIVER,
                    getUrl(oracleBddAccess.getHost(), oracleBddAccess.getPort(), oracleBddAccess.getInstance()),
                    credential.getUser(), credential.getPassword()));
        }
        return list;

    }

    private BddConnexionDto mapOracleBddAccessToDtoWithFirstCredential(OracleBddAccess oracleBddAccess) {

        if (oracleBddAccess == null) {
            return null;
        }
        List<Credential> listCredential = getListCredential(oracleBddAccess.getCredentials());
        if (!listCredential.isEmpty()) {

            Credential firstCredential = listCredential.get(0);
            return new BddConnexionDto(ORACLE_JDBC_DRIVER,
                    getUrl(oracleBddAccess.getHost(), oracleBddAccess.getPort(), oracleBddAccess.getInstance()),
                    firstCredential.getUser(), firstCredential.getPassword());
        }

        return null;

    }

    private OracleBddAccess mapDtoToOracleDbbAccess(BddConnexionDto bddConnexionDto) {

        if (bddConnexionDto == null) {
            return null;
        }

        UrlParametersDto urlParametersDto = this.getUrlParameters(bddConnexionDto.getUrl());

        if (urlParametersDto != null) {
            OracleBddAccess bddAccess = new OracleBddAccess();
            bddAccess.setHost(urlParametersDto.getHost());
            bddAccess.setInstance(urlParametersDto.getInstance());
            bddAccess.setPort(StringUtils.convertToInt(urlParametersDto.getPort(), 1521));

            Credentials credentials = new Credentials();
            credentials.getCredential().add(this.mapToCredential(bddConnexionDto));
            bddAccess.setCredentials(credentials);

            return bddAccess;

        }

        return null;
    }
    
    
    // ---------------------------------------- protected methods
    public String getUrl(String host, Integer port, String instance) {
        return String.format(ORACLE_DB_URL, host, port, instance);
    }
    
    public UrlParametersDto getUrlParameters(String url) {
    	
        UrlParametersDto dto = null;
        CapturingGroups capturingGroups = new CapturingGroups(1, 2, 3);
        RegexUtils.get().extractsGroups(url, PATTERN_DB_URL, capturingGroups);

        if (capturingGroups.isSuccess()) {
            dto = new UrlParametersDto();
            dto.setHost(capturingGroups.valueOf(1));
            dto.setPort(capturingGroups.valueOf(2));
            dto.setInstance(capturingGroups.valueOf(3));
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
