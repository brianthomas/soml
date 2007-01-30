
package net.datamodel.soml.core;

import net.datamodel.soml.BaseCase;
import net.datamodel.soml.Relationship;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.UnmixedSemanticObjectList;

import org.apache.log4j.Logger;

/**
 * Test suite for SemanticObject API. 
 * 
 * @author thomas
 *
 */
public class TestAPI extends BaseCase 
{

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
		logger.info("Check so/relationship methods working right.");
		
		// test addRelationship
		// add several objs to make it interesting 
		SemanticObject so = new SemanticObjectImpl(urn1);
		SemanticObject so2 = new SemanticObjectImpl(urn2);
		SemanticObject so3 = new SemanticObjectImpl(urn3);
		SemanticObject so4 = new SemanticObjectImpl(urn4);
		
		// bi-directional between obj1 and obj2
		assertTrue("can addRelationship obj1 to obj2", so.addRelationship(so2, rel_urn));
		assertTrue("can addRelationship obj2 to obj1", so2.addRelationship(so, rel_urn));
		// the *same* type of bi-directional rel between obj1 and obj3
		assertTrue("can addRelationship obj1 to obj3", so.addRelationship(so3, rel_urn));
		assertTrue("can addRelationship obj3 to obj1", so3.addRelationship(so, rel_urn));
		// bi-directional between obj2 and obj3
		assertTrue("can addrelationship obj3 to obj2", so3.addRelationship(so2, rel_urn2));
		assertTrue("can addrelationship obj2 to obj3", so2.addRelationship(so3, rel_urn2));
		// uni-directional, only obj1 is aware of relationship to obj4 
		assertTrue("can addrelationship obj1 to obj4", so.addRelationship(so4, rel_urn3)); 
		
		// check we have correct number of relationships in both objects now
		assertTrue("SO1 has the correct number of relationships",so.getRelationships().size() == 3);
		assertTrue("SO2 has the correct number of relationships",so2.getRelationships().size() == 2);
		assertTrue("SO3 has the correct number of relationships",so3.getRelationships().size() == 2);
		assertTrue("SO4 has the correct number of relationships",so4.getRelationships().size() == 0);
		
		// check that the 1 object is the one we expect
		SemanticObject check = so.getRelatedSemanticObject(rel_urn);
		assertTrue("proper object returned from relationship",check == so2);
		SemanticObject check1 = so2.getRelatedSemanticObject(rel_urn);
		assertTrue("proper object returned from relationship",check1 == so);
	
		// check that getRelationships for both objs returns a relationship 
		// which points to  the expected object
		SemanticObject check2 = ((Relationship) so.getRelationships().get(0)).getTarget();
		SemanticObject check3 = ((Relationship) so2.getRelationships().get(0)).getTarget();
		assertTrue("getRelationships returns relationship pointing to right obj",
				check2 == so2);
		assertTrue("getRelationships returns relationship pointing to right obj",
				check3 == so);
		
		// removeRelationship. 
		
		
		// test a BAD addRelationship (catch error) 
		
		// test a BAD removeRelationship (catch error) 
		
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
