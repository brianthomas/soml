
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

/**
 * The interface for all Objects which have Semantic meaning as represented
 * by an URI. Each of these objects may be in a relationship to other objects,
 * which are considered to be 'members' of the object. The relationship between
 * these objects is represented by a separate URI.
 * 
 * @author thomas
 * @version $VersionId:$
 */
public interface SemanticObject 
{

    // Operations
	
   /**
     * Add an object of type SemanticObject to the List of member objects.
     * The members are considered to be properties of the parent semantic object.
     * 
     * The only restrictions on membership are that an object may not "own"
     * itself and that all members must have unique URN values. The URN used indicates
     * the <i>relationship</i> between the parent object and the member, not necessarily
     * the <i>semantic value</i> of the member itself.
     * 
     * @throws IllegalArgumentException if adding self, or a member already exists with 
     *         the same (relationship) URN.
     * @throws NullPointerException if attempting to adding an null (!!)
     * @return boolean value of whether addition was successfull or not.
     */
     public boolean addRelationship (SemanticObject member, URN relationship) 
     throws IllegalArgumentException, NullPointerException;
     
     /** Shortcut method which allows added an object using its URN as the URN
      * which defines the relationship.
      * 
      * @param member
      * @return
      * @throws IllegalArgumentException
      * @throws NullPointerException
      */
     public boolean addRelationship (SemanticObject member ) 
     throws IllegalArgumentException, NullPointerException;
     
    /** Remove a relationship by reference to the object which 
     * is in the relationship with the one the method is called on.
     * 
     * @param object
     * @return true if the relationship was removed. 
     */ 
    public boolean removeRelationship ( SemanticObject object);
    
    /** Remove the given relationship as identified by the URN.
     * 
     * @param relationship
     * @return true if the relationship was removed. 
     */
    public boolean removeRelationship ( URN relationship);
    
    /** Retrieve a semantic object which has a matching 
     * relationship URN.
     * 
     * @param URN which represents the relationship between the parent and the member 
     * @return
     */
    public SemanticObject getObject (URN relationship);
    
    /** Get a list of SemanticObjects which are members of the one on which this 
     * method is called.
     * 
     * @return
     */ 
    public List<SemanticObject> getMemberList ( );

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

    /** Get the URN which represents the semantic meaning of this object. 
     * @return URN of the object 
     */
	public URN getURN();
	
}

