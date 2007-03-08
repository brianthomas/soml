

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

public class SOMLElementImpl extends XSSPElementImpl
implements SOMLElement
{
	
	
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

   public SemanticObject getSemanticObject() 
   {
	   return (SemanticObject) getObject();
   }

   public void setSemanticObject (SemanticObject object) 
   throws NullPointerException
   {
	   setObject(object);
   }

   //
   // Other Public Methods
   //

   /** */
   @Override
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
   @Override
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
   @Override
   public Node removeChild(Node oldChild) 
   throws DOMException
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
   @Override
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

}

