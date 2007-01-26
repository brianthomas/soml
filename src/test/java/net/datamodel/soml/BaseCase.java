/**
 * 
 */
package net.datamodel.soml;

import junit.framework.TestCase;
import net.datamodel.soml.core.URNImpl;

import org.apache.log4j.PropertyConfigurator;

/**
 * @author thomas
 *
 */
public class BaseCase  extends TestCase {
	
	protected URN urn1 = null;
	protected URN urn2 = null;
	protected URN rel_urn = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
//		 make sure log4j.props are loaded
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
		
		// initialize URNs for tests
		try {
			urn1 = new URNImpl("urn:SemanticObject1");
			urn2 = new URNImpl("urn:SemanticObject2");
			rel_urn = new URNImpl("urn:rel1");
		} catch (Exception e) {
			fail ("Test Setup Error:"+e.getMessage());
			throw e;
		}
		
	}
	

}
