package fr.tsadeo.app.dsntotree.dto;

import fr.tsadeo.app.dsntotree.util.StringUtils;

public class TnsOracleInstanceDto {

    private String protocole;
    private String host;
    private int port;
    private String service;

    // ---------------------- accessors
    public String getProtocole() {
        return protocole;
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

    //--------------------------- overriding Object
	@Override
	public String toString() {
		return StringUtils.concat(this.protocole, " Host: ", this.host, " Port: ", this.port,
				" Instance: ", this.service);
	}

    
    
}
