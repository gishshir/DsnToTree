package fr.tsadeo.app.dsntotree.dico;

import java.util.HashMap;
import java.util.Map;

public class DsnDictionnary implements IDictionnary {
	
	private Map<String, KeyAndLibelle> mapBlocLabelToPdfBloc = new HashMap<String, KeyAndLibelle>();
	private Map<String, Map<String, KeyAndLibelle>> mapBlocLabelToListRubriques = new HashMap<String, Map<String, KeyAndLibelle>>();
	
	public void setValues(Map<String, KeyAndLibelle> mapBlocLabelToPdfBloc, Map<String, Map<String, KeyAndLibelle>> mapBlocLabelToListRubriques) {
		this.mapBlocLabelToPdfBloc.putAll(mapBlocLabelToPdfBloc);
		this.mapBlocLabelToListRubriques.putAll(mapBlocLabelToListRubriques);
	}
	//---------------------------- implementing IDictionnary

	@Override
	public String getLibelle(String key) {
		if (this.mapBlocLabelToPdfBloc.containsKey(key)) {
			return this.mapBlocLabelToPdfBloc.get(key).getLibelle();
					
		}
		return null;
	}

	@Override
	public String getLibelle(String key, String subkey) {

        if (this.mapBlocLabelToListRubriques.containsKey(key)) {
        	Map<String, KeyAndLibelle> mapRubriques = this.mapBlocLabelToListRubriques.get(key);
        	if (mapRubriques.containsKey(subkey)) {
        		return mapRubriques.get(subkey).getLibelle();
        	}
        }
		return null;
	}
	
	

}
