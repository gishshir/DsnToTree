package fr.tsadeo.app.dsntotree.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;

public class ReadDatasFromSqlRequestServiceTest extends AbstractTest{

	private static ReadDatasFromSqlRequestService service = ServiceFactory.getReadDatasFromSqlRequestService();
	
	@Test
	public void testBuildListDatasFromSqlRequest() throws Exception {
		
		File sqlFile = super.getFile(SQL_DSN_CHRONO_EXTRACTION);
		List<DataDsn> result = service.buildListDatasFromSqlRequest(sqlFile);
		assertNotNull(result);
		assertFalse(result.isEmpty())
;		
		for (DataDsn dataDsn : result) {
			LOG.config(dataDsn.toString());
		}
	}
}
