package net.datamodel.soml.dom.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import net.datamodel.xssp.dom.StartElementHandler;
import net.datamodel.xssp.dom.XSSPDocumentHandler;

public class NullStartElementHandler implements StartElementHandler {

	public Object action(XSSPDocumentHandler handler, String namespaceURI,
			String localName, String name, Attributes attrs)
			throws SAXException {
		return null;
	}

}
