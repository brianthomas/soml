
package net.datamodel.soml.impl;

import java.net.URI;

import net.datamodel.soml.Constant;
import net.datamodel.soml.ObjectProperty;
import net.datamodel.soml.SemanticObject;
import net.datamodel.xssp.XMLFieldType;
import net.datamodel.xssp.impl.AbstractXMLSerializableObject;

import org.apache.log4j.Logger;

/** This class records a single (uni-directional) relationship (called an 
 * "ObjectProperty") between the owning SemanticObject (SO) and the "target" SO. 
 * The ObjectProperty has its own separate URI to semantically identify it.
 * 
 * @author thomas
 *
 */ 
class ObjectPropertyImpl 
extends AbstractXMLSerializableObject
implements ObjectProperty
{
	
	private static final Logger logger = Logger.getLogger(ObjectPropertyImpl.class);
	
//	private static final String uriFieldName = Constant.SOML_RDFTYPE_FIELD_NAME;
   	private static final String targetFieldName = "target";
    	
   	private URI uri = null;
	
   	/** A constructor for properties. Intentionally package
   	 * (non-public) access only. 
   	 * 
   	 * @param propertyURI
   	 * @param target the 'target SemanticObject'
   	 */
   	ObjectPropertyImpl (URI propertyURI, SemanticObject target) { 
   		super(propertyURI.getFragment() != null ? propertyURI.getFragment() : propertyURI.getSchemeSpecificPart());
 		
   		String namespaceURI = propertyURI.toASCIIString().replace(getXMLTagName(), ""); 
   		
   		logger.debug("Objectproperty sets namespaceURI to:"+namespaceURI);
   		logger.debug(" XMLTagname:"+getXMLTagName());
   		setNamespaceURI(namespaceURI);
   		
   		uri = propertyURI;
   		
   		this.setSerializeWhenEmpty(false);
//   		addField(uriFieldName, propertyURI, XMLFieldType.ATTRIBUTE);
   		addField(targetFieldName, target, XMLFieldType.CHILD);
   		logger.debug("Create new Property:"+propertyURI.toASCIIString()+" to target: "+target);
   	}
   	
   	/*
   	 * (non-Javadoc)
   	 * @see net.datamodel.soml.Property#getTarget()
   	 */
	public final SemanticObject getTarget() {
		return (SemanticObject) getFieldValue(targetFieldName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.Property#getURI()
	 */
	public final URI getURI() { 
		return uri;
//		return (URI) getFieldValue(uriFieldName); 
	}
	
}
