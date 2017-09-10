package fr.tsadeo.app.dsntotree.bdd.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.IMessageDsnDao;
import fr.tsadeo.app.dsntotree.bdd.model.MessageDsn;

public class JdbcMessageDsnDaoTest implements IJdbcDaoTest {
	
	private static final Logger LOG = Logger.getLogger(JdbcMessageDsnDaoTest.class.getName());

    private static final String MESS_MSG_FOUND = "Le message DSN de numéro chrono: %1$s a été trouvé en base de données!";
    private String SQL_GET_MESS_BY_CHRONO = "select DSMSCHRMSG, DSMSDATDCL, DSMSNOMMSG from DSMSGDSNCL where DSMSCHRMSG = %1$d";
    private static final IMessageDsnDao dao = new JdbcMessageDsnDao();

    @Test
    public void getMessageDsn() throws Exception {

        MessageDsn result = dao.getMessageDsn(CHRONO_INEXISTANT);
        assertNull(result);

        result = dao.getMessageDsn(CHRONO_OK);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getName());
        assertNotNull(result.getDateReferenceDeclaration());
        assertNotNull(result.getNumeroChronoMessage());

        assertEquals(CHRONO_OK, result.getNumeroChronoMessage());
        LOG.config(result.toString());
    }

    @Test
    public void testMessageFormat() {

        String sql = String.format(SQL_GET_MESS_BY_CHRONO, 9999999L);
        LOG.config(sql);
        assertTrue(sql.endsWith("9999999"));

        String result = String.format(MESS_MSG_FOUND, "7777");
        assertNotNull(result);
        LOG.config(result);

    }
}
