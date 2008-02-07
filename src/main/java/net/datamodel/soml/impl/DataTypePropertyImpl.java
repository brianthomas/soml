/**
 * 
 */
package net.datamodel.soml.impl;

import java.net.URI;

import net.datamodel.soml.Constant;
import net.datamodel.soml.DataTypeProperty;
import net.datamodel.soml.Utility;
import net.datamodel.xssp.XMLFieldType;
import net.datamodel.xssp.impl.AbstractXMLSerializableObject;

import org.apache.log4j.Logger;

/**
 * @author thomas
 *
 */
public class DataTypePropertyImpl 
extends AbstractXMLSerializableObject
implements DataTypeProperty 
{
	
	private static final Logger logger = Logger.getLogger(DataTypePropertyImpl.class);
	
//	private static final String uriFieldName = Constant.SOML_RDFTYPE_FIELD_NAME;
	private static final String dtUriFieldName = "dtURI";
   	private static final String valueFieldName = "value";
   	
   	private URI uri = null;
	
   	/** Create a new Datatype property. 
   	 * 
   	 * @param propertyURI
   	 */
	public DataTypePropertyImpl (
			URI propertyURI, 
			URI datatypeURI, 
			String value) 
	{
 		super("datatypeProperty");
 		
   		logger.debug(" datatype gets uri:"+propertyURI);
   		
 		String namespaceURI = Utility.getNamespaceURI(propertyURI);
   		setNamespaceURI(namespaceURI);
   		
   		String xmlNodeName = propertyURI.toASCIIString().replaceFirst(namespaceURI, "");
   		logger.debug(" datatype sets node to:"+xmlNodeName);
   		setXMLNodeName(xmlNodeName);
   		
   		uri = propertyURI;
   		
  		this.setSerializeWhenEmpty(false);
 // 		addField(uriFieldName, propertyURI, XMLFieldType.ATTRIBUTE);
   		addField(dtUriFieldName, datatypeURI, XMLFieldType.ATTRIBUTE);
   		addField(valueFieldName, value, XMLFieldType.PCDATA);
   		
   		
   		logger.debug("Create new DataTypeProperty:"+propertyURI.toASCIIString()+" w/ value: "+value);
	}

	/** Create a DataTypePropertyImpl with default xsd:string type.
	 * 
	 * @param propertyURI
	 * @param value
	 */
	public DataTypePropertyImpl (URI propertyURI, String value) {
		this(propertyURI, null, value);
	}
	
	/* (non-Javadoc)
	 * @see net.datamodel.soml.DataTypeProperty#getDataTypeURI()
	 */
	public URI getDataTypeURI() {
		return (URI) getFieldValue(dtUriFieldName);
	}

	/* (non-Javadoc)
	 * @see net.datamodel.soml.DataTypeProperty#getValue()
	 */
	public String getValue() {
		return (String) getFieldValue(valueFieldName);
	}

	/* (non-Javadoc)
	 * @see net.datamodel.soml.Property#getURI()
	 */
	public URI getURI() {
		return uri;
//		return (URI) getFieldValue(uriFieldName);
	}

}
