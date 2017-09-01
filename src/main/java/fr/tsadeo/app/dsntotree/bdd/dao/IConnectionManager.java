package fr.tsadeo.app.dsntotree.bdd.dao;

import java.util.regex.Pattern;

import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;

public interface IConnectionManager {
	
	public static final Pattern PATTERN_PORT = Pattern.compile("[0-9]{1,4}");
	

    public enum Type {
        Oracle, Mysql, PostGre
    }
    
    public Type getType();
    
    public String getDriver();

    public BddConnexionDto getDefaultConnexion();

}