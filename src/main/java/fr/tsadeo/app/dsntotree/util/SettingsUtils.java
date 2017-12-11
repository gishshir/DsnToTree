package fr.tsadeo.app.dsntotree.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import fr.tsadeo.app.dsntotree.model.xml.Bdd;
import fr.tsadeo.app.dsntotree.model.xml.Dsn;
import fr.tsadeo.app.dsntotree.model.xml.Norme;
import fr.tsadeo.app.dsntotree.model.xml.ObjectFactory;
import fr.tsadeo.app.dsntotree.model.xml.OracleBddAccess;
import fr.tsadeo.app.dsntotree.model.xml.Settings;

public class SettingsUtils implements IConstants {

	private static final Logger log = Logger.getLogger(SettingsUtils.class.getName());

	private static SettingsUtils instance;

	public static SettingsUtils get() {
		if (instance == null) {
			instance = new SettingsUtils();
		}
		return instance;
	}

	public interface ISettingsListener {
		public void settingsLoaded();

        public void settingsUpdated();
	}

	private List<ISettingsListener> listListeners;

	public void addListener(ISettingsListener listener) {
		if (this.listListeners == null) {
			this.listListeners = new ArrayList<>();
		}
		this.listListeners.add(listener);
	}

	private Settings applicationSettings;

	private SettingsUtils() {
	}

	public boolean hasApplicationSettings() {
		return this.applicationSettings != null;
	}

    public boolean writeApplicationSettings(File settingsFile) throws Exception {

        if (this.applicationSettings != null) {
            return this.writeSettings(settingsFile, this.applicationSettings);
        }
        return false;
    }
	public void readApplicationSettings(File settingsFile) throws Exception {

        boolean reload = this.applicationSettings != null;
		this.applicationSettings = this.readSettings(settingsFile);

		if (this.listListeners != null && applicationSettings != null) {
			
			listListeners.stream()
			   .forEach(settingsListener -> {
				   if (reload) {
	                    settingsListener.settingsUpdated();
	                } else {
	                    settingsListener.settingsLoaded();
	                }  
			   });
		}
	}

    protected Settings readSettings(File settingsFile) throws Exception {

		if (settingsFile == null || !settingsFile.isFile() || !settingsFile.canRead()) {
			return null;
		}

		Settings settings = null;
		InputStream is = null;
		try {

			is = new FileInputStream(settingsFile);
			JAXBContext jc = this.createJAXBContext();
			Unmarshaller u = jc.createUnmarshaller();
			Object o = u.unmarshal(is);
			@SuppressWarnings("unchecked")
			JAXBElement<Settings> jbElement = o == null ? null : (JAXBElement<Settings>) o;
			settings = o == null ? null : jbElement.getValue();

		} catch (Exception e) {
			log.severe("Erreur lors de la lecture du fichier " + settingsFile.getName());
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}

		return settings;

	}

    protected boolean writeSettings(File settingsFile, Settings settings) throws Exception {

		 if (settingsFile == null ) {
		 return false;
		 }

		boolean result = false;
		OutputStream out = null;
		try {
			out = new FileOutputStream(settingsFile);
			JAXBContext jc = this.createJAXBContext();
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));

			// create JAXBElement of type Student
			JAXBElement<Settings> jaxbElement = new ObjectFactory().createSettings(settings);

			if (jaxbElement != null) {
				m.marshal(jaxbElement, out);
				result = true;
			}

		} catch (Exception ex) {
			result = false;
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return result;
	}

    protected void setApplicationSettings(Settings appSettings) {
		this.applicationSettings = appSettings;
	}

	public String getDsnEncoding() {
        Dsn dsn = this.hasApplicationSettings() ? this.applicationSettings.getDsn() : null;
        if (dsn != null) {
            return dsn.getEncoding();
		}
        return ISO_8859_1;
	}

	public File getTnsNameOraFile() {
		Bdd bdd = this.getBddSettings();
		if (bdd != null && bdd.getServices() != null && bdd.getServices().isActif()
				&& bdd.getServices().getOracleServices() != null) {

			return new File(bdd.getServices().getOracleServices());
		}
		return null;
	}

	public File getNormeDsnFile() {
		Norme norme = this.getNormeSettings();
		if (norme != null) {
			return norme.isActif() ? new File(norme.getDsnnormefile()) : null;
		}
		return null;
	}

	public List<OracleBddAccess> getListOracleBddAccess() {
		Bdd bdd = this.getBddSettings();
		if (bdd == null || bdd.getBddAccesses() == null) {
			return null;
		}

		return bdd.getBddAccesses().getOracleBdd();

	}

	private Bdd getBddSettings() {
		return (this.hasApplicationSettings()) ? this.applicationSettings.getBdd() : null;
	}

	private Norme getNormeSettings() {
		return this.hasApplicationSettings() ? this.applicationSettings.getNorme() : null;
	}

	private JAXBContext createJAXBContext() throws JAXBException {
		return JAXBContext.newInstance("fr.tsadeo.app.dsntotree.model.xml");
	}

}
