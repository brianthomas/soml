package net.datamodel.soml.dom.handler;

import net.datamodel.soml.dom.SOMLDocumentHandler;
import net.datamodel.xssp.dom.EndElementHandler;
import net.datamodel.xssp.dom.XSSPDocumentHandler;

import org.xml.sax.SAXException;

public class DataTypePropertyEndElementHandler implements EndElementHandler {

	public void action(XSSPDocumentHandler handler) throws SAXException {
		SOMLDocumentHandler shandler = (SOMLDocumentHandler) handler;
		shandler.unrecordLastDataTypeProperty();
	}

}
