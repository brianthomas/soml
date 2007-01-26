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

//	private List<SemanticObject> objects = new Vector<SemanticObject>();
	private URN urn;
	
	/** Constructor : must supply the URN for this container.
	 * 
	 * @param URN
	 */
	public UnmixedSemanticObjectListImpl (URN urn) { this.urn= urn; }
	
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
	public void add(int arg0, T arg1) {
		// TODO Auto-generated method stub
		super.add(arg0, arg1);
	}

	/** May only add objects which have same URN as the collection.
	 * 
	 */
	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1) {
		// TODO Auto-generated method stub
		return super.addAll(arg0, arg1);
	}
	
	/** May only set objects which have the same URN as the collection.
	 */
	@Override
	public T set(int arg0, T arg1) {
		// TODO Auto-generated method stub
		return super.set(arg0, arg1);
	}
	
}
