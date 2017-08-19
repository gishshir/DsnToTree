package fr.tsadeo.app.dsntotree.gui.bdd;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class OracleConnectComponentTest {

	private final static OracleConnectComponent component = new OracleConnectComponent();
	
	@Test
	public void testGetUrl() {
		String result = component.getUrl("dedtanya04.ext.tdc", 1521, "INFIDEV");
		assertNotNull(result);
		System.out.println(result);
	}
}
