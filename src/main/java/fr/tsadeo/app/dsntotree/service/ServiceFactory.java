package fr.tsadeo.app.dsntotree.service;

public class ServiceFactory {

    private static ReadDsnFromFileService readDsnFromFileService;
    private static ReadDsnFromDatasService readDsnFromDatasService;
    private static WriteDsn writeDsnService;
    private static DsnService dsnService;
    
    public static ReadDsnFromDatasService getReadDsnFromDatasService() {
    	if(readDsnFromDatasService == null) {
    		readDsnFromDatasService = new ReadDsnFromDatasService();
    	}
    	return readDsnFromDatasService;
    }
    public static ReadDsnFromFileService getReadDsnFromFileService() {
    	
    	if (readDsnFromFileService == null) {
    		readDsnFromFileService = new ReadDsnFromFileService();
    	}
    	return readDsnFromFileService;
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
