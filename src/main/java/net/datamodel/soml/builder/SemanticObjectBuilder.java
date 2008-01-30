/**
 * 
 */
package net.datamodel.soml.builder;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.Utility;
import net.datamodel.soml.impl.SemanticObjectImpl;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
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
	private final SemanticObjectHandler DefaultHandler = new DefaultHandler();
	private final SemanticObjectHandler NullHandler = new NullHandler();
	
	private OntModel ontModel = null;
	private Map<String,Integer> numOfSubClasses = new Hashtable<String,Integer>();
	
	public SemanticObjectBuilder (OntModel model) { 
		ontModel = model; 
		// use the default handler for all owl:Thing stuff
		addHandler(OWLThingURI, getDefaultHandler()); 
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
	
	/** Get the Handler which is the default.
	 * 
	 * @return SemanticObjectHandler which is the DefaultHandler 
	 */
	protected SemanticObjectHandler getDefaultHandler() { return DefaultHandler; }
	
	/** Get the Handler which does nothing.
	 * 
	 * @return SemanticObjectHandler which is the NullHandler 
	 */
	protected SemanticObjectHandler getNullHandler() { return NullHandler; }
	
	/** Find the handler associated with the indicated uri.
	 * 
	 * @param uri
	 * @return
	 */
	protected SemanticObjectHandler findHandler (List<String> rdfTypeUris) {
		
		for (String rdfTypeUri : rdfTypeUris)
		{
			if(handlers.containsKey(rdfTypeUri)) {
				logger.debug("RETURN HANDLER for uri:"+rdfTypeUri);
				return handlers.get(rdfTypeUri);
			} 
		}
		// no match!?!? then return default handler
		logger.debug("Cant find declared handler, RETURNing a DEFAULT HANDLER ");
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
		List<String> types = findRDFTypes(in);
		SemanticObject so = findHandler(types).create(this, in, types.get(0));
		
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
	protected List<String> findRDFTypes (Individual in) 
	{
		
		SortedMap<Integer,String> types = new TreeMap<Integer,String>();
		types.put(new Integer(1000000000), OWLThingURI);
		
		logger.debug(" findRDFTypes for uri:"+in.getURI());
		
		// go thru the types
		for (ExtendedIterator i = in.listRDFTypes(false); i.hasNext(); ) {
			Resource r = (Resource) i.next();
			if ( r.getURI().equals(OWLThingURI) 
					|| r.getURI().equals(OWLClassURI)
				) {
					logger.debug("SKIPPING OWL Class/THING type");
				} else {
					OntClass oc = ontModel.getOntClass(r.getURI());
					if (oc != null) {
						int num_subs = findNrofSubClasses(oc);
						logger.debug("** class uri:"+r.getURI()+" has "+num_subs+" subclasses");
						Integer key = new Integer(num_subs);
						// TODO : fix collisions...can occur from multiple inheritance
						if (!types.containsKey(key))
							types.put(key, r.getURI()); 
					} else {
						logger.warn(" Unable to find class uri:"+r.getURI()+" in model!");
					}
				}
		}
		
		List<String> ret = new Vector<String>();
		for (Integer key : types.keySet()) {
			logger.debug(" *****  TYPE : "+types.get(key));
			ret.add(types.get(key));
		}
			
		return ret;
	}
	
	/** Find the number of subclasses for the indicated OntClass. 
	 * Caches values to speed up determination.
	 *  
	 * @param oc
	 * @return
	 */
	protected int findNrofSubClasses (OntClass oc) {
		String classUri = oc.getURI();
		if (!numOfSubClasses.containsKey(classUri)) {
			ExtendedIterator i = oc.listSubClasses();
			numOfSubClasses.put(classUri, new Integer(i.toList().size()));
		}
		return numOfSubClasses.get(classUri).intValue();
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
		
		logger.info("  addProperty:"+propUri+" isResource:"+s.getObject().isResource());
		
		// Special case: setting rdf:type properties for SO 
		if (propUri.equals(RDFTypeURI)) {
			if (s.getObject().isResource()) {
				Resource r = (Resource) s.getObject().as(Resource.class);
				if (!r.getURI().equals(OWLThingURI)) {
					// add type only if NOT owl:Thing, which is a duplicate
					parent.addRDFTypeURI(Utility.createURI(r.getURI()));
				}
				
			} else {
				logger.warn(" Can't do anything with non-resource rdf:type in serialization? dropping information on floor..");
			}
			return;
		}
			
		// other types of properties treated here
		if (s.getObject().canAs(Individual.class))
		{
			
			Individual in = builder.ontModel.getIndividual(s.getObject().toString());
			logger.debug(" #### looking for individual w/ uri:"+s.getObject()+" result:"+in);
//			if (in != null) { logger.debug("   prop value is Individual : "+in.getURI()); }
			
			SemanticObject target = builder.createSemanticObject((Individual) s.getObject().as(Individual.class));
			if (target != null)
				parent.addProperty(Utility.createURI(propUri), target);
			else 
				// is this really an issue? log at warn level for now
				logger.warn("Skipping addProperty for prop:"+propUri+", target object is null");
			
		} else { 
			logger.debug("  prop value is NOT an object, add datatype prop");
			parent.addProperty(Utility.createURI(propUri), s.getObject().toString());
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
		
		public SemanticObject create (SemanticObjectBuilder builder, Individual in, String rdfType) 
		throws SemanticObjectBuilderException 
		{
			logger.info("DefaultHandler called for instance uri:"+in.getURI()+" rdftypeuri:"+rdfType);
			
			SemanticObject so = new SemanticObjectImpl();
			so.addRDFTypeURI(Utility.createURI(rdfType));
			
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
		public SemanticObject create (SemanticObjectBuilder builder, Individual in, String rdfType) 
		throws SemanticObjectBuilderException 
		{
			logger.info("Null handler called for instance uri:"+in.getURI());
			return null;
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
		public SemanticObject create (
				SemanticObjectBuilder b, 
				Individual in,
				String rdfType) 
		throws SemanticObjectBuilderException;
		
	}
	
	
}

