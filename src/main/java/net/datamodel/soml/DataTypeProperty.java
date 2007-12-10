package net.datamodel.soml;

import java.net.URI;

/** A property which holds a datatype (scalar) property.
 * 
 * @author thomas
 *
 */
public interface DataTypeProperty 
extends Property 
{

	/** Get the object which describes this datatype.
	 * 
	 * @return String 
	 */
	public String getValue();
	
	/** Get the URI which describes the type of the value.
	 * 
	 * @return URI 
	 */
	public URI getDataTypeURI();
	
}
