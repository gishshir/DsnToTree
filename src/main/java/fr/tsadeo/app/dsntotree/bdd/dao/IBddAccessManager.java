package fr.tsadeo.app.dsntotree.bdd.dao;

import java.util.List;
import java.util.regex.Pattern;

import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;

public interface IBddAccessManager {
	
	public static final Pattern PATTERN_PORT = Pattern.compile("[0-9]{1,4}");
	

    public enum Type {
        Oracle, Mysql, PostGre
    }
    
    public Type getType();
    
    public String getDriver();

    public BddConnexionDto getDefaultBddConnexionDto();
    
    public BddConnexionDto getCurrentBddConnexionDto();
    
    public List<BddConnexionDto> getListBddConnexionDto(String instance);
    
    public void setCurrentBddConnexionDto(BddConnexionDto bddConnexionDto);
    
    public boolean createOrUpdateBddConnexion(BddConnexionDto bddConnexionDto);
    
    public BddConnexionDto getBddConnectionByName(String name);

}
