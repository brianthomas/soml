
package net.datamodel.soml;

import net.datamodel.xssp.XMLSerializableObject;

/** Models a uni-directional relationship between 2 SemanticObjects.
 * One object is the 'owner' (or caller) and the other is the target
 * SO, to which the relationship 'points to'.
 *  
 * @author thomas
 *
 */
public interface Relationship extends XMLSerializableObject {
	
	/** Find the target SemanticObject of the relationship.
	 * 
	 * @return
	 */
	public SemanticObject getTarget ();
	
	/** Find the relationship URN.
	 * 
	 * @return
	 */
	public URN getURN();
	
}
