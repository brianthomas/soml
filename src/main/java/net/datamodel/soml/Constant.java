package net.datamodel.soml;

import com.hp.hpl.jena.vocabulary.OWL;

public class Constant {
	
	private Constant () {}
	
	/** The namespace URI of this package.
	 */ 
	public static final String SOML_NAMESPACE_URI = 
	        "http://archive.astro.umd.edu/ont/SemanticObject";

	/** The name of the relevant version of the schema file for this package.
	 */ 
	public static final String SOML_SCHEMA_NAME = "SOML_57.xsd";
	
	/** The name of the attribute which stores the URI in the XML 
	 * serialization of instances of SemanticObject, Property and
	 * UnmixedSemanticObjectList classes.
	 */
	public static final String SOML_URI_ATTRIBUTE_NAME = "URI";

	public static final String OWLThingURI = OWL.getURI()+"Thing";
	
}
