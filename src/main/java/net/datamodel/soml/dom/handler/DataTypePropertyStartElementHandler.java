package net.datamodel.soml.dom.handler;

import java.net.URISyntaxException;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.dom.SOMLDocumentHandler;
import net.datamodel.xssp.dom.StartElementHandler;
import net.datamodel.xssp.dom.XSSPDocumentHandler;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DataTypePropertyStartElementHandler implements StartElementHandler {
	
	private static final Logger logger = Logger.getLogger(DataTypePropertyStartElementHandler.class);
	
	public Object action (XSSPDocumentHandler handler, String namespaceURI,
			String localName, String name, Attributes attrs)
			throws SAXException {
		
		logger.debug("************** DataProperty start element handler called ns:"+namespaceURI+" ln:"+localName);
		
		SOMLDocumentHandler shandler = (SOMLDocumentHandler) handler;
		
		SemanticObject so = shandler.getCurrentSemanticObject();
		if (so != null) {
			try {
				shandler.recordDataTypeProperty(namespaceURI, localName, so);
			} catch (URISyntaxException use) {
				throw new SAXException ("Cant add DataType property: "+use.getMessage());
			}
		} else 
			throw new SAXException("Parser config problem? Cant add DataType property...no current SemanticObject.");
		
		return null;
	}

}
