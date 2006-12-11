
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
import java.util.Iterator;
import java.util.List;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.URN;
import net.datamodel.xssp.XMLFieldType;
import net.datamodel.xssp.XMLSerializableField;
import net.datamodel.xssp.core.AbstractXMLSerializableObject;
import net.datamodel.xssp.core.XMLSerialializedObjectListImpl;
import net.datamodel.xssp.core.XMLSerializableFieldImpl;
import net.datamodel.xssp.support.Specification;
import net.datamodel.xssp.support.XMLReferenceSerializationType;

import org.apache.log4j.Logger;

/**
 * An object which holds quantities (as properties). It may be used 
 * as is or as stub code to create other objects which contain quantities.
 */
public class SemanticObjectImpl extends AbstractXMLSerializableObject 
implements SemanticObject {
	
	private static final Logger logger = Logger.getLogger(SemanticObjectImpl.class);

    // Fields
	private static final String MEMBER_XML_FIELD_NAME = "member";
    private static final String ID_XML_FIELD_NAME = "qid";
    private static final String URN_XML_FIELD_NAME = "urn";
    
    protected static final String ReferenceNodeName = "refNode";
    protected static final String IDRefAttributeName = "oidRef";
    
//    private static final String IMMUTABLE_XML_FIELD_NAME = new String("immutable");

    /**
     * @uml.property  name="nrofMembers"
     */
    private int nrofMembers;

    // Methods
    //

    // Constructors

    // Construct with a given URN.
    public SemanticObjectImpl ( URN URN ) { 
       init();
       setURN(URN);
    }
    
    // The no-argument Constructor
    public SemanticObjectImpl () {
    	init();
    }

    // Accessor Methods

    /*
     * Whether or not this quantity or component is mutable. 
     * (e.g. it may change meta-data/data within the instance).
     */
/*
    public Boolean getImmutable (  ) {
        return (Boolean) ((XMLSerializableField) getFields().get(IMMUTABLE_XML_FIELD_NAME)).getValue();
    }
*/

    /*
     * Whether or not this quantity or component is mutable.
     * (e.g. it may change meta-data/data within the instance).
     */
/*
    public void setImmutable ( Boolean value  ) {
        ((XMLSerializableField) getFields().get(IMMUTABLE_XML_FIELD_NAME)).setValue(value);
    }
*/

    /**
     * The id of an instance of this class. It should be unique across all components and quantities within a given document/object tree.
     */
    public String getId (  ) {
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
     * @see net.datamodel.qml.SemanticObject#addMember(net.datamodel.qml.SemanticObject, java.net.URN)
     */ 
	public boolean addRelationship(SemanticObject member, URN relationship) 
	throws IllegalArgumentException, NullPointerException 
    {

       // cant add ourselves as member of ourselves (!)
       if(member == this)
       {
           logger.warn("ignoring attempt to add self to member list");
           return false;
       }
       
       // check if the member already exists
       if (null != getObject(relationship))
       {
    	   throw new IllegalArgumentException("addMember: a member already exists with relationship URN:"+relationship.toString());
       }

       return getObjectList().add(member);
    }

	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.qml.SemanticObject#addMember(net.datamodel.qml.SemanticObject)
	 */
	public boolean addRelationship(SemanticObject member)
	throws IllegalArgumentException, NullPointerException 
	{
		return addRelationship(member, member.getURN());
	}
	
    /*
	 *  (non-Javadoc)
	 * @see net.datamodel.qml.SemanticObject#removeMember(java.net.URN)
	 */
	public boolean removeRelationship(URN relationship) {
		SemanticObject member = getObject(relationship);
		return removeRelationship(member);
	}

	/*
     *  (non-Javadoc)
     * @see net.datamodel.qml.SemanticObject#removeMember(net.datamodel.qml.SemanticObject)
     */ 
    public boolean removeRelationship ( SemanticObject value  ) {
       return getObjectList().remove(value);
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

	/** Retrieve a member by its unique id.
     * 
     * @param id
     * @deprecated
     * @return
     */
	public SemanticObject getMember(String id) 
	{
		
		Iterator<SemanticObject> iter = getObjectList().iterator();
		while (iter.hasNext()) {
			SemanticObject obj = iter.next();
			if (obj.getId().equals( id)) {
				return obj; // matched, so return it
			}
		}
		// nothing matched
		return null;
	}

	public SemanticObject getObject(URN urn) {
		Iterator<SemanticObject> iter = getObjectList().iterator();
		while (iter.hasNext()) {
			SemanticObject obj = iter.next();
			if (obj.getURN().equals(urn)) {
				return obj; // matched, so return it
			}
		}
		// nothing matched
		return null;
	}

	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.soml.SemanticObject#getObjectList()
	 */
    public List<SemanticObject> getObjectList (  ) {
// TODO!
        return (List) null; //((XMLSerialializedObjectListImpl) ((XMLSerializableField) 
        		//getFields().get(MEMBER_XML_FIELD_NAME)).getValue()).getObjectList();
    }

    // Operations

    /** Determine equivalence between objects (quantities). Equivalence is the same
	  * as 'equals' but without checking that the id fields between both
	  * objects are the same.
	  * @@Overrides
	  */
	public boolean equivalent ( Object obj )
	{
	
	    if (obj instanceof SemanticObject )
	    {
	        if (
	              this.getObjectList().equals(((SemanticObject)obj).getObjectList()) // FIXME : need to iterate over members 
	           )
	        return true;
	    }
	    return false;
	}

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

         // we need to check to see if we are referencing some other Q.
         // IF so, then we WONT print out normally, rather, we will print
         // ourselves out as a referenceQuantity node.
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

                } else { // reassign the id of this quantity
                   setId(findUniqueIdName(idTable,id));
                   idTable.put(getId(), this);
                   logger.warn("Reassigning quantity qid from:"+id+" to "+getId()+" to avoid collision of ids");
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

       nrofMembers = 0;
       
       // now initialize XML fields
       // order matters! these are in *reverse* order of their
       // occurence in the schema/DTD
       getFieldOrder().add(0, MEMBER_XML_FIELD_NAME);
//       fieldOrder.add(0, IMMUTABLE_XML_FIELD_NAME);
       getFieldOrder().add(0, ID_XML_FIELD_NAME);
       getFieldOrder().add(0, URN_XML_FIELD_NAME);

       getFields().put(URN_XML_FIELD_NAME, new XMLSerializableFieldImpl("obj:"+this.hashCode(), XMLFieldType.ATTRIBUTE));
       getFields().put(ID_XML_FIELD_NAME, new XMLSerializableFieldImpl("", XMLFieldType.ATTRIBUTE ));
//       getFields().put(IMMUTABLE_XML_FIELD_NAME, new XMLSerializableFieldImpl(new Boolean(false), XMLFieldType.ATTRIBUTE));
       getFields().put(MEMBER_XML_FIELD_NAME, new XMLSerializableFieldImpl(new XMLSerialializedObjectListImpl(null, false), XMLFieldType.CHILD_NODE));
       
    }


    // find unique id name within a idtable of objects
    protected String findUniqueIdName( Hashtable idTable, String baseIdName)
    {

       StringBuffer testName = new StringBuffer(baseIdName);

       while (idTable.containsKey(testName.toString())) {
           testName.append("0"); // isnt there something better to append here??
       }

       return testName.toString();

    }

}

