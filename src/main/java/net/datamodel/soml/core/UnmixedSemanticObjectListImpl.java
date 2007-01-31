/**
 * 
 */
package net.datamodel.soml.core;

import java.util.Collection;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.URN;
import net.datamodel.soml.UnmixedSemanticObjectList;
import net.datamodel.xssp.core.AbstractXMLSerializableObjectList;

import org.apache.log4j.Logger;

/** A restricted collection of objects. All objects must have the same URN. 
 * 
 * @author thomas
 *
 */
public class UnmixedSemanticObjectListImpl<T extends SemanticObject>
extends AbstractXMLSerializableObjectList<T>
implements UnmixedSemanticObjectList<T>
{
	
	// fields
	private static Logger logger = Logger.getLogger(UnmixedSemanticObjectListImpl.class);

	private URN urn;
	
	/** Constructor : must supply the (non-null) URN for this container.
	 * 
	 * @param URN
	 * @throws NullPointerException if a null urn is passed.
	 */
	public UnmixedSemanticObjectListImpl (URN urn) { 
		if(null == urn)
			throw new NullPointerException("UnmixedSemanticObjectList cannot have a null value for URN.");
		this.urn=urn; 
	}
	
	/*
	 *  (non-Javadoc)
	 * @see edu.umd.astro.transform.UnmixedCollection#getURN(java.net.URN)
	 */
	public URN getURN () { return urn; } 
	
	@Override
	public Object clone() throws CloneNotSupportedException 
	{
		// TODO
		return super.clone();
	}
	
	/** May only add objects which have same URN as the collection.
	 * 
	 */
	@Override
	public void add(int arg0, T o) {
		if(can_add(o))
			super.add(arg0, o);
	}

	/** May only add objects which have same URN as the collection.
	 * 
	 */
	@Override
	public boolean addAll(int arg0, Collection<? extends T> col) 
	{
		// ugh. NOT performance oriented! Checking every item 'by hand' 
		for (T item : col)
		{
			// if False, then at least one item in the collection doesnt conform 
			// and we bail from the whole thing..
			if (!can_add(item))
				return false; 
		}
		return super.addAll(arg0, col);
	}
	
	/** May only set objects which have the same URN as the collection.
	 */
	@Override
	public T set(int arg0, T o) {
		if(can_add(o))
			return super.set(arg0, o);
		return null;
	}

	@Override
	public boolean add(T o) {
		if(can_add(o))
			return super.add(o);
		return false;
	}
	
	// make sure object has the same URN as the collection
	private boolean can_add (T o) {
		logger.debug("Try to add object w/ class:"+o.getClass()+" URN:"+o.getURN());
		if (urn.equals(o.getURN()))
			return true;
		logger.warn("Ignoring add of object:"+o+" has the wrong URN("+o.getURN().toAsciiString()+") for UnmixedCollection URN:("+urn.toAsciiString()+")");
		return false;
	}
			
}
