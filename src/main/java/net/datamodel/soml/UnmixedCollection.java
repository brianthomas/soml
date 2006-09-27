/**
 * 
 */
package net.datamodel.soml;

import java.util.List;

/** A collection of objects which all share the same URN
 * (URN is used as an identifier of the semantic meaning).
 * 
 * @author thomas
 *
 */
public interface UnmixedCollection extends Cloneable {
	
	/** Get the data objects held by this container.
	 * 
	 */
	public List<SemanticObject> getDataObjects ();
	
	/** Get the URI of the objects held by this container.
	 * 
	 * @return uri
	 */
	public URN getURN ();
	
	/**
	 * AddProperty an object to this collection.
	 * @param object to be added.
	 */
	public void add (SemanticObject object);
	
	/** Remove an object from the collection.
	 * @param object which is to be removed.
	 * @return true if the object is in the collection and removable.
	 */
	public boolean remove (SemanticObject object);
	
	/** Determine the number of objects within this collection.
	 * 
	 * @return
	 */
	public int size ();
	
	/** Allow cloning.
	 * 
	 * @return
	 */
	public Object clone () throws CloneNotSupportedException;
	
}
