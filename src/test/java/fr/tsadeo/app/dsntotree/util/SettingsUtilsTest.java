package fr.tsadeo.app.dsntotree.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


import org.junit.Test;

import fr.tsadeo.app.dsntotree.model.xml.OracleBddAccess;
import fr.tsadeo.app.dsntotree.model.xml.Settings;
import fr.tsadeo.app.dsntotree.service.AbstractTest;

public class SettingsUtilsTest extends AbstractTest {

	 
	 @Test
	 public void testReadApplicationSettings() throws Exception {
		 
		 SettingsUtils.get().readApplicationSettings(getFile(XML_SETTINGS));
		 assertTrue(SettingsUtils.get().hasApplicationSettings());
	 }
	 @Test
	 public void testReadSettings() throws Exception {

	        Settings settings = SettingsUtils.get().readSettings(getFile(XML_SETTINGS));
	        assertNotNull(settings);
	        SettingsUtils.get().setApplicationSettings(settings);
	        
	        
	        OracleBddAccess result = SettingsUtils.get().getDefaultOracleBddAccess();
	        assertNotNull(result);
	        
	        assertNotNull(result.getHost());
	        assertNotNull(result.getPort());
	        assertNotNull(result.getInstance());
	        assertNotNull(result.getUser());
	        assertNotNull(result.getPassword());
	        
	    }

}
