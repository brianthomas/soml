
// CVS $Id$

// SOMLReader.java Copyright (c) 2004 Brian Thomas. All rights reserved.

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

import net.datamodel.xssp.parse.XSSPReader;

/** This class is used to create SOMLDocument from SOML files/streams.
 * As a default it uses the Xerces2 SAXParser.
 */
public class SOMLReader extends XSSPReader
{
	
//	private static final Logger logger = Logger.getLogger(SOMLReader.class);
	
    // Fields 
    protected SOMLDocumentHandler myDocumentHandler;
    
    //
    // Constructor methods 
    //

    /** Constructor. Pass a SOMLDocument to use as the structure to read 
     *  information into. Note that if the passed SOMLDocument has
     *  prior information in it, it will remain *unless* overridden by 
     *  conflicting information from the input source. 
     */
    public SOMLReader (SOMLDocument doc) {
    	super(new SOMLDocumentHandler(doc));
    }

} // end SOMLReader class

