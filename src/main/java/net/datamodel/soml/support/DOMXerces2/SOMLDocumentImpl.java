

// SOML DOM SOMLDocumentImpl
// CVS $Id$

// SOMLDocumentImpl.java Copyright (C) 2005 Brian Thomas,

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
import java.util.List;

import net.datamodel.soml.Constant;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.support.SOMLDocument;
import net.datamodel.soml.support.SOMLElement;
import net.datamodel.xssp.parse.DOMXerces2.AbstractXSSPDocument;

import org.w3c.dom.DOMException;

/** 
 *  Read any XML Document into a specialized DOM document -- SOMLDocumentImpl.
 */

// Based on Xerces DocumentImpl class.
public class SOMLDocumentImpl extends AbstractXSSPDocument
implements SOMLDocument
{

//	private static final Logger logger = Logger.getLogger(SOMLDocumentImpl.class);

	public SOMLElement createSOMLElement(SemanticObject SemanticObject) 
	throws DOMException
	{

		SOMLElementImpl soElem = null;
		try {
			soElem = new SOMLElementImpl(SemanticObject, this);
		} catch (IOException e) {
			// dunno if this is the right error code.. but what the hell
			throw new DOMException(DOMException.INVALID_STATE_ERR, e.getMessage());
		}
		return soElem;
	}

	/** Create a namespaced element from a semantic object. This method 
	 * picks up the namespaceURI from the semantic object.
	 *
	 * @throws DOMException if a so is passed which lacks namespaceURI or if we get an underlying IOException
	 */
	public SOMLElement createSOMLElementNS(SemanticObject so)
	throws DOMException
	{
		if (so.getNamespaceURI() == null)
			throw new DOMException(DOMException.INVALID_STATE_ERR, "cant create namespaced SemanticObject which has namespaceURI set to null!");
		
		try {
			return new SOMLElementImpl(so, this);
		} catch (IOException e) {
			// dunno if this is the right error code.. but what the hell
			throw new DOMException(DOMException.INVALID_STATE_ERR, e.getMessage());
		}
	}

	@Override
	public String getNamespaceURI() { return Constant.SOML_NAMESPACE_URI; }

	@Override
	public String getSchemaName() { return Constant.SOML_SCHEMA_NAME; }

	public List getSemanticObjects(boolean deep) {
		// TODO Auto-generated method stub
		return null;
	}

}

