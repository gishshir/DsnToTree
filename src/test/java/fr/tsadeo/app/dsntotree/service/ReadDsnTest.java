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
import fr.tsadeo.app.dsntotree.model.ItemRubrique;

public class ReadDsnTest extends AbstractTest implements IJdbcDaoTest {

    private final static IDataDsnDao dao = new JdbcDataDsnDao();
    private static ReadDsnFromFileService readDsnService = ServiceFactory.getReadDsnFromFileService();
    private static DsnService dsnService = ServiceFactory.getDsnService();

    @Test
    public void testLireDsnMensuellePhase3File() throws Exception {

        Dsn dsn = readDsnService.buildTreeFromFile(this.getFile(DSN_MENSUELLE_PHASE3));
        assertNotNull(dsn);
        dsnService.updateDsnWithTree(dsn);
        for (ItemRubrique itemRubrique : dsn.getRubriques()) {
            System.out.println(itemRubrique.toString());
        }
    }

    @Test
    public void testBuildTreeFromDatas() throws Exception {

        // prerequi
        List<DataDsn> listDatas = dao.getListDataDsnForMessage(CHRONO_OK);
        assertTrue(!listDatas.isEmpty());

        Dsn dsn = readDsnService.buildTreeFromDatas("dsn.txt", listDatas);
        assertNotNull(dsn);
        assertNotNull(dsn.getPhase());
        assertNotNull(dsn.getNature());
        assertNotNull(dsn.getRoot());
        assertNotNull(dsn.getTreeRoot());

        assertFalse(dsn.getBlocs().isEmpty());
        System.out.println(dsn.toString());

    }

    @Test
    public void testLireDsnMensuellePhase3ErreurFile() throws Exception {

        readDsnService.buildTreeFromFile(this.getFile(DSN_MENSUELLE_PHASE3_ERREUR));
    }

    @Test
    public void testLireDsnMensuellePhase2File() throws Exception {

        readDsnService.buildTreeFromFile(this.getFile(DSN_MENSUELLE_PHASE2));
    }

    @Test
    public void testLireDsnArretTravailPhase3File() throws Exception {

        readDsnService.buildTreeFromFile(this.getFile(DSN_SIGNAL_ARRET_TRAVAIL_PHASE3));
    }

    @Test
    public void testLireDsnRepriseSuiteArretTravailPhase3File() throws Exception {

        readDsnService.buildTreeFromFile(this.getFile(DSN_SIGNAL_REPRISE_SUITE_ARRET_TRAVAIL_PHASE3));
    }

}
