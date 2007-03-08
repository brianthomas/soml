/**
 * 
 */
package net.datamodel.soml.parse;

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
	
	// check the parsing of a document for which we have no handlers.
	// (e.g. we use the unconfigured/default DocumentHandlerImpl which
	// ships with SOML)
	static void checkDefaultHandlerParse(TestableDocument d) 
	{
	
		SOMLReader r = createReader(d);
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
	
	static SOMLReader createReader (SOMLDocument doc) {
		
		assertTrue("got an reference for the document", null != doc);
		logger.debug("Document ref is:"+doc.getSchemaName());
		
		// create the reader
		SOMLReader r = new SOMLReader(doc);
		logger.debug("Reader ref is:"+r);
		assertTrue("got an object reference for the reader", null != r);
		
		return r;
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
		public String getExpectedOutput() {  return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><doc xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><elem1>dude</elem1></doc>"; }
	}
		
}