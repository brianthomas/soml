/**
 * 
 */
package net.datamodel.soml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;

import junit.framework.TestCase;
import net.datamodel.soml.Constant;
import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.dom.SOMLDocument;
import net.datamodel.soml.dom.SOMLElement;
import net.datamodel.soml.dom.SOMLReader;
import net.datamodel.soml.dom.xerces2.SOMLDocumentImpl;
import net.datamodel.xssp.parse.Specification;
import net.datamodel.xssp.parse.XSSPDocument;

import org.apache.log4j.Logger;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author thomas
 *
 */
public class UtilityForTests 
{

	private static final Logger logger = Logger.getLogger(UtilityForTests.class);

	private static final String SaxParserName = "org.apache.xerces.parsers.SAXParser";
	
	public static final SOMLReader createReader (SOMLDocument doc) {
		
		TestCase.assertTrue("got an reference for the document", null != doc);
		logger.debug("Document ref is:"+doc.getSchemaName());
		
		// create the reader
		SOMLReader r = new SOMLReader(doc);
		logger.debug("Reader ref is:"+r);
		TestCase.assertTrue("got an object reference for the reader", null != r);
		
		return r;
	}

	public static final void checkValidXMLRepresentation (XSSPDocument doc )
	throws Exception
	{
		boolean pretty = Specification.getInstance().isPrettyOutput();
		logger.debug("checkValidXMLRepresentation pretty:"+pretty);

		String xmlRep = doc.toXMLString();
		StringReader sr = new StringReader(xmlRep);

		logger.debug("   Document XML is:\n"+xmlRep);
		TestCase.assertTrue("Is valid version? pretty:"+pretty, validateSrc(new InputSource(sr), SaxParserName));

	}

	/** Check if the XMLrepresentation of the SemanticObject is valid.
	 * 
	 * @param so semantic object to check
	 */
	public static final void checkVariousValidSOMLRepresentations ( SemanticObject so, String testDirectory ) 
	throws Exception
	{
		logger.debug("Check valid string representation");

		// use the xerces representation
		SOMLDocument doc = new SOMLDocumentImpl();

		// TODO : check setting another prefix on the SOML namespace.
//		doc.setPrefixNamespaceMapping("so", Constant.SOML_NAMESPACE_URI);

		// create a new element, which will be the document root
		SOMLElement elem = doc.createSOMLElementNS (Constant.SOML_NAMESPACE_URI, so);

		// TODO: try it this way as well
//		so.setNamespaceURI(Constant.SOML_NAMESPACE_URI); // needed? doesnt it default to this? Check! 
//		SOMLElement elem = doc.createSOMLElement(q);

		// set the schema location
		String schemaLoc = Constant.SOML_NAMESPACE_URI+" "+testDirectory+"/"+Constant.SOML_SCHEMA_NAME;
		logger.debug("Set schema location:"+schemaLoc);
		elem.setAttribute("xsi:schemaLocation",schemaLoc);

		// now set the root element
		doc.setDocumentElement(elem);

		// now check various representations
		Specification.getInstance().setPrettyOutput(false);
		checkValidXMLRepresentation(doc);
		Specification.getInstance().setPrettyOutput(true);
		checkValidXMLRepresentation(doc);
		Specification.getInstance().setPrettyOutput(false);

		// as you where..
		logger.debug("Unset schema location:"+schemaLoc);
		elem.removeAttribute("xsi:schemaLocation"); 
		
	}

	public static final void copyFiles (String[] files, String sourceDirectory, String destDirectory) 
	throws FileNotFoundException, IOException 
	{
		for (int i = 0; i< files.length; i++) {
			copyFile(sourceDirectory+"/"+files[i], destDirectory+"/"+files[i]);
		}
	}

	public static final void copyFile (String infile, String outfile) 
	throws FileNotFoundException, IOException 
	{

		// logger.debug(" -- copy "+infile+" to "+outfile);
		BufferedInputStream is = new BufferedInputStream (new FileInputStream (infile));
		BufferedOutputStream os = new BufferedOutputStream (new FileOutputStream (outfile));

		int b;
		while (( b = is.read()) != -1) {
			os.write(b);
		}
		is.close();
		os.close();

	}

	/** All purpose XML file validator method, should work with any SAX level 2 
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public final static boolean validateFile (String filename)
	throws Exception 
	{
		return validateSrc(new InputSource(filename), SaxParserName);
	}

	/** All purpose validator method, should work with any SAX level 2 
	 * compliant parser.
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public final static boolean validateSrc (InputSource inputsource, String parserName )
	throws Exception 
	{

		try {

			logger.debug("create inputsource/sax parser");

			XMLReader parser = XMLReaderFactory.createXMLReader(parserName);

			logger.debug("set parser feature sets");
			parser.setFeature("http://xml.org/sax/features/validation", true);
			parser.setFeature("http://apache.org/xml/features/validation/schema", true);
			parser.setFeature("http://xml.org/sax/features/namespaces", true);
			//parser.setFeature("http://xml.org/sax/features/xmlns-uris", true);

			logger.debug(" set parser handlers");
			parser.setContentHandler (new MyValidator ());
			parser.setErrorHandler (new MyErrorHandler ());

			logger.debug(" Parsing uri:"+inputsource.getSystemId());
			parser.parse (inputsource);

		} catch (SAXParseException err) {
			String message =  "Failed: ** Parsing error"
				+ ", line " + err.getLineNumber ()
				+ ", uri " + err.getSystemId () + "\n" + err.getMessage();
			logger.error(message);
			return false;
		} catch (SAXException e) {
			Exception   x = e;
			if (e.getException () != null)
			{
				x = e.getException ();
			}
			// x.printStackTrace ();
			logger.error(x.getMessage());
			return false;

		} catch (Throwable t) {
			t.printStackTrace ();
			logger.error(" Failed parse:"+t.getMessage());
			throw new Exception("Failed:"+t.getMessage());
		}

		return true;
	}

	static class MyValidator extends DefaultHandler implements LexicalHandler
	{

		private Writer out;
		// here are all the SAX DocumentHandler methods

		public void setDocumentLocator (Locator l)
		{
			// we'd record this if we needed to resolve relative URIs
			// in content or attributes, or wanted to give diagnostics.
		}

		public void startDTD(String name, String publicId, String systemId)
		throws SAXException
		{
			//        emit("<[DOCTYPE "+name+" "+systemId+" "+publicId+"]>");
		}

		public void endDTD() throws SAXException {
		}

		public void startEntity(String name) throws SAXException {
		}

		public void endEntity(String name) throws SAXException {
		}

		public void startCDATA() throws SAXException {
		}

		public void endCDATA() throws SAXException {
		}

		public void comment(char ch[], int start, int length) throws SAXException {
		}

		public void startDocument ()
		throws SAXException
		{
			try {
				out = new OutputStreamWriter (System.out, "UTF8");
			} catch (IOException e) {
				throw new SAXException ("I/O error", e);
			}
			// emit ("<?xml version='1.0' encoding='UTF-8'?>\n");
		}

		public void endDocument ()
		throws SAXException
		{
			try {
				// out.write ("\n");
				out.flush ();
				out = null;
			} catch (IOException e) {
				throw new SAXException ("I/O error", e);
			}
		}

		public void startElement (String tag, AttributeList attrs)
		throws SAXException
		{
			// emit ("<"); emit (tag);
			// if (attrs != null) {
			//   for (int i = 0; i < attrs.getLength (); i++) {
			//     emit (" "); emit (attrs.getName (i)); emit ("\"");
			// XXX this doesn't quote '&', '<', and '"' in the
			// way it should ... needs to scan the value and
			// emit '&amp;', '&lt;', and '&quot;' respectively
			//     emit (attrs.getValue (i)); emit ("\"");
			//   }
			// }
			// emit (">");
		}

		public void endElement (String name)
		throws SAXException
		{
			// emit ("</"); emit (name); emit (">");
		}

		public void characters (char buf [], int offset, int len)
		throws SAXException
		{
			// NOTE:  this doesn't escape '&' and '<', but it should
			// do so else the output isn't well formed XML.  to do this
			// right, scan the buffer and write '&amp;' and '&lt' as
			// appropriate.

			//         try {
			//             out.write (buf, offset, len);
			//         } catch (IOException e) {
			//             throw new SAXException ("I/O error", e);
			//         }

		}

		public void ignorableWhitespace (char buf [], int offset, int len)
		throws SAXException
		{
			// this whitespace ignorable ... so we ignore it!

			// this callback won't be used consistently by all parsers,
			// unless they read the whole DTD.  Validating parsers will
			// use it, and currently most SAX nonvalidating ones will
			// also; but nonvalidating parsers might hardly use it,
			// depending on the DTD structure.
		}

		public void processingInstruction (String target, String data)
		throws SAXException
		{

		}

		// helpers ... wrap I/O exceptions in SAX exceptions, to
		// suit handler signature requirements
		private void emit (String s)
		throws SAXException
		{
			try {
				out.write (s);
				out.flush();
			} catch (IOException e) {
				throw new SAXException ("I/O error", e);
			}
		}
	} // end class Validator 

	static class MyErrorHandler extends HandlerBase
	{
		// treat validation errors as fatal
		public void error (SAXParseException e)
		throws SAXParseException
		{
			throw e;
		}

		// dump warnings too
		public void warning (SAXParseException err)
		throws SAXParseException
		{
			logger.warn ("** Warning"
					+ ", line " + err.getLineNumber ()
					+ ", uri " + err.getSystemId ());
			logger.warn ("   " + err.getMessage ());
		}
	} // end class MyErrorHandler 


}
