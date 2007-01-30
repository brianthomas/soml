
// CVS $Id$

// SemanticObject.java Copyright (c) 2006 Brian Thomas. All rights reserved.

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

package net.datamodel.soml;

import java.util.List;

import net.datamodel.xssp.XMLSerializableObject;

/**
 * The interface for all objects which have Semantic meaning as represented
 * by a Unique Resource Name (URN). Each of these objects may be in a relationship 
 * to other objects, and that relationship is identified by its own URN which is
 * separate from the objects in the relationship. Ultimately, it is intended for
 * the URNs to be used to identify how these various instance structures map 
 * to an Ontology.
 * 
 * @author thomas
 * @version $VersionId:$
 */
public interface SemanticObject 
extends XMLSerializableObject
{

    // Operations
	
   /**
     * Add a (uni-directional) relationship between 2 SemanticObjects (SO). 
     * The caller is considered to 'own' the relationship which 'points to' 
     * the target SO. After this is successfully called, <i>only the calling</i> 
     * SO will show the other SO in its list of related objects. 
     * 
     * The following restrictions exist on relationships between SOs:
     * <ul> 
     *    Only <i>one</i> relationship may exist between 2 SOs for a given relationship URN. <br/> 
     *    A SO may not be in relationship with itself.
     * </ul>
     *  Note that the relationship URN used identifies only the <i>relationship</i>
     *  between the calling object and the target, not the <i>semantic identity</i> 
     *  of the target object itself (which should have its own, separate URN value).
     * 
     * @throws IllegalArgumentException if adding self, or the same object already exists with 
     *         the same (relationship) URN.
     * @throws NullPointerException if attempting to adding an null (!!)
     * @param target object to set up the relationship to.
     * @param relationship the URN of the relationship to establish.
     * @return boolean value of whether addition was successfull or not.
     */
     public boolean addRelationship (SemanticObject target, URN relationship) 
     throws IllegalArgumentException, NullPointerException;
     
    /** Remove the given relationship as identified by the URN.
     * 
     * @param relationship
     * @return true if the relationship was removed. 
     */
    public boolean removeRelationship (URN relationship);
    
    /** Retrieve the semantic object in relationship to the caller by
     * the value of the relationship URN.
     * 
     * @param URN which represents the relationship between the parent and the member 
     * @return List of SemanticObjects which are in the given relationship to the caller.
     */
    public List<SemanticObject> getRelatedSemanticObjects (URN relationship);
    
    /** Get the list of SemanticObjects which are in relationship to the calling
     * SemanticObject.
     * 
     * @return
     */ 
    public List<Relationship> getRelationships ( );
    
    /**
     * Get the id of an instance of this class. It should be unique across all
     * objects within a given document/object tree.
     */
    public String getId ( );
    
    // TODO: remove the setId method
    /** The id of an instance of this class. It should be unique across all 
     * SemanticObjects within a given document/object tree.
     * 
     */ 
    public void setId ( String value );

    /** Get the URN which represents the semantic meaning (ontological class) 
     * of this object.  The URN maybe the same for different instances of a 
     * SemanticObject. (e.g. it is not guarrenteed to be unique)
     *  
     * @return URN of the object 
     */
	public URN getURN();
	
}

