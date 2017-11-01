package fr.tsadeo.app.dsntotree.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.NatureDsn;
import fr.tsadeo.app.dsntotree.model.PhaseDsn;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class BlocTreeServiceTest extends AbstractTest {

    private static BlocTreeService service = new BlocTreeService();

    @Before
    public void init() throws Exception {

        SettingsUtils.get().readApplicationSettings(getFile(XML_SETTINGS));
        assertTrue(SettingsUtils.get().hasApplicationSettings());
    }

    @Test
    public void testBuildRootTree() {

        for (PhaseDsn phase : PhaseDsn.values()) {

            for (NatureDsn nature : NatureDsn.values()) {

                BlocTree root = service.buildRootTree(phase, nature);
                assertNotNull(root);
                assertTrue(root.hasChildrens());

                System.out.println("");
                this.displayBlocTree(root);
                System.out.println("");
            }
        }

    }

    private void displayBlocTree(BlocTree blocTree) {

        if (blocTree != null) {
            System.out.println(blocTree.toString());

            if (blocTree.hasChildrens()) {
                for (BlocTree child : blocTree.getChildrens()) {
                    this.displayBlocTree(child);
                }
            }

        }
    }

}
