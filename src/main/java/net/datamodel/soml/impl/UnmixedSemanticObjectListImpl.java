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

/** A (simple) restricted collection of objects. All objects must 
 * have the same rdfTypeURI as the collection (inference is <b>not</b> 
 * checked to see if the class and the collection might have a subclass 
 * in common). 
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

	private URI rdfTypeURI = null;
	
	/** Constructor : must supply the (non-null) rdfTypeURI for this container.
	 * 
	 * @param rdftype the rdf:type to limit the collection to 
	 * @throws NullPointerException if a null rdfTypeURI is passed.
	 */
	public UnmixedSemanticObjectListImpl (URI rdfType) { 
		if (null == rdfType)
			throw new NullPointerException("UnmixedSemanticObjectList cannot have a null value for rdfTypeURI.");
		this.rdfTypeURI=rdfType;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see edu.umd.astro.transform.UnmixedCollection#getURI(java.net.URI)
	 */
	public final URI getRDFTypeURI () { return rdfTypeURI; } 
	
	/** May only add objects which have same rdfTypeURI as the collection.
	 * 
	 */
	@Override
	public final void add(int arg0, T o) {
		if(can_add(o))
			super.add(arg0, o);
	}

	/** May only add objects which have same rdfTypeURI as the collection.
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
	
	/** May only set objects which have the same rdfTypeURI as the collection.
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
	
	// make sure object has the same rdfTypeURI as the collection
	// TODO?: To be really kosher, we should do some inference to check if subclass of..
	private boolean can_add (T o) {
		logger.debug("Try to add object w/ class:"+o.getClass()+" rdfTypeURI:"+o.getRDFTypeURIs());
		if (o.getRDFTypeURIs().contains(rdfTypeURI))
			return true;
		logger.warn("Ignoring add of object:"+o+" has the wrong rdfTypeURI(not one of:"+o.getRDFTypeURIs()
				+") for UnmixedCollection rdfTypeURI:("+rdfTypeURI.toASCIIString()+")");
		return false;
	}
			
}
