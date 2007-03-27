
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

import java.net.URI;
import java.util.List;

import net.datamodel.xssp.ReferenceableXMLSerializableObject;

/**
 * The interface for all objects which have Semantic meaning as represented
 * by a Unique Resource Identifier (URI). Each of these objects may be in a relationship 
 * to other objects, and that relationship is identified by its own URI which is
 * separate from the objects in the relationship. Ultimately, it is intended for
 * the URIs to be used to identify how these various instance structures map 
 * to an Ontology (OWL, Web Ontology Language, W3C specification: 
 * @link{http://www.w3.org/2004/OWL/} is the basis for this implementation).
 * 
 * @author thomas
 * @version $VersionId:$
 */
public interface SemanticObject 
extends ReferenceableXMLSerializableObject
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
     *    Only <i>one</i> relationship may exist between 2 SOs for a given relationship /:RI. <br/> 
     *    A SO may not be in relationship with itself.
     * </ul>
     *  Note that the relationship URI used identifies only the <i>relationship</i>
     *  between the calling object and the target, not the <i>semantic identity</i> 
     *  of the target object itself (which should have its own, separate URI value).
     * 
     * @throws IllegalArgumentException if adding self, or the same object already exists with 
     *         the same (relationship) URI.
     * @throws NullPointerException if attempting to adding an null (!!)
     * @param target object to set up the relationship to.
     * @param relationship the URI of the relationship to establish.
     * @return boolean value of whether addition was successfull or not.
     */
     public boolean addRelationship (SemanticObject target, URI relationship) 
     throws IllegalArgumentException, NullPointerException;
     
    /** Remove all relationships which match the passed URI.
     * 
     * @param URI of the relationships to remove 
     * @return true if the relationship was removed. 
     */
    public boolean removeAllRelationships (URI uri);
    
    /** Remove (clear) all relationships in the calling object 
     * regardless of relationship URI. Does not affect target object
     * relationships. 
     */
    public void clearAllRelationships();
    
    /** Remove the relationship which has the passed URI <i>and</i> the
     * identified target SemanticObject.
     * 
     * @param uri
     * @param target
     * @return
     */
    public boolean removeRelationship (URI uri, SemanticObject target);
    
    /** Retrieve the semantic object in relationship to the caller by
     * the value of the relationship URI.
     * 
     * @param URI which represents the relationship between the parent and the member 
     * @return List of SemanticObjects which are in the given relationship to the caller.
     */
    public List<SemanticObject> getRelatedSemanticObjects (URI relationship);
    
    /** Get the list of relationships which the calling SemanticObject 'owns'.
     * 
     * @return List of all relationships which the calling SO owns.
     */ 
    public List<Relationship> getRelationships ( );
    
    /** Get the list of relationships which the calling SemanticObject 'owns'
     * that match the passed URI value.
     * @param relationshipURI the URI of the relationship(s) to match. 
     * @return List of relationships which have the named URI.
     */
    public List<Relationship> getRelationships(URI relationshipURI);

    /** Get the URI which represents the semantic meaning (ontological class) 
     * of this object.  The URI maybe the same for different instances of a 
     * SemanticObject. (e.g. it is not guarrenteed to be unique)
     *  
     * @return URI of the object 
     */
	public URI getURI();
	
}

