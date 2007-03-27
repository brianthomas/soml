// CVS $Id$
// SemanticObjectStartElementHandlerFunc.java Copyright (c) 2004 Brian Thomas. All rights reserved.
 
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


package net.datamodel.soml.support.handlers;

import java.net.URI;

import net.datamodel.soml.impl.SemanticObjectImpl;
import net.datamodel.soml.support.SOMLDocumentHandler;
import net.datamodel.xssp.parse.StartElementHandler;
import net.datamodel.xssp.parse.XSSPDocumentHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SemanticObjectStartElementHandlerFunc 
implements StartElementHandler {
	public Object action ( XSSPDocumentHandler handler, String namespaceURI, 
			String localName, String qName, Attributes attrs)
	throws SAXException {
		
		// allow it to crash if the cast fails 
		SOMLDocumentHandler shandler = (SOMLDocumentHandler) handler;
		// TODO
		URI uri = null; // SOMLDocumentHandler.getURIAttributeValue(attrs); 

		SemanticObjectImpl so = new SemanticObjectImpl(uri);
		so.setAttributeFields(attrs); // set XML attributes from passed list
		
		// TODO: anything else? May need to record SemanticObject so we 
		// get the relationships right..
//		shandler.recordSemanticObject(so);

		return so;
	}
}
