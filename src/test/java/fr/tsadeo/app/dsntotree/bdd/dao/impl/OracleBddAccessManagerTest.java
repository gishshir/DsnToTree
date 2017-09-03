package fr.tsadeo.app.dsntotree.bdd.dao.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.BddAccessManagerFactory;
import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager.Type;

public class OracleBddAccessManagerTest {
	
	@Test
	public void testGetUrl() {
		
		OracleBddAccessManager oracleBddAccessManager = (OracleBddAccessManager)
				BddAccessManagerFactory.get(Type.Oracle);
		
		String result = oracleBddAccessManager.getUrl("dedtanya04.ext.tdc", 1521, "INFIDEV");
		assertNotNull(result);
		System.out.println(result);
	}

}
