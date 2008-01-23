/**
 * 
 */
package net.datamodel.soml.dom.xerces2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.StringWriter;
import java.io.Writer;

import net.datamodel.soml.UtilityForTests;
import net.datamodel.soml.dom.BaseParseCase;
import net.datamodel.soml.dom.SOMLDocument;
import net.datamodel.soml.dom.SOMLReader;
import net.datamodel.xssp.dom.Specification;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author thomas
 *
 */
public class TestLoad extends BaseParseCase 
{
	
	private static final Logger logger = Logger.getLogger(TestLoad.class);
	
	private static final String schemaDirectory = "docs/schema";
	private static final String samplesDirectory = "docs/samples";
	
	private static String [] samplefiles = null;
	private static boolean didInit = false;
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		if (!didInit) 
		{
			
			logger.debug("find sample and schema files");
			samplefiles = new File(samplesDirectory).list(new OnlyXML());
			String [] sampleschemafiles = new File(samplesDirectory).list(new OnlySchema());
			String [] baseschemafiles = new File(schemaDirectory).list(new OnlySchema());
			
			logger.debug("copy over sample and schema files to test directory");
			try {
				UtilityForTests.copyFiles(samplefiles, samplesDirectory, testDirectory); 
				UtilityForTests.copyFiles(sampleschemafiles, samplesDirectory, testDirectory); 
				UtilityForTests.copyFiles(baseschemafiles, schemaDirectory, testDirectory); 
			} catch (Exception e) {
				logger.error("Cant set up tests : "+ e.getMessage());
				e.printStackTrace();
			}
			
			didInit = true; 
		}

	}


	// Attempt to simply load all of the test samples in the samples directory
	public void test1() throws Exception {
		
		logger.info("testLoadSamples");
		try {
			for (int i = 0; i< samplefiles.length; i++)
			{
				SOMLDocument doc = loadFile(testDirectory+"/"+samplefiles[i]);
				assertNotNull("Document reference exists for file"+samplefiles[i], doc);
			    Specification.getInstance().setPrettyOutput(true);
			    
			    StringWriter sw = new StringWriter(); 
			    doc.toXMLWriter(sw);
			    logger.info("STRING DOC:"+sw.toString());
			    sw.close();
			    
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail (e.getMessage());
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	// A test of being able to read, and write the sample.
	// We don't compare the 2 file contents because there may have been
	// some dropped XML comments from the initial parse (which is OK). 
	public void test2() throws Exception {
		
		logger.info("testLoadAndWriteSamples rdf");
		
		// set the output specification
	    Specification.getInstance().setPrettyOutput(true);
	    Specification.getInstance().setPrettyOutputIndentation("  ");
		
		try {
			for (int i = 0; i< samplefiles.length; i++)
			{
				File outputfile = new File(testDirectory+"/tmp.xml");
				SOMLDocument doc = loadFile(testDirectory+"/"+samplefiles[i]);
				
				assertNotNull("Document reference exists", doc);

			    Writer myWriter = new BufferedWriter(new FileWriter(outputfile));
			    StringWriter sw = new StringWriter(); 
			    
			    doc.toXMLWriter(new BufferedWriter(sw));
			    logger.debug("STRING DOC:"+sw.toString());
			    
			    doc.toXMLWriter(myWriter);
			    logger.debug(" DOCUMENT:"+doc.toXMLString());
			    // myWriter.flush();
			    myWriter.close();
			     
				assertTrue("can write file", outputfile.canWrite());
				assertTrue("File has non-zero extent", outputfile.length() > 0);
				assertTrue ("Output document:"+samplefiles[i]+" is valid", UtilityForTests.validateFile (testDirectory+"/tmp.xml"));
				
				// clean up		
				outputfile.delete();
				
			}
		} catch (Exception e) {
			fail (e.getMessage());
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	// Test our ability to get consistent results from loading and reloading
	// files. We compare between the products of the first and second loadings.
	public void test3() throws Exception {
		
		logger.info("testMultiLoadAndWriteSamples");
		
		// set the output specification
		Specification.getInstance().setPrettyOutput(true);
		Specification.getInstance().setPrettyOutputIndentation("  ");
		
		try {
			for (int i = 0; i< samplefiles.length; i++)
			{
				File outputfile = new File(testDirectory+"/tmp.xml");
				SOMLDocument doc = loadFile(testDirectory+"/"+samplefiles[i]);
				//String firstload = "";
				//String secondload = "";
				
				logger.debug("Attempting to multi load/write file : ["+samplefiles[i]+"]");
				
				assertNotNull("initial document reference exists", doc);
				
			    Writer myWriter = new BufferedWriter(new FileWriter(outputfile));
			    StringWriter stringWriter1 = new StringWriter();
			    Writer bsw1 = new BufferedWriter(stringWriter1);
			    doc.toXMLWriter(myWriter);
			    doc.toXMLWriter(bsw1);
			    // myWriter.flush();
			    myWriter.close();
			     
				assertTrue("can write initial file", outputfile.canWrite());
				assertTrue( "initial file has non-zero extent", outputfile.length() > 0);
				
				// now try to read the temp file
				doc = loadFile(testDirectory+"/tmp.xml");
				
				assertNotNull("second document reference exists", doc);
				
			    StringWriter stringWriter2 = new StringWriter();
			    Writer bsw2 = new BufferedWriter(stringWriter2);
			    doc.toXMLWriter(bsw2);
			    
			    stringWriter1.flush();
			    stringWriter2.flush();
			    
			    // logger.debug( "first string:"+stringWriter1.getBuffer().toString());
			    // logger.debug( "second string:"+stringWriter2.getBuffer().toString());
			    
				assertEquals("Document content is the same", 
						stringWriter1.getBuffer().toString(), stringWriter2.getBuffer().toString());
				
				// clean up
				outputfile.delete();
			}
		} catch (Exception e) {
			fail (e.getMessage());
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	private static SOMLDocument loadFile (String inputfile ) throws Exception {

		logger.debug("Attempting to load file : ["+inputfile+"]");

		SOMLDocument doc = new SOMLDocumentImpl();
		SOMLReader r = UtilityForTests.createReader(doc);
		
		r.parseFile(inputfile);
		return doc;

	}
	
	class OnlyXML implements FilenameFilter {
		public boolean accept (File dir, String s) {
			if (s.endsWith(".xml")) { return true; }
			return false;
		}
	}
	
	class OnlySchema implements FilenameFilter {
		public boolean accept (File dir, String s) {
			if (s.endsWith(".xsd")) { return true; }
			return false;
		}
	}
	
}
