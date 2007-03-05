

// QML Xerces2 DOM SOTrackingElementImpl
// CVS $Id$

// SOTrackingElementImpl.java Copyright (C) 2005 Brian Thomas,

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

import net.datamodel.qml.SemanticObject;
import net.datamodel.qml.XMLSerializableObject;
import net.datamodel.qml.core.AtomicQuantityImpl;
import net.datamodel.qml.core.XMLSerializableField;
import net.datamodel.qml.support.Constants;
import net.datamodel.qml.support.QMLElement;

// import net.datamodel.qml.QMLDocumentImpl;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xerces.dom.CoreDocumentImpl;

// import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** 
     An implementation of Element based on the Xerces2 ElementNSImpl class.
     We need this so we can keep track of QMLElements (e.g. quantities) that 
     are within the parent document.
 */

public class SOTrackingElementImpl extends ElementNSImpl
{
	
	private static final Logger logger = Logger.getLogger(SOTrackingElementImpl.class);

   //
   // Constructors
   //

   public SOTrackingElementImpl (CoreDocumentImpl doc, String namespaceURI, String qName, String localName )
   throws org.w3c.dom.DOMException
   {
      super (doc, namespaceURI, qName, localName);
      ownerDocument = doc;
   }

   public SOTrackingElementImpl (CoreDocumentImpl doc, String namespaceURI, String qName )
   throws org.w3c.dom.DOMException
   {
      super (doc, namespaceURI, qName);
      ownerDocument = doc;
   }

   public SOTrackingElementImpl (CoreDocumentImpl doc, String tag)
   {
      super(doc, tag);
      ownerDocument = doc;
   }

   //
   // Other Public Methods
   //

   public Node appendChild (Node newChild) throws DOMException
   {

      newChild = super.appendChild(newChild);

      checkForQuantitiesAdd(newChild);

      return newChild;
   }

   public Node insertBefore(Node newChild, Node refChild) throws DOMException
   {
       Node node = super.insertBefore(newChild,refChild);

       checkForQuantitiesAdd(newChild);

       return node;
   }

   public Node removeChild(Node oldChild) throws DOMException
   {
       Node node = super.removeChild(oldChild);

       checkForQuantitiesRemove(oldChild);

       return node;
   }

   public Node replaceChild(Node newChild, Node oldChild) throws DOMException
   {
       Node node = super.replaceChild(newChild, oldChild);

       checkForQuantitiesAdd(newChild);
       checkForQuantitiesRemove(oldChild);

       return node;
   }

   // Private methods.
   //
   private void checkForQuantitiesAdd (Node node) 
   {

      if (node == null)
          return;

      if (node instanceof QMLElement)
      {
         QMLElement qElem = (QMLElement) node;
         SemanticObject q = qElem.getSemanticObject();

         // now tell parent document about this..
         if(getOwnerDocument() instanceof SOMLDocumentImpl)
         {
             ((SOMLDocumentImpl) getOwnerDocument()).QuantityList.add(q);
             logger.debug("  ** Adding Q to document:"+q);
         }
      }
   }

   private void checkForQuantitiesRemove (Node node)
   {

      if (node == null)
          return;

      if (node instanceof QMLElement)
      {
         QMLElement qElem = (QMLElement) node;
         SemanticObject q = qElem.getSemanticObject();

         // now tell parent document about this..
         if(getOwnerDocument() instanceof SOMLDocumentImpl)
         {
             ((SOMLDocumentImpl) getOwnerDocument()).QuantityList.remove(q);
             logger.debug("  ** Removing Q from document:"+q);
         }

      }
   }


}

