
// CVS $Id$

// XMLSerialializedObjectListImpl.java Copyright (c) 2004 Brian Thomas. All rights reserved.

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

package net.datamodel.soml.core;

import java.io.IOException;
import java.io.Writer;
import java.util.AbstractList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.datamodel.xssp.XMLFieldType;
import net.datamodel.xssp.XMLSerializableField;
import net.datamodel.xssp.XMLSerializableObject;
import net.datamodel.xssp.core.XMLSerializableFieldImpl;
import net.datamodel.xssp.core.XMLSerializableObjectImpl;

/**
 * This is a container class for XMLSerializableObjects. It is an ordered
 * sequence and requires specification of the nodeName to use when a given
 * instance is serialized into XML.
 * 
 * @author thomas
 */
public class XMLSerialializedObjectListImpl
extends AbstractList<XMLSerializableObject>
implements XMLSerializableObject
{
//implements Collection<XMLSerializableObject>

    // FIELDS
    private String XmlNodeName;
    private final static String XML_OBJECTS_FIELD_NAME = "oList";
    
    private List<XMLSerializableObject> objects = new Vector<XMLSerializableObject>(); 
    // should we go ahead an serialize empty objects? 
    // The default is false (e.g "no").
    private boolean serializeIfEmpty = false;
    
    protected List fieldOrder = new Vector ();
    protected Hashtable fieldHash = new Hashtable();

    // Methods
    //

    // Constructors

    /** create a collection for XMLSerializable objects. No XML
     * node name for the container is assigned by default, and 
     * objects will we writtent without appearing as children
     * of a collection.
     */
    // No-argument Constructor set to "private" to prevent use.
    private XMLSerialializedObjectListImpl ( )  { }

    /** Construct collection with a particular XML nodename.
     */
    public XMLSerialializedObjectListImpl (String nodeName )
    {
       init(nodeName, true);
    }

    /** Construct collection with a particular XML nodename. Allow control
     * over whether or not it will be serialized to XML if it is empty.
     */
    public XMLSerialializedObjectListImpl (String nodeName , boolean serializeIfEmpty) 
    {
       init(nodeName, serializeIfEmpty);
    }

    // Accessor Methods

    /** Set whether or not to serialize this object IF no objects
     * are contained within it. collections which have one or more objects
     * are always serialized regardless of the setting of this field.
     * @return boolean value of whether to serialize or not empty collections.
     */ 
    public void setSerializeWhenEmpty ( boolean value) 
    {
        serializeIfEmpty = value;
    }

    /** Determine whether or not to serialize this object IF no objects
     * are contained within it. collections which have one or more objects
     * are always serialized regardless of the setting of this field.
     * @return boolean value of whether to serialize or not empty collections.
     */
    public boolean getSerializeWhenEmpty ( ) 
    {
        return serializeIfEmpty;
    }

    // Protected

    @Override
	public Iterator<XMLSerializableObject> iterator() {
		return objects.iterator();
	}

	@Override
	public int size() {
		return objects.size();
	}

	// Protected
	
	@Override
	public XMLSerializableObject get(int index) {
		return objects.get(index);
	}

	/** A special protected method used by constructor methods to
	 *  conviently build the XML attribute list for a given class.
	 */
	protected void init(String nodeName, boolean serialize )
	{
	
	   // resetFields();
	
	   XmlNodeName = nodeName;
	
	   serializeIfEmpty = serialize;
	
	   fieldOrder.add(0, XML_OBJECTS_FIELD_NAME);
	
	   fieldHash.put(XML_OBJECTS_FIELD_NAME, new XMLSerializableFieldImpl (
			        objects, XMLFieldType.CHILD_NODE_LIST)
			   );
	
	}

	/**
	 * @return boolean value of whether or not one or more an xml nodes were written.
	 */
	protected boolean basicXMLWriter (
	                                  Hashtable idTable,
	                                  Hashtable prefixTable,
	                                  Writer outputWriter,
	                                  String indent,
	                                  String newNodeNameString,
	                                  boolean doFirstIndent
	                                )
	throws java.io.IOException
	{
	
	   // if our list is empty, we dont serialize out if so directed
	   if(size() == 0 && !serializeIfEmpty)
	       return false; 
	
	   // serialize it
	   XMLSerializableObjectImpl.basicXMLWriter(idTable, prefixTable, outputWriter, indent, newNodeNameString, doFirstIndent );
	
	}
	
	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.xssp.XMLSerializableObject#setXMLNodeName(java.lang.String)
	 */
	public void setXMLNodeName(String arg0) {
		XmlNodeName = arg0;
	}

	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.xssp.XMLSerializableObject#getXMLNodeName()
	 */
	public String getXMLNodeName() {
		return XmlNodeName;
	}

	/*
	 *  (non-Javadoc)
	 * @see net.datamodel.xssp.XMLSerializableObject#getNamespaceURI()
	 */
	public String getNamespaceURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public Hashtable getFields() {
		return fieldHash;
	}

	public List getFieldOrder() {
		return fieldOrder;
	}

	public void setNamespaceURI(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void toXMLFile(String arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public String toXMLString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void toXMLWriter(Writer arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void toXMLWriter(Writer arg0, String arg1) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void toXMLWriter(Writer arg0, String arg1, boolean arg2, boolean arg3) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void toXMLWriter(Hashtable arg0, Hashtable arg1, Writer arg2, String arg3, boolean arg4, boolean arg5) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public boolean addXMLSerializableField(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeXMLSerializableField(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public XMLSerializableField findXMLSerializableField(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}

