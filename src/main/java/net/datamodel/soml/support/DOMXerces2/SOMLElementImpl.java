

// SOML Xerces2DOM SOMLElementImpl
// CVS $Id$

// SOMLElementImpl.java Copyright (C) 2005 Brian Thomas,

/* LICENSE

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA

*/

/* AUTHOR

   Brian Thomas  (baba-luu@earthlink.net)
*/

package net.datamodel.soml.support.DOMXerces2;

import java.io.IOException;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.support.SOMLElement;
import net.datamodel.xssp.parse.DOMXerces2.XSSPElementImpl;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/** 
     An implementation of SOMLElement based on the Xerces2 ElementNSImpl class.
 */

public class SOMLElementImpl extends XSSPElementImpl
implements SOMLElement
{
	
	private static final long serialVersionUID = 4925471960280942643L;
	
	private static final Logger logger = Logger.getLogger(SOMLElementImpl.class);

	// create a namespaced (from the so) element. 
	public SOMLElementImpl (SemanticObject so, DocumentImpl doc) 
	throws IOException,NullPointerException {
		super(so, doc);
		setSemanticObject(so);
	}

	//
	// Get/Set Methods 
	//
	
	public final SemanticObject getSemanticObject() { return (SemanticObject) getUserData(); }

	/** Set the semantic object which this element represents.
	 * We reuse the userData field from NodeImpl superclass.
	 * 
	 * @param o
	 * @throws NullPointerException
	 */
	protected final void setSemanticObject (SemanticObject o) 
	throws NullPointerException {
		setUserData(o);
	}

	/*
	 * (non-Javadoc)
	 * @see net.datamodel.xssp.parse.DOMXerces2.XSSPElementImpl#appendChild(org.w3c.dom.Node)
	 */
	@Override
	// TODO: doc on overriding this method
	public Node appendChild(Node newChild) 
	throws DOMException
	{

		if (newChild instanceof SOMLElement) 
		{
			SOMLElement soElem = (SOMLElement) newChild;
			SemanticObject so = soElem.getSemanticObject();

			// Add as a member
			// TODO : add relationship
//			getSemanticObject().addMember(so);

			newChild = super.appendChild(newChild); // needed? 

		} else if (newChild instanceof Comment) 
		{
			logger.warn("Can't append comment node into SOMLElement..ignoring.");
			return null;
		} else {
			String tag = newChild.getNodeName();
			throw new DOMException (DOMException.NOT_SUPPORTED_ERR, "Can't append regular DOM Element:"+tag+" to SOMLElement object:"+this.getNodeName()+".");
		}

		return newChild;
	}

	/*
	 * (non-Javadoc)
	 * @see net.datamodel.xssp.parse.DOMXerces2.XSSPElementImpl#insertBefore(org.w3c.dom.Node, org.w3c.dom.Node)
	 */
	@Override
	// TODO: doc on overriding this method
	public Node insertBefore(Node newChild, Node refChild) throws DOMException
	{

		Node node = null;

		if (newChild instanceof SOMLElement)
		{
			SOMLElement soElem = (SOMLElement) newChild;
			SemanticObject so = soElem.getSemanticObject();

			// Add as a member
			// TODO: add relationship
//			getSemanticObject().addMember(so);

			node = super.insertBefore(newChild,refChild);

		} else {
			throw new DOMException (DOMException.NOT_SUPPORTED_ERR, "Can't remove regular DOM Element from SOMLElement object.");
		}

		return node;
	}

	/*
	 * (non-Javadoc)
	 * @see net.datamodel.xssp.parse.DOMXerces2.XSSPElementImpl#removeChild(org.w3c.dom.Node)
	 */
	@Override
	// TODO: doc on overriding this method
	public Node removeChild(Node oldChild) 
	throws DOMException
	{

		if (oldChild instanceof SOMLElement)
		{
			SOMLElement soElem = (SOMLElement) oldChild;
			SemanticObject so = soElem.getSemanticObject();

			// Add as a member
			// TODO: remove relationship (!)
//			getSemanticObject().removeMember(so);

			oldChild = super.removeChild(oldChild); 

		} else {
			throw new DOMException (DOMException.NOT_SUPPORTED_ERR, "Can't remove regular DOM Element from SOMLElement object.");
		}

		return oldChild;
	}

	/*
	 * (non-Javadoc)
	 * @see net.datamodel.xssp.parse.DOMXerces2.XSSPElementImpl#replaceChild(org.w3c.dom.Node, org.w3c.dom.Node)
	 */
	@Override
	// TODO: doc on overriding this method
	public Node replaceChild(Node newChild, Node oldChild) 
	throws DOMException
	{
		Node node = super.replaceChild(newChild, oldChild);

		if (oldChild instanceof SOMLElement)
		{
			SOMLElement soElem = (SOMLElement) oldChild;
			SemanticObject so = soElem.getSemanticObject();

			// remove member
			// TODO: remove relationship (!)
//			getSemanticObject().removeMember(so);

		} else {
			throw new DOMException (DOMException.NOT_SUPPORTED_ERR, "Can't insertBefore w/ DOM Element in SOMLElement object.");
		}

		if (newChild instanceof SOMLElement)
		{
			SOMLElement soElem = (SOMLElement) newChild;
			SemanticObject so = soElem.getSemanticObject();

			// add member
			// TODO: add relationship (!)
//			getSemanticObject().addMember(so);

		} else {
			throw new DOMException (DOMException.NOT_SUPPORTED_ERR, "Can't insertBefore w/ DOM Element in SOMLElement object.");
		}

		return node;
	}

}

