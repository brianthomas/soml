
// CVS $Id$

// SemanticObjectImpl.java Copyright (c) 2006 Brian Thomas. All rights reserved.

/* LICENSE

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA

*/

/* AUTHOR

   Brian Thomas  (baba-luu@earthlink.net)
   

*/

// code generation timestamp: Tue Apr 20 2004-14:22:31 

package net.datamodel.soml.impl;

import java.net.URI;
import java.util.List;
import java.util.Vector;

import net.datamodel.soml.Constant;
import net.datamodel.soml.ObjectProperty;
import net.datamodel.soml.Property;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.Utility;
import net.datamodel.xssp.XMLFieldType;
import net.datamodel.xssp.impl.AbstractReferenceableXMLSerializableObject;
import net.datamodel.xssp.impl.AbstractXMLSerializableObject;
import net.datamodel.xssp.impl.AbstractXMLSerializableObjectList;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.vocabulary.RDF;

/** A SemanticObject identifies its origin (and semantic nature) by its 
 * URI (Unique Resource Id).
 * A SemanticObject also may be in a (semantically-typed) property 
 * with other SemanticObjects. 
 */
public class SemanticObjectImpl 
extends AbstractReferenceableXMLSerializableObject 
implements SemanticObject {

	public static final Logger logger = Logger.getLogger(SemanticObjectImpl.class);

	// Fields
	//
	private static final String propertyFieldName = "property";
	private static final String rdfTypesFieldName = Constant.SOML_RDFTYPE_FIELD_NAME;
	
	// Constructors
	//

	/** Construct vanilla SemanticObject with a default rdf:type URI 
	 * of "owl:Thing"
	 */
	public SemanticObjectImpl () { 
		this(Utility.createURI(Constant.SemanticObjectURI));
	}

	/** Construct vanilla SemanticObject with a given rdf:type URI.
	 * 
	 */
	public SemanticObjectImpl (URI rdfTypeUri) { 
		this(rdfTypeUri, Constant.SemanticObjectNodeName);
	}

	/** Construct SemanticObject with a given rdf:type URI, and
	 * dedicated xmlNodeName.   
	 * 
	 * @param rdfTypeUri
	 * @param xmlNodeName
	 */
	public SemanticObjectImpl (URI rdfTypeUri, String xmlNodeName) 
	{ 

		// configure the referencing fields/info 
		idRefFieldName = "soRefId"; 
		idFieldName = "soId"; 
		xmlReferenceNodeName = Constant.SemanticObjectRefIDNodeName;

		logger.debug(" SO gets rdf:type "+rdfTypeUri);
		
		String nsUri = Utility.getNamespaceURI(rdfTypeUri);
		logger.debug("Create new SemanticObjectImpl node:"+xmlNodeName+" ns_uri:"+nsUri);
		setXMLNodeName(xmlNodeName);
		setNamespaceURI(nsUri);

		// now initialize XML fields
		// order matters! these are in *reverse* order of their
		// occurence in the schema/DTD
		addField(propertyFieldName, new propertyList(), XMLFieldType.CHILD);
		addField(rdfTypesFieldName, new RDFTypeList(), XMLFieldType.CHILD);

		addRDFTypeURI(rdfTypeUri);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#addProperty(java.net.URI, net.datamodel.soml.SemanticObject)
	 */
	public final boolean addProperty (URI propertyURI, SemanticObject value)
	throws NullPointerException 
	{

		// check if we have a non-null object to relate to.
		if (null == value) {
			throw new NullPointerException("addProperty: passed null value object.");
		}

		// now we add the property and return success 
		return getProperties().add(new ObjectPropertyImpl(propertyURI, value));

	}
	
	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#addProperty(java.net.URI, java.lang.String)
	 */
	public final boolean addProperty(URI propertyURI, String value) {
		// add an xsd:string value
		return getProperties().add(new DataTypePropertyImpl(propertyURI, value));
	}

	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#addProperty(java.net.URI, java.net.URI, java.lang.String)
	 */
	public final boolean addProperty(URI propertyURI, URI dtURI, String value) {
		return getProperties().add(new DataTypePropertyImpl(propertyURI, dtURI, value));
	}
	
	/** Determine if the object has a property-target combination with
	 * the indicated URIs.
	 * 
	 * @param propertyURI
	 * @param targetURI
	 * @return
	 */
	public final boolean hasProperty (URI propertyURI, URI targetURI)
	{
		for (Property prop : getProperties(propertyURI)) {
			if (prop instanceof ObjectProperty) {
				SemanticObject target = ((ObjectProperty) prop).getTarget();
				if (target.getRDFTypeURIs().equals(targetURI)) {
					return true;
				}
			}
		}
		return false;
	}
			
	public final boolean removeObjectProperty(URI URI, SemanticObject target) {
		List<Property> removeList = getProperties();
		for (Property test : removeList) {
			if (test instanceof ObjectProperty 
					&& ((ObjectProperty)test).getTarget() == target) 
			{
				return getProperties().remove(test); // there should only be one, so return now  
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#clearAllpropertys()
	 */
	public final void removeAllProperties() {
		getProperties().clear();
	}

	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.qml.SemanticObject#removeproperty(java.net.URI)
	 */
	public final boolean removeAllProperties (URI URI) {
		boolean success = true;
		List<Property> testList = getProperties(URI);
		if (testList.size() > 0) {
			for (Property target : testList)
			{
				if (!getProperties().remove(target))
					success = false;
			}
		} else 
			success = false; // there are no propertys!! 
		return success;
	}

	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.qml.SemanticObject#getURI()
	 */
	public final List<URI> getRDFTypeURIs() {
		List<URI> uris = new Vector<URI>();
		for (RDFTypeURI type : this.getRDFTypes()) 
			uris.add(type.toURI());
		return uris;
	}


	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#getRelatedSemanticObjects(net.datamodel.soml.URI)
	 */
	public final List<SemanticObject> getSemanticObjectsByType (URI propertyURI) {

		List<SemanticObject> found = new Vector<SemanticObject>();
		for (Property rel: getProperties()) {
			logger.debug("rel:" + rel); 
			logger.debug("  URI :" + rel.getURI()); 
			if (null != rel && rel.getURI().equals(propertyURI)) {
				if (rel instanceof ObjectProperty)
				found.add(((ObjectProperty)rel).getTarget()); // matched, so add it to found list
			}
		}
		return found;
	}

	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#getpropertys(net.datamodel.soml.URI)
	 */
	public final List<Property> getProperties (URI URI) 
	{
		List<Property> found = new Vector<Property>();
		for (Property prop: getProperties()) {
			logger.debug(" property:"+prop);
			if (prop.getURI().equals(URI)) {
				found.add(prop);
			}
		}
		return found; 
	}

	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#getpropertys()
	 */
	public final List<Property> getProperties() {
		return (List<Property>) getFieldValue(propertyFieldName);
	}

	/** Add a URI, representing the semantic meaning (RDF type), 
	 * of this object.
	 * 
	 * @param value of the URI to set
	 */
	public final boolean addRDFTypeURI (URI value) {
		if (value == null)
			throw new NullPointerException("cant add null URI rdf type");
		if (this.hasRDFType(value)) {
			logger.warn("Cant addRDFTypeURI  uri:"+value.toASCIIString()+" is already contained in object");
			// already have that value, avoid duplicates
			return false;
		}
		logger.debug(" addRDFTypeURI "+value.toASCIIString());
		return getRDFTypes().add(new RDFTypeURI(value));
	}
	
	protected List<RDFTypeURI> getRDFTypes () {
		return (List<RDFTypeURI>) getFieldValue(rdfTypesFieldName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#hasRDFType(java.net.URI)
	 */
	public boolean hasRDFType (URI type) {
		for(RDFTypeURI rdftype : getRDFTypes()) {
			if (rdftype.toURI().equals(type))
				return true;
		}
		return false;
	}

	/** Remove an rdf type from the object.
	 * 
	 * @param value
	 * @return
	 */
	protected boolean removeRDFTypeURI (RDFTypeURI value) {
		return getRDFTypes().remove(value);
	}
	
	/** Quick internal class to hold all propertys between our object 
	 * and other SO's. 
	 */
	class propertyList<Property> 
	extends AbstractXMLSerializableObjectList
	{ 
		// simply change the node name to "property"
		// and set no serialization when its empty 
		propertyList() { 
			super(""); // should *not* have a node name 
			this.setSerializeWhenEmpty(false);
		}

		@Override
		public String toString() {
			return this.getClass()+"@"+this.hashCode();
		}

	}
	
	class RDFTypeList<RDFTypeURI> 
	extends AbstractXMLSerializableObjectList
	{ 
		// simply change the node name to "property"
		// and set no serialization when its empty 
		RDFTypeList() { 
			super(""); // should *not* have a node name 
			this.setSerializeWhenEmpty(false);
		}

		@Override
		public String toString() {
			return this.getClass()+"@"+this.hashCode();
		}

	}
	
	class RDFTypeURI 
	extends AbstractXMLSerializableObject
	{

		private URI myURI = null;
		
		public RDFTypeURI(URI uri)  {
			myURI = uri;
			setNamespaceURI(RDF.getURI());
			setXMLNodeName("type");
			addAttributeField("resource", myURI.toASCIIString());
			setSerializeWhenEmpty(false);
		}
		
		public final URI toURI() { return myURI; }

	}

	
}

