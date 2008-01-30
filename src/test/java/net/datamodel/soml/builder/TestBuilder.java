package net.datamodel.soml.builder;

import java.io.File;
import java.util.Iterator;

import junit.framework.TestCase;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.Utility;
import net.datamodel.soml.impl.SemanticObjectImpl;
import net.datamodel.xssp.dom.Specification;

import org.apache.log4j.Logger;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.util.FileManager;

public class TestBuilder 
extends TestCase 
{
	
	private static final Logger logger = Logger.getLogger(TestBuilder.class);

	protected static String BASE_TEST_RESOURCE_DIR = "src/test/resources";
	
	private static final String BaseOntModelUri = "http://test.org/testThings.owl";
//	private static final OntModelSpec modelSpec = OntModelSpec.OWL_MEM;
	private static final OntModelSpec modelSpec = PelletReasonerFactory.THE_SPEC; // dont need a reasoner..we are just counting sub/super classes 

	protected static String[] testModelFile = { 
		BASE_TEST_RESOURCE_DIR + "/testBuilder1.rdf",
		BASE_TEST_RESOURCE_DIR + "/testBuilder2.rdf"
	}; 
	
	// This rdf:type has a special handler we will check for
	private static String SpecialInstanceURI = "http://test.org/testOps.owl#quantity1";

	private static OntModel[] testModels = new OntModel[testModelFile.length];
	
	private static boolean isSetup = false;
	private static SemanticObjectBuilder builder = null;
	private static SemanticObjectBuilder extended_builder = null;
	
	@Override
	protected void setUp() 
	throws Exception 
	{
		super.setUp();
		
		if (!isSetup) {

			logger.debug("Setting up tests");
			
			String Q_URI = "http://archive.astro.umd.edu/ont/Quantity.owl";
			try {
				OntModel model = edu.umd.astro.jenahelper.Utility.createOntModel(BaseOntModelUri,modelSpec,"RDF/XML");
//				model.add(FileManager.get().loadModel(Q_URI, null, "RDF/XML"));
				
				builder = new SemanticObjectBuilder(model);
				extended_builder = new TestBuilder().new ExtendedBuilder(model);
			} catch (Exception e) {
				e.printStackTrace();
				//logger.error(e.getMessage());
				// fail(e.getMessage());
			}
			logger.debug(" created builder:"+builder);
			logger.debug(" created builder:"+extended_builder);
	
			// create the test model query
			try {
				for (int i = 0; i < testModelFile.length; i++) {
					logger.debug(" Create model:"+testModelFile[i]);
					testModels[i] = edu.umd.astro.jenahelper.Utility.createOntModel(new File(testModelFile[i]), modelSpec, "RDF/XML");
					logger.debug(" finished Create model:"+testModelFile[i]);
				}
			} catch (Exception e) {
				logger.error("error in constructing test rdf models:" + e.getMessage());
				fail("cant run tests:"+e.getMessage());
			}
			
			logger.debug("setup finished");
			isSetup = true;
		}
		
	}

	public void test1() {
		
		logger.info("Test builder"); 
		
		Specification.getInstance().setPrettyOutput(true);
		
		// iterate over models, testing heach individual in model data
		int md_num = 1;
		for (OntModel testModel : testModels) {
			logger.debug(" TESTING MODEL:"+md_num++);
			for (Iterator i = testModel.listIndividuals(); i.hasNext(); ) {
				Individual in = (Individual) i.next();
				try {
					
					SemanticObject so = builder.createSemanticObject(in); 
					assertNotNull("Can create SO",so);
					
					logger.debug("Created Individual from model SO w/ uri:"+so.getNamespaceURI());
					logger.debug("Created Individual from model SO :\n"+so.toXMLString());
					// Check the class of the output object. For one object, we 
					// had a special handler build it, otherwise, its SemanticObjectImpl
					// class 
					
				} catch (SemanticObjectBuilderException e) {
					logger.error(e.getMessage());
					fail(e.getLocalizedMessage());
				}
			}
		}
		
	}
	
	public void test2() {
		
		logger.info("Test Extended builder"); 
		
		Specification.getInstance().setPrettyOutput(true);
		
		// iterate over models, testing heach individual in model data
		for (OntModel testModel : testModels) {
			for (Iterator i = testModel.listIndividuals(); i.hasNext(); ) {
				Individual in = (Individual) i.next();
				try {
					
					SemanticObject so = extended_builder.createSemanticObject(in); 
					assertNotNull("Can create SO",so);
					
					logger.debug("Created SO :\n"+so.toXMLString());
					// Check the class of the output object. For one object, we 
					// had a special handler build it, otherwise, its SemanticObjectImpl
					// class 
					
					if (in.getURI().equals(SpecialInstanceURI)) {
						assertEquals("Class for special object is correct", TestSemanticObject.class, so.getClass());
					} else { 
						assertEquals("Class for special object is correct", SemanticObjectImpl.class, so.getClass());
					}
					
				} catch (SemanticObjectBuilderException e) {
					logger.error(e.getMessage());
					fail(e.getLocalizedMessage());
				}
			}
		}
		
	}
	
	/** test handler. to see if the extension mech is working.
	 * 
	 * @author thomas
	 *
	 */
	class ExtendedBuilder 
	extends SemanticObjectBuilder 
	{
		
		// special class to treat
		String classURI = "http://test.org/testThings.owl#FloatQ";
		
		public ExtendedBuilder (OntModel model) {
			super(model); 
			this.addHandler(classURI, new AHandler()); 
		}
		
		class AHandler implements SemanticObjectHandler {

			public SemanticObject create(SemanticObjectBuilder b, Individual in, String rdfType)
					throws SemanticObjectBuilderException 
			{
				logger.info("RUN SPECIAL HANDLER FOR uri:"+in.getURI());
				return new TestSemanticObject();
			}
			
		}
		
	}
	
	class TestSemanticObject 
	extends SemanticObjectImpl 
	{
		
		TestSemanticObject() throws SemanticObjectBuilderException {
			super(Utility.createURI("urn:some-rdf-type-uri-here"));
			this.setXMLNodeName("TestSemanticObject");
		}
	}

}
