package net.datamodel.soml.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

import junit.framework.TestCase;
import net.datamodel.soml.SemanticObject;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestBuilder 
extends TestCase 
{
	
	private static final Logger logger = Logger.getLogger(TestBuilder.class);

	protected static String BASE_TEST_RESOURCE_DIR = "src/test/resources";

	protected static String[] testModelFile = { 
		BASE_TEST_RESOURCE_DIR + "/testBuilder1.rdf",
//		BASE_TEST_RESOURCE_DIR + "/testBuilder2.rdf"
	}; 

	protected static OntModel[] testModels = new OntModel[testModelFile.length];
	
	private static boolean isSetup = false;
	
	@Override
	protected void setUp() 
	throws Exception 
	{
		super.setUp();
		
		if (!isSetup) {

			// create the test model query
			try {
				for (int i = 0; i < testModelFile.length; i++) {
					testModels[i] = createOntModel(new File(testModelFile[i]));
				}
			} catch (Exception e) {
				logger.error("error in constructing test rdf models:" + e.getMessage(), e);
				fail("cant run tests:"+e.getMessage());
				System.exit(0); // harsh toke
			}
			logger.debug("setup finished");
			isSetup = true;
		}
		
	}

	private static OntModel createOntModel (File ontoFile) 
	throws FileNotFoundException 
	{
		OntModel ontModel = ModelFactory.createOntologyModel();
		ontModel.read(new FileInputStream(ontoFile), null);
		return ontModel;
	}

	public void test1() {
		
		for (OntModel testModel : testModels) {
			
			for (Iterator i = testModel.listIndividuals(); i.hasNext(); ) {
				Individual in = (Individual) i.next();
				try {
					SemanticObject so = SemanticObjectBuilder.createSemanticObject(in); 
					assertNotNull("Can create SO",so);
				} catch (SemanticObjectBuilderException e) {
					fail(e.getLocalizedMessage());
				}
			}
		}
		
	}

}
