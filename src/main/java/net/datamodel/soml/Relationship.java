
package net.datamodel.soml;

import net.datamodel.xssp.XMLSerializableObject;

/**
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
