
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
import net.datamodel.xssp.XMLFieldType;
import net.datamodel.xssp.impl.AbstractReferenceableXMLSerializableObject;
import net.datamodel.xssp.impl.AbstractXMLSerializableObjectList;

import org.apache.log4j.Logger;

/** A SemanticObject identifies its origin (and semantic nature) by its 
 * URI (Unique Resource Id).
 * A SemanticObject also may be in a (semantically-typed) relationship 
 * with other SemanticObjects. 
 */
public class SemanticObjectImpl 
extends AbstractReferenceableXMLSerializableObject 
implements SemanticObject {

	private static final Logger logger = Logger.getLogger(SemanticObjectImpl.class);

	// Fields
	//
	private static final String relationshipFieldName = "relationship";
	private static final String uriFieldName = Constant.SOML_URI_ATTRIBUTE_NAME;

	// Constructors
	//

	/** Construct with a given URI.
	 * @throws NullPointerException if the passed URI value is null.
	 */
	public SemanticObjectImpl (URI uri) { 
		this();
		setURI(uri);
	}

	/** Construct with a default URI of "URI:unknown".
	 * Not meant for public consumption..
	 */
	protected SemanticObjectImpl () { 

		// configure the referencing fields/info 
		idRefFieldName = "soRefId"; 
		idFieldName = "soId"; 
		xmlReferenceNodeName = "semanticObjectRef";

		setXMLNodeName("semanticObject");

		// now initialize XML fields
		// order matters! these are in *reverse* order of their
		// occurence in the schema/DTD
		addField(uriFieldName, "URI:unknown", XMLFieldType.ATTRIBUTE);
		addField(relationshipFieldName, new RelationshipList(), XMLFieldType.CHILD);

	}

	// Accessor Methods

	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.qml.SemanticObject#addRELATIONSHIP(net.datamodel.qml.SemanticObject, java.net.URI)
	 */ 
	public final boolean addProperty (SemanticObject target, URI relationURI) 
	throws IllegalArgumentException, NullPointerException 
	{

		// check if we have a non-null object to relate to.
		if (null == target)
		{
			throw new NullPointerException("addRelationship: passed null object.");
		}

		if (hasProperty(relationURI, target.getURI()))
		{
			throw new IllegalArgumentException("Already has a property with the combination prop uri:"+
					relationURI.toASCIIString()+" target uri:"+target.getURI().toASCIIString());
		}
			
		// now we add the relationship and return success 
		return getProperties().add(new ObjectPropertyImpl(relationURI, target));

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
				if (target.getURI().equals(targetURI)) {
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
	 * @see net.datamodel.soml.SemanticObject#clearAllRelationships()
	 */
	public final void removeAllProperties() {
		getProperties().clear();
	}

	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.qml.SemanticObject#removeRELATIONSHIP(java.net.URI)
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
			success = false; // there are no relationships!! 
		return success;
	}

	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.qml.SemanticObject#getURI()
	 */
	public final URI getURI() {
		String value = (String) getFieldValue(uriFieldName);

		try {
			return new URI (value);
		} catch (Exception e) {
			logger.error("Invalid URI for object returned.:"+e.getMessage());
			return (URI) null; // shouldnt happen as we only let valid URIs in..
		}
	}


	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#getRelatedSemanticObjects(net.datamodel.soml.URI)
	 */
	public final List<SemanticObject> getSemanticObjects (URI propertyURI) {

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
	 * @see net.datamodel.soml.SemanticObject#getRelationships(net.datamodel.soml.URI)
	 */
	public final List<Property> getProperties (URI URI) 
	{
		List<Property> found = new Vector<Property>();
		for (Property rel : getProperties()) {
			if (rel.getURI().equals(URI))
				found.add(rel);
		}
		return found; 
	}

	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#getRelationships()
	 */
	public final List<Property> getProperties() {
		return (List<Property>) getFieldValue(relationshipFieldName);
	}

	// Operations
	//

	/** Set the URI, representing the semantic meaning, of this object.
	 * 
	 * @param value of the URI to set
	 */
	protected final void setURI (URI value) {
		// Take the URI and convert it to a string for storage 
		// in object/serialization. If the URI is null, then 
		// toASCIIString will throw nullpointer exception for us.
		setFieldValue(uriFieldName, value.toASCIIString());
	}

	/** Quick internal class to hold all relationships between our object 
	 * and other SO's. 
	 */
	class RelationshipList<Relationship> 
	extends AbstractXMLSerializableObjectList
	{ 
		// simply change the node name to "relationship"
		// and set no serialization when its empty 
		RelationshipList() { 
			super(""); // should *not* have a node name 
			this.setSerializeWhenEmpty(false);
		}

		@Override
		public String toString() {
			return this.getClass()+"@"+this.hashCode();
		}

	}

}

