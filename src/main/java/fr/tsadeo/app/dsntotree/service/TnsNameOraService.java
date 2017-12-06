package fr.tsadeo.app.dsntotree.service;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.tsadeo.app.dsntotree.dto.TnsOracleInstanceDto;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;
import fr.tsadeo.app.dsntotree.util.TnsNameOraParserUtils;

public class TnsNameOraService {
	
	
    private static final Comparator<TnsOracleInstanceDto> COMPARATOR =
    		 (o1,o2) -> o1.getService().compareTo(o2.getService());

    private Map<String, TnsOracleInstanceDto> mapInstances;
    private List<TnsOracleInstanceDto> sortedListInstances;
    
    public TnsOracleInstanceDto getInstance(String service) {
    	return this.mapInstances.get(service);
    }
    
    public List<TnsOracleInstanceDto> filterInstances(final String search) {
        
        return this.getListInstances().stream()
        		.filter(tnsOracleInstanceDto -> search != null && tnsOracleInstanceDto.matches(search))
        		.collect(Collectors.toList());

    }

    public boolean hasTnsNameInstances() {
    	return SettingsUtils.get().getTnsNameOraFile() != null;
    }
    public List<TnsOracleInstanceDto> getListInstances() {
    	if (this.mapInstances == null) {
    		
    		File file = SettingsUtils.get().getTnsNameOraFile();
    		List<TnsOracleInstanceDto> listInstances = TnsNameOraParserUtils.get().loadTnsOracleFile(file);
    		if (listInstances == null) {
    			listInstances = Collections.emptyList();
    		}
    		this.mapInstances = listInstances.stream()
    			.collect(Collectors.toMap(TnsOracleInstanceDto::getService,
    					tnsOracleInstanceDto -> tnsOracleInstanceDto,
    					// pour resoudre Duplicate Key
    					(oldValue, newValue) -> newValue));
    		
    	}
    	if (this.sortedListInstances == null) {
    		this.sortedListInstances =
    				this.mapInstances.values().stream()
        			.sorted(COMPARATOR)
        			.collect(Collectors.toList());
    	}
    	
    	return this.sortedListInstances;
    	
    }

}
