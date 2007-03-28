
package net.datamodel.soml.impl;

import java.net.URI;

import net.datamodel.soml.Constant;
import net.datamodel.soml.Relationship;
import net.datamodel.soml.SemanticObject;
import net.datamodel.xssp.XMLFieldType;
import net.datamodel.xssp.impl.AbstractXMLSerializableObject;

import org.apache.log4j.Logger;

/** This class records a single (uni-directional) relationship between the 
 * owning SemanticObject (SO) and the "target" SO. The relationship has its
 * own separate URI to semantically identify it.
 * 
 * @author thomas
 *
 */ 
class RelationshipImpl  extends AbstractXMLSerializableObject
implements Relationship
{
	
	private static final Logger logger = Logger.getLogger(RelationshipImpl.class);
	
	private static final String uriFieldName = Constant.SOML_URI_ATTRIBUTE_NAME;
   	private static final String targetFieldName = "target";
    	
	
   	/** A constructor for relationships. Intentionally package
   	 * (non-public) access only. 
   	 * 
   	 * @param relationURI
   	 * @param target the 'target SemanticObject'
   	 */
   	RelationshipImpl (URI relationURI, SemanticObject target) { 
   		super("relationship");
   		this.setSerializeWhenEmpty(false);
   		addField(uriFieldName, relationURI, XMLFieldType.ATTRIBUTE);
   		addField(targetFieldName, target, XMLFieldType.CHILD);
   		logger.debug("Create new Relationship:"+relationURI.toASCIIString()+" to target: "+target);
   	}
   	
   	/*
   	 * (non-Javadoc)
   	 * @see net.datamodel.soml.Relationship#getTarget()
   	 */
	public final SemanticObject getTarget() {
		return (SemanticObject) getFieldValue(targetFieldName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.Relationship#getURI()
	 */
	public final URI getURI() { return (URI) getFieldValue(uriFieldName); }
	
}
