package fr.tsadeo.app.dsntotree.util;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Test;

import fr.tsadeo.app.dsntotree.dto.TnsOracleInstanceDto;
import fr.tsadeo.app.dsntotree.service.AbstractTest;

public class TnsNameOraParserUtilsTest extends AbstractTest{
	
	protected static final String TNSNAME_ORA = "tnsnames.ora";
	
	@Test
	public void testLoadTnsOracleFile() throws Exception{
		
		File file = super.getFile(TNSNAME_ORA);
		List<TnsOracleInstanceDto> result = TnsNameOraParserUtils.get().loadTnsOracleFile(file);
		
		assertNotNull(result);
		result.stream().forEach(tnsOracleInstanceDto -> 
			LOG.config(tnsOracleInstanceDto.toString())
		);
	}

}
