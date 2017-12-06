package fr.tsadeo.app.dsntotree.bdd.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager.Type;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.OracleBddAccessManager;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;
import fr.tsadeo.app.dsntotree.service.AbstractTest;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class BddAccessManagerTest extends AbstractTest {

    private static final String INSTANCE_OK = "INSTANCE";
    private static final String INSTANCE_NOK = "NOT_EXITS";

    private static final String USER1 = "TOTO_USER";
    private static final String USER2 = "TITI_USER";

    private static final String PWD1 = "TOTO_PWD";
    private static final String PWD2 = "TITI_PWD";

    @Before
    public void init() throws Exception {

        SettingsUtils.get().readApplicationSettings(getFile(XML_SETTINGS));
    }



    @Test
    public void testGetDefaultConnexion() {

        BddConnexionDto result = BddAccessManagerFactory.get(Type.Oracle).getDefaultBddConnexionDto();
        this.assertBddConnexionDto(result, 1);
    }
    
    @Test
    public void testGetBddConnexionDto() {
        List<BddConnexionDto> result = BddAccessManagerFactory.get(Type.Oracle).getListBddConnexionDto(INSTANCE_OK);
    	this.assertBddConnexionDto(result);
    	
        result = BddAccessManagerFactory.get(Type.Oracle).getListBddConnexionDto(INSTANCE_NOK);
    	assertNull(result);
    }
    
    @Test
    public void testCreateOrUpdateBddConnexion() {

        List<BddConnexionDto> result = BddAccessManagerFactory.get(Type.Oracle).getListBddConnexionDto(INSTANCE_OK);
        assertNotNull(result);
        assertTrue(!result.isEmpty());

        BddConnexionDto dtoUpdated = null;

        // update existing user
        for (BddConnexionDto bddConnexionDto : result) {
            if (bddConnexionDto.getUser().equals(USER1)) {
                dtoUpdated = this.clone(bddConnexionDto, null, "NEW_PWD");
                break;
            }
        }
        assertNotNull(dtoUpdated);
        boolean success = BddAccessManagerFactory.get(Type.Oracle).createOrUpdateBddConnexion(dtoUpdated);
        assertTrue(success);

        result = BddAccessManagerFactory.get(Type.Oracle).getListBddConnexionDto(INSTANCE_OK);
        assertNotNull(result);
        assertEquals(2, result.size());

        // add new user to instance
        dtoUpdated = this.clone(dtoUpdated, "NEW_USER", "NEW_PWD");
        success = BddAccessManagerFactory.get(Type.Oracle).createOrUpdateBddConnexion(dtoUpdated);
        assertTrue(success);

        result = BddAccessManagerFactory.get(Type.Oracle).getListBddConnexionDto(INSTANCE_OK);
        assertNotNull(result);
        assertEquals(3, result.size());

        // add new Instance
        dtoUpdated =
                new BddConnexionDto(dtoUpdated.getDriver(), getUrl("HOST", 1521, "NEW_INSTANCE"), "NEW2_USER",
                        "NEW2_PWD");
        success = BddAccessManagerFactory.get(Type.Oracle).createOrUpdateBddConnexion(dtoUpdated);
        assertTrue(success);

        result = BddAccessManagerFactory.get(Type.Oracle).getListBddConnexionDto("NEW_INSTANCE");
        assertNotNull(result);
        assertEquals(1, result.size());

    }

    // FIXME pas propre!!
    private String getUrl(String host, Integer port, String instance) {
        return new OracleBddAccessManager().getUrl(host, port, instance);
    }
    private BddConnexionDto clone(BddConnexionDto dto, String newUser, String newpwd) {

        return new BddConnexionDto(dto.getDriver(), dto.getUrl(), newUser == null ? dto.getUser() : newUser,
                newpwd == null ? dto.getPwd() : newpwd);
    }

    private void assertBddConnexionDto(List<BddConnexionDto> list) {

        assertNotNull(list);
        int index = 1;
        for (BddConnexionDto bddConnexionDto : list) {
            this.assertBddConnexionDto(bddConnexionDto, index++);
        }
    }

    private void assertBddConnexionDto(BddConnexionDto dto, int index) {
    	
    	assertNotNull(dto);
        
        assertNotNull(dto.getUrl());
        assertNotNull(dto.getPwd());
        assertNotNull(dto.getUser());

        assertEquals(index == 1 ? USER1 : USER2, dto.getUser());
        assertEquals(index == 1 ? PWD1 : PWD2, dto.getPwd());
    }

}
