
// CVS $Id$

// SOMLReader.java Copyright (c) 2004 Brian Thomas. All rights reserved.

/* LICENSE

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA

*/

/* AUTHOR

   Brian Thomas  (baba-luu@earthlink.net)
   
*/

package net.datamodel.soml.support;

import java.io.StringReader;
import java.util.Map;

import net.datamodel.xssp.support.Constants;

import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/** This class is used to create SOMLDocument from SOML files/streams.
 * As a default it uses the Xerces2 SAXParser.
 */
public class SOMLReader
{
	
	private static final Logger logger = Logger.getLogger(SOMLReader.class);
	
    // Fields 
    protected SOMLDocumentHandler myDocumentHandler;
    
    // XML Parser class name. Default to Xerces.
    protected String parsername = "org.apache.xerces.parsers.SAXParser";

    //
    // Constructor methods 
    //

    /** Constructor. Pass a SOMLDocument to use as the structure to read 
     *  information into. Note that if the passed SOMLDocument has
     *  prior information in it, it will remain *unless* overridden by 
     *  conflicting information from the input source. 
     */
    public SOMLReader(SOMLDocument doc) 
    {
       myDocumentHandler = new SOMLDocumentHandler(doc);
    }

    /** Alt constructor. Pass a document handler to use by the SOMLReader. 
     */
    public SOMLReader (SOMLDocumentHandler docHandler) 
    {
       myDocumentHandler = docHandler;
    }

    //
    // Public Methods
    //
    
    /** Set the name of the underlying XML parser class the reader will use.
     * 
     */
    public void setXMLParserName (String val) { parsername = val; }
    
    /** Get the name of the underlying XML Parser class the reader will use.
     * 
     * @return String value of the parser class name. 
     */
    public String getXMLParserName () { return parsername; }
    
    /** Merge in external map to the internal startElement handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        and the value is a code reference to the class that will handle 
        the event. The class must implement the StartElementAction interface. 
        It is possible to override default SOML startElement handlers with 
        this method. 
     */
    public void addStartElementHandlers (Map m, String namespace) 
    throws NullPointerException
    {
       myDocumentHandler.addStartElementHandlers(m,namespace);
    }

    /** Merge in external Hashtable into the internal charData handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        the XML document that has CDATA and the value is a code reference
        to the class that will handle the event. The class must implement 
        the CharDataAction interface. It is possible to override default
        SOML cdata handlers with this method. 
     */
    public void addCharDataHandlers (Map m, String namespace) {
       myDocumentHandler.addCharDataHandlers(m,namespace);
    }

    /** Merge in external map to the internal endElement handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        and the value is a code reference to the class that will handle 
        the event. The class must implement the StartElementAction interface. 
        It is possible to override default SOML startElement handlers with 
        this method. 
    */
    public void addEndElementHandlers (Map m, String namespace) {
       myDocumentHandler.addEndElementHandlers(m,namespace);
    }

    /** Set the default handler for the start elements in the document handler.  
     */
    public void setDefaultStartElementHandler (StartElementHandlerAction handler) {
       myDocumentHandler.setDefaultStartElementHandler(handler);
    }

   /** Set the default handler for the end elements in the document handler.  
     */
    public void setDefaultEndElementHandler (EndElementHandlerAction handler) {
       myDocumentHandler.setDefaultEndElementHandler(handler);
    }

    /** Set the default handler for character data in the document handler.  
     */
    public void setDefaultCharDataHandler (CharDataHandlerAction handler) {
       myDocumentHandler.setDefaultCharDataHandler(handler);
    }

   /** Get the (SOML) Document that the SOMLReader will parse an InputSource into. 
     *  @return SOMLDocument that results from the parsing.
    */
    public SOMLDocument getDocument() 
    {
       return myDocumentHandler.getDocument();
    }

    /** Parse an InputSource into a SOMLDocument object.
     *  @return SOMLDocument that results from the parsing.
     */
    public SOMLDocument parse (InputSource inputsource) 
    throws java.io.IOException
    {
        return parse(inputsource, parsername);
    }

    /** Utility method to parse from file into a SOMLDocument.
     *   @return SOMLDocument that results from the parsing.
     */
    public SOMLDocument parseFile (String file)
    throws java.io.IOException
    {

        InputSource input;

        //
        // Turn the filename into an input source
        //
        // NOTE:  The input source must have a "system ID" if
        // there are relative URLs in the input document.  The
        // static resolver methods handle that automatically
        // in most cases.
        //
//        input = Resolver.createInputSource (new File(file));
        input = new InputSource (file);


        // now parse it, return whatever structure is derived
        return parse(input);

    }

    /** Utility method to parse from a string into a SOMLDocument.
     *  @return SOMLDocument
     */
    public SOMLDocument parseString (String XMLContent)
    throws java.io.IOException
    {

        InputSource input;
        StringReader reader = new StringReader(XMLContent);

        //
        // Turn the filename into an input source
        //
        // NOTE:  The input source must have a "system ID" if
        // there are relative URLs in the input document.  The
        // static resolver methods handle that automatically
        // in most cases.
        //
//        input = Resolver.createInputSource (new File(file));
        input = new InputSource (reader);

        // now parse it, return whatever structure is derived
        return parse(input);

    }

    // Private Methods
    //

    /** Parse an InputSource into a SOMLDocument.
     * Set to private because we want users to only set XML parser in
     *  the Specification object, not override it in the method call. -b.t. 
     * @return SOMLDocument
     */
    private SOMLDocument parse (InputSource inputsource, String parsername) 
    throws java.io.IOException
    {

        logger.debug("SOMLReader is using the XML parser:"+ parsername);
        logger.debug("          is using inputsource sysId:"+ inputsource.getSystemId());
        logger.debug("          is using inputsource pubId:"+ inputsource.getPublicId());

        String[] sysIdPath = inputsource.getSystemId().split(Constants.FILE_SEP);
        if(sysIdPath.length > 1)
        {
            String path = "";
            for(int i=sysIdPath.length-2; i>=0; i--)
              path = sysIdPath[i] + Constants.FILE_SEP + path;
            logger.debug(" setting relative path in contentHandler:"+path);
            myDocumentHandler.setRelativePath(path);
        }

        try {

            XMLReader parser = XMLReaderFactory.createXMLReader(parsername);

            // set parser handlers to SOML standard ones
            parser.setDTDHandler(myDocumentHandler);
            parser.setContentHandler(myDocumentHandler);
            parser.setErrorHandler (new myErrorHandler());
            parser.setEntityResolver(new myEntityResolver());
            
            // To set the LexicalHandler for an XML reader, use the setProperty method with the
            // propertyId "http://xml.org/sax/properties/lexical-handler". If the reader does not support
            // lexical events, it will throw a SAXNotRecognizedException or a
            // SAXNotSupportedException when you attempt to register the handler.
            try {
               parser.setProperty("http://xml.org/sax/properties/lexical-handler", myDocumentHandler);
            } catch (org.xml.sax.SAXNotSupportedException e) {
               logger.info("This parser does not support LexicalHandlers and does not allow inspection of DTD events by the SOMLreader.");
               logger.info("This means that the SOMLDocument returned by this parser may not have their XMLDeclaration and DocumentType objects set appropriately.");
               myDocumentHandler.setForceSetXMLHeaderStuffOnSemanticObject(true);
            } catch (org.xml.sax.SAXNotRecognizedException e) {
               logger.info("This parser does not support LexicalHandlers and does not allow inspection of DTD events by the SOMLReader.");
               logger.info("This means that the SOMLDocument returned by this parser may not have their XMLDeclaration and DocumentType objects set appropriately.");
               myDocumentHandler.setForceSetXMLHeaderStuffOnSemanticObject(true);
            } catch (Exception e) {
               e.printStackTrace();
            }

            // ok, now we are ready to parse the inputsource 
            parser.parse(inputsource);

        } catch (SAXParseException err) {
            String message = "** Parsing error"+", line "+err.getLineNumber()
                +", uri "+err.getSystemId()+"   " + err.getMessage();
            throw new java.io.IOException(message);

        } catch (SAXException e) {
            Exception   x = e;
            if (e.getException () != null)
                x = e.getException ();
            x.printStackTrace();
            throw new java.io.IOException(x.getMessage());

        } catch (Throwable e) {
            e.printStackTrace();
            throw new java.io.IOException(e.getMessage());
        }

        // return the parsed object
        return getDocument();

    }

} // end SOMLReader class

//
// External classes (put here because only SOMLReader uses them)  
//

// The parser error Handler
class myErrorHandler implements ErrorHandler
{
	private static final Logger logger = Logger.getLogger(myErrorHandler.class);
	
  // treat validation errors as fatal
  public void error (SAXParseException e)
  throws SAXParseException
  {
    throw e;
  }

  // dump warnings too
  public void warning (SAXParseException e)
  throws SAXParseException
  {
    logger.error("** Warning"+", line "+e.getLineNumber()
               + ", uri " + e.getSystemId());
    logger.error("   " + e.getMessage()+" **");
  }

  public void fatalError (SAXParseException e)
  throws SAXException
  {
     throw e;
  }

} // End of myErrorHandler class 

// parser EntityResolver
class myEntityResolver implements EntityResolver {
	
	private static final Logger logger = Logger.getLogger(myEntityResolver.class);

   public InputSource resolveEntity (String publicId, String systemId)
   {

     logger.debug("CALL to Entity Resolver:"+publicId+" "+systemId);
     if (systemId != null) {
        // return a special input source
        return new InputSource(systemId);
     } else {
        // use the default behaviour
        return null;
     }
   }


} // End of myEntityResolver class 

