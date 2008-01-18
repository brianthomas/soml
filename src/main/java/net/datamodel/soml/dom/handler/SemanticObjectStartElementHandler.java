package net.datamodel.soml.dom.handler;

import java.net.URI;
import java.net.URISyntaxException;

import net.datamodel.soml.Constant;
import net.datamodel.soml.dom.SOMLDocument;
import net.datamodel.soml.dom.SOMLDocumentHandler;
import net.datamodel.soml.dom.SOMLElement;
import net.datamodel.soml.dom.SOMLDocumentHandler.ObjectPropInfo;
import net.datamodel.soml.impl.SemanticObjectImpl;
import net.datamodel.xssp.dom.StartElementHandler;
import net.datamodel.xssp.dom.XSSPDocumentHandler;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class SemanticObjectStartElementHandler 
implements StartElementHandler 
{

	private static final Logger logger = Logger.getLogger(SemanticObjectStartElementHandler.class);

	public Object action(XSSPDocumentHandler handler, String namespaceURI,
			String localName, String name, Attributes attrs)
			throws SAXException {
		
		logger.debug("***************SO vanilla start element handler called ns:"+namespaceURI+" ln:"+localName);
		
		// let it bomb if the cast doesnt go right
		SOMLDocumentHandler shandler = (SOMLDocumentHandler) handler;
		
		// build the URI string
		StringBuffer soUriStr = new StringBuffer(namespaceURI);
		soUriStr.append(localName);
		
		SemanticObjectImpl so = 
			new SemanticObjectImpl(SemanticObjectImpl.createURI(soUriStr.toString()), localName);
		
		// set XML attributes from passed list
		so.setAttributeFields(attrs); 
		
		// check if we are target of a property
		// and if so, add this in
		ObjectPropInfo oinfo = shandler.getCurrentObjectProperty();
		if (oinfo != null) {
			shandler.getCurrentSemanticObject().addProperty(oinfo.getURI(), so); 
		}
		
		// Add as a SOMLElement to our document in 2 cases:
		// 1. if no doc root exists it becomes the doc root
		// or 
		// 2. if the current node is not a SOMLElement, then add
		//
        Node current = handler.getCurrentNode();
        if(current != null) {
        	if (!(current instanceof SOMLElement)) {
        		Element elem = ((SOMLDocument) handler.getDocument()).createSOMLElementNS(namespaceURI, so);
        		current.appendChild(elem);
        	}
        } else { 
       		Element elem = ((SOMLDocument) handler.getDocument()).createSOMLElementNS(namespaceURI, so);
            handler.getDocument().setDocumentElement(elem);
        }
		
        // note the current, working SemanticObject
		shandler.recordSemanticObject(so);
		
		return so;
	}

}
