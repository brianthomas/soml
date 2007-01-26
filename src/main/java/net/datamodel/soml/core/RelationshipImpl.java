
package net.datamodel.soml.core;

import net.datamodel.soml.Relationship;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.URN;
import net.datamodel.xssp.XMLFieldType;
import net.datamodel.xssp.core.AbstractXMLSerializableObject;

/** This class records a single (uni-directional) relationship between the SO and
 * another SO.
 * 
 * @author thomas
 *
 */ 
class RelationshipImpl extends AbstractXMLSerializableObject
implements Relationship
{
   	static final String TARGET_XML_FIELD_NAME = "target";
    	
   	RelationshipImpl (URN relationURN, SemanticObject o) { 
   		super("relationship");
   		this.setSerializeWhenEmpty(false);
   		addField(TARGET_XML_FIELD_NAME, o, XMLFieldType.CHILD);
   	}
   	
   	/*
   	 * (non-Javadoc)
   	 * @see net.datamodel.soml.Relationship#getTarget()
   	 */
	public SemanticObject getTarget() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.datamodel.soml.Relationship#getURN()
	 */
	public URN getURN() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
