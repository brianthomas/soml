
package net.datamodel.soml;

/** 
  A URN is a Uniform Resource Name. It is a sub-type of URI, which is 
  both opaque and absolute in format, and which names resources but do
  not specify how to locate them. 
 </p><p>
  We use the URN in this package to identify semantic meaning. This is done by
  using the URN as a short, unique identifier for a particular structure
  (class or relationship) in an Ontology (identified elsewhere).
 </p><p>
   At the highest level a URN in string form has the syntax
  <pre>
    [scheme:]scheme-specific-part[#fragment] 
  </pre>
 where square brackets [...] delineate optional components and the 
 characters : and # stand for themselves. 
  </p></p>
 The following examples (taken from the
 Java documentation for the URI interface) are URI's which qualify as URN's: 
 <tt><pre>
      mailto:java-net@java.sun.com	
      news:comp.lang.java	
      urn:isbn:096139210x
 </pre></tt> 
  Note that it is not necessary for the scheme to be 'urn' for the URI to
  be a valid URN.
  </p>
 <p> 
  <b>Warning:</b> this simple class assumes that all Strings conform to the US-ASCII set of
  characters. It is encapable of handling decoding of octets and other 
  subtities.
 </p> 
  
  @author thomas
 */
public interface URN {
	
	/** Get the Scheme of this URN.
     * The scheme component of a URN, if defined, only contains characters in the 
     * alphanum category and in the string "-.+". 
     * 
     * A scheme always starts with an alpha character. 
     * 
	 * @return the scheme part of this URN or null if the scheme is undefined. 
	 */
	public String getScheme();
	
	/** Returns the scheme-specific part of this URN.
      * @return  The scheme-specific part of this URN (never null)
      */
	public String getSchemeSpecificPart();
	
	/** Returns the fragment component of this URN. 
	 * @return: The fragment component of this URN, or null if the fragment is undefined
	 */
	public String getFragment();
	
}
