package net.datamodel.soml.dom.handler;

import net.datamodel.xssp.dom.StartElementHandler;
import net.datamodel.xssp.dom.XSSPDocumentHandler;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DataPropertyStartElementHandler implements StartElementHandler {
	
	private static final Logger logger = Logger.getLogger(DataPropertyStartElementHandler.class);
	
	public Object action (XSSPDocumentHandler handler, String namespaceURI,
			String localName, String name, Attributes attrs)
			throws SAXException {
		
		logger.debug("***************DataProperty start element handler called ns:"+namespaceURI+" ln:"+localName);
		return null;
	}

}
