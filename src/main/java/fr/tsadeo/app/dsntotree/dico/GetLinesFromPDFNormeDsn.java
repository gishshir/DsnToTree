package fr.tsadeo.app.dsntotree.dico;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import fr.tsadeo.app.dsntotree.util.IRegexConstants;


public class GetLinesFromPDFNormeDsn extends PDFTextStripper implements IRegexConstants {
	

	private static final String REGEX_ROOT_AND_BLOC_LABEL = "(S" + REGEX_TWO_DIGIT + REGEX_POINT + "G00" + REGEX_POINT
			+ ")(" + REGEX_TWO_DIGIT + ")";

	private static final String REGEX_DESCR_AND_BLOC = "(.*)" + REGEX_SPACE + REGEX_ROOT_AND_BLOC_LABEL;

	private static final Pattern PATTERN_BLOC = Pattern.compile(REGEX_DESCR_AND_BLOC + "$");
	private static final Pattern PATTERN_RUBRIQUE = Pattern
			.compile(REGEX_DESCR_AND_BLOC + REGEX_POINT + "(" + REGEX_TREE_DIGIT + ")$");


	private int compteur = 0;
	private final Map<Integer, String> mapLineNumberToBlocLabel = new LinkedHashMap<Integer, String>();
	private final Map<String, List<String>> keyToListLines = new HashMap<String, List<String>>();

	private final Map<String, KeyAndLibelle> mapBlocLabelToPdfBloc = new HashMap<String, KeyAndLibelle>();
	private final Map<String, Map<String, KeyAndLibelle>> mapBlocLabelToListRubriques = new HashMap<String, Map<String, KeyAndLibelle>>();

	public Map<String, Map<String, KeyAndLibelle>> getMapBlocLabelToListRubriques() {
		return mapBlocLabelToListRubriques;
	}

	private String currentKey = null;

	public GetLinesFromPDFNormeDsn() throws IOException {
		super();
	}
	


	public Map<String, KeyAndLibelle> getMapBlocLabelToPdfBloc() {
		return mapBlocLabelToPdfBloc;
	}




	/**
	 * Override the default functionality of PDFTextStripper.writeString()
	 */
	@Override
	protected void writeString(String str, List<TextPosition> textPositions) throws IOException {

		String strTrimmed = str.trim();
		if (strTrimmed.matches(REGEX_ROOT_AND_BLOC_LABEL) && !this.keyToListLines.containsKey(strTrimmed)) {
			this.currentKey = strTrimmed;
			mapLineNumberToBlocLabel.put(compteur, strTrimmed);
			this.keyToListLines.put(this.currentKey, new ArrayList<String>());
		} else if (this.currentKey != null) {
			
			if (strTrimmed.contains(this.currentKey)) {
				this.keyToListLines.get(this.currentKey).add(strTrimmed);

				if (!this.addBloc(strTrimmed)) {
					this.addRubrique(strTrimmed);
				}
			}
		}
		compteur++;
	}

	private boolean addBloc(String strTrimmed) {

		Matcher m = PATTERN_BLOC.matcher(strTrimmed);
		if (m.matches()) {
			int count = m.groupCount();
			// libelle - root - bloclabel
			if (count == 3) {
				
				String blocLabel = m.group(3);
				if (!mapBlocLabelToPdfBloc.containsKey(blocLabel)) {
				
				KeyAndLibelle pdfBloc = new KeyAndLibelle(blocLabel, m.group(1));
				mapBlocLabelToPdfBloc.put(blocLabel, pdfBloc);
				return true;
				}
			}
		}
		return false;
	}

	private boolean addRubrique(String strTrimmed) {
		Matcher m = PATTERN_RUBRIQUE.matcher(strTrimmed);
		if (m.matches()) {
			int count = m.groupCount();
			// libelle - root - bloclabel - rubriquelabel
			if (count == 4) {
				
				

				String blocLabel = m.group(3);
				Map<String, KeyAndLibelle> mapRubriques = this.mapBlocLabelToListRubriques.get(blocLabel);
				if (mapRubriques == null) {
					mapRubriques = new HashMap<String, KeyAndLibelle>();
					this.mapBlocLabelToListRubriques.put(blocLabel, mapRubriques);
				}
				
				String rubriqueLabel = m.group(4);
				
				if (!mapRubriques.containsKey(rubriqueLabel)) {
					
					KeyAndLibelle pdfRubrique = new KeyAndLibelle(rubriqueLabel, m.group(1));
					mapRubriques.put(rubriqueLabel, pdfRubrique);

					return true;
				}
			}
		}
		return false;
	}

}
