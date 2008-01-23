/**
 * 
 */
package net.datamodel.soml.dom.handler;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.dom.SOMLDocument;
import net.datamodel.soml.dom.SOMLDocumentHandler;
import net.datamodel.soml.dom.SOMLElement;
import net.datamodel.soml.dom.SOMLDocumentHandler.PropInfo;
import net.datamodel.xssp.dom.StartElementHandler;
import net.datamodel.xssp.dom.XSSPDocumentHandler;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author thomas
 *
 */
public class SemanticObjectRefStartElementHandler implements
		StartElementHandler {
	
	private static final Logger logger = Logger.getLogger(SemanticObjectRefStartElementHandler.class);

	/* (non-Javadoc)
	 * @see net.datamodel.xssp.dom.StartElementHandler#action(net.datamodel.xssp.dom.XSSPDocumentHandler, java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	
	public Object action (XSSPDocumentHandler handler, String namespaceURI,
			String localName, String name, Attributes attrs)
	throws SAXException {

		logger.debug("********SORef start element handler called ns:"+namespaceURI+" ln:"+localName);
		
		// let it bomb if the cast doesnt go right
		SOMLDocumentHandler shandler = (SOMLDocumentHandler) handler;
		
		SemanticObject so = shandler.findSemanticObjectById(attrs.getValue("soIdRef"));
		
		if (so != null) {
			
			// handle the reference now
			shandler.handleSOReference(so, shandler.getCurrentObjectProperty(), shandler.getCurrentNode());
			
		} else {
			
			// Cant find referenced SemanticObject, it may occur later, so we will add it
			// to a stack of things to 'post process'
			shandler.addPostProcessSOReference(attrs.getValue("soIdRef"));
			
		}
		
		return so;
	}

}
