package fr.tsadeo.app.dsntotree.service;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.IDataDsnDao;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.IJdbcDaoTest;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.JdbcDataDsnDao;
import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;
import fr.tsadeo.app.dsntotree.model.Dsn;

public class ReadDsnFromDatasTest extends AbstractTest implements IJdbcDaoTest {
	
	private final static IDataDsnDao dao = new JdbcDataDsnDao();
	 private static ReadDsnFromDatasService readDsnFromDatasService = ServiceFactory.getReadDsnFromDatasService();
	
  @Test
  public void testBuildTreeFromDatas() throws Exception {

      // prerequi
      List<DataDsn> listDatas = dao.getListDataDsnForMessage(CHRONO_OK);
      assertTrue(!listDatas.isEmpty());

      Dsn dsn = readDsnFromDatasService.buildTreeFromDatas("dsn.txt", listDatas);
      assertNotNull(dsn);
      assertNotNull(dsn.getPhase());
      assertNotNull(dsn.getNature());
      assertNotNull(dsn.getRoot());
      assertNotNull(dsn.getTreeRoot());

      assertFalse(dsn.getBlocs().isEmpty());
      System.out.println(dsn.toString());

  }


}
