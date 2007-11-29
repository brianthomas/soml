/**
 * 
 */
package net.datamodel.soml.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;

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
public class SemanticObjectBuilder 
{
	
	// our trusty logger
	private static final Logger logger = Logger.getLogger(SemanticObjectBuilder.class);
	
	private static final String RDFTypeURI = RDF.getURI()+"type";
	private static final String OWLSameAsURI = OWL.getURI()+"sameAs";
	
	private Map<String,SemanticObjectHandler> handlers = new Hashtable<String,SemanticObjectHandler>(); 
	private SemanticObjectHandler DefaultHandler = new DefaultHandler();
	
	public SemanticObjectBuilder() { }
	
	/** Add a special handler for particular URI.
	 * 
	 * @param uri
	 * @param handler
	 */
	protected void addHandler(String uri, SemanticObjectHandler handler) {
		handlers.put(uri,handler);
	}
	
	/** Find the handler associated with the indicated uri.
	 * 
	 * @param uri
	 * @return
	 */
	protected SemanticObjectHandler findHandler (String uri) {
		if(handlers.containsKey(uri))
			return handlers.get(uri);
		else
			return DefaultHandler;
	}
	
	/** Create a SemanticObject from a {@link com.hp.hpl.jena.ontology.Individual}.
	 * 
	 * @param in
	 * @return
	 * @throws SemanticObjectBuilderException
	 */
	public final SemanticObject createSemanticObject (Individual in) 
	throws SemanticObjectBuilderException
	{
		
		String uri = in.getURI();
		logger.debug("createSemanticObject (uri:"+uri+")");
		
		// create the SO
		SemanticObject so = findHandler(uri).create(this,in);
		
		return so;
		
	}

	/** Small utility method to create an URI and throw an exeception appropriately.
	 * 
	 * @param uriStr
	 * @return
	 * @throws SemanticObjectBuilderException
	 */
	public final static URI createURI (String uriStr) 
	throws SemanticObjectBuilderException
	{
		try {
			return new URI(uriStr);
		} catch (URISyntaxException e) {
			throw new SemanticObjectBuilderException(e);
		}
	}
	
	/** Small utility to add a property to a semantic object. 
	 * 
	 * @param parent
	 * @param s
	 * @throws SemanticObjectBuilderException
	 */
	protected static void addProperty (
			SemanticObjectBuilder builder, 
			SemanticObject parent, 
			Statement s
	) 
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
			SemanticObject target = builder.createSemanticObject((Individual) s.getObject().as(Individual.class));
			parent.addProperty(target, createURI(s.getPredicate().getURI()));
			
		} else { 
			logger.debug("  prop value is NOT an object");
			// TODO: add namespace URIs/prefixes on attributes
			parent.addAttributeField(s.getPredicate().getLocalName(), s.getObject().toString());
		}
		
	}
	
	/** Default handler to create vanilla SemanticObjects.
	 * 
	 * @author thomas
	 *
	 */
	class DefaultHandler 
	implements SemanticObjectHandler 
	{
		public SemanticObject create (SemanticObjectBuilder builder, Individual in) 
		throws SemanticObjectBuilderException 
		{
			SemanticObject so = new SemanticObjectImpl(createURI(in.getURI()));
			// add in properties
			for(StmtIterator i = in.listProperties(); i.hasNext(); ) {
				Statement s  = i.nextStatement(); 
				addProperty(builder, so, s);
			}
			return so;
		}
		
	}
	
	/**
	 * @author thomas
	 *
	 */
	protected interface SemanticObjectHandler {

		/** Create a new SemanticObject.
		 * 
		 * @param b
		 * @param in
		 * @return
		 * @throws SemanticObjectBuilderException
		 */ 
		public SemanticObject create (SemanticObjectBuilder b, Individual in) throws SemanticObjectBuilderException;
		
	}
	
}

