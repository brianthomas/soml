/**
 * 
 */
package net.datamodel.soml.impl;

import java.net.URI;
import java.net.URISyntaxException;

import net.datamodel.soml.BaseCase;
import net.datamodel.soml.Constant;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.UtilityForTests;
import net.datamodel.soml.support.SOMLDocument;
import net.datamodel.soml.support.SOMLElement;
import net.datamodel.soml.support.DOMXerces2.SOMLDocumentImpl;
import net.datamodel.xssp.XMLSerializableObject;
import net.datamodel.xssp.parse.Specification;

import org.apache.log4j.Logger;

/**
 * @author thomas
 *
 */
public class TestSerialization 
extends BaseCase 
{

	private static final Logger logger = Logger.getLogger(TestSerialization.class);
	
	// TODO
	/*
	public void test1() {
		
		logger.info("Check URI serialization.");
		
		checkBuildURI("urn:test1"); 
		checkBuildURI("urn:test2"); 
		checkBuildURI("urn:dude"); 
		checkBuildURI("news:comp.lang.java");
		checkBuildURI("mailto:java-net@java.sun.com"); 
	    checkBuildURI("urn:isbn:096139210x"); 
		
	}
	*/
	
	public void test2() {
		logger.info("Check SO serialization.");
		
		Specification spec = Specification.getInstance();
		spec.setPrettyOutput(false);
		
		SemanticObject so = new SemanticObjectImpl (uri1);
		SemanticObject so2 = new SemanticObjectImpl (uri2);
		so2.addProperty(so, rel_URI); 
		
		// test non-pretty output 
		/*
		checkXMLOutput(so,"<semanticObject URI=\"urn:test:SemanticObject1\" soId=\"id0\"/>");
		checkXMLOutput(so2,"<semanticObject URI=\"urn:test:SemanticObject2\" soId=\"id0\">"+
				"<property URI=\"urn:test:rel1\">"+
				"<semanticObject URI=\"urn:test:SemanticObject1\" soId=\"id1\"/>"+
				"</property></semanticObject>");
				*/
		checkDocumentRepresentation(so);
		
		// test pretty output 
		spec.setPrettyOutput(true);
		
	//	checkXMLOutput(so,"<semanticObject URI=\"urn:test:SemanticObject1\" soId=\"id1\"/>");
	//	checkDocumentRepresentation(so);
		
		// TODO
		/*
		String indent = spec.getPrettyOutputIndentation();
		String newLine = System.getProperty("line.separator");
		checkXMLOutput(so2,
				"<semanticObject URI=\"urn:test:SemanticObject2\" soId=\"id0\">"+newLine
				+indent+"<property URI=\"urn:test:rel1\">"+newLine
				+indent+indent+"<semanticObject URI=\"urn:test:SemanticObject1\" soId=\"id1\"/>"+newLine
				+indent+"</property>"+newLine
				+"</semanticObject>");
		checkDocumentRepresentation(so2);
		*/
		
		spec.setPrettyOutput(false); // reset for next test 
		
	}
	
	// test UnmixedList serialization
	//
		// TODO
	/*
	public void test3() {
		logger.info("Check unmixedlist serialization. -- TODO!");
	}
	*/
	
	// test Inter-referential propertys
	//
	// TODO
	/*
	public void test4() {
		
		logger.info("test serialization of Inter-referential propertys"); 
		
		// first create some test objects...
		SemanticObject so1 = new SemanticObjectImpl(uri1);
		SemanticObject so2 = new SemanticObjectImpl(uri2);
		
		so1.addProperty(so2, rel_URI);
		so2.addProperty(so1, rel_URI2);
		
		// pretty output is more rigourous test
		Specification.getInstance().setPrettyOutput(true);
		String indent = Specification.getInstance().getPrettyOutputIndentation();
		String newLine = System.getProperty("line.separator");
		String expectedOutput1 = 
			"<semanticObject URI=\"urn:test:SemanticObject1\" soId=\"id0\">"+newLine+ 
			indent+"<property URI=\"urn:test:rel1\">"+newLine+ 
			indent+indent+"<semanticObject URI=\"urn:test:SemanticObject2\" soId=\"id1\">"+newLine+ 
			indent+indent+indent+"<property URI=\"urn:test:rel2\">"+newLine+ 
			indent+indent+indent+indent+"<semanticObjectRef soRefId=\"id0\"/>"+newLine+ 
			indent+indent+indent+"</property>"+newLine+
			indent+indent+"</semanticObject>"+newLine+
			indent+"</property>"+newLine+
			"</semanticObject>";
		
		String expectedOutput2 = 
			"<semanticObject URI=\"urn:test:SemanticObject2\" soId=\"id1\">"+newLine+ 
			indent+"<property URI=\"urn:test:rel2\">"+newLine+ 
			indent+indent+"<semanticObject URI=\"urn:test:SemanticObject1\" soId=\"id0\">"+newLine+ 
			indent+indent+indent+"<property URI=\"urn:test:rel1\">"+newLine+ 
			indent+indent+indent+indent+"<semanticObjectRef soRefId=\"id1\"/>"+newLine+ 
			indent+indent+indent+"</property>"+newLine+
			indent+indent+"</semanticObject>"+newLine+
			indent+"</property>"+newLine+
			"</semanticObject>";
		
		
		checkXMLOutput(so1,expectedOutput1);
		checkDocumentRepresentation(so1);
		
		checkXMLOutput(so2,expectedOutput2);
		checkDocumentRepresentation(so2);

		Specification.getInstance().setPrettyOutput(false);
	}
	*/
	
	private static void checkDocumentRepresentation (SemanticObject so) {
		
		SOMLDocument doc = new SOMLDocumentImpl();
		SOMLElement selem = doc.createSOMLElement(so); 
		
		// set the schema location
		String schemaLoc = Constant.SOML_NAMESPACE_URI+" "+testDirectory+"/"+Constant.SOML_SCHEMA_NAME;
		logger.debug("Set schema location:"+schemaLoc);
		selem.setAttribute("xsi:schemaLocation",schemaLoc);
		doc.setDocumentElement(selem);
		
		try {
			Specification.getInstance().setPrettyOutput(true);
			UtilityForTests.checkValidXMLRepresentation(doc); 
			Specification.getInstance().setPrettyOutput(false);
			UtilityForTests.checkValidXMLRepresentation(doc); 
		} catch (Exception e) {
			fail("check doc representation check failed:"+e.getMessage());
		}
	}
	
	private static void checkBuildURI (String content) {
		logger.debug("check build URI:["+content+"]");
		URI test = null;
		try {
			test = new URI(content);
		} catch (URISyntaxException e) {
			logger.debug(" Cant build URI given value:["+content+"]!!");
		}
		assertNotNull("URI is ok:["+content+"]", test);
	}
	
	private static void checkXMLOutput (XMLSerializableObject obj, String expectedOut) {
		logger.debug("XML output:["+obj.toXMLString()+"]");
		logger.debug("  expected:["+expectedOut+"]");
		assertEquals("XML output as expected", expectedOut, obj.toXMLString());
	}
	

}
