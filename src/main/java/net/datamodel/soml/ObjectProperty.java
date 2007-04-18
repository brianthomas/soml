package net.datamodel.soml;

/** A uni-directional relationship between a parent SemanticObject 
 * and some other 'target' SemanticObject, which is its property.
 * 
 * @author thomas
 */
public interface ObjectProperty 
extends Property 
{

	/** Find the target SemanticObject of the property.
	 * 
	 * @return the target object.
	 */
	public SemanticObject getTarget();
	
}
