/**
 * 
 */
package net.datamodel.soml.core;

import java.util.List;
import java.util.Vector;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.URN;
import net.datamodel.soml.UnmixedList;

import net.datamodel.xssp.*;

import org.apache.log4j.Logger;

/** A restricted collection of objects. All objects must have the same URN. 
 * 
 * @author thomas
 *
 */
public class UnmixedListImpl
extends XMLSerialializedObjectListImpl
implements UnmixedList
{
	
	// fields
	private static Logger logger = Logger.getLogger(UnmixedListImpl.class);

//	private List<SemanticObject> objects = new Vector<SemanticObject>();
	private URN urn;
	
	/** Constructor : must supply the URN for this container.
	 * 
	 * @param URN
	 */
	public UnmixedListImpl (URN urn) { this.urn= urn; }
	
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
	
}
