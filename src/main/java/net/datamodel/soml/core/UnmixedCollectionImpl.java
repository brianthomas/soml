/**
 * 
 */
package net.datamodel.soml.core;

import java.util.List;
import java.util.Vector;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.URN;
import net.datamodel.soml.UnmixedCollection;

import org.apache.log4j.Logger;

/** A restricted collection of objects. All objects must have the same URN. 
 * 
 * @author thomas
 *
 */
public class UnmixedCollectionImpl 
implements UnmixedCollection 
{
	
	// fields
	private static Logger logger = Logger.getLogger(UnmixedCollectionImpl.class);

	protected List<SemanticObject> objects = new Vector<SemanticObject>();
	protected URN urn;
	
	/** Constructor : must supply the URN for this container.
	 * 
	 * @param URN
	 */
	public UnmixedCollectionImpl (URN urn) {
		this.urn= urn;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see edu.umd.astro.transform.UnmixedCollection#getURN(java.net.URN)
	 */
	public URN getURN () { return urn; } 
	
	/*
	 *  (non-Javadoc)
	 * @see edu.umd.astro.transform.UnmixedCollection#getDataObjects()
	 */
	public List<SemanticObject> getDataObjects() {
		return objects;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see edu.umd.astro.transform.UnmixedCollection#add(net.datamodel.qml.SemanticObject)
	 */
	public void add (SemanticObject object) {
		if (object.getURN().equals(urn)) 
		{ 
			objects.add(object);
		} else {
			// TODO : throw an exception for URN mis-match here instead.
			logger.error("Can't add object to DataCollection because it lacks the correct URN:"+urn+" != "+object.getURN()); 
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see edu.umd.astro.transform.UnmixedCollection#remove(net.datamodel.qml.SemanticObject)
	 */
	public boolean remove (SemanticObject object) {
		return objects.remove(object);
	}
	 
	/*
	 *  (non-Javadoc)
	 * @see edu.umd.astro.transform.UnmixedCollection#size()
	 */
	public int size() {
		return objects.size();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException 
	{
		// TODO
		return super.clone();
	}
	
}
