package fr.tsadeo.app.dsntotree.dto;

import fr.tsadeo.app.dsntotree.util.StringUtils;

public class TnsOracleInstanceDto {

    private String tnsname;
    private String protocole;
    private String host;
    private int port;
    private String service;

    // ---------------------- accessors

    public String getProtocole() {
        return protocole;
    }

    public String getTnsname() {
        return tnsname;
    }

    public void setTnsname(String tnsname) {
        this.tnsname = tnsname;
    }

    public void setProtocole(String protocole) {
        this.protocole = protocole;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getService() {
        return service == null?"":service;
    }

    public void setService(String service) {
        this.service = service;
    }

    // ------------------------------------------- public methods

    public boolean matches(String search) {

    	String upperCase = search.toUpperCase();
        return this.tnsname.toUpperCase().indexOf(upperCase) != -1
|| this.service.toUpperCase().indexOf(upperCase) != -1;
    }


    //--------------------------- overriding Object
	@Override
	public String toString() {
        return StringUtils.concat(this.tnsname, " - ", this.protocole, " Host: ", this.host, " Port: ", this.port,
				" Instance: ", this.service);
	}


    
    
}
