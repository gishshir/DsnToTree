package fr.tsadeo.app.dsntotree.service;

public class ServiceFactory {

    private static ReadDsn readDsnService;
    private static WriteDsn writeDsnService;
    private static DsnService dsnService;
    
    public static ReadDsn getReadDsnService() {
    	
    	if (readDsnService == null) {
    		readDsnService = new ReadDsn();
    	}
    	return readDsnService;
    }
    
    public static WriteDsn getWriteDsnService () {
    	
    	if (writeDsnService == null) {
    		writeDsnService = new WriteDsn();
    	}
    	return writeDsnService;
    }
    
    public static DsnService getDsnService() {
    	
    	if (dsnService == null) {
    		dsnService = new DsnService();
    	}
    	return dsnService;
    }
}
