package fr.tsadeo.app.dsntotree.dto;

import org.apache.commons.lang3.StringUtils;

public class BddConnexionDto {

	private final String driver;
	private final String url;
	private final String user;
	private final String pwd;
	
	
	public String getDriver() {
		return driver;
	}


	public String getUrl() {
		return url;
	}


	public String getUser() {
		return user;
	}


	public String getPwd() {
		return pwd;
	}


	public BddConnexionDto(String driver, String url, String user, String pwd)
 {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.pwd = pwd;
		}

    // ---------------------------------------- overriding Object
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((driver == null) ? 0 : driver.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        result = prime * result + ((pwd == null) ? 0 : pwd.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        BddConnexionDto other = (BddConnexionDto) obj;
        if (!StringUtils.equals(this.driver, other.driver)) {
            return false;
        }
        if (!StringUtils.equals(this.url, other.url)) {
            return false;
        }
        if (!StringUtils.equals(this.user, other.user)) {
            return false;
        }
        if (!StringUtils.equals(this.pwd, other.pwd)) {
            return false;
        }
        return true;
    }

 }
