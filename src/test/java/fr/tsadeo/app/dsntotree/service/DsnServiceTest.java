package fr.tsadeo.app.dsntotree.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ItemBloc;

public class DsnServiceTest extends AbstractTest{
	
	private static ReadDsn readDsnService = ServiceFactory.getReadDsnService();
	private static final DsnService service = ServiceFactory.getDsnService();
	
	@Test
	public void testUpdateDsnListBloc() throws Exception {
		
		Dsn dsn = readDsnService.buildTreeFromFile(this.getFile(DSN_MENSUELLE_PHASE3));
		assertNotNull(dsn);
		
		assertNotNull(dsn.getBlocs());
		int nbBlocs = dsn.getBlocs().size();
		
		// list de tous les blocs
		System.out.println("AVANT MODIFICATION");
		ItemBloc firstBloc30 = null;
		for (ItemBloc itemBloc : dsn.getBlocs()) {
			System.out.println(itemBloc.getBlocLabel());
			if (firstBloc30 == null && itemBloc.getBlocLabel().equals("30")) {
				firstBloc30 = itemBloc;
			}
		}
		
		// on modifie le bloc 30 en supprimant le bloc 40 (et enfants 70, 71)
		assertNotNull(firstBloc30);
		assertTrue(firstBloc30.hasChildren());
		
		Iterator<ItemBloc> iter = firstBloc30.getChildrens().iterator();
		while (iter.hasNext()) {
			if (iter.next().getBlocLabel().equals("40")) {
				iter.remove();
				firstBloc30.setChildrenModified(true);
			}
		}
		
		// on met à jour la liste des blocs de la dsn
		service.updateDsnListBloc(dsn, firstBloc30);
		
		System.out.println("");
		System.out.println("APRES MODIFICATION");
		for (ItemBloc itemBloc : dsn.getBlocs()) {
			System.out.println(itemBloc.getBlocLabel());
		}
		assertEquals(nbBlocs - 3, dsn.getBlocs().size());
	}
	

}