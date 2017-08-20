package fr.tsadeo.app.dsntotree.service;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;

import org.junit.Assert;

import fr.tsadeo.app.dsntotree.util.IConstants;

public abstract class AbstractTest {
	
	protected static final String DSN_MENSUELLE_PHASE3 = "SFDSN_KIEFFER_P03V01_022016_DSNPC880.txt";
	protected static final String DSN_MENSUELLE_PHASE3_ERREUR = "SFDSN_KIEFFER_P03V01_AvecErreurs.txt";
	protected static final String DSN_MENSUELLE_PHASE2 = "DSN_KIEFFER_P02V01_092015_DSNPC_NOUVADH.txt";
	protected static final String DSN_SIGNAL_ARRET_TRAVAIL_PHASE3 = "DSN_P03V01_ArretTravail.txt";
	protected static final String DSN_SIGNAL_REPRISE_SUITE_ARRET_TRAVAIL_PHASE3 = "DSN_P03V01_RepriseTravail.txt";
	
	protected static final String SQL_DSN_CHRONO_EXTRACTION = "chrono118660.txt";
	
	
	protected File getFile(String resource) throws Exception {
	    	
	    	URL url = this.getClass().getClassLoader().getResource(resource);
	        Assert.assertNotNull(url);
	        return new File( URLDecoder.decode(url.getPath(), IConstants.UTF8));
	    }

}
