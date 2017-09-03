package fr.tsadeo.app.dsntotree.bdd.dao;

import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager.Type;
import fr.tsadeo.app.dsntotree.bdd.dao.impl.OracleBddAccessManager;

public class BddAccessManagerFactory {
	
	private static BddAccessManagerFactory instance;
	private static BddAccessManagerFactory getInstance() {
		if (instance == null) {
			instance = new BddAccessManagerFactory();
		}
		return instance;
	}
	private BddAccessManagerFactory() {}
	
	private IBddAccessManager oracleBddAccessManager;
	private Type currentType = Type.Oracle;
	
	public static void setCurrentType(Type type) {
		getInstance().currentType = type;
	}
	public static IBddAccessManager get()  {
		return get(getInstance().currentType);
	}
	public static IBddAccessManager get(Type type)  {
		
		switch (type) {
		case Oracle:
			return getInstance().getOracleBddAccessManager();

		default:
			return getInstance().getOracleBddAccessManager();
		}
	}
	
	
	private IBddAccessManager getOracleBddAccessManager() {
		
		if (oracleBddAccessManager == null) {
			oracleBddAccessManager = new OracleBddAccessManager();
		}
		return oracleBddAccessManager;
	}

}
