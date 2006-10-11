/**
 * 
 */
package net.datamodel.soml;

import java.util.List;

import net.datamodel.xssp.XMLSerializableObject;

/** A collection of objects which all share the same URN
 * (URN is used as an identifier of the semantic meaning).
 * 
 * @author thomas
 *
 */
public interface UnmixedList<T extends SemanticObject> 
extends List<T>,Cloneable,XMLSerializableObject 
{

	/** Get the URI of the objects held by this container.
	 * 
	 * @return uri
	 */
	public URN getURN ();
	
}
