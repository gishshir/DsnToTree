package fr.tsadeo.app.dsntotree.util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.regex.Pattern;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.service.AbstractTest;
import fr.tsadeo.app.dsntotree.util.RegexUtils.CapturingGroups;

public class RegexUtilsTest extends AbstractTest {
	
	private static final String VALUE = "Envoi S10.G00.00";
	private static final Pattern PATTERN = Pattern.compile("(.*)\\s(S[\\d]{2}\\.G00\\.)([\\d]{2})$");

	
	@Test
	public void testExtractsGroupsWIithCapturingGroups() {
	
		
		CapturingGroups capturingGroups = new CapturingGroups(3, 1);
		RegexUtils.get().extractsGroups(VALUE, PATTERN, capturingGroups);
		
		assertEquals (2, capturingGroups.getGroups().size());
		assertTrue(capturingGroups.isSuccess());
		assertEquals("00", capturingGroups.valueOf(3));
		assertEquals("Envoi", capturingGroups.valueOf(1));
		
		capturingGroups = new CapturingGroups(4, 1);
		RegexUtils.get().extractsGroups(VALUE, PATTERN, capturingGroups);
		assertFalse(capturingGroups.isSuccess());
	}
	
	@Test
	public void testExtractsGroups () {
		
		String[] result = RegexUtils.get().extractsGroups(VALUE, PATTERN, 3, 1);
		assertNotNull(result);
		assertEquals (2,result.length);
		assertEquals("00", result[0]);
		assertEquals("Envoi", result[1]);
	}
}
