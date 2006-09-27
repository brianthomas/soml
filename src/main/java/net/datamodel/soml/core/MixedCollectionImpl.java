
// CVS $Id$

// MixedCollectionImpl.java Copyright (c) 2004 Brian Thomas. All rights reserved.

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

import java.io.Writer;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import net.datamodel.soml.SemanticObject;
import net.datamodel.xssp.XMLFieldType;
import net.datamodel.xssp.XMLSerializableField;
import net.datamodel.xssp.core.XMLSerializableFieldImpl;
import net.datamodel.xssp.core.XMLSerializableObjectImpl;

/**
 * This is a XMLSerializableCollection class which holds any admixture
 * of objects.
 * It is used to hold any admixture of semantic objects.
 *
 */
public class MixedCollectionImpl 
extends XMLSerializableObjectImpl 
implements Collection
{

    // FIELDS
    private static final String XML_FIELD_NAME = "oList";

    // should we go ahead an serialize empty objects? 
    // The default is false (e.g "no").
    protected boolean serializeIfEmpty = false;

    // Methods
    //

    // Constructors

    // No-argument Constructor set to "private" to prevent use.
    private MixedCollectionImpl ( )  { }

    /** Construct collection with a particular XML nodename.
     */
    public MixedCollectionImpl (String nodeName )
    {
       init(nodeName, true);
    }

    /** Construct collection with a particular XML nodename. Allow control
     * over whether or not it will be serialized to XML if it is empty.
     */
    public MixedCollectionImpl (String nodeName , boolean serializeIfEmpty) 
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

    /**
     * Add a object to this collection. 
     * @param object 
     * @return boolean value of whether addition was successful or not. 
     */
    public boolean add ( SemanticObject object) 
    {
    	return getObjectList().add(object); 
    }

    /**
     * Remove a object to from this collection. 
     * @param object
     * @return boolean value of whether removal was successful or not. 
     */
    public boolean removeObject ( SemanticObject object) {
    	return getObjectList().remove(object);    
    }

    /**
     * Get the list of objects in this collection. 
     * @return List  
     */
    public List getObjectList ( ) {
       return (List) ((XMLSerializableField) fieldHash.get(XML_FIELD_NAME)).getValue();
    }

    // Protected

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
       if(getObjectList().size() == 0 && !serializeIfEmpty)
           return false; 

       // serialize it
       return super.basicXMLWriter(idTable, prefixTable, outputWriter, indent, newNodeNameString, doFirstIndent );

    }

    /** A special protected method used by constructor methods to
     *  conviently build the XML attribute list for a given class.
     */
    protected void init(String nodeName, boolean serialize )
    {

       resetFields();

       xmlNodeName = nodeName;

       serializeIfEmpty = serialize;

       fieldOrder.add(0, XML_FIELD_NAME);

       fieldHash.put(XML_FIELD_NAME, new XMLSerializableFieldImpl (
    		     new Vector<SemanticObject>(), XMLFieldType.CHILD_NODE_LIST)
    		   );

    }

}

