
package net.datamodel.soml;

import java.net.URI;
import java.util.List;

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
		
		SemanticObject so0 = new SemanticObjectImpl();
		assertNotNull("Can build SemanticObjectImpl", so0);
		
		SemanticObject so = new SemanticObjectImpl(uri1);
		assertNotNull("Can build SemanticObjectImpl", so);
		
		logger.debug("URN1:"+uri1);
		UnmixedSemanticObjectList unlist = new UnmixedSemanticObjectListImpl(uri1);
		assertNotNull("Can build UnmixedSemanticObjectListImpl", unlist);
		
		// try to set null URI in SO 
		boolean nullProhibited = false;
		try {
			SemanticObject so_null = new SemanticObjectImpl (null);
		} catch (NullPointerException e ) {
			nullProhibited = true;
		}
		assertTrue("Not Allowed to pass null URI value in SO constructor!", nullProhibited); 
		
		// try to set null URI in UnmixedList
		nullProhibited = false ;
		try {
			new UnmixedSemanticObjectListImpl(null);
		} catch (NullPointerException e) {
			nullProhibited = true;
		}
		assertTrue("Not allowed to pass null URI value in UnmixedSemanticObjectList constructor!", nullProhibited); 
		
	}
	
	// test SO, relationship methods.
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
		assertTrue("SO retURIs the correct URI", so.getRDFTypeURI().equals(uri1));
		
		// bi-directional between obj1 and obj2
		assertTrue("can addRelationship obj1 to obj2", so.addProperty(rel_URI,so2));
		assertTrue("can addRelationship obj2 to obj1", so2.addProperty(rel_URI,so));
		// the *same* type of bi-directional rel between obj1 and obj3
		assertTrue("can addRelationship obj1 to obj3", so.addProperty(rel_URI, so3));
		assertTrue("can addRelationship obj3 to obj1", so3.addProperty(rel_URI, so));
		// bi-directional between obj2 and obj3
		assertTrue("can addrelationship obj3 to obj2", so3.addProperty(rel_URI2, so2));
		assertTrue("can addrelationship obj2 to obj3", so2.addProperty(rel_URI2, so3));
		// uni-directional, only obj1 is aware of relationship to obj4 
		assertTrue("can addrelationship obj1 to obj4", so.addProperty(rel_URI3, so4)); 

		/*  // test no longer true 
		// test a BAD addRelationship (catch error for re-adding duplicate propURI/targetURI combination) 
		boolean badRelProhibited = false;
		try {
			so.addProperty(rel_URI3, so4); 
		} catch (IllegalArgumentException e) {
			badRelProhibited = true;
		}
		assertTrue("Throws error correctly for bad addRelationship", badRelProhibited);
		*/
		
		// check we have correct number of relationships in both objects now
		assertTrue("SO1 has the correct number of relationships",so.getProperties().size() == 3);
		assertTrue("SO2 has the correct number of relationships",so2.getProperties().size() == 2);
		assertTrue("SO3 has the correct number of relationships",so3.getProperties().size() == 2);
		assertTrue("SO4 has the correct number of relationships",so4.getProperties().size() == 0);
		
		List<Property> relList = so.getProperties(rel_URI); 
		Property rel1 = relList.get(0);
		assertEquals("Property URI is expected one", rel_URI, rel1.getURI());
		assertTrue("Property is an object property", rel1 instanceof ObjectProperty); 
		assertEquals("Property target is expected one", so2, ((ObjectProperty) rel1).getTarget());
		
		// check that the 1 object is the one we expect
		List<SemanticObject> check1 = so.getSemanticObjectsByType(rel_URI);
		List<SemanticObject> check2 = so2.getSemanticObjectsByType(rel_URI2);
		this.assertEquals("Object 1: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 2, check1.size()); 
		this.assertEquals("Object 2: number of objs in relationship:"+rel_URI2.toASCIIString()+" is correct", 1, check2.size()); 
		
		assertTrue("proper object retURIed from relationship (obj1->obj3, rel_URI)",check1.get(1) == so3);
		assertTrue("proper object retURIed from relationship (obj2->obj3, rel_URI2)",check2.get(0) == so3);
	
		// check that getRelationships for both objs retURIs a relationship 
		// which points to  the expected object
		SemanticObject check3 = ((ObjectProperty) so2.getProperties().get(0)).getTarget();
		SemanticObject check4 = ((ObjectProperty) so.getProperties().get(0)).getTarget();
		assertTrue("getRelationships retURIs relationship pointing to right obj (and equals method)", check4.equals(so2));
		assertTrue("getRelationships retURIs relationship pointing to right obj", check3 == so);
		
		// RemoveRelationship(URI, target) tests
		assertTrue("can remove specific relationships with given URI, target", so.removeObjectProperty(rel_URI, so2)); 
		this.assertEquals("After removal Object 1: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 1, so.getProperties(rel_URI).size()); 
		// but the objects pointed to still have their relationships, even if
		// the URI of the relationship is the same.
		this.assertEquals("After removal Object 2: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 1, so2.getProperties(rel_URI).size()); 
		this.assertEquals("After removal Object 3: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 1, so3.getProperties(rel_URI).size()); 
		
		// removeAllRelationships tests. 
		assertTrue("can remove all relationships with given URI", so.removeAllProperties(rel_URI)); 
		this.assertEquals("After removal Object 1: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 0, so.getProperties(rel_URI).size()); 
		// but the objects pointed to still have their relationships, even if
		// the URI of the relationship is the same.
		this.assertEquals("After removal Object 2: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 1, so2.getProperties(rel_URI).size()); 
		this.assertEquals("After removal Object 3: number of objs in relationship:"+rel_URI.toASCIIString()+" is correct", 1, so3.getProperties(rel_URI).size()); 
		
		
		// test a BAD removeRelationship (catch error) 
		assertTrue("Should fail to remove rel_URI2 from Obj1", !so.removeAllProperties(rel_URI2));
		assertTrue("Should fail to remove rel_URI from Obj1 for Obj2 (already removed)",!so.removeObjectProperty(rel_URI, so2));
		
		so.removeAllProperties(rel_URI);
		// no objects left now
		for (Property r : so.getProperties()) {
			logger.debug(" **** SO Property uri:"+r.getURI().toASCIIString()+" target:"+((ObjectProperty)r).getTarget().getRDFTypeURI().toASCIIString());
		}
		assertEquals("Correct number of remaining relationships", 1, so.getProperties().size());
		
		so.removeAllProperties();
		assertEquals("Correct number of remaining relationships", 0, so.getProperties().size());
		
		// Method checks not yet implemented..
		//
		/*
		so.findChildObjs();
		so.getXMLNodeName();
		so.getNamespaceURI();
		so.getId();
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
		assertTrue("List1 retURI the correct URI", soList1.getRDFTypeURI().equals(uri1));
		
		// check contains
		assertTrue("check contains method", soList1.contains(so2));
		assertTrue("check containsAll method", soList1.containsAll(soList2)); 
		
		// try to clone
		/*
		UnmixedSemanticObjectList cloneList = (UnmixedSemanticObjectList) soList1.clone();
		assertEquals("Clone equivalent to original unmixedList", soList1, cloneList);
		
		// check contents, and equals method
		logger.debug("Clone uri is:"+cloneList.getURI().toASCIIString());
		assertTrue("Clone is equals to parent", soList1.equals(cloneList));
		*/
		
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
		soList1.serializeWhenEmpty();
		soList1.findChildObjs();
		soList1.getId();
		*/
		
	}
	
	
	class NamespacedList extends UnmixedSemanticObjectListImpl {
		
		public String nameURIString = "dude";
		
		NamespacedList (URI uri) {
			super(uri);
			setNamespaceURI(nameURIString);
		}
		
	}
}
