package fr.tsadeo.app.dsntotree.util;

import java.util.List;

import fr.tsadeo.app.dsntotree.model.xml.OracleConnexion;
import fr.tsadeo.app.dsntotree.model.xml.Settings;

public class SettingsUtils {

    private static SettingsUtils instance;

    public static SettingsUtils get() {
        if (instance == null) {
            instance = new SettingsUtils();
        }
        return instance;
    }

    private Settings applicationSettings;

    private SettingsUtils() {
    }

    public void setApplicationSettings(Settings appSettings) {
        this.applicationSettings = appSettings;
    }

    public OracleConnexion getDefaultOracleConnexion() {

        List<OracleConnexion> list = this.getListOracleConnexion();
        if (list != null) {
            for (OracleConnexion oracleConnexion : list) {
                if (oracleConnexion.getDefaut()) {
                    return oracleConnexion;
                }
            }
        }
        return null;
    }

    public List<OracleConnexion> getListOracleConnexion() {
        if (this.applicationSettings == null) {
            return null;
        }

        return this.applicationSettings.getBdd().getConnexions().getOraclecon();

    }

}
