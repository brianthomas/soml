

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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.datamodel.soml.Constant;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.dom.handler.DataTypePropertyCharDataHandler;
import net.datamodel.soml.dom.handler.DataTypePropertyEndElementHandler;
import net.datamodel.soml.dom.handler.DataTypePropertyStartElementHandler;
import net.datamodel.soml.dom.handler.NullCharDataHandler;
import net.datamodel.soml.dom.handler.NullEndElementHandler;
import net.datamodel.soml.dom.handler.ObjectPropertyEndElementHandler;
import net.datamodel.soml.dom.handler.ObjectPropertyStartElementHandler;
import net.datamodel.soml.dom.handler.RDFTypeStartElementHandler;
import net.datamodel.soml.dom.handler.SemanticObjectEndElementHandler;
import net.datamodel.soml.dom.handler.SemanticObjectRefStartElementHandler;
import net.datamodel.soml.dom.handler.SemanticObjectStartElementHandler;
import net.datamodel.xssp.dom.CharDataHandler;
import net.datamodel.xssp.dom.EndElementHandler;
import net.datamodel.xssp.dom.StartElementHandler;
import net.datamodel.xssp.dom.XSSPDocumentHandler;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.vocabulary.RDF;

/** 
 *     Contains the core SAX document handler for the Reader. It also contains
 * the basic SOML element/charData handlers (as internal classes).
 * @version $Revision$
 */
public class SOMLDocumentHandler extends XSSPDocumentHandler
{

	private static final Logger logger = Logger.getLogger(SOMLDocumentHandler.class);
	
	/** */
	private List<SemanticObject> CurrentSemanticObjectList = new Vector<SemanticObject>(); 
	private Map<String,SemanticObject> KnownSemanticObjects = new Hashtable<String,SemanticObject>();
	private List<PropInfo> CurrentObjectProperty = new Vector<PropInfo>(); 
	private PropInfo CurrentDataTypeProperty = null;
	private Map<String,PostProcessRefInfo> PostProcessRefs = new Hashtable<String, PostProcessRefInfo>(); 
	
	public SOMLDocumentHandler (SOMLDocument doc) { 
		this (doc, (Map<String,String>) null);
	}
	
	public SOMLDocumentHandler (SOMLDocument doc, Map<String,String> options) { 
		super(doc, options); 
	
		// First set up {start|end}element and char data handlers for SOML namespace
		// init start handlers
		Map<String,StartElementHandler> startHandlers = new Hashtable<String,StartElementHandler>();
		startHandlers.put("SemanticObjectType", new SemanticObjectStartElementHandler()); 
		startHandlers.put("ObjectPropertyType", new ObjectPropertyStartElementHandler()); 
		startHandlers.put("DataTypePropertyType", new DataTypePropertyStartElementHandler()); 
		startHandlers.put("refSOType", new SemanticObjectRefStartElementHandler()); 
		addStartElementHandlers(startHandlers, Constant.SOML_NAMESPACE_URI); 
	
		// init end element handlers
		Map<String,EndElementHandler> endHandlers = new Hashtable<String,EndElementHandler>();
		endHandlers.put("SemanticObjectType", new SemanticObjectEndElementHandler()); 
		endHandlers.put("ObjectPropertyType", new ObjectPropertyEndElementHandler()); 
		endHandlers.put("DataTypePropertyType", new DataTypePropertyEndElementHandler()); 
		endHandlers.put("refSOType", new NullEndElementHandler()); 
		addEndElementHandlers(endHandlers, Constant.SOML_NAMESPACE_URI); 
		
		Map<String,CharDataHandler> cDataHandlers = new Hashtable<String,CharDataHandler>();
		cDataHandlers.put("SemanticObjectType", new NullCharDataHandler());
		cDataHandlers.put("ObjectPropertyType", new NullCharDataHandler());
		cDataHandlers.put("DataTypePropertyType", new DataTypePropertyCharDataHandler());
		addCharDataHandlers(cDataHandlers, Constant.SOML_NAMESPACE_URI);
		
		// RDF namespace stuff
		Map<String,StartElementHandler> rdfstartHandlers = new Hashtable<String,StartElementHandler>();
		rdfstartHandlers.put("rdfPropertyType", new RDFTypeStartElementHandler()); 
//		rdfstartHandlers.put("type", new RDFTypeStartElementHandler()); 
		addStartElementHandlers(rdfstartHandlers, RDF.getURI()); 
		
		Map<String,EndElementHandler> rdfEndHandlers = new Hashtable<String,EndElementHandler>();
		rdfEndHandlers.put("rdfPropertyType", new NullEndElementHandler()); 
		addEndElementHandlers(rdfEndHandlers, RDF.getURI()); 
		
		// Set up (standalone) Element associations
		addElementToComplexTypeAssociation("semanticObject", Constant.SOML_NAMESPACE_URI, "SemanticObjectType", Constant.SOML_NAMESPACE_URI);
		addElementToComplexTypeAssociation("semanticObjectRef", Constant.SOML_NAMESPACE_URI, "refSOType", Constant.SOML_NAMESPACE_URI);
		addElementToComplexTypeAssociation("type", RDF.getURI(), "rdfPropertyType", RDF.getURI());
		
	}
	
	public final void handleSOReference (SemanticObject so, PropInfo oinfo, Node current) 
	throws SAXException 
	{
		// check if we are target of a property
		// and if so, add this in
		if (oinfo != null) {
			oinfo.getParentSO().addProperty(oinfo.getURI(), so); 
		}
		
		// Add as a SOMLElement to our document in 2 cases:
		// 1. if no doc root exists it becomes the doc root
		// or 
		// 2. if the current node is not a SOMLElement, then add
		//
        if(current != null) {
        	if (!(current instanceof SOMLElement)) {
        		Element elem = ((SOMLDocument) getDocument()).createSOMLElement(so);
        		current.appendChild(elem);
        	}
        } else { 
        	String msg = "Cant possibly add a referenced SO as the document root! Throw a big stinkin error here"; 
        	logger.error(msg);
        	throw new SAXException(msg);
        }
        
	}
	
	/** Return the current, working SO.
	 * 
	 * @return
	 */
	public final SemanticObject getCurrentSemanticObject() {
		if (CurrentSemanticObjectList.size() > 0)
			return CurrentSemanticObjectList.get(this.CurrentSemanticObjectList.size()-1);
		return null;
	}
	
	/**
	 * 
	 * @param so
	 */
	public final void recordSemanticObject (SemanticObject so) {
		CurrentSemanticObjectList.add(so);
		logger.debug("recording SO with id:"+so.getId());
		KnownSemanticObjects.put(so.getId(), so);
	}
	
	/**
	 * 
	 * @param so
	 */
	public final void unrecordLastSemanticObject () {
		CurrentSemanticObjectList.remove(CurrentSemanticObjectList.size()-1);
	}
	
	/** Return the current, working ObjectProperty.
	 * 
	 * @return
	 */
	public final PropInfo getCurrentObjectProperty() {
		if (CurrentObjectProperty.size() > 0)
			return CurrentObjectProperty.get(this.CurrentObjectProperty.size()-1);
		return null;
	}
	
	/** Return the current, working DataType property.
	 * 
	 * @return
	 */
	public final PropInfo getCurrentDataTypeProperty() {
		return CurrentDataTypeProperty;
	}
	
	/**
	 */
	public final void recordObjectProperty(String namespaceURI, String ln, SemanticObject parent) 
	throws URISyntaxException 
	{
		CurrentObjectProperty.add(new PropInfo(namespaceURI, ln, parent));
	}
	
	/**
	 * 
	 * @param so
	 */
	public final void unrecordLastObjectProperty () {
		CurrentObjectProperty.remove(CurrentObjectProperty.size()-1);
	}

	/**
	 * 
	 * @param namespaceURI
	 * @param ln
	 * @param parent
	 * @throws URISyntaxException
	 */
	public final void recordDataTypeProperty(String namespaceURI, String ln, SemanticObject parent) 
	throws URISyntaxException 
	{
		CurrentDataTypeProperty = new PropInfo(namespaceURI, ln, parent);
	}
	
	/**
	 * 
	 */
	public final void unrecordLastDataTypeProperty () { CurrentDataTypeProperty = null; }
	
	/**
	 * 
	 * @param id
	 */
	public final void addPostProcessSOReference (String id) {
		PostProcessRefs.put (id, new PostProcessRefInfo(getCurrentObjectProperty(), getCurrentNode()));
	}

	/** Find a SemanticObject by id. If the object being requested has not
	 * yet been parsed, this method will return null.
	 * 
	 * @param id
	 * @return
	 */
	public final SemanticObject findSemanticObjectById (String id) {
		return KnownSemanticObjects.get(id);
	}
	
	@Override
	public void endDocument() 
	throws SAXException 
	{
		// pick up all dangling refs
		for (String id : PostProcessRefs.keySet()) {
			SemanticObject so = this.findSemanticObjectById(id);
			if (so == null)
				throw new SAXException("Cant find referenced SO (id:"+id+") in document.");
			
			PostProcessRefInfo ppri = PostProcessRefs.get(id);
			handleSOReference(so, ppri.getObjPropInfo(), ppri.getNode()); 
		}
		
		// do super class stuff
		super.endDocument();
	}

	public class PostProcessRefInfo {
		private Node n = null;
		private PropInfo opi = null;
		PostProcessRefInfo (PropInfo opInfo, Node node) {
			opi = opInfo; n = node;
		}
		public final Node getNode() {return n; }
		public final PropInfo getObjPropInfo() {return opi; }
	}
	
	public class PropInfo {
		private String namespaceURI;
		private String localName; 
		private SemanticObject parent;
		private URI uri = null;
		
		public PropInfo(String n, String l, SemanticObject p) 
		throws URISyntaxException {
			namespaceURI = n; localName = l;
			parent = p;
			uri = new URI(n+l);
		}
		public final SemanticObject getParentSO() { return parent; }
		public final URI getURI() { return uri; }
		public final String getNamespaceURI() { return namespaceURI; }
	}
	
} // End of SOMLDocumentHandler class 

