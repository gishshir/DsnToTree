package fr.tsadeo.app.dsntotree.bdd.dao.impl;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.IDataDsnDao;
import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;
import fr.tsadeo.app.dsntotree.service.AbstractTest;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class JdbcDataDsnDaoTest extends AbstractTest implements IJdbcDaoTest {
	
	private static final Logger LOG = Logger.getLogger(JdbcDataDsnDaoTest.class.getName());

    private final static IDataDsnDao dao = new JdbcDataDsnDao();

    @Before
    public void init() throws Exception {

        SettingsUtils.get().readApplicationSettings(getFile(XML_SETTINGS));
    }

    @Test
    public void testGetListDataDsnForMessage() throws Exception {

        List<DataDsn> result = dao.getListDataDsnForMessage(CHRONO_INEXISTANT);
        assertTrue(result.isEmpty());

        result = dao.getListDataDsnForMessage(CHRONO_OK);
        assertTrue(!result.isEmpty());

        result.stream().forEach(dataDSN -> LOG.config(dataDSN.toString()));
    }

}
