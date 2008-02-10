/**
 * 
 */
package net.datamodel.soml;

import java.net.URI;
import java.net.URISyntaxException;

import net.datamodel.soml.impl.SemanticObjectImpl;

import org.apache.log4j.Logger;

/** Some utility programs.
 * 
 * @author thomas
 *
 */
public class Utility {

	// our trusty logger
	private static final Logger logger = Logger.getLogger(Utility.class);
	
	private Utility() {}

	/** Find the namespace Uri portion of a URI.
	 * 
	 * @param uri
	 * @return
	 */
	public static final String getNamespaceURI (URI uri) {
		
		logger.debug("getNamespaceURI parses uri:"+uri.toASCIIString());
		StringBuffer nsUri = new StringBuffer();
		nsUri.append(uri.getScheme());
		nsUri.append(":");
		nsUri.append(uri.getSchemeSpecificPart());
		if (uri.getFragment() != null && !uri.getFragment().equals(""))
			nsUri.append("#");
		
		logger.debug("getNamespaceURI returns:"+nsUri.toString());
		return nsUri.toString();
	}

	/** A no-hassle method for creating URIs from string representations. 
	 * Swallows the URISyntaxException for mal-formed URI's and instead 
	 * prints an error to the log (but allows the program to continue on).
	 * 
	 * @param struri
	 * @return the URI the value will be null if the uri is mal-formed.
	 */
	public static final URI createURI(String struri) {
		URI uri = null;
		try {
			uri = new URI(struri);
		} catch (URISyntaxException e) {
			// pass
			logger.info("cant create URI! (blank node?):"+struri);
		}
		return uri;
	}
	
	
}
