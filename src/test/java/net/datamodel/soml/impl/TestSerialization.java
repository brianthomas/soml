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
import net.datamodel.soml.dom.SOMLDocument;
import net.datamodel.soml.dom.SOMLElement;
import net.datamodel.soml.dom.xerces2.SOMLDocumentImpl;

import net.datamodel.xssp.XMLSerializableObject;
import net.datamodel.xssp.dom.Specification;

import org.apache.log4j.Logger;

/**
 * @author thomas
 *
 */
public class TestSerialization 
extends BaseCase 
{

	private static final Logger logger = Logger.getLogger(TestSerialization.class);
	
	public void test1() {
		
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
		
		logger.debug(" uri1:"+uri1);
		SemanticObject so = new SemanticObjectImpl (uri1);
		SemanticObject so2 = new SemanticObjectImpl (uri2);
		logger.debug(" REL_URI is:"+rel_URI);
		so2.addProperty(rel_URI, so); 
		
		// test non-pretty output 
		checkXMLOutput(so,"<a:semanticObject xmlns:b=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:a=\"urn:test#\" soId=\"id0\"><b:type resource=\"urn:test#SemanticObject1\"/></a:semanticObject>");
		/*
		checkXMLOutput(so2,"<semanticObject soId=\"id0\">"+
				"<c:test_rel1>"+
				"<semanticObject soId=\"id1\">"+
				"<a:type resource=\"urn:test#SemanticObject1\"/>" +
				"</semanticObject>" +
				"</c:test_rel1>"+
				"<a:type resource=\"urn:test#SemanticObject2\"/>" +
				"</semanticObject>");
		checkDocumentRepresentation(so);
		
		// test pretty output 
		spec.setPrettyOutput(true);
		
		checkXMLOutput(so,"<semanticObject URI=\"urn:test#SemanticObject1\" soId=\"id1\"/>");
		checkDocumentRepresentation(so);
		*/
		
		logger.warn("missing pretty-print serialization test for so2");
		/*
		// TODO
		String indent = spec.getPrettyOutputIndentation();
		String newLine = System.getProperty("line.separator");
		checkXMLOutput(so2,
				"<semanticObject URI=\"urn:test#SemanticObject2\" soId=\"id0\">"+newLine
				+indent+"<property URI=\"urn:test:rel1\">"+newLine
				+indent+indent+"<semanticObject URI=\"urn:test#SemanticObject1\" soId=\"id1\"/>"+newLine
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
	
	// test Inter-referential properties
	//
	/*
	public void test4() {
		
		logger.info("test serialization of Inter-referential properties"); 
		
		// first create some test objects...
		SemanticObject so1 = new SemanticObjectImpl(uri1);
		SemanticObject so2 = new SemanticObjectImpl(uri2);
		
		so1.addProperty(rel_URI, so2);
		so2.addProperty(rel_URI2, so1);
		
		// pretty output is more rigourous test
		Specification.getInstance().setPrettyOutput(true);
		String indent = Specification.getInstance().getPrettyOutputIndentation();
		String newLine = System.getProperty("line.separator");
		String expectedOutput1 = 
			"<semanticObject soId=\"id0\">"+newLine+ 
			indent+"<c:test_rel1>"+newLine+ 
			indent+indent+"<semanticObject soId=\"id1\">"+newLine+ 
			indent+indent+indent+"<c:test_rel2>"+newLine+ 
			indent+indent+indent+indent+"<semanticObjectRef soRefId=\"id0\"/>"+newLine+ 
			indent+indent+indent+"</c:test_rel2>"+newLine+
			indent+indent+indent+"<a:type resource=\"urn:test#SemanticObject2\"/>" +newLine+ 
			indent+indent+"</semanticObject>"+newLine+
			indent+"</c:test_rel1>"+newLine+
			indent+"<a:type resource=\"urn:test#SemanticObject1\"/>"+newLine+ 
			"</semanticObject>";
		
		String expectedOutput2 = 
			"<semanticObject URI=\"urn:test#SemanticObject2\" soId=\"id1\">"+newLine+ 
			indent+"<test_rel2>"+newLine+ 
			indent+indent+"<semanticObject URI=\"urn:test#SemanticObject1\" soId=\"id0\">"+newLine+ 
			indent+indent+indent+"<test_rel1>"+newLine+ 
			indent+indent+indent+indent+"<semanticObjectRef soRefId=\"id1\"/>"+newLine+ 
			indent+indent+indent+"</test_rel1>"+newLine+
			indent+indent+"</semanticObject>"+newLine+
			indent+"</test_rel2>"+newLine+
			"</semanticObject>";
		
		checkXMLOutput(so1,expectedOutput1);
		checkXMLOutput(so2,expectedOutput2);
		
		// FIXME: get stackoverflow error on this
		logger.warn("Fix doc representation tests for inter-referential test");
//		checkDocumentRepresentation(so1);
//		checkDocumentRepresentation(so2);

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
		
		boolean success = false;
		try {
			Specification.getInstance().setPrettyOutput(true);
			logger.error("run test");
			UtilityForTests.checkValidXMLRepresentation(doc); 
			Specification.getInstance().setPrettyOutput(false);
			UtilityForTests.checkValidXMLRepresentation(doc); 
			success = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		assertTrue("check doc representation OK", success);
		
		// back to way it was..
		selem.removeAttribute("xsi:schemaLocation");
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
