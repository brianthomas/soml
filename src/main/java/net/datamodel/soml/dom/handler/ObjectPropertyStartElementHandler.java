package net.datamodel.soml.dom.handler;

import java.net.URI;
import java.net.URISyntaxException;

import net.datamodel.soml.dom.SOMLDocumentHandler;
import net.datamodel.xssp.dom.StartElementHandler;
import net.datamodel.xssp.dom.XSSPDocumentHandler;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ObjectPropertyStartElementHandler implements StartElementHandler {
	
	private static final Logger logger = Logger.getLogger(ObjectPropertyStartElementHandler.class);
	
	public Object action (XSSPDocumentHandler handler, String namespaceURI,
			String localName, String name, Attributes attrs)
			throws SAXException {
		
		logger.debug("***************ObjectProperty start element handler called ns:"+namespaceURI+" ln:"+localName);
		SOMLDocumentHandler shandler = (SOMLDocumentHandler) handler;
		
		try {
			shandler.recordObjectProperty(namespaceURI, localName, shandler.getCurrentSemanticObject());
		} catch (URISyntaxException e) {
			throw new SAXException (e.getMessage());
		}
		
		logger.debug("  current SO:"+shandler.getCurrentSemanticObject());
		
		return null;
	}

}
