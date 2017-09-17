package fr.tsadeo.app.dsntotree.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.business.SalarieDto;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ItemBloc;

public class DsnServiceTest extends AbstractTest {

    private static ReadDsnFromFileService readDsnService = ServiceFactory.getReadDsnFromFileService();
    private static final DsnService service = ServiceFactory.getDsnService();

    @Test
    public void testBuildListSalarieDtos() throws Exception {
        Dsn dsn = readDsnService.buildTreeFromFile(this.getFile(DSN_MENSUELLE_PHASE3));
        assertNotNull(dsn);

        List<SalarieDto> result = service.buildListSalarieDtos(dsn);
        assertNotNull(result);
        assertEquals(3, result.size());
        for (SalarieDto salarieDto : result) {
            LOG.config(salarieDto.toString());
        }
    }

    @Test
    public void testUpdateDsnListBloc() throws Exception {

        Dsn dsn = readDsnService.buildTreeFromFile(this.getFile(DSN_MENSUELLE_PHASE3));
        assertNotNull(dsn);

        assertNotNull(dsn.getBlocs());
        int nbBlocs = dsn.getBlocs().size();

        // list de tous les blocs
        LOG.config("AVANT MODIFICATION");
        ItemBloc firstBloc30 = null;
        for (ItemBloc itemBloc : dsn.getBlocs()) {
            LOG.config(itemBloc.getBlocLabel());
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

        LOG.config("");
        LOG.config("APRES MODIFICATION");
        for (ItemBloc itemBloc : dsn.getBlocs()) {
            LOG.config(itemBloc.getBlocLabel());
        }
        assertEquals(nbBlocs - 3, dsn.getBlocs().size());

    }

    @Test
    public void testFindItemBlocEquivalent() throws Exception {

        Dsn dsn = readDsnService.buildTreeFromFile(this.getFile(DSN_MENSUELLE_PHASE3));
        assertNotNull(dsn);

        ItemBloc firstBloc30 = null;
        for (ItemBloc itemBloc : dsn.getBlocs()) {
            LOG.config(itemBloc.getBlocLabel());
            if (firstBloc30 == null && itemBloc.getBlocLabel().equals("30")) {
                firstBloc30 = itemBloc;
            }
        }

        ItemBloc result = service.findItemBlocEquivalent(dsn, firstBloc30);
        assertNotNull(result);
        assertEquals(result, firstBloc30);

        ItemBloc copy = service.createNewChild(firstBloc30, true, true);
        assertNotNull(copy);
        assertTrue(copy != firstBloc30);
        result = service.findItemBlocEquivalent(dsn, copy);
        assertNotNull(result);
        assertEquals(result, firstBloc30);
    }

}
