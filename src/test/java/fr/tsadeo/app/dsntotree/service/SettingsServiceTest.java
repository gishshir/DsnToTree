package fr.tsadeo.app.dsntotree.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.model.xml.Bdd;
import fr.tsadeo.app.dsntotree.model.xml.OracleConnexion;
import fr.tsadeo.app.dsntotree.model.xml.Settings;

public class SettingsServiceTest extends AbstractTest {
	
	private static SettingsService service = ServiceFactory.getSettingsService();
	
	@Test
	public void testReadSettings() throws Exception {
		
		Settings result = service.readSettings(new File("toto.xml"));
		assertNull(result);
		
		result = service.readSettings(super.getFile(XML_SETTINGS));
		assertNotNull(result);
		
		Bdd bdd = result.getBdd();
		assertNotNull(bdd);
		assertNotNull(bdd.getConnexions());
		assertNotNull(bdd.getConnexions().getOraclecon());
		assertEquals(1, bdd.getConnexions().getOraclecon().size());
		
		OracleConnexion oracleConnexion = bdd.getConnexions().getOraclecon().get(0);
		assertNotNull(oracleConnexion);
		assertNotNull(oracleConnexion.getHost());
		assertNotNull(oracleConnexion.getPort());
		assertNotNull(oracleConnexion.getInstance());
		assertNotNull(oracleConnexion.getUser());
		assertNotNull(oracleConnexion.getPassword());
	}

}
