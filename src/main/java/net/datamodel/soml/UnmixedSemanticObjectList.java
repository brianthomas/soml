/**
 * 
 */
package net.datamodel.soml;

import net.datamodel.xssp.XMLSerializableObjectList;

/** A list of objects which all share the same URN
 * (URN is used as an identifier of the semantic meaning).
 * 
 * @author thomas
 *
 */
public interface UnmixedSemanticObjectList<T extends SemanticObject> 
extends XMLSerializableObjectList<T> 
{

	/** Get the URI of the objects held by this container.
	 * 
	 * @return uri
	 */
	public URN getURN ();
	
}
