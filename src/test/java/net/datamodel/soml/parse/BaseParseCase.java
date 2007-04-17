
package net.datamodel.soml.parse;

import java.io.File;
import java.io.FilenameFilter;
import java.io.StringReader;

import net.datamodel.soml.BaseCase;
import net.datamodel.soml.support.SOMLDocument;
import net.datamodel.soml.support.SOMLReader;
import net.datamodel.soml.support.DOMXerces2.SOMLDocumentImpl;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

/**
 * @author thomas
 *
 */
public class BaseParseCase extends BaseCase {

	private static final Logger logger = Logger.getLogger(BaseParseCase.class);
	
	protected static final String schemaDirectory = "docs/schema";
	protected static final String samplesDirectory = "docs/samples";
	protected static final String testDirectory = "target/test-samples";
	
	protected static String [] samplefiles = null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
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
			UtilityForParseTests.copyFiles(samplefiles, samplesDirectory, testDirectory); 
			UtilityForParseTests.copyFiles(sampleschemafiles, samplesDirectory, testDirectory); 
			UtilityForParseTests.copyFiles(baseschemafiles, schemaDirectory, testDirectory); 
		} catch (Exception e) {
			logger.error("Cant set up tests : "+ e.getMessage());
			e.printStackTrace();
		}
		
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

	// check the parsing of a document for which we have no handlers.
	// (e.g. we use the unconfigured/default DocumentHandlerImpl which
	// ships with SOML)
	static void checkDefaultHandlerParse (TestableDocument d) 
	{
	
		SOMLReader r = UtilityForParseTests.createReader(d);
		boolean canRead = true;
		try {
			InputSource inputsource = new InputSource(new StringReader(d.getExpectedOutput()));
			r.parse(inputsource);
		} catch (Exception e) {
			canRead = false;
			e.printStackTrace();
		}
		assertTrue("Can read source without crashing", canRead);
	
		// check serialization
		checkXMLOutput(d.getExpectedOutput(), r.getDocument().toXMLString());
		
	}
	
	static void checkXMLOutput (String actual, String expected) {
		logger.debug("XML output:["+actual+"]");
		logger.debug("  expected:["+expected+"]");
		assertEquals("XML output as expected", actual, expected );
	}
	
	interface TestableDocument extends SOMLDocument {
		public String getExpectedOutput();
	}
	
	// a test document where we wont load any soml stuff into..just to
	// see if the XSSP stuff carried along unharmed.
	class NonSOMLTestDocumentImpl extends SOMLDocumentImpl
	implements TestableDocument
	{
		public String getExpectedOutput() { 
			return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><doc xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><elem1>dude</elem1></doc>"; 
		}
	}
		
}