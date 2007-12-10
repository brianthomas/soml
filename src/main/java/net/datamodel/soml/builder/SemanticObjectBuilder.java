/**
 * 
 */
package net.datamodel.soml.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

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
	private static final String OWLClassURI = OWL.getURI()+"Class";
	private static final String OWLSameAsURI = OWL.getURI()+"sameAs";
	
	private Map<String,SemanticObjectHandler> handlers = new Hashtable<String,SemanticObjectHandler>(); 
	private SemanticObjectHandler DefaultHandler = new DefaultHandler();
	private OntModel ontModel = null;
	private Map<String,Integer> numOfSubClasses = new Hashtable<String,Integer>();
	
	public SemanticObjectBuilder (OntModel model) { 
		ontModel = model; 
		addHandler(OWLClassURI, new NullHandler()); // dont build class definitions 
		addHandler(OWLThingURI, new ThingHandler()); 
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
	protected SemanticObjectHandler findHandler (List<String> rdfTypeUris) {
		
		SemanticObjectHandler h = null;
		for (String rdfTypeUri : rdfTypeUris)
		{
			if(handlers.containsKey(rdfTypeUri)) {
				logger.debug("RETURN SPECIAL HANDLER for uri:"+rdfTypeUri);
				return handlers.get(rdfTypeUri);
			} 
		}
		// no match!?!? then return default handler
		logger.debug("RETURN DEFAULT HANDLER");
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
		logger.debug("---> createSemanticObject (uri:"+uri+")");
		
		// create the SO using the desired rdf:type 
		SemanticObject so = findHandler(findRDFTypes(in)).create(this,in);
		
		return so;
		
	}

	/** Look for the most specific rdf:type (e.g. the one which
	 * has the most superclasses) of a given Individual. Problems
	 * will arise if the OntModel allows for multiple inheritance
	 * trees, but as this is bad practice, we ignore this issue for
	 * now.
	 */
	// return 'ranked' list of classes, with most preferable class
	// at the beginning of the list
	private List<String> findRDFTypes (Individual in) 
	{
		
		SortedMap<Integer,String> types = new TreeMap<Integer,String>();
		types.put(new Integer(1000000000),OWLThingURI);
		
		// look for rdf:type properties...then rank them
		// according to how many superclasses each has
		for(StmtIterator i = in.listProperties(); i.hasNext(); ) {
			Statement s  = i.nextStatement(); 
			if (s.getPredicate().getURI().equals(RDFTypeURI)) {
				String typeUri = s.getObject().toString();
				logger.debug(" FOUND rdf:type:"+typeUri);
				// only consider non owl:Thing rdf:type's as we
				// have owl:Thing as our default.
				if ( typeUri.equals(OWLThingURI) 
					|| typeUri.equals(OWLClassURI)
				) {
					logger.debug("SKIPPING OWL Class/THING type");
				} else {
					OntClass oc = ontModel.getOntClass(typeUri);
					if (oc != null) {
						int num_subs = findNrofSubClasses(oc);
						logger.debug("** class uri:"+typeUri+" has "+num_subs+" subclasses");
						Integer key = new Integer(num_subs);
						// TODO : fix collisions...can occur from multiple inheritance
						if (!types.containsKey(key))
							types.put(key, typeUri); 
					} else
						logger.warn(" Unable to find class uri:"+typeUri+" in model!");
				}
			}
		}
		
		// hmm. 'values()' returns a COllection, which is not guarrenteed to
		// be in any order (??). So use this hack to build a list
		List<String> ret = new Vector<String>();
		for (Integer key : types.keySet())
			ret.add(types.get(key));
			
		return ret;
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
				parent.addProperty(createURI(s.getPredicate().getURI()), target);
			else 
				logger.info("Skipping add property, target object is null");
			
		} else { 
			logger.debug("  prop value is NOT an object, add datatype prop");
			String target = s.getObject().toString();
//			parent.addAttributeField(s.getPredicate().getLocalName(), target);
			parent.addProperty(createURI(s.getPredicate().getURI()), target);
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
			logger.info("DefaultHandler called for uri"+in.getURI());
			SemanticObject so = new SemanticObjectImpl(createURI(in.getURI()));
			// add in properties
			for(StmtIterator i = in.listProperties(); i.hasNext(); ) {
				Statement s  = i.nextStatement(); 
				addProperty(builder, so, s);
			}
			return so;
		}
		
	}
	
	/** Default handler to create vanilla SemanticObjects.
	 * 
	 * @author thomas
	 *
	 */
	protected class NullHandler 
	implements SemanticObjectHandler 
	{
		public SemanticObject create (SemanticObjectBuilder builder, Individual in) 
		throws SemanticObjectBuilderException 
		{
			logger.info("Null handler called for uri:"+in.getURI());
			return null;
		}
		
	}
	
	/** Default handler to create vanilla SemanticObjects.
	 * 
	 * @author thomas
	 *
	 */
	protected class ThingHandler 
	implements SemanticObjectHandler 
	{
		public SemanticObject create (SemanticObjectBuilder builder, Individual in) 
		throws SemanticObjectBuilderException 
		{
			
			logger.info("ThingHandler called for uri"+in.getURI());
			SemanticObject so = new SemanticObjectImpl(createURI(in.getURI()));
			// add in properties
			/*
			for(StmtIterator i = in.listProperties(); i.hasNext(); ) {
				Statement s  = i.nextStatement(); 
				addProperty(builder, so, s);
			}
			*/
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

