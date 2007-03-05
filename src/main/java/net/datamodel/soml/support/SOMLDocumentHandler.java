

// SOMLDocumentHandler.java Copyright (c) 2004 Brian Thomas. All rights reserved.

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

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.datamodel.soml.SemanticObject;
import net.datamodel.soml.support.handlers.DefaultCharDataHandlerFunc;
import net.datamodel.soml.support.handlers.DefaultElementWithCharDataHandlerFunc;
import net.datamodel.soml.support.handlers.DefaultEndElementHandlerFunc;
import net.datamodel.soml.support.handlers.DefaultStartElementHandlerFunc;
import net.datamodel.xssp.XMLSerializableObject;
import net.datamodel.xssp.support.Constants;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/** 
     Contains the core SAX document handler for the Reader. It also contains
     the basic SOML element/charData handlers (as internal classes).
     @version $Revision$
 */
public class SOMLDocumentHandler extends DefaultHandler 
implements LexicalHandler
{

    // Fields
    //
	
	private static final Logger logger = Logger.getLogger(SOMLDocumentHandler.class);
	
	private static enum HandlerType { START, END, CHAR }
	
	/** The namespace URI of this package.
	 */ 
	public static final String SOML_NAMESPACE_URI = "http://www.data-model.net/SOML";

	/** The XML schema version used by this package.
	 */ 
	public static final String XML_SCHEMA_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
	
	/** The XML schema-instance version used by this package.
	 */ 
	public static final String XML_SCHEMA_INSTANCE_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance"; 
	
	/** The name of the relevant version of the schema file for this package.
	 */ 
	public static final String SOML_SCHEMA_NAME = "SOML_01.xsd";
	  
	/** The document into which we will parse.
	 * 
	 */
    private SOMLDocument myDocument;
    
    // Options for the document handler
    protected Map<String,String> Options = new Hashtable<String,String>();

    /** The association between complexType name 
     *  (used has a key to lookup handlers) and elem name
     */
    private Map<String,Map<String,HandlerInfo>> ElementTypeAssoc = new Hashtable<String,Map<String,HandlerInfo>>(); 

    // dispatch table action handler Maps
    private Map<String,Map<String,HandlerAction>> StartElementHandlers = new Hashtable<String,Map<String,HandlerAction>>(); // start element handlers
    private Map<String,Map<String,HandlerAction>> EndElementHandlers = new Hashtable<String,Map<String,HandlerAction>>();   // end element handlers 
    private Map<String,Map<String,HandlerAction>> CharDataHandlers = new Hashtable<String,Map<String,HandlerAction>>();     // charData handlers
    private Map<String,HandlerAction> DefaultHandlers = new Hashtable<String,HandlerAction>();      // default handlers 

    // FIX: hurm.. needed? Lack of LexicalHandler in parser seems to require it.. 
    private boolean ForceSetXMLHeaderStuff = false;
    
    // TODO: needed?
    private List<Object> ObjectList = new Vector<Object>();

    // References to the  working objects
    private List<String> NodeName = new Vector<String>();
    private List<Node> Nodes = new Vector<Node>();
    private List<String> ElementNamespaceURIList = new Vector<String>();

    /** Keep track of the present hierarchy of semantic objects
     * we have parsed/created. Earlier items in the list are the
     * 'parent' of later listed items.
     */ 
    private Map<String,SemanticObject> SemanticObjects = new Hashtable<String,SemanticObject>(); 

    // needed to capture internal entities.
    private Set<Map<String,String>> Notation = new HashSet<Map<String,String>>();
    private Map<String,Map<String,String>> UnParsedEntity = new Hashtable<String,Map<String,String>>();
    private Map<String,String> PrefixNamespaceMapping = new Hashtable<String,String>();
    private Map<String,String> DoctypeObjectAttributes = new Hashtable<String,String>(); 

    /** The relative path to the inputsource this content handler is working on.
     */ 
    protected String RelativePath;

    /** Have we attempted to load the schema (and ancillary handlers) yet?
     */  
    private boolean AttemptedSchemaLoad = false;
    
    /** Remembers which schema we have loaded.
     */ 
    private Map<String,String> LoadedSchema = new Hashtable<String,String>(); 

    /** Keeping track of whether or not we are reading in a CDATA section.
     */ 
    private boolean ReadingCDATASection = false; 

    /** Whether or not to ignore whitespace only char data. 
     * This is a BAD thing.  I have been having troubles 
     * distinguishing between important whitespace (e.g. 
     * char data within a data node) and text nodes that are 
     * purely for the layout of the XML document. Right now 
     * this code uses the CRUDE distinquishing characteristic 
     * that fluff (eg. only there for the sake of formatting 
     * the output doc) text nodes are all whitespace.
     */
    public boolean IgnoreWhitespaceOnlyData = true;

    // patterns..
    private Pattern XMLNamespacePrefixPattern = Pattern.compile ("(xmlns):?(.*?)", Pattern.DOTALL | Pattern.COMMENTS);
    private Pattern PrefixPattern = Pattern.compile ("(.*?):(.*?)", Pattern.DOTALL | Pattern.COMMENTS);
    private Pattern SchemaLocationPattern = Pattern.compile ("(.*?)\\s+(.*?)", Pattern.DOTALL | Pattern.COMMENTS);
    
    // TODO : put schema pattern in SOMLDocument, not the doc handler.
    private Pattern SOMLSchemaPattern = Pattern.compile (".*"+SOML_SCHEMA_NAME, Pattern.COMMENTS);

    // Constuctors
    //

    /** Construct a handler for a given document. 
     * 
     * @param doc
     */
    public SOMLDocumentHandler (SOMLDocument doc)
    {
       init();
       setDocument(doc);
    }
    
    /** Construct a handler for given document and handling options.
     * 
     * @param doc
     * @param options
     */ 
    public SOMLDocumentHandler (SOMLDocument doc, Map<String,String> options)
    {
       init();
       Options = options;
       setDocument(doc);
    }

    //
    // Non-Sax Public Methods
    //

    /** Get the document the Document that the SAX handler will parse into. 
    */
    public SOMLDocument getDocument() { return myDocument; }

    /**
     * Record the relative path for the inputSource that the content handler is working on.
     */
    public void setRelativePath (String path) { RelativePath = path; }

   /** Set the document the Document that the handler will parse into. 
    * @throws NullPointerException
    */
    public void setDocument (SOMLDocument doc)
    {

       if(doc == null)
         throw new NullPointerException();

       myDocument = doc;
    }

    /** Merge in external map to the internal startElement handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        and the value is a code reference to the class that will handle 
        the event. The class must implement the StartElementAction interface. 
        It is possible to override default SOML startElement handlers with 
        this method by specifying the SOML namespace URI. 
     */
    public void addStartElementHandlers (Map<String, HandlerAction> m,
    		String namespace) 
    throws NullPointerException
    {
       if (m == null || namespace == null )
           throw new NullPointerException();

       if (StartElementHandlers.containsKey(namespace)) {
          //  merge to existing table 
          StartElementHandlers.get(namespace).putAll(m);
       } else {
          // create whole new table added with given namespace 
          Map<String, HandlerAction> newHandlers = new Hashtable<String,HandlerAction>();
          newHandlers.putAll(m);
          StartElementHandlers.put(namespace, newHandlers);
       }
    }

    /** Merge in external Hashtable into the internal charData handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        the XML document that has CDATA/PCDATA and the value is a code reference
        to the class that will handle the event. The class must implement 
        the CharDataAction interface. It is possible to override default
        SOML cdata handlers with this method by specifying the SOML namespace URI. 
        @return true if merge succeeds, false otherwise (null map was passed).
     */
    public void addCharDataHandlers (Map<String,HandlerAction> m, String namespace) 
    throws NullPointerException
    {

       if (m == null || namespace == null )
           throw new NullPointerException();

       if (CharDataHandlers.containsKey(namespace)) {
          CharDataHandlers.get(namespace).putAll(m);
       } else {
          Map<String,HandlerAction> newHandlers = new Hashtable<String,HandlerAction>();
          newHandlers.putAll(m);
          CharDataHandlers.put(namespace, newHandlers);
       }

    }

    /** Merge in external map to the internal endElement handler Hashtable. 
        Keys in the Hashtable are strings describing the node name in
        and the value is a code reference to the class that will handle 
        the event. The class must implement the EndElementAction interface. 
        It is possible to override default SOML endElement handlers with 
        this method by specifying the SOML namespace URI. 
    */
    public void addEndElementHandlers (Map<String,HandlerAction> m, String namespace) 
    throws NullPointerException
    {
       if (m == null || namespace == null )
           throw new NullPointerException();

       if (EndElementHandlers.containsKey(namespace)) {
          EndElementHandlers.get(namespace).putAll(m);
       } else {
          Map<String,HandlerAction> newHandlers = new Hashtable<String,HandlerAction>();
          newHandlers.putAll(m);
          EndElementHandlers.put(namespace, newHandlers);
       }
    }

    /** Add an association between an element of a particular URI namespace
     *  and a complexType of another URI namespace. This allows the document handler
     * to choose the appropriate complexType handler action (either a startElement,
     * endElement or CharData type, depending on the context of the parse) will
     * be invoked when that element is parsed.
     */
    public void addElementToComplexTypeAssociation ( String elementName, String elementURI,
                                         String complexTypeName, String complexTypeURI) {

        Map<String,HandlerInfo> table = null;
        if(!ElementTypeAssoc.containsKey(elementURI))
             table = new Hashtable<String,HandlerInfo> ();
        else
             table = ElementTypeAssoc.get(elementURI);

        logger.info(" Associating Element: "+elementName+"["+elementURI+"] --> complexType:"+complexTypeName+"["+complexTypeURI+"]");
        table.put(elementName, new HandlerInfo(complexTypeName, complexTypeURI));

    }

    /**
        Set the default Start Element Handler. This specifies what happens to nodes
        which are not explicitly defined in the startElementHandler table. When this
        method is called, the original default handler is replaced with the passed
        handler.
    */
    public void setDefaultStartElementHandler (StartElementHandlerAction handler) {
       DefaultHandlers.put("startElement", handler);
    }

   /**
        Set the default End Element Handler. This specifies what happens to nodes
        which are not explicitly defined in the endElementHandler table. When this
        method is called, the original default handler is replaced with the passed
        handler.
    */ 
    public void setDefaultEndElementHandler (EndElementHandlerAction handler) {
       DefaultHandlers.put("endElement", handler);
    }

   /**
        Set the default Character Data Handler. This specifies what happens to nodes
        which are not explicitly defined in the charDataElementHandler table. When this
        method is called, the original default handler is replaced with the passed
        handler.
    */
    public void setDefaultCharDataHandler (CharDataHandlerAction handler) {
       DefaultHandlers.put("charData", handler);
    }

    /** Allow setting a heavy-handled mode. 
     * If true it tells this SOMLDocumentHandler that it should 
     * go ahead and insert XMLHeader stuff even if the  
     * parser doesnt support DTD events using reasonable values.
     */
    public void setForceSetXMLHeaderStuff (boolean value) { ForceSetXMLHeaderStuff = value; }
    
    /** Get the value of 'forceSetXMLHeaderStuff'. 
     * If true it tells this SOMLDocumentHandler that it should 
     * go ahead and insert XMLHeader stuff even if the  
     * parser doesnt support DTD events using reasonable values.
     * 
     * @return value of the mode  
     */
    public boolean getForceSetXMLHeaderStuff() { return ForceSetXMLHeaderStuff; }

    /** In order to look for referenced Semantic Objects, we "record" each that we parse.
     */
    // TODO : needed??
    public void recordSemanticObject (SemanticObject so) {
       String Id = so.getId();
       if (!Id.equals("")) {
          // If we have an id, then add this into the list of 
    	  // semantic object objects we have
          SemanticObjects.put(Id, so);
       }
    }

    //
    // Methods that describe the  parsing
    //

    /** Get the  object with Semantic Objects we are working on. 
     */
    public SemanticObject getCurrentSemanticObject() {
       SemanticObject lastSO = null;
       if (SemanticObjects.size() > 0) {
          lastSO = SemanticObjects.get(SemanticObjects.size()-1);
       }
       return lastSO;
    }

    /** Remove the  semantic object.
     *  @return SemanticObject that was removed from the list of "" Semantic Objects.
     */
    public SemanticObject removeSemanticObject()  {
       return SemanticObjects.remove(SemanticObjects.size()-1);
    }

    /** Get the current object we are working on.
     */
    public Object getCurrentObject() {
       Object lastObject = (Object) null;
       if (ObjectList.size() > 0) {
          lastObject = ObjectList.get(ObjectList.size()-1);
       }
       return lastObject;
    }

    /** Get the namespace URI value for the  element being parsed.
     */
    public String getElementNamespaceURI() {
       String lastURI = (String) null;
       if (ElementNamespaceURIList.size() > 0) {
          lastURI = (String) ElementNamespaceURIList.get(ElementNamespaceURIList.size()-1);
       }
       return lastURI;
    }
    
    /** Determine whether or not we are reading a CDATASection.
     * 
     * @return
     */
    public boolean isReadingCDATASection () { return ReadingCDATASection; }

    /** Get the 'current' node in the list of nodes we have parsed.
     * @return Node
     */
    public Node getCurrentNode () {
       int size = Nodes.size();
       Node node = null;

       if(size > 0) {
          node = Nodes.get(size-1);
       }
       
       // Hmm. nothing in the list? then try to take the root node, the
       // document element, as a backup.
       if(node == null) {
          node = (Node) getDocument().getDocumentElement();
       }
       
       return node;
    }

    /** Add a node to the list of nodes.
     * @param Node to add 
     */
    public void addNode (Node node) { Nodes.add(node); }

    /** Remove the  node from the list of nodes.
     * 
     * @return Node that was removed.
     */
    public Node removeCurrentNode () { return Nodes.remove(Nodes.size()-1); }

    /** Get the name of the present node in the parse.
     * 
     * @return
     */
    public String getCurrentNodeName () {
       int pathSize = NodeName.size();
       return (String) NodeName.get((pathSize-1));
    }

    /** Utility method to create a new element node for the document.
     */
    public Element createElement(String namespaceURI, String qName, Attributes attrs)
    {
        Element elem = getDocument().createElementNS(namespaceURI, qName);

        int size = attrs.getLength();
        for (int i = 0; i < size; i++) {
              String qname = attrs.getQName(i);
              String value = attrs.getValue(i);
              Attr attrib = getDocument().createAttributeNS(namespaceURI,qname);
              attrib.setValue(value);
              elem.setAttributeNodeNS(attrib);
        }

        return elem;
    }

    //
    // SAX methods
    //

    /** StartElement handler.
     */
    public void startElement (String namespaceURI, String localName, String qName, Attributes attrs)
    throws SAXException
    {
           
        String elementName = localName;
        Object thisObject = (Object) null;

        // if we haven't done this already, load schema in order to get
        // the element->complexType and complexType->handler associations
        if(!AttemptedSchemaLoad) 
        {
        	InitFromSchema(attrs);
           AttemptedSchemaLoad = true;
        }

        logger.info("H_START:["+localName+","+qName+","+namespaceURI+"]");

        // find complexType (key) for handler
        HandlerInfo handlerInfo = findHandlerInfoFromElementName(namespaceURI,elementName);
        logger.debug(" * got handler Info:"+handlerInfo);
        String handlerName = handlerInfo.name;
        logger.debug(" * got handler Name:"+handlerName);
        String handlerURI = handlerInfo.uri;
        logger.debug(" * got handler URI:"+handlerURI);
        Hashtable uriStartHandlers = (Hashtable) StartElementHandlers.get(handlerURI);

        // if a handler exists, run it, else give a warning
        if (uriStartHandlers != null && uriStartHandlers.containsKey(handlerName)) {

        	// run the appropriate start handler
        	StartElementHandlerAction event = (StartElementHandlerAction) uriStartHandlers.get(handlerName);
        	thisObject = event.action(this, namespaceURI, localName, qName, attrs);

        		// TODO: need to determine if we need this stuff below..
    	/*
           // Treat any special handling here
           if (thisObject != null && thisObject instanceof SemanticObject)
           {

               logger.debug(" *** THIS ELEMENT is A semantic object "+qName);
               SemanticObject q = (SemanticObject) thisObject;

               // do special check for dealing with Semantic Objects
               // this is here because its easier to deal with adding member
               // Semantic Objects, AxisFrames, etc here to prevent repeating code
               // that defaults to adding QElements here rather than in the Element Handler (?)
               // I know that it looks bad to have this call here, but I'd rather 
               // treat this here rather repeat this code in all SemanticObject handlers..
               startHandlerAddsemantic objectToParent(namespaceURI, q);

               // record this as our "" semantic object
               SemanticObjects.add(q);

               // also record a locator from it
               if(q instanceof semantic object)
                  LocatorList.add(((semantic object) q).createLocator());

           } 
       */

       
        	// take care of issues related to being XMLSerializableObject
        	if( thisObject != null && thisObject instanceof XMLSerializableObject)
        	{
        
        		logger.debug(" *** THIS ELEMENT is an XMLSerializableObject qName:"+qName+" localName:"+localName);
        		// don't set local name or prefix for reference Semantic Objects! 
         
        		// TODO: need to determine if we need this stuff below..
        		/*
               if(!localName.equals(Constants.NodeName.REFERENCE_semantic object))
               {
                  ((XMLSerializableObject) thisObject).setXMLNodeName(localName);

                  // set the prefix on this object
                  logger.debug("CHECK FOR prefix - qName.:["+qName+"] local:["+localName+"]");
                  if( !localName.equals(qName))
                  {
   
                     Matcher myMatcher = PrefixPattern.matcher(qName);
                     if(myMatcher.matches()) {
                        logger.debug("YES have prefix - adding.:["+myMatcher.group(1).trim()+"]");
                   // set the prefix
                        String prefix = myMatcher.group(1).trim();
                        if(PrefixNamespaceMapping.containsKey(prefix))
                        {
                            String namespace = (String) PrefixNamespaceMapping.get(prefix);
                            ((XMLSerializableObject) thisObject).setNamespaceURI(namespace);
                            logger.debug(" SETTING NamespaceURI for "+localName+" as "+namespace);
                        }
   
                     }
                  } else { // go with declared default document namespace IF differs from  one
                  */
 
        		// TODO: is this really needed?
        		/*
        		String doc_default_namespace = (String) PrefixNamespaceMapping.get("");
        		String orig_namespace = ((XMLSerializableObject) thisObject).getNamespaceURI();
         
        		// is the original namespace the document namespace? if not we have to fix this
        		// to match...
        		if (!doc_default_namespace.equals(orig_namespace)) {
        			((XMLSerializableObject) thisObject).setNamespaceURI(doc_default_namespace);
        		}
        		*/
        		
//        		}

        	}

        
        	// Bookeeping. Add "element" to path, object we created to object list
        	NodeName.add(elementName);
        	ObjectList.add(thisObject);
        	ElementNamespaceURIList.add(namespaceURI);
        
        } else {
           logger.error("ERROR: No start element handler for:"+elementName+". Doing nothing. This may cause later errors.");
        }
        
    }

    public void endElement (String namespaceURI, String localName, String qName )
    throws SAXException
    {
 
        String element = localName;
        logger.info("H_END:["+localName+","+qName+","+namespaceURI+"]");

        // find complexType (key) for handler
        HandlerInfo handlerInfo = findHandlerInfoFromElementName(namespaceURI,element);
        String handlerName = handlerInfo.name;
        String handlerURI = handlerInfo.uri;
        Hashtable uriEndHandlers = (Hashtable) EndElementHandlers.get(handlerURI);

        // if a handler exists, run it, else give a warning
        if (uriEndHandlers != null && uriEndHandlers.containsKey(handlerName)) 
        {

           // run the appropriate end handler
           EndElementHandlerAction event = (EndElementHandlerAction) uriEndHandlers.get(handlerName);
           event.action(this);
           
           // Bookeeping
           NodeName.remove(NodeName.size()-1); // peel off the last element in the  path
           ObjectList.remove(ObjectList.size()-1); // peel off last object in object list
           ElementNamespaceURIList.remove(ElementNamespaceURIList.size()-1);

        } else {

           logger.error("ERROR: No end element handler for:"+element+". Doing nothing. This may cause later errors.");

        }

    }

    /**  character Data handler
     */
    public void characters (char buf [], int offset, int len)
    throws SAXException
    {

        // Are we reading a CDATA section? IF NOT, then we should
        // replace all whitespace chars with just spaces. 
        if (!ReadingCDATASection) {
 
            // *sigh* this would be easy, but its not implemented in all Java
            // thisString = thisString.replaceAll("\\s+"," "); // Java 1.4 only!
            // so we have to do the following instead, slow ?
            char newBuf[] = new char[len];
            int newIndex = 0;
            boolean gotWhitespace = false;
            int size = len+offset;
            for (int i=offset; i<size; i++) {

                   // || buf[i] != '\x0B'
               if ( buf[i] == ' ' 
                   || buf[i] == '\n'
                   || buf[i] == '\r'
                   || buf[i] == '\t'
                   || buf[i] == '\f'
                  )
               {
                  gotWhitespace = true;
               } else { 
                  // add back in ONE space character 
                  if (gotWhitespace) {
                     newBuf[newIndex++] = ' ';
                     gotWhitespace = false;
                  }
                  newBuf[newIndex++] = buf[i];
               }
            }

            if (gotWhitespace) {
                 newBuf[newIndex++] = ' ';
            }

            buf = newBuf;
            offset = 0;
            len = newIndex;
        }

        /* we need to know what the  node is in order to 
           know what to do with this data, however, 
           early on when reading the DOCTYPE, other nodes we can get 
           text nodes which are not meaningful to us. Ignore all
           character data until we open the root node.
         */

        String name = (String) NodeName.get(NodeName.size()-1); 
        String namespaceURI = getElementNamespaceURI();
 
        logger.info("H_CharData:["+name+",value:["+new String(buf,offset,len)+"],"+namespaceURI+"]");

        // find complexType (key) for handler
        HandlerInfo handlerInfo = findHandlerInfoFromElementName(namespaceURI,name);
        String handlerName = handlerInfo.name;
        String handlerURI = handlerInfo.uri;
        Hashtable uriCDHandlers = (Hashtable) CharDataHandlers.get(handlerURI);

        // if a handler exists, run it, else give a warning
        if (uriCDHandlers != null && uriCDHandlers.containsKey(handlerName))
        {

          // run the appropriate end handler
           CharDataHandlerAction event = (CharDataHandlerAction) uriCDHandlers.get(handlerName);
           event.action(this,buf,offset,len);

        } else {

           logger.error("ERROR: No char data handler for:"+name+". Doing nothing. This may cause later errors.");

        }

    }

    public void startPrefixMapping(String prefix, String uri) {
        logger.info("H_StartPrefixMapping:["+prefix+","+uri+"]");
        PrefixNamespaceMapping.put(prefix,uri); 
    } 

    public void endPrefixMapping(String prefix) {
        logger.info("H_EndPrefixMapping:["+prefix+"]");
    } 
 
    public void startDocument()
    throws SAXException
    {
        // do nothing
        logger.info("H_StartDocument:[]");
    }

    public void endDocument()
    throws SAXException
    {

        logger.info("H_EndDocument:[]");

        // need to setprefix mappings in SOMLDocument
        getDocument().setPrefixNamespaceMappings(PrefixNamespaceMapping);
/*
        if (DoctypeObjectAttributes != null || ForceSetXMLHeaderStuff ) {
            
           // bah, this doesnt belong here
           XMLDeclaration xmlDecl = new XMLDeclaration();
           xmlDecl.setStandalone("no");

           DocumentType doctype = new DocumentType(SemanticObject);

           // set the values of the DocumentType object appropriately
           if (!ForceSetXMLHeaderStuff) {
              if (DoctypeObjectAttributes.containsKey("sysId")) 
                  doctype.setSystemId((String) DoctypeObjectAttributes.get("sysId")); 
              if (DoctypeObjectAttributes.containsKey("pubId")) 
                 doctype.setPublicId((String) DoctypeObjectAttributes.get("pubId")); 
           } else {
              // we have to guess values
              doctype.setSystemId(Constants.semantic object_DTD_NAME); 
           }

           SemanticObject.setXMLDeclaration (xmlDecl);
           SemanticObject.setDocumentType(doctype);
        }

        // Now that it exists, lets
        // set the notation hash for the SemanticObject structure
        Iterator iter = Notation.iterator();
        while (iter.hasNext()) {
           Hashtable initValues = (Hashtable) iter.next(); 
           if (SemanticObject.getDocumentType() == null) {
              // force having document type
              SemanticObject.setDocumentType(new DocumentType(SemanticObject)); 
           }
           SemanticObject.getDocumentType().addNotation(new NotationNode(initValues));
        }
*/

    }

    public void ignorableWhitespace(char buf [], int offset, int len)
    throws SAXException
    {
        // Note from the SAX API:
        // this callback won't be used consistently by all parsers,
        // unless they read the whole DTD.  Validating parsers will
        // use it, and ly most SAX nonvalidating ones will
        // also; but nonvalidating parsers might hardly use it,
        // depending on the DTD structure.
        // logger.debug("I Whitespace:["+new String(buf,offset,len)+"]");

        // do nothing, method required by interface 
    }

    // not used ?? 
    public void internalEntityDecl( String name, String value)
    throws SAXException
    {

       logger.info("H_INTERNAL_ENTITY: "+name+" "+value);

    }

    // not used ?? 
    public void externalEntityDecl ( String name,
                                     String publicId,
                                     String systemId )
    throws SAXException
    {

        logger.info("H_EXTERNAL_ENTITY: "+name+" "+publicId+" "+systemId);

    }

    /* Hurm, why doesnt this method treat 'base'?? */
    public void unparsedEntityDecl ( String name,  
                                     String publicId, 
                                     String systemId,
                                     String notationName ) 
    {
        logger.info("H_UNPARSED_ENTITY: "+name+" "+publicId+" "+systemId+" "+notationName);

        // create hashtable to hold information about Unparsed entity
        Map<String,String> information = new Hashtable<String,String> ();
        information.put("name", name);
        // if (base != null) information.put("base", base);
        if (publicId != null) information.put("publicId", publicId);
        if (systemId != null) information.put("systemId", systemId);
        if (notationName != null) information.put("ndata", notationName);

        // add this to the UnparsedEntity hash
        UnParsedEntity.put(name, information);
    }

    // Report the start of DTD declarations, if any.
    public void startDTD(String name, String publicId, String systemId) 
    throws SAXException
    {
        logger.info("H_DTD_Start:["+name+","+publicId+","+systemId+"]");

        DoctypeObjectAttributes = new Hashtable<String,String>();
        DoctypeObjectAttributes.put("name", name);
        if (publicId != null) 
            DoctypeObjectAttributes.put("pubId", publicId);
        if (systemId != null) 
            DoctypeObjectAttributes.put("sysId", systemId);
    }

    /* Hurm, why doesnt this method treat 'base'?? */
    public void notationDecl (String name, String publicId, String systemId )
    throws SAXException
    {
        logger.info("H_NOTATION: "+name+" "+publicId+" "+systemId);

        // create hash to hold information about notation.
        Map<String,String> information = new Hashtable<String,String> ();
        information.put("name", name);
        if (publicId != null) information.put("publicId", publicId);
        if (systemId != null) information.put("systemId", systemId);
       
        // add this to the Notation hash
        Notation.add(information);

    }

    public void processingInstruction(String target, String data)
    throws SAXException
    {
        logger.info("H_PROCESSING_INSTRUCTION:"+"<?"+target+" "+data+"?>");
        // do nothing
    }

    // Lexical handler methods
    public void endDTD() throws SAXException
    {
       logger.info("H_End_DTD");
        // do nothing
    }

    public void endCDATA() throws SAXException 
    {
       logger.info("H_End_CDATASection");
       ReadingCDATASection = false;
    }

    public void startCDATA() throws SAXException 
    {
       logger.info("H_Start_CDATASection");
       ReadingCDATASection = true;
    }

    public void startEntity(String name)
    throws SAXException
    {
       logger.info("H_Start_Entity["+name+"]");
    }

    public void endEntity(String name)
    throws SAXException
    {
       logger.info("H_End_Entity["+name+"]");
    }

    public void comment(char[] ch, int start, int length)
    throws SAXException
    {
        String value = new String(ch, start, length);
        logger.info("H_Comment ["+value+"]");
       // add to  node, if we are not inside of a SemanticObject right now.

        Comment comment = getDocument().createComment(value);

        Node n = getCurrentNode();
        if (n != null) {
            n.appendChild(comment);
        } else { 
            getDocument().appendChild(comment);
        }

    }

    //
    // Protected Methods
    //

    // Placeholder to remind me to do some version checking w/ base class
    protected boolean checkDocVersion (String version)
    {
// FIX
      // if(version != SOMLVersion) { return false; } else { return true; }
      return false;
    }

    /** Do special check for dealing with adding Semantic Objects. 
     *  This method exists because its easier to deal with adding related
     *  Semantic Objects in a global fashion rather than repeating code
     *  in each of the SemanticObject handlers.
     *  At any rate the logic is that if no parent SO exists, then it defaults to 
     *  adding the Semantic Objects as SOMLElements in the SOMLDocument.
     */
    // TODO : this actually should be in a handler (for relationship) I think
    protected void startHandlerAddSemanticObjectToParent(String namespaceURI, 
    		SemanticObject newSO) 
    {

            SemanticObject so = getCurrentSemanticObject();
            if(so != null) {

            	// TODO: This is not correct
          //  	so.addRelationship(newSO, new URI("")); // everything else becomes a "member"

             } else {

                 // Add as a QElement to our document, as appropriate (e.g.
                 // either to  node or as document root).
                Element elem = getDocument().createSOMLElementNS(namespaceURI, so);
                Node node = getCurrentNode();
                if( node != null) {
                   node.appendChild(elem);
                } else { 
                   getDocument().setDocumentElement(elem);
                }

             }

    }


    protected StartElementHandlerAction findStartHandler (String complexTypeName, String namespaceURI)
    {
       Hashtable handlers = (Hashtable) StartElementHandlers.get(namespaceURI);
       StartElementHandlerAction handler = null;

       if(handlers != null && handlers.containsKey(complexTypeName))
          handler = (StartElementHandlerAction) handlers.get(complexTypeName);

       return handler;
    }

    protected CharDataHandlerAction findCharDataHandler (String complexTypeName, String namespaceURI)
    {
        return findCharDataHandler (complexTypeName, namespaceURI, "");
    }

    // Find the character data handler which is appropriate. "Mixed" nature of the
    // node is very important. IF its mixed, AND the handler we find is the default
    // handler, we need to use the alternative handler instead.
    protected CharDataHandlerAction findCharDataHandler (String complexTypeName, String namespaceURI, String mixed)
    {
       Hashtable handlers = (Hashtable) CharDataHandlers.get(namespaceURI);
       CharDataHandlerAction handler = null;

       if(handlers != null && handlers.containsKey(complexTypeName))
          handler = (CharDataHandlerAction) handlers.get(complexTypeName);

       // double check that the correct default handler is being used here.
       // There can be a situation where the parent node uses the default 
       // Chardata handler, but a child node has set the 'mixed' attribute to 'true'
       // and therefore needs the 'other default' chardata handler.
       if (mixed.equals("true") && handler != null)
           if (handler == (CharDataHandlerAction) DefaultHandlers.get("ignoreCharData"))
               handler = (CharDataHandlerAction) DefaultHandlers.get("charData");

       return handler;
    }

    protected EndElementHandlerAction findEndHandler (String complexTypeName, String namespaceURI)
    {
       Hashtable handlers = (Hashtable) EndElementHandlers.get(namespaceURI);
       EndElementHandlerAction handler = null;

       if(handlers != null && handlers.containsKey(complexTypeName))
          handler = (EndElementHandlerAction) handlers.get(complexTypeName);

       return handler;
    }

    // base type of a complex/simple type decl.
    protected String findBaseType(Element typeDecl, String prefix)
    {
       String base = "";
       // drill down to look for "extension" or "restriction" children
       NodeList children = typeDecl.getChildNodes();
       int nrof_children = children.getLength();
       Node contentElem = (Node) null;
       for (int i=0; i<nrof_children; i++) {
          Node item = children.item(i);
          if(item.getNodeType() == Node.ELEMENT_NODE)
             if(item.getNodeName().equals(prefix+"complexContent")
                || item.getNodeName().equals(prefix+"simpleContent"))
             {
                contentElem = item;
                break;
             }
       }

       if(contentElem != null) {
           NodeList cnodes = contentElem.getChildNodes();
           int nrof_cnodes = cnodes.getLength();
           Element baseElem = (Element) null;
           for (int i=0; i<nrof_cnodes; i++) {
              Node item = cnodes.item(i);
              if(item.getNodeType() == Node.ELEMENT_NODE &&
                  (item.getNodeName().equals(prefix+"extension")
                    || item.getNodeName().equals(prefix+"restriction") ))
              {
                  baseElem = (Element) item;
                  break;
              }
           }

           if(baseElem != null) {
              base = baseElem.getAttribute("base");
           } // else
             //  logger.debug(" complexType:"+typeDecl.getAttribute("name")+" which has no ("+
             //                prefix+"restriction|"+prefix+"extension) grandchild node.");
       } // else
//          logger.info(" Inheriting Schema issue? got complexType:"+typeDecl.getAttribute("name")+" which has no "+prefix+"complexContent or simpleContent child. Schema loader will assign a default handler.");

       return base;
    }

    protected Map<String,ComplexTypeInfo> getBaseTypesOfComplexTypes ( List types, String prefix) {
       Map<String,ComplexTypeInfo> baseTypes = new Hashtable<String,ComplexTypeInfo>();
       Iterator titer = types.iterator();

       if(!prefix.equals("")) {
          prefix = prefix+":";
       }

       // go thru..find which complex types extend Q's
       while (titer.hasNext()) {
          Element elemDecl = (Element) titer.next();

          String name = elemDecl.getAttribute("name");
          String mixed = elemDecl.getAttribute("mixed");
          String base = findBaseType(elemDecl, prefix);

          ComplexTypeInfo info = new ComplexTypeInfo (name, base, mixed);
          logger.debug("   Got schema complexType decl  n:"+name+" b:"+base+" mixed:"+mixed);
          baseTypes.put(name,info);
       }

       return baseTypes;
    }

    // generic utility routine to locate elements in DOM Document
    protected List<Node> findElements (Document doc, String nodeName, String prefix) {
       List<Node> list = new Vector<Node>();

       // collect all import nodes
       String qName = nodeName;
       if(!prefix.equals(""))
          qName = prefix + ":" + nodeName;

       NodeList nodes = doc.getElementsByTagName(qName);
       int size = nodes.getLength();
       for (int i=0; i<size; i++)
          list.add(nodes.item(i));

       return list;
    }

    protected HandlerInfo findHandlerInfoFromElementName(String namespaceURI, String element)
    {
       HandlerInfo handlerInfo = null;
       Hashtable assocs = (Hashtable) ElementTypeAssoc.get(namespaceURI);

       if(assocs != null && assocs.containsKey(element))
          handlerInfo = (HandlerInfo) assocs.get(element);

       return handlerInfo;
    }

    protected String findSchemaLocationFromAttribs(Attributes attrs) {

       String schema_location_attrib_name = "schemaLocation";
       String schema_xmlns = "";
       String url = "";

       // first: find the schema prefix mapping for our instance
       for (String prefix : PrefixNamespaceMapping.keySet()) {
          String namespace = (String) PrefixNamespaceMapping.get(prefix);
          if (namespace.equals(XML_SCHEMA_INSTANCE_NAMESPACE_URI)) {
              schema_xmlns = prefix;
              break;
           }
       }
       
       if(!schema_xmlns.equals(""))
          schema_location_attrib_name = schema_xmlns + ":" + schema_location_attrib_name;

       int size = attrs.getLength();
       for (int i = 0; i < size; i++) {
          String name = attrs.getQName(i);
          if (name.equals(schema_location_attrib_name))
          {
             url = attrs.getValue(i);
             break;
          }
       }

       return url;
    }

    protected List<HandlerMapInfo> initHandlerAssociations(String myURI, Map<String,String> prefixMap, Map<String,ComplexTypeInfo> complexTypeMap)
    {

       List<HandlerMapInfo> missingHandlers = new Vector<HandlerMapInfo>();

       // go thru each complexType, adding as needed the mappings between handlers
       for (String name : complexTypeMap.keySet()) {
    	   
             // String base = (String) complexTypeMap.get(name);
             ComplexTypeInfo cinfo = (ComplexTypeInfo) complexTypeMap.get(name);
             String base = cinfo.base;
             String mixed= cinfo.mixed;

             String prefix = "";
             Matcher myMatcher = PrefixPattern.matcher(base);
             if(myMatcher.matches()) {
                  prefix = myMatcher.group(1).trim();
                  base = myMatcher.group(2).trim();
             }
             String uri = (String) prefixMap.get(prefix);
             logger.debug(" n:["+name+"] prefix:["+prefix+"] uri:["+myURI+"] b:["+base+"] uri:["+uri+"] mixed:["+mixed+"]");

             // find the appropriate handlers, if possible
             //

             if(!StartElementHandlers.containsKey(myURI))
                 StartElementHandlers.put(myURI,new Hashtable<String,HandlerAction>());

             if(!EndElementHandlers.containsKey(myURI))
                 EndElementHandlers.put(myURI,new Hashtable<String,HandlerAction>());

             if(!CharDataHandlers.containsKey(myURI))
                CharDataHandlers.put(myURI,new Hashtable<String,HandlerAction>());

             // There are 2 possibilities. 1. its a "vanilla" (no base type) element/type
             // which we can rely on the regular DOM to handle. 2. it has a base type (has an
             // extension/restriction of prior type) and we need to look for a prior handler
             // definition.
             logger.debug (" Set handler check BaseType:"+base+" URI:"+myURI); 
             if(base.equals("")) { // Vanilla
                 
                if(findStartHandler(name,myURI) == null ) {
                     logger.debug(" Setting Default Start Handler for :"+name+" uri:"+uri+" myURI:"+myURI);
                     StartElementHandlers.get(myURI).put(name,DefaultHandlers.get("startElement"));
                }

                if(findEndHandler(name,myURI) == null) {
                     logger.debug(" Setting Default End Handler for :"+name+" uri:"+uri+" myURI:"+myURI);
                     EndElementHandlers.get(myURI).put(name,DefaultHandlers.get("endElement"));
                }

                if(findCharDataHandler(name,myURI,mixed) == null) {
                     logger.debug(" Setting Default CharData Handler for :"+name+" uri:"+uri+" myURI:"+myURI);
                     if (mixed == null || mixed.equals("")) 
                         CharDataHandlers.get(myURI).put(name,DefaultHandlers.get("ignoreCharData"));
                     else 
                         CharDataHandlers.get(myURI).put(name,DefaultHandlers.get("charData"));
                }

             } else {  // has base type... this means it was extended from a prior complex type, we 
                       // either could use a user-defined handler, or use the super classes previously defined handler.

                 // check if we need to set the Start handler
                 if(findStartHandler(name,myURI) != null) {
                    logger.debug(" Using already defined start handler for complexType:"+name+"["+myURI+"]");
                 } else {
                    // no prior handler so map to the parent types handler 
                    StartElementHandlerAction shandler = findStartHandler(base,uri);
                    if(shandler != null) {
                       StartElementHandlers.get(myURI).put(name,shandler);
                       logger.debug(" ==> Mapping complexType:"+name+"["+myURI+"] to"+Constants.NEW_LINE+"       start Handler:"+base+"["+uri+"]");
                    } 
                    else
                         missingHandlers.add(new HandlerMapInfo(name,myURI,base,uri,HandlerType.START));
                 }

                 // check if we need to set the End handler
                 if(findEndHandler(name,myURI) != null) {
                    logger.debug(" Using already defined end handler for complexType:"+name+"["+myURI+"]");
                 } else {
                    // no prior handler so map to the parent types handler 
                    EndElementHandlerAction ehandler = findEndHandler(base,uri);
                    if(ehandler != null) {
                       EndElementHandlers.get(myURI).put(name,ehandler);
                       logger.debug(" ==> Mapping complexType:"+name+"["+myURI+"] to"+Constants.NEW_LINE+"       end Handler:"+base+"["+uri+"]");
                    } else
                       missingHandlers.add(new HandlerMapInfo(name,myURI,base,uri,HandlerType.END));
                 }

                 // check if we need to set the CharData handler
                 if(findCharDataHandler(name,myURI,mixed) != null) {
                    logger.debug(" Using already defined char data handler for complexType:"+name+"["+myURI+"]");
                 } else {
                    CharDataHandlerAction cdhandler = findCharDataHandler(base,uri,mixed);
                    if(cdhandler != null) {
                       CharDataHandlers.get(myURI).put(name,cdhandler);
                       logger.debug(" ==> Mapping complexType:"+name+"["+myURI+"] to"+Constants.NEW_LINE+"       charData Handler:"+base+"["+uri+"] mixed:["+mixed+"]");
                    } else
                       missingHandlers.add(new HandlerMapInfo(name,myURI,base,uri,HandlerType.CHAR,mixed));
                 }

             }
       }

       return missingHandlers;

    }

    protected void initElementTypeAssociations(String myURI, Map<String,String> prefixMap, Map<String,List<Node>> elements)
    {

    	for (String namespace : elements.keySet()) {

           List<Node> elemList = elements.get(namespace);
           Iterator eiter = elemList.iterator();
           while (eiter.hasNext()) {
              Element elemDecl = (Element) eiter.next();

              String type = elemDecl.getAttribute("type");

              // Type can be empty as it may be a "reference" to another
              // element, if so, we skip for now.
              //
              if(!type.equals(""))
              {

                 String name = elemDecl.getAttribute("name");
                 String prefix = "";
                 Matcher myMatcher = PrefixPattern.matcher(type);
                 if(myMatcher.matches()) {
                    prefix = myMatcher.group(1).trim();
                    type = myMatcher.group(2).trim();
                 }
                 String uri = (String) prefixMap.get(prefix);
//logger.debug("   Got schema element decl  n:"+name+" t:"+type+" uri:"+uri);

                 // expand table, if needed
                 if(!ElementTypeAssoc.containsKey(myURI))
                    ElementTypeAssoc.put(myURI,new Hashtable<String,HandlerInfo>());

                 logger.debug(" --> Mapping element:"+name+"["+myURI+"]"+Constants.NEW_LINE+"       to handlerKey:"+type+"["+uri+"]");
                 HandlerInfo info = new HandlerInfo(type,uri);
                 ElementTypeAssoc.get(myURI).put(name,info);

              }

           }
       }
    }


    // set up SOML handler associtions w/ schema complexTypes
    protected void initStartElementHandlers ()
    {

        Map<String,HandlerAction> SOMLAssoc = new Hashtable<String,HandlerAction>();
        Map<String,HandlerAction> xmlAssoc = new Hashtable<String,HandlerAction>();
        
        /*
        SOMLAssoc.put(Constants.NodeTypeName.semantic object, new IllegalStartElementHandlerFunc()); // its abstract..never invoked as a node! 
        SOMLAssoc.put(Constants.NodeTypeName.semantic object_CONTAINER, new semantic objectContainerStartElementHandlerFunc());
        */

        // generic XML handlers. we can certainly treat simple string and anyURI -based elements 
        xmlAssoc.put("string", new DefaultStartElementHandlerFunc());
        xmlAssoc.put("anyURI", new DefaultStartElementHandlerFunc());

        StartElementHandlers.put(SOML_NAMESPACE_URI, SOMLAssoc); 
        StartElementHandlers.put(XML_SCHEMA_NAMESPACE_URI, xmlAssoc); 

    }

    // set up SOML handler associtions w/ schema complexTypes
    protected void initCharDataHandlers()
    {
        Map<String,HandlerAction> SOMLAssoc = new Hashtable<String,HandlerAction>();
        Map<String,HandlerAction> xmlAssoc = new Hashtable<String,HandlerAction>();
        
        // SOMLAssoc.put(Constants.NodeTypeName.COMPOSITE_semantic object, new NullCharDataHandlerFunc());

        // generic XML handlers. we can certainly treat simple string and anyURI-based elements 
        xmlAssoc.put("string", new DefaultElementWithCharDataHandlerFunc());
        xmlAssoc.put("anyURI", new DefaultElementWithCharDataHandlerFunc());

        CharDataHandlers.put(SOML_NAMESPACE_URI, SOMLAssoc);
        CharDataHandlers.put(XML_SCHEMA_NAMESPACE_URI, xmlAssoc);

    }

   // set up SOML handler associtions w/ schema complexTypes
    protected void initEndElementHandlers ()
    {
        Map<String,HandlerAction> SOMLAssoc = new Hashtable<String,HandlerAction>();
        Map<String,HandlerAction> xmlAssoc = new Hashtable<String,HandlerAction>();
        
//        SOMLAssoc.put(Constants.NodeTypeName.COMPOSITE_semantic object, new semantic objectEndElementHandlerFunc());

        // generic XML handlers. we can certainly treat simple string and anyURI-based elements 
        xmlAssoc.put("string", new DefaultEndElementHandlerFunc());
        xmlAssoc.put("anyURI", new DefaultEndElementHandlerFunc());

        EndElementHandlers.put(SOML_NAMESPACE_URI, SOMLAssoc); 
        EndElementHandlers.put(XML_SCHEMA_NAMESPACE_URI, xmlAssoc); 

    }

    /** Initialize handlers, etc from declarations within the schema.
     * 
     * @param attrs
     */
    protected void InitFromSchema (Attributes attrs) {

           String schema_info = findSchemaLocationFromAttribs(attrs);

           Matcher myMatcher = SchemaLocationPattern.matcher(schema_info);
           if(myMatcher.matches()) {
              String uri = myMatcher.group(1).trim();
              String url = myMatcher.group(2).trim();
              List handlers = LoadSchema (uri,url);
              Iterator iter = handlers.iterator();

              // second pass: try to recover missing handlers
              while (iter.hasNext()) {
                  HandlerMapInfo info = (HandlerMapInfo) iter.next();
                  boolean gotHandler = false;

                  if (info.type == HandlerType.START) {
                	  StartElementHandlerAction shandler = findStartHandler(info.name2,info.uri2);
                	  if(shandler != null) {
                		  StartElementHandlers.get(info.uri1).put(info.name1,shandler);
                		  gotHandler = true;
                   
                	  }
                  } else if (info.type == HandlerType.END) {
                	  EndElementHandlerAction ehandler = findEndHandler(info.name2,info.uri2);
                	  if(ehandler != null) {
                		  EndElementHandlers.get(info.uri1).put(info.name1,ehandler);
                		  gotHandler = true;
                	  }
                  } else if (info.type == HandlerType.CHAR) {
                	  CharDataHandlerAction cdhandler = findCharDataHandler(info.name2,info.uri2,info.mixed);
                	  if(cdhandler != null) {
                		  CharDataHandlers.get(info.uri1).put(info.name1,cdhandler);
                		  gotHandler = true;
                	  }
                  }

                  // reporting..probably a better way to do this..
                  if(gotHandler) { 
                      logger.debug(" ==> Mapping complexType:"+info.name1+"["+info.uri1+"] to"+Constants.NEW_LINE+"       start Handler:"+info.name2+"["+info.uri2+"] for type:"+info.type);
                  } else {
                      String handlerTypeName = "start element";
                      if(info.type == HandlerType.END) 
                    	  handlerTypeName = "end element";
                      if(info.type == HandlerType.CHAR) 
                    	  handlerTypeName = "char data";
                      logger.error(" ==> Mapping complexType:"+info.name1+"["+info.uri1+"] to"+Constants.NEW_LINE+"       start Handler:"+info.name2+"["+info.uri2+"] for type:"+info.type);
                      logger.error(" ** Can't find "+handlerTypeName+" Handler for complexType:"+info.name1
                                  +"["+info.uri1+"] "+Constants.NEW_LINE
                                  +"       (Missing handler:"+info.name2+"["+info.uri2+"])");
                  }

              }
           }

    }

    // convenience method
    protected List<HandlerMapInfo> LoadSchema (String uri, String url )
    {
        return LoadSchema (uri, url, true);
    }

    /** Base method for loading handlers from XML schema. Will trace back (and forward)
      * through the indicated schema to identify all the element handlers (and the associated
      * namespaces) which are needed.
      */
    protected List<HandlerMapInfo> LoadSchema (String uri, String url, boolean warnLoaded )
    {

      List<HandlerMapInfo> handlers = new Vector<HandlerMapInfo>();

      // tack in the relative path to our source in the url
      url = RelativePath + url;

      if(LoadedSchema.containsKey(uri))
      {
         String priorUrl = (String) LoadedSchema.get(uri);
         if(priorUrl.equals(url))
         {
//            logger.debug("    returning..already loaded schema:"+uri+" "+url);
            return handlers;
         } else
            logger.info(" H_LOAD_SCHEMA: hmmm..already loaded schema with uri:"+uri
                          +" but this has different URL prior_url:["+priorUrl+
                          "] _url:[" +url
                          +"] If this is a schema that simply extends the orignal namespace, then things are OK, otherwise, maddess may reign.");
      }
      LoadedSchema.put(uri,url);

      logger.info("H_LOAD_SCHEMA : ["+uri+","+url+"]");

      // parse the schema into a DOM representation
      DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();

      try {

          DocumentBuilder docBuilder = fac.newDocumentBuilder();
          InputSource is = new InputSource (url);
          Document schemaDoc = docBuilder.parse(is);

          // find prefix/namespacesURI pairs for this schema.
          String targetNamespace = "";
          Map<String,String> schemaPrefixNamespaces = new Hashtable<String,String>();
          Element root = schemaDoc.getDocumentElement();
          NamedNodeMap attrs = root.getAttributes();
          int at_size = attrs.getLength();
          for (int i = 0; i < at_size; i++) {
             Node item = attrs.item(i);
             String nodeName = item.getNodeName();

             if(nodeName.equals("targetNamespace")) {
                targetNamespace = item.getNodeValue();
             } else {
               Matcher myMatcher = XMLNamespacePrefixPattern.matcher(nodeName);
               if(myMatcher.matches()) {
                  String prefix = myMatcher.group(2).trim();
                  schemaPrefixNamespaces.put(prefix,item.getNodeValue());
               }
             }
          }

          // now gather information about included/imported/redefined sub-schema..
          List<Node> imports = new Vector<Node>();
          List<Node> redefine = new Vector<Node>();
          List<Node> includes = new Vector<Node>();
          Map<String,List<Node>> elements = new Hashtable<String,List<Node>>();
          Map<String,List<Node>> types = new Hashtable<String,List<Node>>();
          for (String prefix : schemaPrefixNamespaces.keySet())
          {
             imports.addAll(findElements(schemaDoc,"import",prefix));
             redefine.addAll(findElements(schemaDoc,"redefine",prefix));
             includes.addAll(findElements(schemaDoc,"include",prefix));
             elements.put(prefix, findElements(schemaDoc,"element",prefix));
             types.put(prefix, findElements(schemaDoc,"complexType",prefix));
          }

          // init handlers for INCLUDEd schema..
          Iterator iiter = includes.iterator();
          while (iiter.hasNext()) {
              Element includeElem = (Element) iiter.next();
              List<HandlerMapInfo> missingImportSchemaHandlers =
                   LoadSchema(uri, includeElem.getAttribute("schemaLocation"), false);
              handlers.addAll(missingImportSchemaHandlers);
          }

          // init handlers for IMPORTed schema..
//          logger.debug(" FOUND "+imports.size()+" imports in schema:"+url);
          Iterator iter = imports.iterator();
          while (iter.hasNext()) {
              Element importElem = (Element) iter.next();
              List<HandlerMapInfo> missingImportSchemaHandlers =
                   LoadSchema(importElem.getAttribute("namespace"), importElem.getAttribute("schemaLocation"));
              handlers.addAll(missingImportSchemaHandlers);
          }

          // init handlers for REDEFINEd schema..
          // There should ONLY be 1 of these 
          logger.debug(" FOUND "+redefine.size()+" redefines in schema:"+url);
          iter = redefine.iterator();
          while (iter.hasNext()) {
              Element redefineElem = (Element) iter.next();
 // check if redefine namespace is same as our present schema?
              String schemaLoc = redefineElem.getAttribute("schemaLocation");
              logger.debug(" redef namespace : "+targetNamespace+" schema:"+schemaLoc);

              // Q: isnt it more efficient to just to find the reference to these handlers 
              // rather than re-load them afresh again?
              List<HandlerMapInfo> missingRedefineSchemaHandlers =
                   LoadSchema(targetNamespace,redefineElem.getAttribute("schemaLocation"), false);
              handlers.addAll(missingRedefineSchemaHandlers);

              // Q: do I need to remove the older namespace stuff??
          }

          // init handlers for this schema..
          // lets cheat a little..we have already "by hand" set the element handlers
          // for the SOML schema, so no need to go thru this to do it
          Matcher myMatcher = SOMLSchemaPattern.matcher(url);
          if(uri.equals(SOML_NAMESPACE_URI) && myMatcher.matches()) {
              logger.debug("   skipping loading element handlers for SOML schema.");
              return handlers;
          }
          
          Map<String,ComplexTypeInfo> complexTypes = new Hashtable<String,ComplexTypeInfo>();
          for (String prefix : schemaPrefixNamespaces.keySet()) {
             logger.debug(" Check input COMPLEXTYPE prefix:"+prefix);
             complexTypes.putAll(getBaseTypesOfComplexTypes(types.get(prefix), prefix));
          }

          handlers.addAll(initHandlerAssociations(targetNamespace, schemaPrefixNamespaces, complexTypes));

          //logger.debug(" FOUND "+elements.size()+" elements, "+types.size()+" complexTypes in schema:"+url);
          initElementTypeAssociations(targetNamespace, schemaPrefixNamespaces, elements);

      } catch (IOException e) {
    	  // TODO: this this really a warning??
          logger.warn("Can't create input source for schema parser : "+e.getMessage());
          logger.warn("Using the default SOML handlers (there may be further problems related to this...)");
      } catch (SAXException e) {
    	  // TODO: this this really a warning??
          logger.warn("Can't parse schema input source : "+e.getMessage());
          logger.warn("Using the default SOML handlers (there may be further problems related to this...)");
      } catch (ParserConfigurationException e) {
    	  // TODO: this this really a warning??
          logger.warn("Can't create schema DOM parser : "+e.getMessage());
          logger.warn("Using the default SOML handlers (there may be further problems related to this...)");
      }

      return handlers;

    }


    //
    // Private Methods
    //

    /** called by all constructors. May be used to re-initalize reader. 
     */
    private void init () {
    
    	// TODO : is this needed? initialize in the field declaration seems better.
    	// Do we ever recall init()??

      // assign/init 'globals' (e.g. object fields)
      Options = new Hashtable<String,String>();  
      myDocument = (SOMLDocument) null;

      Notation = new HashSet<Map<String,String>>();
      UnParsedEntity = new Hashtable<String,Map<String,String>>();
      PrefixNamespaceMapping = new Hashtable<String,String>();

      NodeName = new Vector<String>();
      Nodes = new Vector<Node>();
      ElementNamespaceURIList = new Vector<String>();

      RelativePath = "";

      // initialize the default parser dispatch tables
      // and element/type associations
      initHandlers();

      LoadedSchema = new Hashtable<String,String>(); 
      AttemptedSchemaLoad = false;

    }

    private void initHandlers() {
 
      // default handlers
      DefaultHandlers = new Hashtable<String,HandlerAction>(); // table of default handlers 
      initDefaultHandlerHashtable();

      // element to complexType association
      ElementTypeAssoc = new Hashtable<String,Map<String,HandlerInfo>>(); // assoc between element names and handler keys 
      initElementTypeAssoc();

      // start Element
      StartElementHandlers = new Hashtable<String,Map<String,HandlerAction>>(); // start node handler
      initStartElementHandlers(); 
    
      // end Element
      EndElementHandlers = new Hashtable<String,Map<String,HandlerAction>>(); // end node handler
      initEndElementHandlers();

      // character data 
      CharDataHandlers = new Hashtable<String,Map<String,HandlerAction>>(); // charData handler
      initCharDataHandlers();

    }

    private void initDefaultHandlerHashtable () {
       DefaultHandlers.put("startElement", new DefaultStartElementHandlerFunc());
       DefaultHandlers.put("endElement", new DefaultEndElementHandlerFunc());
       DefaultHandlers.put("ignoreCharData", new DefaultCharDataHandlerFunc());
       DefaultHandlers.put("charData", new DefaultElementWithCharDataHandlerFunc());
    }

    // initialize the associations between element names and complextypes (aka. keys for 
    // the dispatch table for start handler events) 
    // You may ask: why "hardwire" this table?
    // We *could* do this by analysis of the SOML.xsd everytime we load the document handler, 
    // but thats probably overkill, and a performance hit that we dont need to take.
    //
    // Optionally, it might be nice to have this table declared in the "Constants" class..that
    // seems better..
    private void initElementTypeAssoc() {

       Map<String,HandlerInfo> SOMLAssoc = new Hashtable<String,HandlerInfo>();
       Map<String,HandlerInfo> xmlAssoc = new Hashtable<String,HandlerInfo>();

       // SOML namespace associations
//       SOMLAssoc.put(Constants.NodeName.COMPOSITE_semantic object, new HandlerInfo(Constants.NodeTypeName.COMPOSITE_semantic object));
//       SOMLAssoc.put(Constants.NodeName.REFERENCE_semantic object, new HandlerInfo(Constants.NodeTypeName.REFERENCE_semantic object));

       // Simple string-based elements get the default handler
       xmlAssoc.put("string", new HandlerInfo("string"));
       xmlAssoc.put("anyURI", new HandlerInfo("anyURI"));
 
       // set up the associated namespace stuff
       ElementTypeAssoc.put(SOML_NAMESPACE_URI, SOMLAssoc);
       ElementTypeAssoc.put(XML_SCHEMA_NAMESPACE_URI, xmlAssoc);

    }
    
    //
    // Internal Classes
    //

    // MAPPING HANDLERS - dont really belong here (!)
    //

    /** A small class to hold information about ComplexTypes */
    protected class ComplexTypeInfo {
       public String name = "";
       public String base = "";
       public String mixed = "";

       public ComplexTypeInfo (String n, String b, String m) {
         name = n; 
         base = b; 
         mixed = m; 
       }

    }

    /** A small class to hold information about SAX handler to element mappings.
     */
    protected class HandlerInfo {
       public String name = "";
       public String uri = SOML_NAMESPACE_URI;

       public HandlerInfo (String n ) {
         name = n; 
       }

       public HandlerInfo (String n, String u) {
         name = n; uri = u;
       }

    }

   /** A small class to hold information about Mappings between SAX handlers.
     */
   protected class HandlerMapInfo {
       public String name1 = "";
       public String uri1 = SOML_NAMESPACE_URI;
       public String name2 = "";
       public String uri2 = SOML_NAMESPACE_URI;
       public String mixed = "";
       public HandlerType type = HandlerType.START;

       public HandlerMapInfo (String n1, String u1, String n2, String u2, HandlerType t, String mx ) {
         name1 = n1; uri1 = u1;
         name2 = n2; uri2 = u2;
         type = t;
         mixed = mx;
       }

       public HandlerMapInfo (String n1, String u1, String n2, String u2 ) {
         name1 = n1; uri1 = u1;
         name2 = n2; uri2 = u2;
       }

       public HandlerMapInfo (String n1, String u1, String n2, String u2, HandlerType t) {
         name1 = n1; uri1 = u1;
         name2 = n2; uri2 = u2;
         type = t;
       }

    }

} // End of SOMLDocumentHandler class 

