package fr.tsadeo.app.dsntotree.bdd.dao.impl;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.IDataDsnDao;
import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;

public class JdbcDataDsnDaoTest implements IJdbcDaoTest {

    private final static IDataDsnDao dao = new JdbcDataDsnDao();

    @Test
    public void testGetListDataDsnForMessage() throws Exception {

        List<DataDsn> result = dao.getListDataDsnForMessage(CHRONO_INEXISTANT);
        assertTrue(result.isEmpty());

        result = dao.getListDataDsnForMessage(CHRONO_OK);
        assertTrue(!result.isEmpty());

        for (DataDsn dataDSN : result) {
            System.out.println(dataDSN.toString());
        }
    }

}
