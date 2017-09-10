package fr.tsadeo.app.dsntotree.bdd.dao.impl;

import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.BddAccessManagerFactory;
import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager.Type;

public class OracleBddAccessManagerTest {
	
	private static final Logger LOG = Logger.getLogger(OracleBddAccessManagerTest.class.getName());
	
	@Test
	public void testGetUrl() {
		
		OracleBddAccessManager oracleBddAccessManager = (OracleBddAccessManager)
				BddAccessManagerFactory.get(Type.Oracle);
		
		String result = oracleBddAccessManager.getUrl("dedtanya04.ext.tdc", 1521, "INFIDEV");
		assertNotNull(result);
		LOG.config(result);
	}

}
