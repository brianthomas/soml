
package net.datamodel.soml.core;

import java.util.List;

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
		List<SemanticObject> check1 = so.getRelatedSemanticObjects(rel_urn);
		List<SemanticObject> check2 = so2.getRelatedSemanticObjects(rel_urn2);
		this.assertEquals("Object 1: number of objs in relationship:"+rel_urn.toAsciiString()+" is correct", 2, check1.size()); 
		this.assertEquals("Object 2: number of objs in relationship:"+rel_urn2.toAsciiString()+" is correct", 1, check2.size()); 
		
		assertTrue("proper object returned from relationship (obj1->obj3, rel_urn)",check1.get(1) == so3);
		assertTrue("proper object returned from relationship (obj2->obj3, rel_urn2)",check2.get(0) == so3);
	
		// check that getRelationships for both objs returns a relationship 
		// which points to  the expected object
		SemanticObject check3 = ((Relationship) so2.getRelationships().get(0)).getTarget();
		SemanticObject check4 = ((Relationship) so.getRelationships().get(0)).getTarget();
		assertTrue("getRelationships returns relationship pointing to right obj", check4 == so2);
		assertTrue("getRelationships returns relationship pointing to right obj", check3 == so);
		
		// RemoveRelationship(urn, target) tests
		assertTrue("can remove specific relationships with given URN, target", so.removeRelationship(rel_urn, so2)); 
		this.assertEquals("After removal Object 1: number of objs in relationship:"+rel_urn.toAsciiString()+" is correct", 1, so.getRelationships(rel_urn).size()); 
		// but the objects pointed to still have their relationships, even if
		// the urn of the relationship is the same.
		this.assertEquals("After removal Object 2: number of objs in relationship:"+rel_urn.toAsciiString()+" is correct", 1, so2.getRelationships(rel_urn).size()); 
		this.assertEquals("After removal Object 3: number of objs in relationship:"+rel_urn.toAsciiString()+" is correct", 1, so3.getRelationships(rel_urn).size()); 
		
		// removeAllRelationships tests. 
		assertTrue("can remove all relationships with given URN", so.removeAllRelationships(rel_urn)); 
		this.assertEquals("After removal Object 1: number of objs in relationship:"+rel_urn.toAsciiString()+" is correct", 0, so.getRelationships(rel_urn).size()); 
		// but the objects pointed to still have their relationships, even if
		// the urn of the relationship is the same.
		this.assertEquals("After removal Object 2: number of objs in relationship:"+rel_urn.toAsciiString()+" is correct", 1, so2.getRelationships(rel_urn).size()); 
		this.assertEquals("After removal Object 3: number of objs in relationship:"+rel_urn.toAsciiString()+" is correct", 1, so3.getRelationships(rel_urn).size()); 
		
		// test a BAD addRelationship (catch error) 
		try {
				so.addRelationship(so4, rel_urn3); 
				fail ("Can't add already added rel-SO should throw error.");
		} catch (IllegalArgumentException e) {
			assertTrue("Throws error correctly for bad addRelationship", true);
		}
		
		// test a BAD removeRelationship (catch error) 
		assertTrue("Should fail to remove rel_urn2 from Obj1",!so.removeAllRelationships(rel_urn2));
		assertTrue("Should fail to remove rel_urn from Obj1 for Obj2 (already removed)",!so.removeRelationship(rel_urn, so2));
		
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
