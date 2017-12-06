package fr.tsadeo.app.dsntotree.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.model.xml.Credentials;
import fr.tsadeo.app.dsntotree.model.xml.OracleBddAccess;
import fr.tsadeo.app.dsntotree.model.xml.Settings;
import fr.tsadeo.app.dsntotree.service.AbstractTest;

public class SettingsUtilsTest extends AbstractTest {

    @Test
    public void testReadApplicationSettings() throws Exception {

        SettingsUtils.get().readApplicationSettings(getFile(XML_SETTINGS));
        assertTrue(SettingsUtils.get().hasApplicationSettings());
    }

    @Test
    public void testReadSettings() throws Exception {

        Settings settings = SettingsUtils.get().readSettings(getFile(XML_SETTINGS));
        assertNotNull(settings);
        SettingsUtils.get().setApplicationSettings(settings);

        List<OracleBddAccess> result = SettingsUtils.get().getListOracleBddAccess();
        assertNotNull(result);
        assertTrue(!result.isEmpty());

        result.stream().forEachOrdered(oracleBddAccess -> {
        	
        	assertNotNull(oracleBddAccess.getHost());
            assertNotNull(oracleBddAccess.getPort());
            assertNotNull(oracleBddAccess.getInstance());

            Credentials credentials = oracleBddAccess.getCredentials();
            assertNotNull(credentials);
            credentials.getCredential().stream().forEachOrdered(credential -> {
            	assertNotNull(credential.getUser());
                assertNotNull(credential.getPassword());
            });

            
        });

        File file = SettingsUtils.get().getNormeDsnFile();
        assertNotNull(file);
        assertTrue(file.exists());
        assertTrue(file.isFile());
        assertTrue(file.canRead());

    }

    @Test
    public void testWriteApplicationSettings() throws Exception {

        Settings settings = SettingsUtils.get().readSettings(getFile(XML_SETTINGS));
        assertNotNull(settings);
        SettingsUtils.get().setApplicationSettings(settings);

        boolean result = SettingsUtils.get().writeApplicationSettings(new File(XML_WRITE_SETTINGS));
        assertTrue(result);
    }

}
