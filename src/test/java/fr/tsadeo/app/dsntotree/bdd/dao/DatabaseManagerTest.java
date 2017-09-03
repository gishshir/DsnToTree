package fr.tsadeo.app.dsntotree.bdd.dao;

import static org.junit.Assert.assertTrue;

import org.junit.Before;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager.Type;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.service.AbstractTest;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class DatabaseManagerTest extends AbstractTest{
	
    @Before
    public void init() throws Exception {

        SettingsUtils.get().readApplicationSettings(getFile(XML_SETTINGS));
    }



    
	@Test
	public void testTesterConnexion() {
		
		BddConnexionDto connexionDto = new BddConnexionDto("", "", "", "");
		boolean result = DatabaseManager.get().testerConnexion(connexionDto);
		assertFalse(result);
		
		connexionDto = BddAccessManagerFactory.get(Type.Oracle).getDefaultBddConnexionDto();
		assertNotNull(connexionDto);
		result = DatabaseManager.get().testerConnexion(connexionDto);
		assertTrue(result);
	}
}
