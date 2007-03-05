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

package net.datamodel.soml.support;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import net.datamodel.soml.SemanticObject;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * SOMLDocument is a DOM-based document interface. 
 * It aggregates the common signature of SOML DOM 
 * Documents regardless of underlying DOM implementation 
 */

public interface SOMLDocument extends Document 
{
	/** Write this document out in XML as a string.
	 * 
	 * @return
	 */
	public String toXMLString ();
   
	/** Write this document out in XML to the target writer.
     * 
     * @param outputWriter
     * @throws IOException
     */
	public void toXMLWriter (Writer outputWriter) throws IOException;
   
	/** Write this document out in XML to the target file.
     * 
     * @param fileName
     * @throws IOException
     */ 
    public void toXMLFile (String fileName) throws IOException;
   
    /** Set the document element of this document.
     * 
     * @param elem
     */
   // TODO: do we need this? Can we get away with putting in the constructor??
    public void setDocumentElement (Element elem);

   /** Create an element node which is linked to the given target
    * semantic object. The namespace of the element node created 
    * defaults to the target namespace of the document.
    * 
    * @param so
    * @return
    * @throws DOMException
    */
   public SOMLElement createSOMLElement(SemanticObject so) throws DOMException;
   
   /** Create an namespaced element node which is linked to the given target
    * semantic object.
    * @param namespaceURI
    * @param so
    * @return
    * @throws DOMException
    */ 
   public SOMLElement createSOMLElementNS(String namespaceURI, SemanticObject so) throws DOMException;
   
   // TODO: WHat is this, is this needed??
   public Map<String,SemanticObject> getSemanticObjectIdTable();
   
   /** Return references to all semantic objects held within this document.
    * 
    * @param deep
    * @return
    */
   public List getSemanticObjects(boolean deep);


   public String getNamespace(String prefix);
   
   /** Set a particular prefix to namespace mapping in the document.
    * 
    * @param prefix
    * @param namespaceURI
    */
   public void setPrefixNamespaceMapping(String prefix, String namespaceURI);
   
   /** Set a group of prefix mappings for this document. 
    * 
    * @param prefixNamespaceMappings  the prefix to namespace mappings to set
    */
   public void setPrefixNamespaceMappings(Map<String,String> prefixMappings);

   /** Get the prefix mappings for this document.
    * 
    * @return
    */
   public Map<String,String> getPrefixNamespaceMappings();

}

