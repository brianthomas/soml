
package net.datamodel.soml;

import java.io.File;
import java.net.URI;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * @author thomas
 *
 */
public class BaseCase extends TestCase {
	
	private static final Logger logger = Logger.getLogger(BaseCase.class);

	protected URI uri1 = null;
	protected URI uri2 = null;
	protected URI uri3 = null;
	protected URI uri4 = null;
	protected URI rel_URI = null;
	protected URI rel_URI2 = null;
	protected URI rel_URI3 = null;
	
	protected static final String testDirectory = "target/test-samples";
	
	static boolean didInit = false;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
//		if (!didInit) {
		// initialize URIs for tests
//		try {
			uri1 = new URI("urn:testSemanticObject1");
			uri2 = new URI("urn:testSemanticObject2");
			uri3 = new URI("urn:testSemanticObject3");
			uri4 = new URI("urn:testSemanticObject4");
			rel_URI = new URI("urn:test_rel1");
			rel_URI2 = new URI("urn:test_rel2");
			rel_URI3 = new URI("urn:test_rel3");
//		} catch (Exception e) {
//			fail ("Test Setup Error:"+e.getMessage());
//			throw e;
//		}
		
			// copy over sample files into test directory
			logger.debug("Setup test directory");
			
			logger.debug("mkdir test sample directory: "+testDirectory);
			new File(testDirectory).mkdir();
			
			didInit = true;
		//}
		
	}

}
