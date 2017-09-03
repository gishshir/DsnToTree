package fr.tsadeo.app.dsntotree.bdd.dao;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager.Type;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.service.AbstractTest;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class BddAccessManagerTest extends AbstractTest {

    @Before
    public void init() throws Exception {

        SettingsUtils.get().readApplicationSettings(getFile(XML_SETTINGS));
    }



    @Test
    public void testGetDefaultConnexion() {

        BddConnexionDto result = BddAccessManagerFactory.get(Type.Oracle).getDefaultBddConnexionDto();
        assertNotNull(result);
    }

}
