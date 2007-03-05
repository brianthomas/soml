
// SOMLElement Interface 
// CVS $Id$

// SOMLElement.java Copyright (C) 2004 Brian Thomas,

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

package net.datamodel.soml.support;

import java.io.Writer;

import net.datamodel.soml.SemanticObject;

import org.w3c.dom.Element;

/**
 * SOMLElement is a DOM element interface for elements which hold Quantities within.
 */

public interface SOMLElement extends Element
{
	/** Write this element out in XML as a String.
	 * 
	 * @return
	 */
	public String toXMLString ();
	
	/** Write this element out in XML to the target writer.
	 * 
	 * @param outputWriter
	 * @throws java.io.IOException
	 */
	public void toXMLWriter (Writer outputWriter) throws java.io.IOException;

	/** Return the target SO of which this element represents.
	 * 
	 * @return
	 */
	public SemanticObject getSemanticObject();

	/** Set the target SO which the element will represent.
	 * 
	 * @param q
	 */
	// TODO: needed? Cant we get away with putting this in the constructor?
	public void setso(SemanticObject q);

}

