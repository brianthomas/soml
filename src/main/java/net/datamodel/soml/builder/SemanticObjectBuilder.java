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
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

/** Build SemanticObjects from the indicated individuals using the
 * indicated OntModel for guidance. If the OntModel has provisions
 * for multiple inheritance for a given class, then the builder should
 * <b>not</b> have special handlers created for classes at the point in the class
 * tree where the hierarchy joins a subclass from 2 or more superclasses.  
 * 
 * @author thomas
 *
 */
public class SemanticObjectBuilder 
{
	
	// our trusty logger
	private static final Logger logger = Logger.getLogger(SemanticObjectBuilder.class);
	
	private static final String RDFTypeURI = RDF.getURI()+"type";
	private static final String OWLThingURI = OWL.getURI()+"Thing";
	private static final String OWLSameAsURI = OWL.getURI()+"sameAs";
	
	private Map<String,SemanticObjectHandler> handlers = new Hashtable<String,SemanticObjectHandler>(); 
	private SemanticObjectHandler DefaultHandler = new DefaultHandler();
	private OntModel ontModel = null;
	private Map<String,Integer> numOfSubClasses = new Hashtable<String,Integer>();
	
	public SemanticObjectBuilder (OntModel model) { 
		ontModel = model; 
	}
	
	/** Add a special handler for particular rdf:type.
	 * 
	 * @param uri
	 * @param handler
	 */
	protected void addHandler(String rdfTypeUri, SemanticObjectHandler handler) {
		logger.debug("addHandler rdf:type="+rdfTypeUri+" handler:"+handler); 
		handlers.put(rdfTypeUri,handler);
	}
	
	/** Find the handler associated with the indicated uri.
	 * 
	 * @param uri
	 * @return
	 */
	protected SemanticObjectHandler findHandler (String rdfTypeUri) {
		if(handlers.containsKey(rdfTypeUri)) {
			return handlers.get(rdfTypeUri);
		} else {
			return DefaultHandler;
		}
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
		
		// create the SO using the desired rdf:type 
		SemanticObject so = findHandler(findRDFType(in)).create(this,in);
		
		return so;
		
	}

	/** Look for the most specific rdf:type (e.g. the one which
	 * has the most superclasses) of a given Individual. Problems
	 * will arise if the OntModel allows for multiple inheritance
	 * trees, but as this is bad practice, we ignore this issue for
	 * now.
	 */
	private String findRDFType (Individual in) {
		
		String type = OWLThingURI;
		int numOfSubClasses = 1000000; // pick something arbitrarily high
		
		// look for rdf:type properties...then rank them
		// according to how many superclasses each has
		for(StmtIterator i = in.listProperties(); i.hasNext(); ) {
			Statement s  = i.nextStatement(); 
			if (s.getPredicate().getURI().equals(RDFTypeURI)) {
				String typeUri = s.getObject().toString();
				logger.debug(" FOUND class rdf:type:"+typeUri);
				logger.debug("     vs owl uri:"+OWLThingURI);
				// only consider non owl:Thing rdf:type's as we
				// have owl:Thing as our default.
				if (!typeUri.equals(OWLThingURI)) {
					logger.debug(" IN non-Thing conditional ");
					OntClass oc = ontModel.getOntClass(typeUri);
					logger.debug(" FOUND Class match oc:"+oc);
					if (oc != null) {
						int num_subs = findNrofSubClasses(oc);
						logger.debug("** class uri:"+typeUri+" has "+num_subs+" subclasses");
						// if this new class has more super classes, then
						// it is preferable 
						if (num_subs < numOfSubClasses) {
							type = typeUri;
							numOfSubClasses = num_subs;
						}
					}
				}
			}
		}
		
		logger.debug(" = Found Individual rdf:type = "+type); 
		return type;
	}
	
	private int findNrofSubClasses (OntClass oc) {
		String classUri = oc.getURI();
		if (!numOfSubClasses.containsKey(classUri)) {
			ExtendedIterator i = oc.listSubClasses();
			numOfSubClasses.put(classUri, new Integer(i.toList().size()));
		}
		return numOfSubClasses.get(classUri).intValue();
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
		
		// we skip adding owl:sameAs properties
		if (propUri.equals(OWLSameAsURI))
			return;
		
		logger.debug("addProperty ( pred:"+propUri+" obj:"+s.getObject()+")");
		
		if (s.getObject().canAs(Individual.class))
		{
			
			logger.debug("  prop value is Individual"); 
			SemanticObject target = builder.createSemanticObject((Individual) s.getObject().as(Individual.class));
			if (target != null)
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

