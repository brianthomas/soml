/**
 * 
 */
package net.datamodel.soml.dom;

import net.datamodel.soml.UtilityForTests;
import net.datamodel.xssp.dom.Specification;

import org.apache.log4j.Logger;

/**
 * @author thomas
 *
 */
public class TestReader extends BaseParseCase {
	
	private static final Logger logger = Logger.getLogger(TestReader.class);
	
	public void test1() {
		logger.info("test creating reader with several document types");
		
		UtilityForTests.createReader(new NonSOMLTestDocumentImpl());
		
	}
	
	public void test2 () {
		logger.info("Try to load SOML document with no soml stuff");
		Specification.getInstance().setPrettyOutput(false);
		checkDefaultHandlerParse(new NonSOMLTestDocumentImpl()); 
	}
	
	
}
