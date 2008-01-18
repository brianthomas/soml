// CVS $Id$

// SOMLDocument.java Copyright (C) 2004 Brian Thomas


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

import java.util.List;

import net.datamodel.soml.SemanticObject;
import net.datamodel.xssp.dom.XSSPDocument;

import org.w3c.dom.DOMException;

/**
 * SOMLDocument is a DOM-based document interface. 
 * It aggregates the common signature of SOML DOM 
 * Documents regardless of underlying DOM implementation 
 */

public interface SOMLDocument extends XSSPDocument 
{

   /** Create an element node which is linked to the given target
    * semantic object. The namespace of the element node created 
    * defaults to the target namespace of the passed semantic object.
    * 
    * @param so
    * @throws DOMException
    * @return the XML ELement node which represents the SemanticObject
    */
   public SOMLElement createSOMLElement(SemanticObject so) throws DOMException;
  
   /** Return references to all semantic objects held within this document.
    * 
    * @return a list of semantic objects held by the document 
    */
   public List<SemanticObject> getSemanticObjects();
  
}

