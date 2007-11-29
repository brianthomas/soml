

// SOMLDocumentHandler.java Copyright (c) 2004 Brian Thomas. All rights reserved.

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

package net.datamodel.soml.dom;

import java.util.Map;

import net.datamodel.xssp.dom.XSSPDocumentHandler;

/** 
 *     Contains the core SAX document handler for the Reader. It also contains
 * the basic SOML element/charData handlers (as internal classes).
 * @version $Revision$
 */
public class SOMLDocumentHandler extends XSSPDocumentHandler
{
	
	public SOMLDocumentHandler (SOMLDocument doc) { super(doc); }
	
	public SOMLDocumentHandler (SOMLDocument doc, Map<String,String> options) { super(doc, options); }

} // End of SOMLDocumentHandler class 

