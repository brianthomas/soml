

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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.support.SOMLDocument;
import net.datamodel.soml.support.SOMLElement;
import net.datamodel.xssp.XMLSerializableObject;
import net.datamodel.xssp.support.Constants;
import net.datamodel.xssp.support.Specification;
import net.datamodel.xssp.support.XMLWriter;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.ElementNSImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** 
 *  Read any XML Document into a specialized DOM document -- SOMLDocumentImpl.
 */

// Based on Xerces DocumentImpl class.
public class SOMLDocumentImpl extends DocumentImpl 
implements SOMLDocument
{

	private static final Logger logger = Logger.getLogger(SOMLDocumentImpl.class);
	
   // 
   // Fields
   //
   protected Map<String,String> PrefixNamespaceMappingHashtable = new Hashtable<String,String>();
   protected Map<String,SemanticObject> SemanticObjectIdTable = new Hashtable<String,SemanticObject>();

   // package private.. 
   List<SemanticObject> SemanticObjectList = new Vector<SemanticObject>();

   //
   // Constructors
   //

   /** Set the prefix mappings for this document.
    */
   public void setPrefixNamespaceMappings(Map<String,String> prefixMappings) {
      PrefixNamespaceMappingHashtable.clear();
      PrefixNamespaceMappingHashtable.putAll(prefixMappings);
   }

   /** Get the prefix mappings for this document.
    */
   public Map<String,String> getPrefixNamespaceMappings() {
      return PrefixNamespaceMappingHashtable;
   }

   /** Set a particular prefix/namespaceURI mapping in this document.
    */
   public void setPrefixNamespaceMapping (String prefix, String namespaceURI) 
   {
      PrefixNamespaceMappingHashtable.put(prefix, namespaceURI);
   }

   /** Get a the namespaceURI for a particular prefix.
    */
   public String getNamespace (String prefix)
   {
      return (String) PrefixNamespaceMappingHashtable.get(prefix);
   }

   /** Set the (root) document element.
    */
   public void setDocumentElement (Element elem) 
   {

      Element oldRoot = getDocumentElement();
      if(oldRoot != null) 
         this.replaceChild(elem, oldRoot);
      else
         this.appendChild(elem);

   }

   // public void renameNode (org.w3c.dom.Node node, java.lang.String s1, java.lang.String s2) { }

   public Map<String,SemanticObject> getSemanticObjectIdTable ()  {
   //    updateSemanticObjectIdTable();
       return SemanticObjectIdTable;
   }

   /** Get the quantities held by this document.
    * @param deep if true then get all quantities in the document. A false value will
    *             only return quantities which are not "owned" by other quantities.
    * @return List of SemanticObject objects
    */

   public List<SemanticObject> getSemanticObjects (boolean deep) 
   {
      //return findQuantities(this, deep);
      if(deep)
      {
         List deepList = new Vector(); 
         Iterator iter = SemanticObjectList.iterator();
         while (iter.hasNext()) 
         {
            SemanticObject so = (SemanticObject) iter.next();
            deepList.add(so);
            deepList.addAll(findSemanticObjects(so));
         }
         return deepList;
      }

      return SemanticObjectList;
   }

   //
   // Other Public Methods
   //

   public Node appendChild(Node newChild) throws DOMException
   {
        Node node = super.appendChild(newChild);

        if(node instanceof SOMLElement) {
           SemanticObjectList.add(((SOMLElement) node).getSemanticObject());
        }

        return node;
   }

   public Node removeChild(Node oldChild) throws DOMException
   {
        Node node = super.removeChild(oldChild);
        if(node instanceof SOMLElement) {
           SemanticObjectList.remove(((SOMLElement) node).getSemanticObject());
        }
        return node;
   }

   public Node replaceChild(Node newChild, Node oldChild) throws DOMException
   {
       Node node = super.replaceChild(newChild,oldChild);

       if(oldChild instanceof SOMLElement) 
           SemanticObjectList.remove(((SOMLElement) oldChild).getSemanticObject());

       if(newChild instanceof SOMLElement) 
           SemanticObjectList.add(((SOMLElement) newChild).getSemanticObject());

       return node;
   }

   public Node insertBefore(Node newChild, Node refChild) throws DOMException
   {
       Node node = super.insertBefore(newChild,refChild);
        
       if(newChild instanceof SOMLElement) 
           SemanticObjectList.add(((SOMLElement) newChild).getSemanticObject());

       return node;
   }

   public Element createElement (String tagName) throws DOMException
   {

      ElementImpl retval = null;

      try {
         retval = new SOTrackingElementImpl(this, tagName );
      } catch (DOMException e) {
         // tailor QTrackingElement message?
         throw new DOMException(DOMException.INVALID_STATE_ERR, e.getMessage());
      }
      return retval;
   }

   public Element createElementNS (String uri, String tagName) 
      throws DOMException
   {

      ElementNSImpl retval = null;

      try {
         retval = new SOTrackingElementImpl(this, uri, tagName);
      } catch (DOMException e) {
         // tailor QTrackingElement message?
         throw new DOMException(DOMException.INVALID_STATE_ERR, e.getMessage());
      }
      return retval;
   }

   public Element createElementNS (String uri, String qName, String lName) 
       throws DOMException
   {
      ElementNSImpl retval = null;

      try {
         retval = new SOTrackingElementImpl(this, uri, qName, lName);
      } catch (DOMException e) {
         // tailor QTrackingElement message?
         throw new DOMException(DOMException.INVALID_STATE_ERR, e.getMessage());
      }

      return retval;
   }


   public SOMLElement createSOMLElement(SemanticObject SemanticObject) 
   throws DOMException
   {

      SOMLElementImpl qElem = null;
      try {
         qElem = new SOMLElementImpl(SemanticObject, this);
      } catch (IOException e) {
         // dunno if this is the right error code.. but what the hell
         throw new DOMException(DOMException.INVALID_STATE_ERR, e.getMessage());
      }
      return qElem;
   }

   public SOMLElement createSOMLElementNS(String namespaceURI, SemanticObject SemanticObject)
   throws DOMException
   {

      SOMLElementImpl qElem = null;
      try {
         qElem = new SOMLElementImpl(namespaceURI, SemanticObject, this);
      } catch (IOException e) {
         // dunno if this is the right error code.. but what the hell
         throw new DOMException(DOMException.INVALID_STATE_ERR, e.getMessage());
      }
      return qElem;

   }

   /** Write the XML representation of this document as a string.
    */
   public String toXMLString () 
   {

      StringWriter writer = new StringWriter();
      try {
         toXMLWriter(writer);
      } catch (IOException e) {
        logger.error("Can't create string representation of SOML document");
        e.printStackTrace();
      }

      return writer.toString();

   }


   /** Write this document out the supplied Writer.
    */
   public void toXMLWriter (Writer outputWriter) 
   throws java.io.IOException 
   {

// unfortunately, enforcing an update at this point
// is the only way to be sure
       updateSemanticObjectIdTable();

      Specification spec = Specification.getInstance();
      int indentLevel = spec.getPrettyOutputIndentationLength();
      boolean isPretty = spec.isPrettyOutput();

      // need to add in attributes which are part of the prefix-namespaceURI
      // mappings...
      insertPrefixMappings();

      logger.debug("Normalize the document");
      getDocumentElement().normalize();

      XMLWriter xmlWriter = new XMLWriter (outputWriter, false);
      xmlWriter.write(this);

//      if(isPretty)
//          outputWriter.write(Constants.NEW_LINE);

      // and now remove them.
      logger.debug("Remove prefix mappings");
      removePrefixMappings();

      logger.debug("Finish write document");
   }

  /** Write this document out to the indicated file. The file will be clobbered
      by the output, so it is advisable to check for the existence of the file
      *before* using this method if you are worried about losing prior information.
  */
  public void toXMLFile (String filename)
  throws java.io.IOException
  {

    // open file writer
      Writer fileout = new BufferedWriter (new FileWriter(filename));
      // FileWriter fileout = new FileWriter(filename);
      toXMLWriter(fileout);
      fileout.close();
  }

   //
   // Private Methods
   //

   // insert prefix mappings in root element
   private void insertPrefixMappings() 
   {

       Element root = getDocumentElement();
       logger.debug("START insertPrefixMappings");
       if(root != null) {

    	   logger.debug(" root not null insertPrefixMappings");
          // first check if the table is empty, if so, try to simulate one
          // based on the quantitie that we find.
          if (PrefixNamespaceMappingHashtable.isEmpty()) 
          {
             logger.warn("No prefix namespace table in document...(rootNode:"+root.getTagName()+")...doing slow auto-generation technique.");

             // first add in the necessaries
             PrefixNamespaceMappingHashtable.put("xsi",Constants.XML_SCHEMA_INSTANCE_NAMESPACE_URI);
             // PrefixNamespaceMappingHashtable.put("",Constants.SOML_NAMESPACE_URI);

             // pre-add in the root element namespace as the default
             // this overrides, and is simpler/faster than the following chunk of 
             // commented out code
             String rootURI = root.getNamespaceURI();
             if (rootURI != null) {
                 logger.debug(" ROOT Namepsace URI is :"+rootURI);
                 PrefixNamespaceMappingHashtable.put("",rootURI);
             } 

             // get the root namespace (old way) 
             // if(root instanceof XMLSerializableObject) {
             //    XMLSerializableObject so = (XMLSerializableObject) root;
             //    logger.debug("Generation of namespace table from SemanticObject:"+so.getXMLNodeName());
             //    PrefixNamespaceMappingHashtable.putAll( 
             //                 XMLSerializableObjectImpl.generatePrefixNamespaceTable(so, PrefixNamespaceMappingHashtable));
             // }
             
             // go deep?
             for (SemanticObject so : getSemanticObjects(true)) {
                    PrefixNamespaceMappingHashtable.putAll( 
                              Utility.generatePrefixNamespaceTable(so, PrefixNamespaceMappingHashtable));
             }
             logger.debug("* Finished Auto-Generation of namespace table.");
          }

          Enumeration keys = PrefixNamespaceMappingHashtable.keys();
          if(root instanceof SOMLElement) {
            SemanticObject q = ((SOMLElement) root).getSemanticObject(); 
            XMLSerializableObject xmlq = (XMLSerializableObject) q;
            while (keys.hasMoreElements()) {
               String prefix = (String) keys.nextElement(); 
               String namespaceURI = (String) PrefixNamespaceMappingHashtable.get(prefix);
               String name = "xmlns";
               if(!prefix.equals(""))
                   name = "xmlns:"+prefix;
               xmlq.addXMLSerializableField (name, namespaceURI);
               logger.debug("   "+name+"="+namespaceURI);
            }

          } else {

            while (keys.hasMoreElements()) {
               String prefix = (String) keys.nextElement(); 
               String namespaceURI = (String) PrefixNamespaceMappingHashtable.get(prefix);
               String name = "xmlns";
               Attr attrib = createAttribute(name);
               if(!prefix.equals(""))
               {
                   name = "xmlns:"+prefix;
                   attrib = createAttributeNS(namespaceURI, name, prefix);
               }
               attrib.setValue(namespaceURI);
               root.setAttributeNode(attrib);
               logger.debug("   "+name+"="+namespaceURI);
            }
         }

       }
       logger.debug("FINISH insertPrefixMappings");
   }

   // remove prefix mappings in root element
   private void removePrefixMappings() 
   {
       Element root = getDocumentElement();
       if(root != null) {

          Enumeration keys = PrefixNamespaceMappingHashtable.keys();
          if(root instanceof SOMLElement) {
            SemanticObject q = ((SOMLElement) root).getSemanticObject();
            XMLSerializableObject xmlq = (XMLSerializableObject) q;
            while (keys.hasMoreElements()) {
               String prefix = (String) keys.nextElement();
               String name = "xmlns";
               if(!prefix.equals(""))
                   name = "xmlns:"+prefix;
               xmlq.removeXMLSerializableField(name);
            }

          } else {

             while (keys.hasMoreElements()) {
                String prefix = (String) keys.nextElement();
                String name = "xmlns";
                if(!prefix.equals(""))
                   name = "xmlns:"+prefix;
                root.removeAttribute(name);
             }
          }
       }
   }

   // build a hashtable of all Q's with qIds.
   // repeat ids in q's are ignored
   private void updateSemanticObjectIdTable() 
   {
      List<SemanticObject> deepList = getSemanticObjects(true);
      SemanticObjectIdTable = new Hashtable<String,String>();
      Iterator iter = deepList.iterator();
      while(iter.hasNext()) {
         SemanticObject so = (SemanticObject) iter.next();
         if(so.getId() != null) {
            if(!SemanticObjectIdTable.containsKey(so.getId()))
               SemanticObjectIdTable.put(so.getId(),so);
         }
      }
   }

   private List findSemanticObjects (SemanticObject parent)
   {
       List qList = new Vector();
       List members = parent.getMemberList();
       Iterator iter = members.iterator();
       while (iter.hasNext())
       {
            SemanticObject q = (SemanticObject) iter.next();
            qList.add(q);
            qList.addAll(findSemanticObjects(q));
       }

       return qList;
   }

}

