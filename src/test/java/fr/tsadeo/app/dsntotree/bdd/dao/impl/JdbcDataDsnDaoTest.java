package fr.tsadeo.app.dsntotree.bdd.dao.impl;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.IDataDsnDao;
import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;

public class JdbcDataDsnDaoTest implements IJdbcDaoTest {
	
	private static final Logger LOG = Logger.getLogger(JdbcDataDsnDaoTest.class.getName());

    private final static IDataDsnDao dao = new JdbcDataDsnDao();

    @Test
    public void testGetListDataDsnForMessage() throws Exception {

        List<DataDsn> result = dao.getListDataDsnForMessage(CHRONO_INEXISTANT);
        assertTrue(result.isEmpty());

        result = dao.getListDataDsnForMessage(CHRONO_OK);
        assertTrue(!result.isEmpty());

        result.stream().forEach(dataDSN -> LOG.config(dataDSN.toString()));
    }

}
