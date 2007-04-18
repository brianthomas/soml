
package net.datamodel.soml;

import java.net.URI;

import net.datamodel.xssp.XMLSerializableObject;

/** <p>Models a uni-directional relationship between a parent SemanticObject
 * and its 'properties'. 
 * </p>
 *  
 * <p>A Note on interface extension: Making this XMLSerializableObject instead of XMLSerializableObjectWithFields
 * is intentional, in this manner we control/prohibit the user from adding any
 * more fields to this object type which seems to make sense, at this time :).
 * </p>
 * 
 * @author thomas
 */
public interface Property extends XMLSerializableObject 
{
	
	/** Find the property URI.
	 * 
	 * @return URI of the property
	 */
	public URI getURI();
	
}
