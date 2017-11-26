package fr.tsadeo.app.dsntotree.bdd.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager.Type;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.service.AbstractTest;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class BddAccessManagerTest extends AbstractTest {

    @Before
    public void init() throws Exception {

        SettingsUtils.get().readApplicationSettings(getFile(XML_SETTINGS));
    }



    @Test
    public void testGetDefaultConnexion() {

        BddConnexionDto result = BddAccessManagerFactory.get(Type.Oracle).getDefaultBddConnexionDto();
        this.assertBddConnexionDto(result);
    }
    
    @Test
    public void getBddConnexionDto() {
    	BddConnexionDto result = BddAccessManagerFactory.get(Type.Oracle).getBddConnexionDto("INSTANCE");
    	this.assertBddConnexionDto(result);
    	
    	result = BddAccessManagerFactory.get(Type.Oracle).getBddConnexionDto("NOT EXISTS");
    	assertNull(result);
    }
    
    private void assertBddConnexionDto(BddConnexionDto dto) {
    	
    	assertNotNull(dto);
        
        assertNotNull(dto.getUrl());
        assertNotNull(dto.getPwd());
        assertNotNull(dto.getUser());
    }

}
