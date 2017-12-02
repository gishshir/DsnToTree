package fr.tsadeo.app.dsntotree.util;

import java.io.File;
import java.util.logging.Logger;

import fr.tsadeo.app.dsntotree.bdd.dao.BddAccessManagerFactory;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;

public class ApplicationManager implements IConstants {

    private static final Logger LOG = Logger.getLogger(ApplicationManager.class.getName());

    private static ApplicationManager instance;

    public static ApplicationManager get() {
        if (instance == null) {
            instance = new ApplicationManager();
        }
        return instance;
    }

    private ApplicationManager() {
    }

    public boolean readSettings() {
        boolean result = false;
        try {
            SettingsUtils.get().readApplicationSettings(new File(SETTINGS_XML));
            result = true;
        } catch (Exception e) {
            LOG.severe("Echec lors du chargement des préférences: " + e.getMessage());
        }

        return result;
    }

    public boolean writeSettings(boolean reload) {
        boolean result = false;
        try {
            SettingsUtils.get().writeApplicationSettings(new File(SETTINGS_XML));

            if (reload) {
                this.readSettings();
            }
            result = true;
        } catch (Exception e) {
            LOG.severe("Echec lors du chargement des préférences: " + e.getMessage());
        }

        return result;

    }

    /**
     * FIXME revoir le model pour le stockage des connexions
     * 
     * @param connexionDto
     * @return
     */
    public boolean updateSettings(final BddConnexionDto connexionDto) {

        boolean result = this.readSettings();
        if (result) {
            result = BddAccessManagerFactory.get().createOrUpdateBddConnexion(connexionDto);
            if (result) {
                result = this.writeSettings(false);
            }
        }
        return result;

    }
}
