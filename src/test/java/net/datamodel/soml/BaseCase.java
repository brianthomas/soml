
package net.datamodel.soml;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
	
	protected static final String schemaDirectory = "docs/schema";
	protected static final String samplesDirectory = "docs/samples";
	protected static final String testDirectory = "target/test-samples";
	
	protected static String [] samplefiles = null;
	
	static boolean didInit = false;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
//		if (!didInit) {
		// initialize URIs for tests
//		try {
			uri1 = new URI("urn:test:SemanticObject1");
			uri2 = new URI("urn:test:SemanticObject2");
			uri3 = new URI("urn:test:SemanticObject3");
			uri4 = new URI("urn:test:SemanticObject4");
			rel_URI = new URI("urn:test:rel1");
			rel_URI2 = new URI("urn:test:rel2");
			rel_URI3 = new URI("urn:test:rel3");
//		} catch (Exception e) {
//			fail ("Test Setup Error:"+e.getMessage());
//			throw e;
//		}
		
			// copy over sample files into test directory
			logger.debug("Setup test directory");
			
			logger.debug("mkdir test sample directory: "+testDirectory);
			new File(testDirectory).mkdir();
			
			logger.debug("find sample and schema files");
			samplefiles = new File(samplesDirectory).list(new OnlyXML());
			String [] sampleschemafiles = new File(samplesDirectory).list(new OnlySchema());
			String [] baseschemafiles = new File(schemaDirectory).list(new OnlySchema());
			
			logger.debug("copy over sample and schema files to test directory");
			try {
				UtilityForTests.copyFiles(samplefiles, samplesDirectory, testDirectory); 
				UtilityForTests.copyFiles(sampleschemafiles, samplesDirectory, testDirectory); 
				UtilityForTests.copyFiles(baseschemafiles, schemaDirectory, testDirectory); 
			} catch (Exception e) {
				logger.error("Cant set up tests : "+ e.getMessage());
				e.printStackTrace();
			}
			
			didInit = true;
		//}
		
	}
	
	class OnlyXML implements FilenameFilter {
		public boolean accept (File dir, String s) {
			if (s.endsWith(".xml")) { return true; }
			return false;
		}
	}
	
	class OnlySchema implements FilenameFilter {
		public boolean accept (File dir, String s) {
			if (s.endsWith(".xsd")) { return true; }
			return false;
		}
	}

}
