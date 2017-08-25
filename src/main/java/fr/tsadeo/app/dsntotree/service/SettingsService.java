package fr.tsadeo.app.dsntotree.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class SettingsService {

//    public Settings readSettings(File settingsFile) throws Exception {
//    	
//    	if (settingsFile == null || !settingsFile.isFile() || !settingsFile.canRead()) {
//    		return null;
//    	}
//
//    	InputStream is = null;
//    	try {
//		
//    		is = new FileInputStream(settingsFile);
//            JAXBContext jc = JAXBContext.newInstance("com.acme.foo");
//            Unmarshaller u = jc.createUnmarshaller();
//            Object o = u.unmarshal(is);
//            return o == null ? null : (Settings) o;
//            
//		} catch (Exception e) {
//			// TODO: handle exception
//		} finally {
//			if (is != null) {
//				is.close();
//			}
//		}
//    	return null;
//        
//    }

}
