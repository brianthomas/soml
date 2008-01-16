

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
import net.datamodel.soml.dom.handler.DataPropertyStartElementHandler;
import net.datamodel.soml.dom.handler.NullCharDataHandler;
import net.datamodel.soml.dom.handler.NullEndElementHandler;
import net.datamodel.soml.dom.handler.NullStartElementHandler;
import net.datamodel.soml.dom.handler.ObjectPropertyStartElementHandler;
import net.datamodel.soml.dom.handler.RDFTypeStartElementHandler;
import net.datamodel.soml.dom.handler.SemanticObjectEndElementHandler;
import net.datamodel.soml.dom.handler.SemanticObjectStartElementHandler;
import net.datamodel.xssp.dom.CharDataHandler;
import net.datamodel.xssp.dom.EndElementHandler;
import net.datamodel.xssp.dom.StartElementHandler;
import net.datamodel.xssp.dom.XSSPDocumentHandler;

import org.apache.log4j.Logger;

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
	private List<ObjectPropInfo> CurrentObjectProperty = new Vector<ObjectPropInfo>(); 
	
	public SOMLDocumentHandler (SOMLDocument doc) { 
		this (doc, (Map<String,String>) null);
	}
	
	public SOMLDocumentHandler (SOMLDocument doc, Map<String,String> options) { 
		super(doc, options); 
	
		// init start handlers
		Map<String,StartElementHandler> startHandlers = new Hashtable<String,StartElementHandler>();
		startHandlers.put("SemanticObjectType", new SemanticObjectStartElementHandler()); 
		startHandlers.put("ObjectPropertyType", new ObjectPropertyStartElementHandler()); 
		startHandlers.put("DataPropertyType", new DataPropertyStartElementHandler()); 
		startHandlers.put("rdfPropertyType", new NullStartElementHandler()); 
		addStartElementHandlers(startHandlers, Constant.SOML_NAMESPACE_URI); 
	
		// init end element handlers
		Map<String,EndElementHandler> endHandlers = new Hashtable<String,EndElementHandler>();
		endHandlers.put("SemanticObjectType", new SemanticObjectEndElementHandler()); 
		endHandlers.put("ObjectPropertyType", new NullEndElementHandler()); 
		addEndElementHandlers(endHandlers, Constant.SOML_NAMESPACE_URI); 
		
		Map<String,CharDataHandler> cDataHandlers = new Hashtable<String,CharDataHandler>();
		cDataHandlers.put("SemanticObjectType", new NullCharDataHandler());
		cDataHandlers.put("ObjectPropertyType", new NullCharDataHandler());
		addCharDataHandlers(cDataHandlers, Constant.SOML_NAMESPACE_URI);
		
		Map<String,StartElementHandler> rdfstartHandlers = new Hashtable<String,StartElementHandler>();
		rdfstartHandlers.put("type", new RDFTypeStartElementHandler()); 
		addStartElementHandlers(rdfstartHandlers, RDF.getURI()); 
		
		Map<String,EndElementHandler> rdfEndHandlers = new Hashtable<String,EndElementHandler>();
		rdfEndHandlers.put("type", new NullEndElementHandler()); 
		addEndElementHandlers(rdfEndHandlers, RDF.getURI()); 
		
		addElementToComplexTypeAssociation("semanticObject", Constant.SOML_NAMESPACE_URI, "SemanticObjectType", Constant.SOML_NAMESPACE_URI);
		addElementToComplexTypeAssociation("type", RDF.getURI(), "type", RDF.getURI());
		
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
	public final ObjectPropInfo getCurrentObjectProperty() {
		if (CurrentObjectProperty.size() > 0)
			return CurrentObjectProperty.get(this.CurrentObjectProperty.size()-1);
		return null;
	}
	
	/**
	 */
	public final void recordObjectProperty(String namespaceURI, String ln) 
	throws URISyntaxException 
	{
		CurrentObjectProperty.add(new ObjectPropInfo(namespaceURI, ln));
	}
	
	/**
	 * 
	 * @param so
	 */
	public final void unrecordLastObjectProperty () {
		CurrentObjectProperty.remove(CurrentObjectProperty.size()-1);
	}

	public class ObjectPropInfo {
		private String namespaceURI;
		private String localName; 
		private URI uri = null;
		public ObjectPropInfo(String n, String l) 
		throws URISyntaxException {
			namespaceURI = n; localName = l;
			uri = new URI(n+l);
		}
		public final URI getURI() { return uri; }
		public final String getNamespaceURI() { return namespaceURI; }
	}
} // End of SOMLDocumentHandler class 

