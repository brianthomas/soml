
package net.datamodel.soml.impl;

import java.net.URI;

import net.datamodel.soml.Relationship;
import net.datamodel.soml.SemanticObject;
import net.datamodel.xssp.XMLFieldType;
import net.datamodel.xssp.XMLSerializableField;
import net.datamodel.xssp.core.AbstractXMLSerializableObject;

import org.apache.log4j.Logger;

/** This class records a single (uni-directional) relationship between the 
 * owning SemanticObject (SO) and the "target" SO. The relationship has its
 * own separate URI to semantically identify it.
 * 
 * @author thomas
 *
 */ 
class RelationshipImpl extends AbstractXMLSerializableObject
implements Relationship
{
   	private static final String URI_XML_FIELD_NAME = "URI";
   	private static final String TARGET_XML_FIELD_NAME = "target";
    	
	private static final Logger logger = Logger.getLogger(RelationshipImpl.class);
	
   	/** A constructor for relationships. Intentionally package
   	 * (non-public) access only. 
   	 * 
   	 * @param relationURI
   	 * @param target the 'target SemanticObject'
   	 */
   	RelationshipImpl (URI relationURI, SemanticObject target) { 
   		super("relationship");
   		this.setSerializeWhenEmpty(false);
   		addField(URI_XML_FIELD_NAME, relationURI, XMLFieldType.ATTRIBUTE);
   		addField(TARGET_XML_FIELD_NAME, target, XMLFieldType.CHILD);
   		logger.debug("Create new Relationship:"+relationURI.toASCIIString()+" to target: "+target);
   	}
   	
   	/*
   	 * (non-Javadoc)
   	 * @see net.datamodel.soml.Relationship#getTarget()
   	 */
	public SemanticObject getTarget() {
		return (SemanticObject) getFieldValue(TARGET_XML_FIELD_NAME);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.Relationship#getURI()
	 */
	public URI getURI() { return (URI) getFieldValue(URI_XML_FIELD_NAME); }
	
}
