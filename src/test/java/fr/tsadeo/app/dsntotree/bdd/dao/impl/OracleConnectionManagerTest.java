package fr.tsadeo.app.dsntotree.bdd.dao.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.ConnexionManagerFactory;
import fr.tsadeo.app.dsntotree.bdd.dao.IConnectionManager.Type;

public class OracleConnectionManagerTest {
	
	@Test
	public void testGetUrl() {
		
		OracleConnexionManager oracleConnectionManager = (OracleConnexionManager)
				ConnexionManagerFactory.get(Type.Oracle);
		
		String result = oracleConnectionManager.getUrl("dedtanya04.ext.tdc", 1521, "INFIDEV");
		assertNotNull(result);
		System.out.println(result);
	}

}
