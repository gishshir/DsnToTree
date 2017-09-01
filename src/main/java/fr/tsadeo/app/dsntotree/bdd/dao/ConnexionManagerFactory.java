package fr.tsadeo.app.dsntotree.bdd.dao;

import fr.tsadeo.app.dsntotree.bdd.dao.IConnectionManager.Type;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.OracleConnexionManager;

public class ConnexionManagerFactory {
	
	private static ConnexionManagerFactory instance;
	private static ConnexionManagerFactory get() {
		if (instance == null) {
			instance = new ConnexionManagerFactory();
		}
		return instance;
	}
	private ConnexionManagerFactory() {}
	
	private IConnectionManager oracleConnexionManager;
	
	public static IConnectionManager get(Type type)  {
		
		switch (type) {
		case Oracle:
			return get().getOracleConnexionManager();

		default:
			return get().getOracleConnexionManager();
		}
	}
	
	
	private IConnectionManager getOracleConnexionManager() {
		
		if (oracleConnexionManager == null) {
			oracleConnexionManager = new OracleConnexionManager();
		}
		return oracleConnexionManager;
	}

}
