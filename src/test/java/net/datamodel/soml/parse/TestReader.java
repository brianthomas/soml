/**
 * 
 */
package net.datamodel.soml.parse;

import net.datamodel.soml.UtilityForTests;

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
		checkDefaultHandlerParse(new NonSOMLTestDocumentImpl()); 
	}
	
	
}
