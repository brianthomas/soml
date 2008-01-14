package net.datamodel.soml.dom.handler;

import net.datamodel.soml.dom.SOMLDocumentHandler;
import net.datamodel.xssp.dom.EndElementHandler;
import net.datamodel.xssp.dom.XSSPDocumentHandler;

import org.xml.sax.SAXException;

public class SemanticObjectEndElementHandler implements EndElementHandler {

	public void action(XSSPDocumentHandler handler) throws SAXException {
		net.datamodel.soml.dom.SOMLDocumentHandler shandler = (SOMLDocumentHandler) handler;
		// remove currentSO
		shandler.unrecordLastSemanticObject(); 
	}

}
