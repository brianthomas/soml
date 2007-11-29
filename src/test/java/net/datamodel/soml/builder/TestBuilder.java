package net.datamodel.soml.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

import junit.framework.TestCase;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.impl.SemanticObjectImpl;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class TestBuilder 
extends TestCase 
{
	
	private static final Logger logger = Logger.getLogger(TestBuilder.class);

	protected static String BASE_TEST_RESOURCE_DIR = "src/test/resources";
	
	private static final String BaseOntModelUri = "http://test.org/testThings.owl";
	private static final OntModelSpec modelSpec = OntModelSpec.OWL_MEM;
//	private static final OntModelSpec modelSpec = PelletReasonerFactory.THE_SPEC; // dont need a reasoner..we are just counting sub/super classes 

	protected static String[] testModelFile = { 
		BASE_TEST_RESOURCE_DIR + "/testBuilder1.rdf",
//		BASE_TEST_RESOURCE_DIR + "/testBuilder2.rdf"
	}; 
	
	// This rdf:type has a special handler we will check for
	private static String SpecialInstanceURI = "http://test.org/testOps.owl#quantity1";

	private static OntModel[] testModels = new OntModel[testModelFile.length];
	
	private static boolean isSetup = false;
	private static SemanticObjectBuilder builder = null;
	
	@Override
	protected void setUp() 
	throws Exception 
	{
		super.setUp();
		
		if (!isSetup) {

			logger.debug("Setting up tests");
			
			try {
				OntModel model = createOntModel(BaseOntModelUri);
				builder = new TestBuilder().new ExtendedBuilder(model);
			} catch (Exception e) {
				e.printStackTrace();
				//logger.error(e.getMessage());
				// fail(e.getMessage());
			}
			logger.debug(" created builder:"+builder);
	
			// create the test model query
			try {
				for (int i = 0; i < testModelFile.length; i++) {
					logger.debug(" Create model:"+testModelFile[i]);
					testModels[i] = createOntModel(new File(testModelFile[i]));
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

	/** Build an OntModel for the passed query string.
	 * 
	 * @param queryStr
	 * 
	 * @return OntModel which represents the query 
	 */
	private static OntModel createOntModel (String ontoUri) {

		logger.debug("CreateOntModel uri:"+ontoUri);
		OntModel queryModel = ModelFactory.createOntologyModel(modelSpec);
		FileManager fm = FileManager.get();
		logger.debug("  file manager:"+fm);
		queryModel.add(fm.loadModel(ontoUri,null, "RDF/XML-ABBREV"));
		return queryModel;
	}
	
	private static OntModel createOntModel (File ontoFile) 
	throws FileNotFoundException 
	{
		logger.debug("CreateOntMOdel w/ file");
		OntModel ontModel = ModelFactory.createOntologyModel(modelSpec);
		logger.debug(" TRY TO READ ONTOFILE:"+ontoFile);
		ontModel.read(new FileInputStream(ontoFile), null);
		logger.debug(" return MODEL:"+ontModel);
		return ontModel;
	}

	public void test1() {
		
		logger.info("Test builder"); 
		
		// iterate over models, testing heach individual in model data
		for (OntModel testModel : testModels) {
			for (Iterator i = testModel.listIndividuals(); i.hasNext(); ) {
				Individual in = (Individual) i.next();
				try {
					SemanticObject so = builder.createSemanticObject(in); 
					assertNotNull("Can create SO",so);
					
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
		
		String classURI = "http://test.org/testThings.owl#FloatQ3";
		
		public ExtendedBuilder (OntModel model) {
			super(model); 
			this.addHandler(classURI, new AHandler()); 
		}
		
		class AHandler implements SemanticObjectHandler {

			public SemanticObject create(SemanticObjectBuilder b, Individual in)
					throws SemanticObjectBuilderException 
			{
				return new TestSemanticObject();
			}
			
		}
		
	}
	
	class TestSemanticObject 
	extends SemanticObjectImpl 
	{
		
		TestSemanticObject() throws SemanticObjectBuilderException {
			super(SemanticObjectBuilder.createURI("urn:no-uri-here"));
		}
	}

}
