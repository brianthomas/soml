/**
 * 
 */
package net.datamodel.soml.impl;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.datamodel.soml.URN;

import org.apache.log4j.Logger;

/**
 * @author thomas
 *
 */
public class URNImpl implements URN 
{
	
	   // Fields
	private volatile int hashCode = 0; 
	private static final Logger logger = Logger.getLogger(URNImpl.class);
	
	private final static String allowedSSPChars = "[\\w\\d\\-\\_\\@]";
    private final static Pattern fullPattern = Pattern.compile ("(\\w[\\w\\d]*):("+allowedSSPChars
    		+"+)\\#([\\w\\d]+)",  Pattern.DOTALL | Pattern.COMMENTS);
    
    private final static Pattern noFragPattern = Pattern.compile ("(\\w[\\w\\d]*):("+allowedSSPChars 
    		+"+)", Pattern.DOTALL | Pattern.COMMENTS);
    		
	private String scheme;
	private String ssp;
	private String fragment;
	
	/** Constructor of URN using a string representation. 
	 * 
	 * @param stringRep
	 * @throws URISyntaxException
	 */
	public URNImpl (String stringRep) 
	throws URISyntaxException
	{
		logger.debug("trying to construct URN from pattern:["+stringRep+"]"); 
		Matcher fullMatcher = fullPattern.matcher(stringRep);
		Matcher noFragMatcher = noFragPattern.matcher(stringRep);
		if(fullMatcher.matches()) {
			logger.debug("matched full pattern");
			this.scheme = fullMatcher.group(1);
			this.ssp = fullMatcher.group(2);
			this.fragment = fullMatcher.group(3);
		} else if(noFragMatcher.matches()) {
			this.scheme = noFragMatcher.group(1);
			this.ssp = noFragMatcher.group(2);
		} else {
			throw new URISyntaxException (stringRep, "Cant parse string");
		}
		
		logger.debug ("Constructor Got URN of:"+this.toAsciiString());
	}
	
	/** Constructor with separate fields for each field in the URN.
	 * 
	 * @param scheme
	 * @param ssp
	 * @throws URISyntaxException
	 */ 
	public URNImpl (String scheme, String ssp)
	throws URISyntaxException
	{
		this(scheme, ssp, (String) null);
	}
	
	/** Constructor with separate fields for each field in the URN.
	 * 
	 * @param scheme
	 * @param ssp
	 * @param fragment
	 * @throws URISyntaxException
	 */ 
	public URNImpl (String scheme, String ssp, String fragment)
	throws URISyntaxException
	{
		if (null != ssp) {
			throw new URISyntaxException ("", "Can't have null value for ssp in constructor of URN");
		}
		this.scheme = scheme;
		this.ssp = ssp;
		this.fragment = fragment;
		logger.debug ("Constructor Got URN of:"+toAsciiString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toAsciiString()
	 */
	public String toAsciiString () {
		
		StringBuilder str = new StringBuilder();
		if (null != scheme) {
			str.append(scheme);
			str.append(":");
		}
		
		str.append(ssp);
		
		if (null != fragment) {
			str.append("#");
			str.append(fragment);
		}
		
		return str.toString();
	}
	
	@Override
	public String toString() {
		return toAsciiString();
	}

	/* (non-Javadoc)
	 * @see net.datamodel.qml.URN#getScheme()
	 */
	public String getScheme() {
		return scheme;
	}

	/* (non-Javadoc)
	 * @see net.datamodel.qml.URN#getSchemeSpecificPart()
	 */
	public String getSchemeSpecificPart() {
		return ssp;
	}

	/* (non-Javadoc)
	 * @see net.datamodel.qml.URN#getFragment()
	 */
	public String getFragment() {
		return fragment;
	}

	@Override
	public boolean equals (Object obj) {
		if (obj instanceof URNImpl) {
			URNImpl test = (URNImpl) obj;
			if (test.toAsciiString().equals(toAsciiString())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		// cache the hashcode for performance..
		// Warning: IF we ever allow setting of fields after construction
		// this will produce some errors!!
		if (hashCode == 0) {
			int code = 5345;
			hashCode = code + toAsciiString().hashCode();
		}
		return hashCode;
	}
	

}
