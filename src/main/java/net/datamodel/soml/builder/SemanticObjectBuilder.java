/**
 * 
 */
package net.datamodel.soml.builder;

import java.net.URI;
import java.net.URISyntaxException;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.impl.SemanticObjectImpl;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author thomas
 *
 */
public class SemanticObjectBuilder {
	
	// our trusty logger
	private static final Logger logger = Logger.getLogger(SemanticObjectBuilder.class);
	
	private static final String RDFTypeURI = RDF.getURI()+"type";
	private static final String OWLSameAsURI = OWL.getURI()+"sameAs";
	
	/** Create a SemanticObject from a {@link com.hp.hpl.jena.ontology.Individual}.
	 * 
	 * @param in
	 * @return
	 * @throws SemanticObjectBuilderException
	 */
	public static final SemanticObject createSemanticObject (Individual in) 
	throws SemanticObjectBuilderException
	{
		
		// create the SO
		SemanticObject so = new SemanticObjectImpl(createURI(in.getURI()));
		
		// add in properties
		for(StmtIterator i = in.listProperties(); i.hasNext(); ) {
			Statement s  = i.nextStatement(); 
			addProperty(so, s);
		}
		
		return so;
	}

	/** Small utility to add a property to a semantic object. 
	 * 
	 * @param parent
	 * @param s
	 * @throws SemanticObjectBuilderException
	 */
	protected static void addProperty (SemanticObject parent, Statement s) 
	throws SemanticObjectBuilderException
	{
		String propUri = s.getPredicate().getURI();
		
		// we skip adding rdf:type, owl:sameAs properties
		if (propUri.equals(RDFTypeURI) || propUri.equals(OWLSameAsURI))
			return;
		
		logger.debug("addProperty ( pred:"+propUri+" obj:"+s.getObject()+")");
		
		if (s.getObject().canAs(Individual.class))
		{
			
			logger.debug("  prop value is Individual"); 
			SemanticObject target = createSemanticObject((Individual) s.getObject().as(Individual.class));
			parent.addProperty(target, createURI(s.getPredicate().getURI()));
			
		} else { 
			logger.debug("  prop value is NOT an object");
			// TODO: add namespace URIs/prefixes on attributes
			parent.addAttributeField(s.getPredicate().getLocalName(), s.getObject().toString());
		}
		
	}
	
	/** Small utility method to create an URI and throw an exeception appropriately.
	 * 
	 * @param uriStr
	 * @return
	 * @throws SemanticObjectBuilderException
	 */
	protected static URI createURI (String uriStr) 
	throws SemanticObjectBuilderException
	{
		try {
			return new URI(uriStr);
		} catch (URISyntaxException e) {
			throw new SemanticObjectBuilderException(e);
		}
	}
	
}

