/**
 * 
 */
package net.datamodel.soml.core;

import net.datamodel.soml.BaseCase;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.URN;
import net.datamodel.soml.UnmixedSemanticObjectList;

import org.apache.log4j.Logger;

/**
 * @author thomas
 *
 */
public class TestAPI extends BaseCase {

	private static final Logger logger = Logger.getLogger(TestAPI.class);

	// test creation of SemnaticObject, UnmixedSemanticObjectList
	//
	public void test1() {
		
		logger.info("Check we can construct main classes.");
		
		SemanticObject so = new SemanticObjectImpl(urn1);
		assertNotNull("Can build SemanticObjectImpl", so);
		
		UnmixedSemanticObjectList unlist = new UnmixedSemanticObjectListImpl(urn1);
		assertNotNull("Can build UnmixedSemanticObjectListImpl", unlist);
		
	}
	
	// test SO methods.
	//
	public void test2() {
		logger.info("Check inhertance of so methods working right.");
		
		SemanticObject so = new SemanticObjectImpl(urn1);
		
		SemanticObject so2 = new SemanticObjectImpl(urn2);
		so.addRelationship(so2, rel_urn);
		
		SemanticObject check = so.getRelatedSemanticObject(rel_urn);
		assertTrue("proper object returned from relationship",check == so2);
		
	}
	
	// test SO serialization
	//
	public void test3() {
		logger.info("Check inhertance of so serialization (minimal).");

	}
	
	// test UL methods
	//
	public void test4() {
		logger.info("Check ui methods.");
		
		// test setting a number of different good URN patterns here
		
		// test setting a number of BAD patterns here
		
	}
	
	// test UL serialization
	//
	public void test5() {
		logger.info("Check ul serialization.");
		
	}
	
	// test inheritance of so/ul
	//
	public void test6() {
		logger.info("Check inhertance of so/ul classes.");
		
		// test methods
		// test serialization
		
	}
	
}
