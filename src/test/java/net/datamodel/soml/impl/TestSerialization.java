/**
 * 
 */
package net.datamodel.soml.impl;

import java.net.URI;
import java.net.URISyntaxException;

import net.datamodel.soml.BaseCase;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.impl.SemanticObjectImpl;
import net.datamodel.xssp.XMLSerializableObject;
import net.datamodel.xssp.support.Constants;
import net.datamodel.xssp.support.Specification;

import org.apache.log4j.Logger;

/**
 * @author thomas
 *
 */
public class TestSerialization extends BaseCase {

	private static final Logger logger = Logger.getLogger(TestSerialization.class);
	
	private static void checkXMLOutput (XMLSerializableObject obj, String expectedOut) {
		logger.debug("XML output:["+obj.toXMLString()+"]");
		logger.debug("  expected:["+expectedOut+"]");
		assertEquals("XML output as expected", expectedOut, obj.toXMLString());
	}
	
	public void test1a() {
		
		logger.info("Check URI serialization.");
		
		checkBuildURI("urn:test1"); 
		checkBuildURI("urn:test2"); 
		checkBuildURI("urn:dude"); 
		checkBuildURI("news:comp.lang.java");
		checkBuildURI("mailto:java-net@java.sun.com"); 
	    checkBuildURI("urn:isbn:096139210x"); 
		
	}
	
	public void test2() {
		logger.info("Check SO serialization.");
		
		Specification spec = Specification.getInstance();
		spec.setPrettyOutput(false);
		
		SemanticObject so = new SemanticObjectImpl (uri1);
		SemanticObject so2 = new SemanticObjectImpl (uri2);
		so2.addRelationship(so, rel_URI); 
		
		// test non-pretty output 
		checkXMLOutput(so,"<semanticObject URI=\"urn:test:SemanticObject1\"/>");
		checkXMLOutput(so2,"<semanticObject URI=\"urn:test:SemanticObject2\">"+
				"<relationship URI=\"urn:test:rel1\"><semanticObject URI=\"urn:test:SemanticObject1\"/>"+
				"</relationship></semanticObject>");
		
		// test pretty output 
		spec.setPrettyOutput(true);
		
		String indent = spec.getPrettyOutputIndentation();
		String newLine = Constants.NEW_LINE;
		checkXMLOutput(so,"<semanticObject URI=\"urn:test:SemanticObject1\"/>");
		checkXMLOutput(so2,
				"<semanticObject URI=\"urn:test:SemanticObject2\">"+newLine
				+indent+"<relationship URI=\"urn:test:rel1\">"+newLine
				+indent+indent+"<semanticObject URI=\"urn:test:SemanticObject1\"/>"+newLine
				+indent+"</relationship>"+newLine
				+"</semanticObject>");
		
		spec.setPrettyOutput(false); // reset for next test 
		
	}
	
	// test UnmixedList serialization
	//
	public void test3() {
		logger.info("Check unmixedlist serialization.");
	}
	
	
	private void checkBuildURI (String content) {
		logger.debug("check build URI:["+content+"]");
		URI test = null;
		try {
			test = new URI(content);
		} catch (URISyntaxException e) {
			logger.debug(" Cant build URI given value:["+content+"]!!");
		}
		assertNotNull("URI is ok:["+content+"]", test);
	}

}
