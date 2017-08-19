package fr.tsadeo.app.dsntotree.dto;

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
 }
