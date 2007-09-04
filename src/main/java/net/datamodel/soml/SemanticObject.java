
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
 * by a Unique Resource Identifier (URI). Each of these objects may be in a 
 * relationship with other semantic objects (the objects are then known as 
 * properties of the parent object). Each property is identified by its own 
 * URI which is separate from the objects which are serving as the target of
 * the property. 
 * 
 * Ultimately, it is intended for the URIs to be used to identify how these 
 * various instance structures map  to an Ontology (OWL, Web Ontology Language, 
 * W3C specification ;<i>see <b>http://www.w3.org/2004/OWL/</b></i>; is 
 * the basis for this implementation).
 * 
 * @author thomas
 * @version $VersionId:$
 */
public interface SemanticObject 
extends ReferenceableXMLSerializableObject
{

    // Operations
	
   /**
     * Add a (uni-directional) property between 2 SemanticObjects (SO). 
     * The caller is considered to 'own' the property which 'points to' 
     * the target SO. After this is successfully called, <i>only the calling</i> 
     * SO will show the other SO in its list of related objects/properties. 
     * 
     * The following restrictions exist on properties between SOs:
     * <ul> 
     *    Only <i>one</i> property may exist between 2 SOs for a given property /:RI. <br/> 
     *    A SO may not be in property with itself.
     * </ul>
     *  Note that the property URI used identifies only the <i>property</i>
     *  between the calling object and the target, not the <i>semantic identity</i> 
     *  of the target object itself (which should have its own, separate URI value).
     * 
     * @throws IllegalArgumentException if adding self, or the same object already exists with 
     *         the same (property) URI.
     * @throws NullPointerException if attempting to adding an null (!!)
     * @param target object to set up the property to.
     * @param property the URI of the property to establish.
     * @return boolean value of whether addition was successfull or not.
     */
     public boolean addProperty (SemanticObject target, URI property) 
     throws NullPointerException;
     
    /** Remove all properties which match the passed URI.
     * 
     * @param uri of the properties to remove 
     * @return true if the property was removed. 
     */
    public boolean removeAllProperties (URI uri);
    
    /** Remove (clear) all properties in the calling object 
     * regardless of property URI. Does not affect target object
     * properties. 
     */
    public void removeAllProperties();
    
    /** Remove any properties which have the passed URI <i>and</i> the
     * identified target SemanticObject.
     * 
     * @param propertyUri
     * @param target
     * @return true if the property was removed
     */
    public boolean removeObjectProperty (URI propertyUri, SemanticObject target);
    
    /** Retrieve any semantic objects which are are properties of 
     * the parent SemanticObject with the given property URI. 
     * 
     * @param propertyURI the URI which represents the property describing the objects to search for 
     * @return List of SemanticObjects which are in the given property to the caller.
     */
    public List<SemanticObject> getSemanticObjects (URI propertyURI);
    
    /** Get the list of properties which the calling SemanticObject 'owns'.
     * 
     * @return List of all properties which the calling SO owns.
     */ 
    public List<Property> getProperties ( );
    
    /** Get the list of properties which the calling SemanticObject 'owns'
     * that match the passed URI value.
     * @param propertyURI the URI of the property(s) to match. 
     * @return List of properties which have the named URI.
     */
    public List<Property> getProperties(URI propertyURI);

    /** Get the URI which represents the semantic meaning (ontological class) 
     * of this object.  The URI maybe the same for different instances of a 
     * SemanticObject. (e.g. it is not guarrenteed to be unique)
     *  
     * @return a URI of the SemanticObject 
     */
	public URI getURI();
	
}

