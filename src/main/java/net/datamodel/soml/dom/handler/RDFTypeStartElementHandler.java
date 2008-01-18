/**
 * 
 */
package net.datamodel.soml.dom.handler;

import java.net.URI;
import java.net.URISyntaxException;

import net.datamodel.soml.dom.SOMLDocumentHandler;
import net.datamodel.xssp.dom.StartElementHandler;
import net.datamodel.xssp.dom.XSSPDocumentHandler;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author thomas
 *
 */
public class RDFTypeStartElementHandler implements StartElementHandler {

	private static final Logger logger = Logger.getLogger(RDFTypeStartElementHandler.class);

	/* (non-Javadoc)
	 * @see net.datamodel.xssp.dom.StartElementHandler#action(net.datamodel.xssp.dom.XSSPDocumentHandler, java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public Object action(XSSPDocumentHandler handler, String namespaceURI,
			String localName, String name, Attributes attrs)
			throws SAXException 
	{
		
		logger.debug("RDFType handler called");
		// TODO Auto-generated method stub
		SOMLDocumentHandler shandler = (SOMLDocumentHandler) handler;
		
		String strType = attrs.getValue("resource");
		try {
			URI type = new URI(strType);
			logger.debug(" current SO:"+shandler.getCurrentSemanticObject());
			shandler.getCurrentSemanticObject().addRDFTypeURI(type);
		} catch (URISyntaxException e) {
			logger.error(" Cant set rdf:type for SO. URI:"+strType+" is bad??");
		}
		
		return null;
	}

}
