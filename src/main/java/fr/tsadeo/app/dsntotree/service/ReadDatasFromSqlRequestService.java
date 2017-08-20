package fr.tsadeo.app.dsntotree.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;
import fr.tsadeo.app.dsntotree.util.IConstants;

/**
 * Recupération dans SQLDevelopper du resultat de la requete SQL d'extraction
 * des datas d'un chrono message donne CHRONOMESS ID BLOC SEQ_BLOC BLOC_SUP
 * SEQ_SUP CODERUBRIQUE VALUE
 * 
 * @author sylvie
 *
 */
public class ReadDatasFromSqlRequestService implements IConstants {

	private static final Pattern PATTERN_DATAS_NO_BLOC_SUP = Pattern.compile(
			"^[\\s]+[\\d]+[\\s]+[\\d]+[\\s]+([\\d]{2})[\\s]+([\\d]{1,2})[\\s]+([\\d]{2}.[\\d]{3})[\\s]+([\\w\\d\\s.@]+)$");
	private static final Pattern PATTERN_DATAS_WITH_BLOC_SUP = Pattern.compile(
			"^[\\s]+[\\d]+[\\s]+[\\d]+[\\s]+([\\d]{2})[\\s]+([\\d]{1,2})[\\s]+([\\d]{2})[\\s]+([\\d]{1,2})[\\s]+([\\d]{2}.[\\d]{3})[\\s]+([\\w\\d\\s.@]+)$");

	public List<DataDsn> buildListDatasFromSqlRequest(File sqlFile) throws Exception {

		List<DataDsn> listDatas = null;

		if (sqlFile.exists() && sqlFile.isFile() && sqlFile.canRead()) {

			InputStream in = null;
			try {
				in = new FileInputStream(sqlFile);
				listDatas = this.readSqlListLines(IOUtils.readLines(in, UTF8));

			} catch (Exception ex) {
				throw new Exception("Impossible de parcourir le fichier: ".concat(sqlFile.getAbsolutePath()));
			} finally {
				IOUtils.closeQuietly(in);
			}

			
		}

		return listDatas;
	}

	private List<DataDsn> readSqlListLines(List<String> readLines) {
		List<DataDsn> listDatas = new ArrayList<DataDsn>();

		for (String line : readLines) {
			DataDsn dataDsn = this.extractDataDsnFromLine(line);
			if (dataDsn != null) {
				listDatas.add(dataDsn);
			}
		}

		return listDatas;
	}
	
	private DataDsn extractDataDsnFromLine(String line) {
		
		Matcher // bloc, seqBloc, bloc sup, seq sup, codeRubrique, value
		m = PATTERN_DATAS_WITH_BLOC_SUP.matcher(line);
		if (m.matches()) {
            int count = m.groupCount();
            if (count == 6) {
            	
            	DataDsn dataDsn = new DataDsn();
            	dataDsn.setBloc(m.group(1));
            	dataDsn.setNumSequenceBloc(this.getIntValue(m.group(2), 0));
            	
            	dataDsn.setNumSequenceBlocSup(this.getIntValue(m.group(4), 0));
            	
            	dataDsn.setCodeRubrique(m.group(5));
            	
            	String value = m.group(6);
            	dataDsn.setValue(value.substring(0, 255).trim());
            	return dataDsn;
            }
	    }
		// bloc, seqBloc, codeRubrique, value
		m = PATTERN_DATAS_NO_BLOC_SUP.matcher(line);
		
		if (m.matches()) {
            int count = m.groupCount();
            if (count == 4) {
            	
            	DataDsn dataDsn = new DataDsn();
            	dataDsn.setBloc(m.group(1));
            	dataDsn.setNumSequenceBloc(this.getIntValue(m.group(2), 0));
            	dataDsn.setNumSequenceBlocSup(0);
            	dataDsn.setCodeRubrique(m.group(3));
            	
            	String value = m.group(4);
            	dataDsn.setValue(value.substring(0, 255).trim());
            	return dataDsn;
            }
	    }
		
		return null;
	}
	
	private int getIntValue(String value, int defaultValue) {
		
		int intValue = Integer.MIN_VALUE;
		try{
		 intValue = Integer.parseInt(value);
		}
		catch (NumberFormatException ex) {
			intValue = defaultValue;
		}
		return intValue;
		
	}
	
}
