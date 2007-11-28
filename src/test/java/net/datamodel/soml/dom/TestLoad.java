/**
 * 
 */
package net.datamodel.soml.dom;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;

import net.datamodel.soml.UtilityForTests;
import net.datamodel.soml.dom.SOMLDocument;
import net.datamodel.soml.dom.SOMLReader;
import net.datamodel.soml.dom.DOMXerces2.SOMLDocumentImpl;
import net.datamodel.xssp.parse.Specification;

import org.apache.log4j.Logger;

/**
 * @author thomas
 *
 */
public class TestLoad extends BaseParseCase 
{
	
	private static final Logger logger = Logger.getLogger(TestLoad.class);
	
	// Attempt to simply load all of the test samples in the samples directory
	public void testLoadSamples () throws Exception {
		
		logger.info("testLoadSamples");
		try {
			for (int i = 0; i< samplefiles.length; i++)
			{
				SOMLDocument doc = loadFile(testDirectory+"/"+samplefiles[i]);
				assertNotNull("Document reference exists for file"+samplefiles[i], doc);
			}
		} catch (Exception e) {
			fail (e.getMessage());
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	// A test of being able to read, and write the sample.
	// We don't compare the 2 file contents because there may have been
	// some dropped XML comments from the initial parse (which is OK). 
	public void testLoadAndWriteSamples () throws Exception {
		
		logger.info("testLoadAndWriteSamples");
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
			    doc.toXMLWriter(myWriter);
			    // myWriter.flush();
			    myWriter.close();
			     
				assertTrue("can write file", outputfile.canWrite());
				assertTrue("File has non-zero extent", outputfile.length() > 0);
				assertTrue ("Output document is valid", UtilityForTests.validateFile (testDirectory+"/tmp.xml"));
				
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
	public void testMulitLoadAndWriteSamples () throws Exception {
		
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
	
	
}
