/**
 * 
 */
package net.datamodel.soml.impl;

import java.net.URI;
import java.util.Collection;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.UnmixedSemanticObjectList;
import net.datamodel.xssp.impl.AbstractXMLSerializableObjectList;

import org.apache.log4j.Logger;

/** A restricted collection of objects. All objects must have the same uri. 
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

	private URI uri;
	
	/** Constructor : must supply the (non-null) uri for this container.
	 * 
	 * @param uri
	 * @throws NullPointerException if a null uri is passed.
	 */
	public UnmixedSemanticObjectListImpl (URI uri) { 
		if(null == uri)
			throw new NullPointerException("UnmixedSemanticObjectList cannot have a null value for uri.");
		this.uri=uri;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see edu.umd.astro.transform.UnmixedCollection#getURI(java.net.URI)
	 */
	public final URI getURI () { return uri; } 
	
	/** May only add objects which have same uri as the collection.
	 * 
	 */
	@Override
	public final void add(int arg0, T o) {
		if(can_add(o))
			super.add(arg0, o);
	}

	/** May only add objects which have same uri as the collection.
	 * 
	 */
	@Override
	public final boolean addAll(int arg0, Collection<? extends T> col) 
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
	
	/** May only set objects which have the same uri as the collection.
	 */
	@Override
	public final T set(int arg0, T o) {
		if(can_add(o))
			return super.set(arg0, o);
		return null;
	}

	@Override
	public final boolean add(T o) {
		if(can_add(o))
			return super.add(o);
		return false;
	}
	
	// make sure object has the same uri as the collection
	private boolean can_add (T o) {
		logger.debug("Try to add object w/ class:"+o.getClass()+" uri:"+o.getURI());
		if (uri.equals(o.getURI()))
			return true;
		logger.warn("Ignoring add of object:"+o+" has the wrong uri("+o.getURI().toASCIIString()
				+") for UnmixedCollection uri:("+uri.toASCIIString()+")");
		return false;
	}
			
}
