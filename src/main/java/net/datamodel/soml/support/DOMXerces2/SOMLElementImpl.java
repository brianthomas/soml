

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
import java.io.Writer;
import java.util.Hashtable;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.support.SOMLElement;
import net.datamodel.xssp.XMLSerializableField;
import net.datamodel.xssp.XMLSerializableObject;
import net.datamodel.xssp.support.Constants;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementNSImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/** 
     An implementation of SOMLElement based on the Xerces2 ElementNSImpl class.
 */

public class SOMLElementImpl extends ElementNSImpl
implements SOMLElement
{
	
	private static final Logger logger = Logger.getLogger(SOMLElementImpl.class);

   // 
   // Fields
   //
   SemanticObject mySemanticObject = null;

   //
   // Constructors
   //

   // hmm..badness. We expect SemanticObject interface, then demand (internally) the 
   // XMLSerializableObject interface. This could lead to problems down the
   // line. :P 
   public SOMLElementImpl (String namespaceURI, SemanticObject SemanticObject, DocumentImpl doc) 
   throws IOException,NullPointerException
   {
      super (doc, namespaceURI, ((XMLSerializableObject) SemanticObject).getXMLNodeName());
      ((XMLSerializableObject) SemanticObject).setNamespaceURI(namespaceURI);
      setSemanticObject(SemanticObject);
   }

   // this has same issue as the constructor above
   public SOMLElementImpl (SemanticObject SemanticObject, DocumentImpl doc) 
   throws IOException,NullPointerException
   {
      super (doc, Constants.SOML_NAMESPACE_URI, ((XMLSerializableObject) SemanticObject).getXMLNodeName());
      setSemanticObject(SemanticObject);
   }

   //
   // Get/Set Methods 
   //

   public String getAttribute(String name) 
   {
      XMLSerializableField field = ((XMLSerializableObject) getSemanticObject()).findXMLSerializableField(name);
      if (field != null)
         return field.getValue().toString();
      return null;
   }

   public String getTagName() {
      return ((XMLSerializableObject) getSemanticObject()).getXMLNodeName();
   }

   // hmmm. sense badness here..how to keep these in sync?
   public void setAttribute(String name, String value) 
   {
//      super(name,value);
      ((XMLSerializableObject)getSemanticObject()).addXMLSerializableField(name,value); 
   }

   public String getAttributeNS(String namespaceURI, String localName) 
   {
       logger.error("getAttributeNS not allowed for Xerces2DOM.SOMLElementImpl");
       return null;
   }

   public void setAttributeNS ( String uri, String name, String value)
   {
       // not allowed
       logger.error("setAttributeNS not allowed for Xerces2DOM.SOMLElementImpl");
   }

   public Attr setAttributeNode ( Attr node) 
   {
//      ((XMLSerializableObject)getSemanticObject()).addXMLSerializableField(node.getName(),node.getValue()); 
       logger.error("setAttributeNode not allowed for Xerces2DOM.SOMLElementImpl");
       return null;
   }

   public Attr getAttributeNode(String name) 
   {
       logger.error("getAttributeNode not allowed for Xerces2DOM.SOMLElementImpl");
       return null;
   }

   public Attr setAttributeNodeNS ( Attr node) 
   {
       logger.error("setAttributeNodeNS not allowed for Xerces2DOM.SOMLElementImpl");
       return null;
   }

   public Attr getAttributeNodeNS(String namespaceURI, String localName) 
   {
       logger.error("getAttributeNodeNS not allowed for Xerces2DOM.SOMLElementImpl");
       return null;
   }

   public boolean hasAttribute (String name) 
   {
      XMLSerializableField field = ((XMLSerializableObject) getSemanticObject()).findXMLSerializableField(name);
      return (field == null) ? false : true;
   }

   public boolean hasAttributeNS (String namespaceURI, String localName) 
   {
       logger.error("hasAttributeNS not allowed for Xerces2DOM.SOMLElementImpl");
       return false;
   }

   public void removeAttribute(String name) 
   {
      ((XMLSerializableObject) getSemanticObject()).removeXMLSerializableField(name);
   }

   public void removeAttributeNS (String namespaceURI, String name) 
   {
       logger.error("removeAttributeNS not allowed for Xerces2DOM.SOMLElementImpl");
   }

   public Attr removeAttributeNode (Attr oldAttr) 
   {
       logger.error("removeAttributeNode not allowed for Xerces2DOM.SOMLElementImpl");
       return null;
   }

   public SemanticObject getSemanticObject() 
   {
      return mySemanticObject;
   }

   public void setSemanticObject (SemanticObject object) 
   throws NullPointerException
   {
      if (object == null) 
         throw new NullPointerException("Can't set SOMLElement with null SemanticObject object pointer");
      else
         mySemanticObject = object;
   }

   //
   // Other Public Methods
   //

   /** */
   public Node appendChild(Node newChild) 
   throws DOMException
   {

      if (newChild instanceof SOMLElement) 
      {
         SOMLElement soElem = (SOMLElement) newChild;
         SemanticObject so = soElem.getSemanticObject();

         // Add as a member
         // TODO : add relationship
         getSemanticObject().addMember(so);

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

   /** */
   public Node insertBefore(Node newChild, Node refChild) throws DOMException
   {

      Node node = null;

      if (newChild instanceof SOMLElement)
      {
         SOMLElement soElem = (SOMLElement) newChild;
         SemanticObject so = soElem.getSemanticObject();

         // Add as a member
         // TODO: add relationship
         getSemanticObject().addMember(so);

         node = super.insertBefore(newChild,refChild);

      } else {
         throw new DOMException (DOMException.NOT_SUPPORTED_ERR, "Can't remove regular DOM Element from SOMLElement object.");
      }

      return node;
   }

   /** */
   public Node removeChild(Node oldChild) throws DOMException
   {

      if (oldChild instanceof SOMLElement)
      {
         SOMLElement soElem = (SOMLElement) oldChild;
         SemanticObject so = soElem.getSemanticObject();

         // Add as a member
         // TODO: remove relationship (!)
         getSemanticObject().removeMember(so);

         oldChild = super.removeChild(oldChild); 

      } else {
         throw new DOMException (DOMException.NOT_SUPPORTED_ERR, "Can't remove regular DOM Element from SOMLElement object.");
      }

      return oldChild;
   }

   /** */
   public Node replaceChild(Node newChild, Node oldChild) throws DOMException
   {
       Node node = super.replaceChild(newChild, oldChild);

       if (oldChild instanceof SOMLElement)
       {
          SOMLElement soElem = (SOMLElement) oldChild;
          SemanticObject so = soElem.getSemanticObject();

          // remove member
         // TODO: remove relationship (!)
          getSemanticObject().removeMember(so);

       } else {
         throw new DOMException (DOMException.NOT_SUPPORTED_ERR, "Can't insertBefore w/ DOM Element in SOMLElement object.");
       }

       if (newChild instanceof SOMLElement)
       {
          SOMLElement soElem = (SOMLElement) newChild;
          SemanticObject so = soElem.getSemanticObject();

          // add member
         // TODO: add relationship (!)
          getSemanticObject().addMember(so);

       } else {
         throw new DOMException (DOMException.NOT_SUPPORTED_ERR, "Can't insertBefore w/ DOM Element in SOMLElement object.");
       }

       return node;
   }

   /** 
   */
   public String toXMLString () {
      return ((XMLSerializableObject) getSemanticObject()).toXMLString();
   }

   /** */
   public void toXMLWriter (Writer outputWriter)
   throws java.io.IOException
   {
      toXMLWriter(outputWriter,"",false, false);
   }

   /**
   */
   public void toXMLWriter (Writer outputWriter, String indent)
   throws java.io.IOException
   {
      toXMLWriter(outputWriter, indent, false, false);
   }

   /**
    */
   public void toXMLWriter (Writer outputWriter, String indent, boolean doFirstIndent, boolean doLastNewLine)
   throws java.io.IOException
   {

      // How should we write ourselves out? IF we have a SemanticObject
      // with an ID, then we need to check the document to see
      // if we write out as an refSemanticObject node or not. Otherwise..just
      // the normal proceedure is ok.

      SemanticObject q = getSemanticObject();

      // check parent document about this..
      if(getOwnerDocument() instanceof SOMLDocumentImpl)
      {
         Hashtable idTable = ((SOMLDocumentImpl) getOwnerDocument()).getSemanticObjectIdTable();
         Hashtable prefixTable = ((SOMLDocumentImpl) getOwnerDocument()).getPrefixNamespaceMappings();
         ((XMLSerializableObject) q).toXMLWriter(idTable, prefixTable, outputWriter, indent, doFirstIndent, doLastNewLine);
      } else 
         ((XMLSerializableObject) q).toXMLWriter(outputWriter, indent, doFirstIndent, doLastNewLine);

   }

   // 
   // Private Methods
   //

}

