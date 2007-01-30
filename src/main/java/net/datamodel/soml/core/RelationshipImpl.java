
package net.datamodel.soml.core;

import net.datamodel.soml.Relationship;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.URN;
import net.datamodel.xssp.XMLFieldType;
import net.datamodel.xssp.XMLSerializableField;
import net.datamodel.xssp.core.AbstractXMLSerializableObject;

import org.apache.log4j.Logger;

/** This class records a single (uni-directional) relationship between the 
 * owning SemanticObject (SO) and the "target" SO. The relationship has its
 * own separate URN to semantically identify it.
 * 
 * @author thomas
 *
 */ 
class RelationshipImpl extends AbstractXMLSerializableObject
implements Relationship
{
   	private static final String URN_XML_FIELD_NAME = "urn";
   	private static final String TARGET_XML_FIELD_NAME = "target";
    	
	private static final Logger logger = Logger.getLogger(RelationshipImpl.class);
	
   	/** A constructor for relationships. Intentionally package
   	 * (non-public) access only. 
   	 * 
   	 * @param relationURN
   	 * @param so
   	 */
   	RelationshipImpl (URN relationURN, SemanticObject so) { 
   		super("relationship");
   		this.setSerializeWhenEmpty(false);
   		addField(URN_XML_FIELD_NAME, relationURN, XMLFieldType.ATTRIBUTE);
   		addField(TARGET_XML_FIELD_NAME, so, XMLFieldType.CHILD);
   		logger.debug("Create new Relationship:"+relationURN+" : "+so);
   	}
   	
   	/*
   	 * (non-Javadoc)
   	 * @see net.datamodel.soml.Relationship#getTarget()
   	 */
	public SemanticObject getTarget() {
		logger.debug(" relationship.getTarget called on "+this);
		XMLSerializableField soField = getFields().get(TARGET_XML_FIELD_NAME);
		logger.debug (" object in SO field:"+ soField.getValue());
		return (SemanticObject) soField.getValue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.Relationship#getURN()
	 */
	public URN getURN() {
		return (URN) ((XMLSerializableField) getFields().get(URN_XML_FIELD_NAME)).getValue();
	}
	
}
