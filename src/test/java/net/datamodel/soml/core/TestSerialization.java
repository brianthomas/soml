/**
 * 
 */
package net.datamodel.soml.core;

import java.net.URISyntaxException;

import net.datamodel.soml.BaseCase;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.URN;
import net.datamodel.soml.impl.SemanticObjectImpl;
import net.datamodel.soml.impl.URNImpl;
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
		
		logger.info("Check URN serialization.");
		
		checkBuildURN("urn:test1"); 
		checkBuildURN("urn:test2"); 
		checkBuildURN("urn:dude"); 
		checkBuildURN("news:comp.lang.java");
		checkBuildURN("mailto:java-net@java.sun.com"); 
	    checkBuildURN("urn:isbn:096139210x"); 
		
	}
	
	public void test2() {
		logger.info("Check SO serialization.");
		
		Specification spec = Specification.getInstance();
		spec.setPrettyOutput(false);
		
		SemanticObject so = new SemanticObjectImpl (urn1);
		SemanticObject so2 = new SemanticObjectImpl (urn2);
		so2.addRelationship(so, rel_urn); 
		
		// test non-pretty output 
		checkXMLOutput(so,"<semanticObject urn=\"urn:SemanticObject1\"/>");
		checkXMLOutput(so2,"<semanticObject urn=\"urn:SemanticObject2\">"+
				"<relationship urn=\"urn:rel1\"><semanticObject urn=\"urn:SemanticObject1\"/>"+
				"</relationship></semanticObject>");
		
		// test pretty output 
		spec.setPrettyOutput(true);
		
		String indent = spec.getPrettyOutputIndentation();
		String newLine = Constants.NEW_LINE;
		checkXMLOutput(so,"<semanticObject urn=\"urn:SemanticObject1\"/>");
		checkXMLOutput(so2,
				"<semanticObject urn=\"urn:SemanticObject2\">"+newLine
				+indent+"<relationship urn=\"urn:rel1\">"+newLine
				+indent+indent+"<semanticObject urn=\"urn:SemanticObject1\"/>"+newLine
				+indent+"</relationship>"+newLine
				+"</semanticObject>");
		
		spec.setPrettyOutput(false); // reset for next test 
		
	}
	
	// test UnmixedList serialization
	//
	public void test3() {
		logger.info("Check unmixedlist serialization.");
	}
	
	
	private void checkBuildURN (String content) {
		logger.debug("check build URN:["+content+"]");
		URN test = null;
		try {
			test = new URNImpl(content);
		} catch (URISyntaxException e) {
			logger.debug(" Cant build URN given value:["+content+"]!!");
		}
		assertNotNull("urn is ok:["+content+"]", test);
	}

}
