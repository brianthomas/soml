package net.datamodel.soml.dom.handler;

import net.datamodel.soml.dom.SOMLDocumentHandler;
import net.datamodel.soml.dom.SOMLDocumentHandler.PropInfo;
import net.datamodel.xssp.dom.CharDataHandler;
import net.datamodel.xssp.dom.XSSPDocumentHandler;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class DataTypePropertyCharDataHandler implements CharDataHandler {

	private static final Logger logger = Logger.getLogger(DataTypePropertyCharDataHandler.class);
	
	public void action(XSSPDocumentHandler handler, char[] buf, int offset,
			int len) throws SAXException {
		
		logger.debug("********** DT CharDataHandler called.");
		SOMLDocumentHandler shandler = (SOMLDocumentHandler) handler;
		
		PropInfo dtpi = shandler.getCurrentDataTypeProperty();
		String value = new String (buf,offset,len);
		
		dtpi.getParentSO().addProperty(dtpi.getURI(), value);

	}

}
