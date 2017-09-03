package fr.tsadeo.app.dsntotree.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import fr.tsadeo.app.dsntotree.model.xml.OracleBddAccess;
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
    
    public boolean hasApplicationSettings() {
    	return this.applicationSettings != null;
    }
    
    public void readApplicationSettings(File settingsFile) throws Exception {
    	
    	this.applicationSettings = this.readSettings(settingsFile);
    }
    public Settings readSettings(File settingsFile) throws Exception {
    	
    	if (settingsFile == null || !settingsFile.isFile() || !settingsFile.canRead()) {
    		return null;
    	}

    	InputStream is = null;
    	try {
		
    		is = new FileInputStream(settingsFile);
            JAXBContext jc = JAXBContext.newInstance("fr.tsadeo.app.dsntotree.model.xml");
            Unmarshaller u = jc.createUnmarshaller();
            Object o = u.unmarshal(is);
            @SuppressWarnings("unchecked")
			JAXBElement<Settings> jbElement = o == null?null:(JAXBElement<Settings>)o;
            return o == null ? null :  jbElement.getValue();
            
		} catch (Exception e) {
			System.out.println("Erreur lors de la lecture du fichie " + settingsFile.getName());
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}
    	return null;
        
    }

    
    public void setApplicationSettings(Settings appSettings) {
        this.applicationSettings = appSettings;
    }

    public OracleBddAccess getDefaultOracleBddAccess() {

        List<OracleBddAccess> list = this.getListOracleBddAccess();
        if (list != null) {
            for (OracleBddAccess oracleBddAccess : list) {
                if (oracleBddAccess.getDefaut()) {
                    return oracleBddAccess;
                }
            }
        }
        return null;
    }

    public List<OracleBddAccess> getListOracleBddAccess() {
        if (this.applicationSettings == null || this.applicationSettings.getBdd() == null ||
        		this.applicationSettings.getBdd().getBddAccesses() == null) {
            return null;
        }

        return this.applicationSettings.getBdd().getBddAccesses().getOracleBdd();

    }

}
