package fr.tsadeo.app.dsntotree.dico;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import fr.tsadeo.app.dsntotree.util.IRegexConstants;
import fr.tsadeo.app.dsntotree.util.RegexUtils;
import fr.tsadeo.app.dsntotree.util.RegexUtils.CapturingGroups;


public class GetLinesFromPDFNormeDsn extends PDFTextStripper implements IRegexConstants {
	
    private static final Logger LOG = Logger.getLogger(GetLinesFromPDFNormeDsn.class.getName());

	private static final String REGEX_ROOT_AND_BLOC_LABEL = "(S" + REGEX_TWO_DIGIT + REGEX_POINT + "G00" + REGEX_POINT
			+ ")(" + REGEX_TWO_DIGIT + ")";

	private static final String REGEX_DESCR_AND_BLOC = "(.*)" + REGEX_SPACE + REGEX_ROOT_AND_BLOC_LABEL;

	private static final Pattern PATTERN_BLOC = Pattern.compile(REGEX_DESCR_AND_BLOC + "$");
	private static final Pattern PATTERN_RUBRIQUE = Pattern
			.compile(REGEX_DESCR_AND_BLOC + REGEX_POINT + "(" + REGEX_TREE_DIGIT + ")$");
	
	private static final Pattern PATTERN_START_NON_NUMERIQUE = Pattern.compile("^" + REGEX_NO_NUMERIQUE + "{1,3}.*");


	private int compteur = 0;
	private final Map<Integer, String> mapLineNumberToBlocLabel = new LinkedHashMap<Integer, String>();
	private final Map<String, List<String>> keyToListLines = new HashMap<String, List<String>>();

	private final Map<String, KeyAndLibelle> mapBlocLabelToPdfBloc = new HashMap<String, KeyAndLibelle>();
	private final Map<String, Map<String, KeyAndLibelle>> mapBlocLabelToListRubriques = new HashMap<String, Map<String, KeyAndLibelle>>();

	public Map<String, Map<String, KeyAndLibelle>> getMapBlocLabelToListRubriques() {
		return mapBlocLabelToListRubriques;
	}

	private String currentKey = null;
    private String currentBlocLabel = null;
    boolean ajoutBlocInProgress = false;

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
		
        if (strTrimmed.matches(REGEX_ROOT_AND_BLOC_LABEL) && !this.isKeyAlreadyInUse(strTrimmed)) {
            LOG.config(strTrimmed);
			this.currentKey = strTrimmed;
			mapLineNumberToBlocLabel.put(compteur, strTrimmed);
			this.keyToListLines.put(this.currentKey, new ArrayList<String>());
		} else if (this.currentKey != null) {
			
            if (ajoutBlocInProgress) {
                if (strTrimmed.isEmpty()) {
                    ajoutBlocInProgress = false;
                } else {
                    this.completeLibelleBloc(strTrimmed);
                    ajoutBlocInProgress = false;
                }
            }

            if (strTrimmed.contains(this.currentKey)) {

				this.keyToListLines.get(this.currentKey).add(strTrimmed);

				if (!this.addBloc(strTrimmed)) {
					LOG.config(strTrimmed);
					this.addRubrique(strTrimmed);
                } else {
                    ajoutBlocInProgress = true;
				}
			}
		}
		compteur++;
	}

    private boolean isKeyAlreadyInUse(String key) {

        return this.keyToListLines.containsKey(key) && this.mapBlocLabelToListRubriques.containsKey(key);
    }

	private void completeLibelleBloc(String libelleToAdd) {

		if (this.mapBlocLabelToPdfBloc.containsKey(this.currentBlocLabel)) {

			if (RegexUtils.get().matches(libelleToAdd, PATTERN_START_NON_NUMERIQUE)) {
				LOG.config("Completer le libelle du bloc " + currentBlocLabel + " avec " + libelleToAdd);
				KeyAndLibelle keyAndLibelle = this.mapBlocLabelToPdfBloc.get(currentBlocLabel);
				KeyAndLibelle updatedLibelle = new KeyAndLibelle(keyAndLibelle.getKey(),
						keyAndLibelle.getLibelle() + " " + libelleToAdd);
				this.mapBlocLabelToPdfBloc.put(currentBlocLabel, updatedLibelle);
			}
		}
	}
	private boolean addBloc(String strTrimmed) {

		CapturingGroups capturingGroups = new CapturingGroups(3, 1);
		RegexUtils.get().extractsGroups(strTrimmed, PATTERN_BLOC, capturingGroups);
		if (capturingGroups.isSuccess()) {
			this.currentBlocLabel = capturingGroups.valueOf(3);
			if (!mapBlocLabelToPdfBloc.containsKey(this.currentBlocLabel)) {

				KeyAndLibelle pdfBloc = new KeyAndLibelle(this.currentBlocLabel, capturingGroups.valueOf(1));
				mapBlocLabelToPdfBloc.put(this.currentBlocLabel, pdfBloc);
				return true;
			}
		}
		return false;
	}

	private boolean addRubrique(String strTrimmed) {
		
		CapturingGroups capturingGroups = new CapturingGroups(3, 4, 1);
		RegexUtils.get().extractsGroups(strTrimmed, PATTERN_RUBRIQUE, capturingGroups);
		if (capturingGroups.isSuccess()) {
			
			String blocLabel = capturingGroups.valueOf(3);
			Map<String, KeyAndLibelle> mapRubriques = this.mapBlocLabelToListRubriques.get(blocLabel);
			if (mapRubriques == null) {
				mapRubriques = new HashMap<String, KeyAndLibelle>();
				this.mapBlocLabelToListRubriques.put(blocLabel, mapRubriques);
			}
			
			String rubriqueLabel = capturingGroups.valueOf(4);
			
			if (!mapRubriques.containsKey(rubriqueLabel)) {
				
				KeyAndLibelle pdfRubrique = new KeyAndLibelle(rubriqueLabel, capturingGroups.valueOf(1));
				mapRubriques.put(rubriqueLabel, pdfRubrique);

				return true;
			}
			
		}
		
		return false;
	}

}
