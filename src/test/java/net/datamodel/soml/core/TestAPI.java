
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
		
		// try to set null URN in SO 
		boolean nullProhibited = false;
		try {
			SemanticObject so_null = new SemanticObjectImpl (null);
		} catch (NullPointerException e ) {
			nullProhibited = true;
		}
		assertTrue("Not allowed to pass null URN value in SO constructor!", nullProhibited); 
		
		// try to set null URN in UnmixedList
		nullProhibited = false;
		try {
			UnmixedSemanticObjectList unlist_null = new UnmixedSemanticObjectListImpl(null);
		} catch (NullPointerException e) {
			nullProhibited = true;
		}
		assertTrue("Not allowed to pass null URN value in UnmixedSemanticObjectList constructor!", nullProhibited); 
		
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
		
		// check URN retrieval
		assertTrue("SO returns the correct URN", so.getURN().equals(urn1));
		
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
		boolean badRelProhibited = false;
		try {
			so.addRelationship(so4, rel_urn3); 
		} catch (IllegalArgumentException e) {
			badRelProhibited = true;
		}
		assertTrue("Throws error correctly for bad addRelationship", badRelProhibited);
		
		// test a BAD removeRelationship (catch error) 
		assertTrue("Should fail to remove rel_urn2 from Obj1",!so.removeAllRelationships(rel_urn2));
		assertTrue("Should fail to remove rel_urn from Obj1 for Obj2 (already removed)",!so.removeRelationship(rel_urn, so2));
		
	}

	// test URN methods
	//
	public void test3() {
		logger.info("Check URN methods.");
		// test setting a number of different good URN patterns here
		// test setting a number of BAD patterns here
	}
	
	// test UnmixedList methods
	//
	public void test4() {
		logger.info("Check unmixed list methods.");
		
		// first create some test objects...
		UnmixedSemanticObjectList soList1 = new UnmixedSemanticObjectListImpl(urn1);
		UnmixedSemanticObjectList soList2 = new UnmixedSemanticObjectListImpl(urn1);
		UnmixedSemanticObjectList soList3 = new UnmixedSemanticObjectListImpl(urn2);
		SemanticObject so1 = new SemanticObjectImpl(urn1);
		SemanticObject so2 = new SemanticObjectImpl(urn1);
		SemanticObject so3 = new SemanticObjectImpl(urn1);
		SemanticObject so4 = new SemanticObjectImpl(urn1);
		SemanticObject so5 = new SemanticObjectImpl(urn2);
		SemanticObject so6 = new SemanticObjectImpl(urn2);
		
		// test various methods
		// Note: we should only test those we override/implement in this package.
		//
		
		// adding objects in various ways, checking sizes
		//
		assertTrue("Can append kosher object to list", soList1.add(so1));
		// try to insert kosher object to list
		soList1.add(0, so2);
		assertTrue("List1 has right number of objects", soList1.size() == 2); 
		
		assertTrue("Can append kosher object to list", soList2.add(so3));
		// try to insert kosher object to list
		soList2.add(0, so4);
		soList2.add(0, so5); // should be NOT added, wrong URN 
		assertTrue("Can NOT append non-kosher object to list", !soList2.add(so5));
		assertTrue("List2 has right number of objects", soList2.size() == 2); 
		
		soList3.add(so5); 
		soList3.add(so6);
		assertTrue("List3 has right number of objects", soList3.size() == 2); 
		
		// try addAll
		soList1.addAll(soList2);
		assertTrue("List1 has right number of objects", soList1.size() == 4); 
		assertTrue("Can NOT addAll non-kosher objects to list", !soList1.addAll(soList3));
		
		// check getURN
		assertTrue("List1 return the correct URN", soList1.getURN().equals(urn1));
		
		/*
		soList1.clear();
		soList1.clone();
		soList1.contains(object);
		soList1.containsAll(collection); 
		soList1.equals(object);
		soList1.findChildObjs();
		soList1.getNamespaceURI();
		soList1.indexOf(object); 
		soList1.isEmpty();
		soList1.remove(index);
		soList1.remove(object);
		soList1.removeAll(collection);
		soList1.retainAll(collection);
		soList1.subList(fromIndex, toIndex);
		*/
		
	}
	
	// test inheritance of so/ul
	//
	public void test5() {
		logger.info("Check inhertance of so/ul classes.");
		// test methods
	}
	
}
