package fr.tsadeo.app.dsntotree.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.tsadeo.app.dsntotree.dto.TnsOracleInstanceDto;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;
import fr.tsadeo.app.dsntotree.util.TnsNameOraParserUtils;

public class TnsNameOraService {
	
	private static final Logger LOG = Logger.getLogger(TnsNameOraService.class.getName());
	
    private static final Comparator<TnsOracleInstanceDto> COMPARATOR =
    		new Comparator<TnsOracleInstanceDto>() {

				@Override
				public int compare(TnsOracleInstanceDto o1, TnsOracleInstanceDto o2) {
					return o1.getService().compareTo(o2.getService());
				}
			};

    private Map<String, TnsOracleInstanceDto> mapInstances;
    
    public TnsOracleInstanceDto getInstance(String service) {
    	return this.mapInstances.get(service);
    }
    
    public List<TnsOracleInstanceDto> getListInstances() {
    	if (this.mapInstances == null) {
    		
    		this.mapInstances = new HashMap<>();
    		
    		File file = SettingsUtils.get().getTnsNameOraFile();
    		List<TnsOracleInstanceDto> listInstances = TnsNameOraParserUtils.get().loadTnsOracleFile(file);
    		if (listInstances == null) {
    			listInstances = Collections.emptyList();
    		}
    		for (TnsOracleInstanceDto tnsOracleInstanceDto : listInstances) {
				this.mapInstances.put(tnsOracleInstanceDto.getService(), tnsOracleInstanceDto);
			}
    	}
    	List<TnsOracleInstanceDto> listInstances = new ArrayList<>(this.mapInstances.values());
    	Collections.sort(listInstances, COMPARATOR);
    	return listInstances;
    }

}
