
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

package net.datamodel.soml.core;

import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import net.datamodel.soml.Relationship;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.URN;
import net.datamodel.xssp.XMLFieldType;
import net.datamodel.xssp.XMLSerializableField;
import net.datamodel.xssp.core.AbstractXMLSerializableObject;
import net.datamodel.xssp.core.AbstractXMLSerializableObjectList;
import net.datamodel.xssp.core.XMLSerializableFieldImpl;
import net.datamodel.xssp.support.Specification;
import net.datamodel.xssp.support.XMLReferenceSerializationType;

import org.apache.log4j.Logger;

/** A SemanticObject identifies its origin (and semantic nature) by its URN (Unique Resource Name).
 * A SemanticObject also may be in a SemanticRelationship with other SemanticObjects. 
 * SemanticRelationships include being properities of another SemanticObject.
 */
public class SemanticObjectImpl extends AbstractXMLSerializableObject 
implements SemanticObject {
	
	private static final Logger logger = Logger.getLogger(SemanticObjectImpl.class);

    // Fields
	private static final String RELATIONSHIP_XML_FIELD_NAME = "relationship";
    private static final String ID_XML_FIELD_NAME = "soid";
    private static final String URN_XML_FIELD_NAME = "urn";
    
    protected static final String ReferenceNodeName = "refNode";
    protected static final String IDRefAttributeName = "oidRef";
    
    // Methods
    //

    // Constructors

    // Construct with a given URN.
    public SemanticObjectImpl ( URN URN ) { 
       init();
       setURN(URN);
    }
    
    // The no-argument Constructor
    protected SemanticObjectImpl () { init(); }

    // Accessor Methods

    /**
     * The id of an instance of this class. It should be unique across all components and quantities within a given document/object tree.
     */
    public String getId () {
        // return (String) ((XMLSerializableField) getFields().get(ID_XML_FIELD_NAME)).getValue();
        return (String) ((XMLSerializableField) getFields().get(ID_XML_FIELD_NAME)).getValue();
    }

    /*
     *  (non-Javadoc)
     * @see net.datamodel.qml.SemanticObject#setId(java.lang.String)
     */ 
    public void setId ( String value  ) {
        ((XMLSerializableFieldImpl) getFields().get(ID_XML_FIELD_NAME)).setValue(value);
    }

    /*
     *  (non-Javadoc)
     * @see net.datamodel.qml.SemanticObject#addRELATIONSHIP(net.datamodel.qml.SemanticObject, java.net.URN)
     */ 
	public boolean addRelationship(SemanticObject relatedObj, URN relationURN) 
	throws IllegalArgumentException, NullPointerException 
    {

		// check if we have a non-null object to relate to.
		if (null == relatedObj)
		{
			throw new NullPointerException("addRelationship: passed null object.");
		}
		
		// check if the RELATIONSHIP already exists in this object
		if (null != getRelatedSemanticObject(relationURN))
		{
			throw new IllegalArgumentException("addRelationship: a relationship already exists with relationship URN:"+relationURN.toAsciiString());
		}
		
		// check if the RELATIONSHIP already exists in related object
		if (null != relatedObj.getRelatedSemanticObject(relationURN))
		{
			throw new IllegalArgumentException("addRelationship: a the requested relationship: "+
					relationURN.toAsciiString()+" already exists in the related object:"+relatedObj);
		}
		    
		// now we add this to BOTH Semantic objects
		// (each relationship object is uni-directional, so we need 2)
		boolean add_success =  getRelationships().add(new RelationshipImpl(relationURN, relatedObj));
		if (add_success) 
		{
			add_success = relatedObj.getRelationships().add(new RelationshipImpl(relationURN, this)); 
		}
		
		// if we fail for any reason, try to remove the relationship
		// from BOTH objects.
		if (!add_success)
		{
			removeRelationship(relationURN); 
			relatedObj.removeRelationship(relationURN); 
		}
			
		return add_success;
    }

    /*
	 *  (non-Javadoc)
	 * @see net.datamodel.qml.SemanticObject#removeRELATIONSHIP(java.net.URN)
	 */
	public boolean removeRelationship(URN relationship) {
		SemanticObject RELATIONSHIP = getRelatedSemanticObject(relationship);
		return removeRelationship(RELATIONSHIP);
	}

	/*
     *  (non-Javadoc)
     * @see net.datamodel.qml.SemanticObject#removeRELATIONSHIP(net.datamodel.qml.SemanticObject)
     */ 
    public boolean removeRelationship ( SemanticObject value  ) {
       return getRelationships().remove(value);
    }

    /*
	 *  (non-Javadoc)
	 * @see net.datamodel.qml.SemanticObject#getURN()
	 */
	public URN getURN() {
		try {
			return new URNImpl ((String) ((XMLSerializableField) getFields().get(URN_XML_FIELD_NAME)).getValue());
		} catch (Exception e) {
			logger.error("Invalid URN for object returned.:"+e.getMessage());
			return (URN) null; // shouldnt happen as we only let valid URNs in..
		}
	}

	
	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#getRelatedSemanticObject(net.datamodel.soml.URN)
	 */
	public SemanticObject getRelatedSemanticObject (URN relationshipURN) {
		
		logger.debug("getRelatedSO called by "+this);
		for (Relationship rel: getRelationships()) {
			logger.debug("  got relationship ("+rel.getURN()+") "+rel);
			if (null != rel && rel.getURN().equals(relationshipURN)) {
				return rel.getTarget(); // matched, so return it
			}
		}
		// nothing matched
		return null;
	}

	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#getObjectList()
	 */
	public List<Relationship> getRelationships() {
        return ((List<Relationship>) ((XMLSerializableField) 
        		getFields().get(RELATIONSHIP_XML_FIELD_NAME)).getValue());
    }

    // Operations
    //

	/** Set the URN, representing the semantic meaning, of this object.
	 * 
	 * @param value of the URN to set
	 */
	protected void setURN (URN value) {
		// Take the URN and convert it to a string for storage in object/serialization.
		// Not optimal, but works (for now).
	    ((XMLSerializableFieldImpl) getFields().get(URN_XML_FIELD_NAME)).setValue(value.toString());
	}

	/**
     * @return boolean value of whether or not some content was written.
     */
    protected boolean basicXMLWriter (
                                      Hashtable idTable,
                                      Hashtable prefixTable,
                                      Writer outputWriter,
                                      String indent,
                                      String newNodeNameString,
                                      boolean indentFirstNode
                                    )
    throws IOException
    {

         // we need to check to see if we are referencing some other SO.
         // IF so, then we WONT print out normally, rather, we will print
         // ourselves out as a reference node.
         String id = getId();
         if(id != null && !id.equals("") && idTable != null)
         {
             SemanticObject idOwner = (SemanticObject) idTable.get(id);
             if(idOwner != null && idOwner != this)
             {

               Specification spec = Specification.getInstance();
                if( spec.getSerializeRefStyle() == XMLReferenceSerializationType.COLLAPSE)
                {

                   boolean isPretty = spec.isPrettyOutput();

                   if(isPretty && indentFirstNode)
                      outputWriter.write(indent);

                   outputWriter.write("<"+ReferenceNodeName+" "+IDRefAttributeName+"=\""+id+"\"/>");

                   return true;

                } else { // reassign the id of this semantic object
                   setId(findUniqueIdName(idTable,id));
                   idTable.put(getId(), this);
                   logger.warn("Reassigning semantic id soid from:"+id+" to "+getId()+" to avoid collision of ids");
                }

             }
         }

         // use regular method
         return super.basicXMLWriter(idTable, prefixTable, outputWriter, indent, newNodeNameString, indentFirstNode);
    }

    /** A special protected method used by constructor methods to
     *  conviently build the XML attribute list for a given class.
     */
    protected void init( )
    {

       resetFields();

       setXMLNodeName("semanticObject");
       
       // now initialize XML fields
       // order matters! these are in *reverse* order of their
       // occurence in the schema/DTD
       addField(URN_XML_FIELD_NAME, "urn:unknown", XMLFieldType.ATTRIBUTE);
       addField(ID_XML_FIELD_NAME, "", XMLFieldType.ATTRIBUTE);
       addField(RELATIONSHIP_XML_FIELD_NAME, new Vector<Relationship>(), XMLFieldType.CHILD);
       
    }

    // find unique id name within a idtable of objects
    protected String findUniqueIdName( Hashtable idTable, String baseIdName)
    {
       StringBuilder testName = new StringBuilder (baseIdName);
       while (idTable.containsKey(testName.toString())) {
           testName.append("0"); // isn't there something better to append here??
       }
       return testName.toString();
    }
    
    /** This class will hold all relationships between our object and other SO's
     */
    class RelationList<RelationshipImpl> 
    extends AbstractXMLSerializableObjectList
    { 
    	// simply change the node name to "relationship"
    	RelationList() { 
    		super("RelationList");
    		this.setSerializeWhenEmpty(false);
    	}
    	
    }

    
}

