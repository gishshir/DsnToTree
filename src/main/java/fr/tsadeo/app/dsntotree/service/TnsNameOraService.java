package fr.tsadeo.app.dsntotree.service;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import fr.tsadeo.app.dsntotree.dto.TnsOracleInstanceDto;
import fr.tsadeo.app.dsntotree.util.SettingsUtils;
import fr.tsadeo.app.dsntotree.util.TnsNameOraParserUtils;

public class TnsNameOraService {
	
	private static final Logger LOG = Logger.getLogger(TnsNameOraService.class.getName());

    private List<TnsOracleInstanceDto> listInstances;
    
    public List<TnsOracleInstanceDto> getListInstances() {
    	if (this.listInstances == null) {
    		
    		File file = SettingsUtils.get().getTnsNameOraFile();
    		this.listInstances = TnsNameOraParserUtils.get().loadTnsOracleFile(file);
    		if (this.listInstances == null) {
    			this.listInstances = Collections.emptyList();
    		}
    	}
    	return this.listInstances;
    }

}
