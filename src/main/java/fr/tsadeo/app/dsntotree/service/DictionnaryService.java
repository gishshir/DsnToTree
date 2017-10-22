package fr.tsadeo.app.dsntotree.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;

import fr.tsadeo.app.dsntotree.dico.DsnDictionnary;
import fr.tsadeo.app.dsntotree.dico.GetLinesFromPDFNormeDsn;
import fr.tsadeo.app.dsntotree.dico.IDictionnary;
import fr.tsadeo.app.dsntotree.dico.KeyAndLibelle;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;

public class DictionnaryService {
	
	private static final Logger LOG = Logger.getLogger(DictionnaryService.class.getName());
	
	private DsnDictionnary  dnsDictionnary;
	
	public IDictionnary getDsnDictionnary(){
		if (this.dnsDictionnary == null) {
    	  this.createDsnDictionnary();
    	}
		return this.dnsDictionnary;
		
	}
	
    private void createDsnDictionnary() {

    	this.dnsDictionnary = new DsnDictionnary();
    	// recuperer le fichier de norme
    	File normeDsnFile = SettingsUtils.get().getNormeDsnFile();
    	if (normeDsnFile != null && normeDsnFile.exists() && normeDsnFile.isFile() && normeDsnFile.canRead()) {
    		this.loadDsnDictionnary(normeDsnFile);
    	}
    }
	
	private void loadDsnDictionnary(File normeDsnFile) {
		
			PDDocument document = null;
			try {
				document = PDDocument.load(normeDsnFile);
				GetLinesFromPDFNormeDsn stripper = new GetLinesFromPDFNormeDsn();
				stripper.setSortByPosition(true);
				stripper.setStartPage(0);
				stripper.setEndPage(document.getNumberOfPages());

				Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
				stripper.writeText(document, dummy);

				this.dnsDictionnary.setValues(stripper.getMapBlocLabelToPdfBloc(),
						stripper.getMapBlocLabelToListRubriques());

				// voir le dictionnaire
				for (String blocLabel : stripper.getMapBlocLabelToPdfBloc().keySet()) {
					LOG.fine("\n".concat(stripper.getMapBlocLabelToPdfBloc().get(blocLabel).toString()));

					Map<String, KeyAndLibelle> mapRubriques = stripper.getMapBlocLabelToListRubriques().get(blocLabel);
					if (mapRubriques != null) {
						for (String labelRubrique : mapRubriques.keySet()) {
							KeyAndLibelle pdfRubrique = mapRubriques.get(labelRubrique);
							LOG.fine("\t".concat(pdfRubrique.toString()));
						}
					}
				}
			} 
			catch (Exception exeption) {
				LOG.severe("Echec dans la lecture du dictionnaire: " + exeption.getMessage());
			}
			finally {
				if (document != null) {
					try {
						document.close();
					} catch (IOException ignored) {}
				}
			}
	}

}
