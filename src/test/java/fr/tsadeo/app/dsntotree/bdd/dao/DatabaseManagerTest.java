package fr.tsadeo.app.dsntotree.bdd.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;

public class DatabaseManagerTest {

    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DB_URL = "jdbc:oracle:thin:@dedtanya04.ext.tdc:1521/INFIDEV";
    private static final String DB_USER = "IDEV00_USER";
    private static final String DB_PWD = "IDEV00_USER";
    
	@Test
	public void testTesterConnexion() {
		
		BddConnexionDto connexionDto = new BddConnexionDto("", "", "", "");
		boolean result = DatabaseManager.get().testerConnexion(connexionDto);
		assertFalse(result);
		
		connexionDto = new BddConnexionDto(JDBC_DRIVER, DB_URL, DB_USER, DB_PWD);
		result = DatabaseManager.get().testerConnexion(connexionDto);
		assertTrue(result);
	}
}
