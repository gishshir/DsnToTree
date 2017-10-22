package fr.tsadeo.app.dsntotree.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import fr.tsadeo.app.dsntotree.dico.IDictionnary;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class DictionnaryServiceTest extends AbstractTest {

	 private static DictionnaryService service = new DictionnaryService();
	 
	 @Before
	 public void init() throws Exception {
		 
		 SettingsUtils.get().readApplicationSettings(getFile(XML_SETTINGS));
		 assertTrue(SettingsUtils.get().hasApplicationSettings());
	 }
	 
	 @Test
	 public void testGetDsnDictionnary() {
		 
		 IDictionnary dictionnary = service.getDsnDictionnary();
		 assertNotNull(dictionnary);
		 
		 String result = dictionnary.getLibelle("15");
		 assertNotNull(result);
		 LOG.config(result);
		 
		 result = dictionnary.getLibelle("15", "003");
		 assertNotNull(result);
		 LOG.config(result);
		 
	 }


}
