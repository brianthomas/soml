package net.datamodel.soml.dom.handler;

import java.net.URI;

import net.datamodel.soml.dom.SOMLDocument;
import net.datamodel.soml.dom.SOMLDocumentHandler;
import net.datamodel.soml.dom.SOMLElement;
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
		
		SemanticObjectImpl so = new SemanticObjectImpl();
		so.setAttributeFields(attrs); // set XML attributes from passed list
		
		// check if we are target of a property
		URI uri = shandler.getCurrentObjectProperty();
		if (uri != null) {
			shandler.getCurrentSemanticObject().addProperty(uri, so); 
		}
		
		// Add as a SOMLElement to our document, if no doc root exists,
		// otherwise, it will wait to be added as an object property later
		Element elem = ((SOMLDocument) handler.getDocument()).createSOMLElementNS(namespaceURI, so);
		
        Node current = handler.getCurrentNode();
        if(current != null) {
        	if (!(current instanceof SOMLElement))
        		current.appendChild(elem);
        } else { 
            handler.getDocument().setDocumentElement(elem);
        }
		
		shandler.recordSemanticObject(so);
		
		return so;
	}

}
