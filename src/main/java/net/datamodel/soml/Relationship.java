
package net.datamodel.soml;

import java.net.URI;

import net.datamodel.xssp.XMLSerializableObject;

/** <p>Models a uni-directional relationship between 2 SemanticObjects.
 * One object is the 'owner' (or caller) and the other is the target
 * SO, to which the relationship 'points to'.
 * </p>
 *  
 * <p>A Note on interface extension: Making this XMLSerializableObject instead of XMLSerializableObjectWithFields
 * is intentional, in this manner we control/prohibit the user from adding any
 * more fields to this object type which seems to make sense, at this time :).
 * </p>
 * 
 * @author thomas
 */
public interface Relationship extends XMLSerializableObject 
{
	
	/** Find the target SemanticObject of the relationship.
	 * 
	 * @return
	 */
	public SemanticObject getTarget ();
	
	/** Find the relationship URI.
	 * 
	 * @return
	 */
	public URI getURI();
	
}
