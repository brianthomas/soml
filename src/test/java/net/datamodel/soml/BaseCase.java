
package net.datamodel.soml;

import java.net.URI;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

/**
 * @author thomas
 *
 */
public class BaseCase  extends TestCase {
	
	protected URI uri1 = null;
	protected URI uri2 = null;
	protected URI uri3 = null;
	protected URI uri4 = null;
	protected URI rel_URI = null;
	protected URI rel_URI2 = null;
	protected URI rel_URI3 = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
//		 make sure log4j.props are loaded
		PropertyConfigurator.configure("src/test/resources/log4j.properties");
		
		// initialize URIs for tests
		try {
			uri1 = new URI("urn:test:SemanticObject1");
			uri2 = new URI("urn:test:SemanticObject2");
			uri3 = new URI("urn:test:SemanticObject3");
			uri4 = new URI("urn:test:SemanticObject4");
			rel_URI = new URI("urn:test:rel1");
			rel_URI2 = new URI("urn:test:rel2");
			rel_URI3 = new URI("urn:test:rel3");
		} catch (Exception e) {
			fail ("Test Setup Error:"+e.getMessage());
			throw e;
		}
		
	}
	

}
