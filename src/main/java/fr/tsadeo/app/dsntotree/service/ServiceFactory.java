package fr.tsadeo.app.dsntotree.service;

public class ServiceFactory {

    private static ReadDsnFromFileService readDsnFromFileService;
    private static ReadDsnFromDatasService readDsnFromDatasService;
    private static WriteDsn writeDsnService;
    private static DsnService dsnService;
    private static ReadDatasFromSqlRequestService readDatasFromSqlRequestService;
    private static DictionnaryService dictionnaryService;
    private static BlocTreeService blocTreeService;
    private static TnsNameOraService tnsNameOraService;
    
    public static TnsNameOraService getTnsNameOraService() {
    	if (tnsNameOraService == null) {
    		tnsNameOraService = new TnsNameOraService();
    	}
    	return tnsNameOraService;
    }
    
    public static BlocTreeService getBlocTreeService() {
        if (blocTreeService == null) {
            blocTreeService = new BlocTreeService();
        }
        return blocTreeService;
    }
    public static DictionnaryService getDictionnaryService() {
    	if (dictionnaryService == null) {
    		dictionnaryService = new DictionnaryService();
    	}
    	return dictionnaryService;
    }
    public static ReadDatasFromSqlRequestService getReadDatasFromSqlRequestService() {
    	if (readDatasFromSqlRequestService == null) {
    		readDatasFromSqlRequestService = new ReadDatasFromSqlRequestService();
    	}
    	return readDatasFromSqlRequestService;
    }
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
