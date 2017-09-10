package fr.tsadeo.app.dsntotree.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.impl.IJdbcDaoTest;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ItemRubrique;

public class ReadDsnFromFileTest extends AbstractTest implements IJdbcDaoTest {

    
    private static ReadDsnFromFileService readDsnFromFileService = ServiceFactory.getReadDsnFromFileService();
    private static DsnService dsnService = ServiceFactory.getDsnService();

    @Test
    public void testLireDsnMensuellePhase3File() throws Exception {

        Dsn dsn = readDsnFromFileService.buildTreeFromFile(this.getFile(DSN_MENSUELLE_PHASE3));
        assertNotNull(dsn);
        dsnService.updateDsnWithTree(dsn);
        for (ItemRubrique itemRubrique : dsn.getRubriques()) {
            LOG.config(itemRubrique.toString());
        }
    }


    @Test
    public void testLireDsnMensuellePhase3ErreurFile() throws Exception {

        readDsnFromFileService.buildTreeFromFile(this.getFile(DSN_MENSUELLE_PHASE3_ERREUR));
    }

    @Test
    public void testLireDsnMensuellePhase2File() throws Exception {

        readDsnFromFileService.buildTreeFromFile(this.getFile(DSN_MENSUELLE_PHASE2));
    }

    @Test
    public void testLireDsnArretTravailPhase3File() throws Exception {

        readDsnFromFileService.buildTreeFromFile(this.getFile(DSN_SIGNAL_ARRET_TRAVAIL_PHASE3));
    }

    @Test
    public void testLireDsnRepriseSuiteArretTravailPhase3File() throws Exception {

        readDsnFromFileService.buildTreeFromFile(this.getFile(DSN_SIGNAL_REPRISE_SUITE_ARRET_TRAVAIL_PHASE3));
    }

}
