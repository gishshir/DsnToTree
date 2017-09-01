package fr.tsadeo.app.dsntotree.bdd.dao;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.IConnectionManager.Type;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.model.xml.Settings;
import fr.tsadeo.app.dsntotree.service.AbstractTest;
import fr.tsadeo.app.dsntotree.service.ServiceFactory;
import fr.tsadeo.app.dsntotree.service.SettingsService;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class ConnexionManagerTest extends AbstractTest {

    private static SettingsService settingsService = ServiceFactory.getSettingsService();

    @Before
    public void init() throws Exception {

        Settings settings = settingsService.readSettings(getFile(XML_SETTINGS));
        SettingsUtils.get().setApplicationSettings(settings);
    }

    @Test
    public void testGetDefaultConnexion() {

        BddConnexionDto result = ConnexionManagerFactory.get(Type.Oracle).getDefaultConnexion();
        assertNotNull(result);
    }

}
