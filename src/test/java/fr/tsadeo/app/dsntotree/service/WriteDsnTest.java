package fr.tsadeo.app.dsntotree.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;
import fr.tsadeo.app.dsntotree.util.IConstants;

public class WriteDsnTest extends AbstractTest implements IConstants {

	private static ReadDsn readService = new ReadDsn();
	private static WriteDsn writeService = new WriteDsn();

	@Test
	public void testWriteDsn() throws Exception {

		Dsn dsn = readService.buildTreeFromFile(this.getFile(DSN_MENSUELLE_PHASE3));
		assertNotNull(dsn);
		
		// modification
		for (ItemRubrique itemRubrique: dsn.getRubriques()) {
			
			itemRubrique.setValue(itemRubrique.getValue() + US + "modified!");
			itemRubrique.setModified(true);
		}
		
		writeService.write(dsn);
		
	}

}
