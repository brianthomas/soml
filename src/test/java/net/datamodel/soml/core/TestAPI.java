
package net.datamodel.soml.core;

import java.net.URI;
import java.util.List;

import net.datamodel.soml.BaseCase;
import net.datamodel.soml.Relationship;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.UnmixedSemanticObjectList;
import net.datamodel.soml.impl.SemanticObjectImpl;
import net.datamodel.soml.impl.UnmixedSemanticObjectListImpl;

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
	// note we don't test relationship construction as that class has package accessability
	// but we should test relationships via a SO in a later test..
	//
	public void test1() {
		
		logger.info("Check we can construct main classes.");
		
		SemanticObject so = new SemanticObjectImpl(uri1);
		assertNotNull("Can build SemanticObjectImpl", so);
		
		UnmixedSemanticObjectList unlist = new UnmixedSemanticObjectListImpl(uri1);
		assertNotNull("Can build UnmixedSemanticObjectListImpl", unlist);
		
		// try to set null URI in SO 
		boolean nullProhibited = false;
		try {
			SemanticObject so_null = new SemanticObjectImpl (null);
		} catch (NullPointerException e ) {
			nullProhibited = true;
		}
		assertTrue("Not allowed to pass null URI value in SO constructor!", nullProhibited); 
		
		// try to set null URI in UnmixedList
		nullProhibited = false;
		try {
			UnmixedSemanticObjectList unlist_null = new UnmixedSemanticObjectListImpl(null);
		} catch (NullPointerException e) {
			nullProhibited = true;
		}
		assertTrue("Not allowed to pass null URI value in UnmixedSemanticObjectList constructor!", nullProhibited); 
		
	}
	
	// test SO methods.
	//
	public void test2() {
		logger.info("Check so/relationship methods working right.");
		
		// test addRelationship
		// add several objs to make it interesting 
		SemanticObject so = new SemanticObjectImpl(uri1);
		SemanticObject so2 = new SemanticObjectImpl(uri2);
		SemanticObject so3 = new SemanticObjectImpl(uri3);
		SemanticObject so4 = new SemanticObjectImpl(uri4);
		
		// check URI retrieval
		assertTrue("SO retURIs the correct URI", so.getURI().equals(uri1));
		
		// bi-directional between obj1 and obj2
		assertTrue("can addRelationship obj1 to obj2", so.addRelationship(so2, rel_URI));
		assertTrue("can addRelationship obj2 to obj1", so2.addRelationship(so, rel_URI));
		// the *same* type of bi-directional rel between obj1 and obj3
		assertTrue("can addRelationship obj1 to obj3", so.addRelationship(so3, rel_URI));
		assertTrue("can addRelationship obj3 to obj1", so3.addRelationship(so, rel_URI));
		// bi-directional between obj2 and obj3
		assertTrue("can addrelationship obj3 to obj2", so3.addRelationship(so2, rel_URI2));
		assertTrue("can addrelationship obj2 to obj3", so2.addRelationship(so3, rel_URI2));
		// uni-directional, only obj1 is aware of relationship to obj4 
		assertTrue("can addrelationship obj1 to obj4", so.addRelationship(so4, rel_URI3)); 
		
		// check we have correct number of relationships in both objects now
		assertTrue("SO1 has the correct number of relationships",so.getRelationships().size() == 3);
		assertTrue("SO2 has the correct number of relationships",so2.getRelationships().size() == 2);
		assertTrue("SO3 has the correct number of relationships",so3.getRelationships().size() == 2);
		assertTrue("SO4 has the correct number of relationships",so4.getRelationships().size() == 0);
		
		// check that the 1 object is the one we expect
		List<SemanticObject> check1 = so.getRelatedSemanticObjects(rel_URI);
		List<SemanticObject> check2 = so2.getRelatedSemanticObjects(rel_URI2);
		this.assertEquals("Object 1: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 2, check1.size()); 
		this.assertEquals("Object 2: number of objs in relationship:"+rel_URI2.toASCIIString()+" is correct", 1, check2.size()); 
		
		assertTrue("proper object retURIed from relationship (obj1->obj3, rel_URI)",check1.get(1) == so3);
		assertTrue("proper object retURIed from relationship (obj2->obj3, rel_URI2)",check2.get(0) == so3);
	
		// check that getRelationships for both objs retURIs a relationship 
		// which points to  the expected object
		SemanticObject check3 = ((Relationship) so2.getRelationships().get(0)).getTarget();
		SemanticObject check4 = ((Relationship) so.getRelationships().get(0)).getTarget();
		assertTrue("getRelationships retURIs relationship pointing to right obj", check4 == so2);
		assertTrue("getRelationships retURIs relationship pointing to right obj", check3 == so);
		
		// RemoveRelationship(URI, target) tests
		assertTrue("can remove specific relationships with given URI, target", so.removeRelationship(rel_URI, so2)); 
		this.assertEquals("After removal Object 1: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 1, so.getRelationships(rel_URI).size()); 
		// but the objects pointed to still have their relationships, even if
		// the URI of the relationship is the same.
		this.assertEquals("After removal Object 2: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 1, so2.getRelationships(rel_URI).size()); 
		this.assertEquals("After removal Object 3: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 1, so3.getRelationships(rel_URI).size()); 
		
		// removeAllRelationships tests. 
		assertTrue("can remove all relationships with given URI", so.removeAllRelationships(rel_URI)); 
		this.assertEquals("After removal Object 1: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 0, so.getRelationships(rel_URI).size()); 
		// but the objects pointed to still have their relationships, even if
		// the URI of the relationship is the same.
		this.assertEquals("After removal Object 2: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 1, so2.getRelationships(rel_URI).size()); 
		this.assertEquals("After removal Object 3: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 1, so3.getRelationships(rel_URI).size()); 
		
		// test a BAD addRelationship (catch error) 
		boolean badRelProhibited = false;
		try {
			so.addRelationship(so4, rel_URI3); 
		} catch (IllegalArgumentException e) {
			badRelProhibited = true;
		}
		assertTrue("Throws error correctly for bad addRelationship", badRelProhibited);
		
		// test a BAD removeRelationship (catch error) 
		assertTrue("Should fail to remove rel_URI2 from Obj1",!so.removeAllRelationships(rel_URI2));
		assertTrue("Should fail to remove rel_URI from Obj1 for Obj2 (already removed)",!so.removeRelationship(rel_URI, so2));
		
		// Method checks not yet implemented..
		//
		/*
		so.clone();
		so.equals();
		so.findChildObjs();
		so.getId();
		so.getURI();
		so.getNamespaceURI();
		so.getRelatedSemanticObjects(relatinoshipURI);
		so.getRelationships();
		so.getXMLNodeName();
		so.removeAllRelationships(uri);
		so.removeRelationship(uri, target);
		so.setId(value);
		so.serializeWhenEmpty();
		*/
		
	}

	// test UnmixedList methods
	//
	public void test3() {
		logger.info("Check unmixed list methods.");
		
		// first create some test objects...
		UnmixedSemanticObjectList soList1 = new UnmixedSemanticObjectListImpl(uri1);
		UnmixedSemanticObjectList soList2 = new UnmixedSemanticObjectListImpl(uri1);
		NamespacedList soList3 = new NamespacedList(uri2);
		UnmixedSemanticObjectList soList4 = new UnmixedSemanticObjectListImpl(uri3);
		SemanticObject so1 = new SemanticObjectImpl(uri1);
		SemanticObject so2 = new SemanticObjectImpl(uri1);
		SemanticObject so3 = new SemanticObjectImpl(uri1);
		SemanticObject so4 = new SemanticObjectImpl(uri1);
		SemanticObject so5 = new SemanticObjectImpl(uri2);
		SemanticObject so6 = new SemanticObjectImpl(uri2);
		
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
		soList2.add(0, so5); // should be NOT added, wrong URI 
		assertTrue("Can NOT append non-kosher object to list", !soList2.add(so5));
		assertTrue("List2 has right number of objects", soList2.size() == 2); 
		
		soList3.add(so5); 
		soList3.add(so6);
		assertTrue("List3 has right number of objects", soList3.size() == 2); 
		
		// try addAll
		soList1.addAll(soList2);
		assertTrue("List1 has right number of objects", soList1.size() == 4); 
		assertTrue("Can NOT addAll non-kosher objects to list", !soList1.addAll(soList3));
		
		// check getURI
		assertTrue("List1 retURI the correct URI", soList1.getURI().equals(uri1));
		
		// check contains
		assertTrue("check contains method", soList1.contains(so2));
		assertTrue("check containsAll method", soList1.containsAll(soList2)); 
		
		// try to clone
		boolean can_clone = true;
		UnmixedSemanticObjectList cloneList = null;
		try {
			cloneList = (UnmixedSemanticObjectList) soList1.clone();
		} catch (CloneNotSupportedException e) {
			can_clone = false;
		}
		assertTrue("Can clone unmixedList", can_clone);
		
		// check contents, and equals method
		logger.debug("Clone uri is:"+cloneList.getURI().toASCIIString());
		assertTrue("Clone is equals to parent", soList1.equals(cloneList));
		
		// indexing
		assertEquals("item is at correct index", 0, soList1.indexOf(so2)); 
		
		// from the XSSP package?
		assertEquals(" namespaced list has correct URI string value", 
				soList3.nameURIString, soList3.getNamespaceURI());
		
		assertTrue("list is not empty", !soList1.isEmpty());
		assertTrue("list is not empty", !soList2.isEmpty());
		assertTrue("list is empty", soList4.isEmpty());
		
		List subList = soList1.subList(2, 4);
		assertEquals("Sublist returns equivalent added list", soList2, subList);

		soList1.remove(0);
		assertEquals("List has 3 obj after index remove", 3, soList1.size());
		assertEquals("List has coorect obj at 1st index after remove", so1, soList1.get(0));
		
		soList1.remove(so1);
		assertEquals("List has 2 obj after object remove", 2, soList1.size());
		assertEquals("List has coorect obj at 1st index after remove", so4, soList1.get(0));
		
		soList1.removeAll(soList2);
		assertEquals("List has 0 obj after collection remove", 0, soList1.size());
		
		soList1.add(so1);
		soList1.addAll(soList2);
		soList1.retainAll(soList2);
		assertEquals("List has so1 obj after retained remove", so4, soList1.get(0));
		
		soList2.clear();
		assertTrue("list is empty after a clear", soList2.isEmpty());
		
		/*
		soList1.findChildObjs();
		so.serializeWhenEmpty();
		*/
		
	}
	
	// test Relationship methods
	//
	public void test4() {
		logger.info("Check Relationship methods.");
		// TODO: test setting a number of different good URI patterns here
		// TODO: test setting a number of BAD patterns here (incl. null)
		
		// methods..
		// URI.clone(), URI.equals
	}
	
	class NamespacedList extends UnmixedSemanticObjectListImpl {
		
		public String nameURIString = "dude";
		
		NamespacedList (URI uri) {
			super(uri);
			setNamespaceURI(nameURIString);
		}
		
	}
}
