/**
 * 
 */
package net.datamodel.soml.builder;

/**
 * @author thomas
 *
 */
public class SemanticObjectBuilderException extends Exception {

	/**
	 * @param message
	 */
	public SemanticObjectBuilderException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SemanticObjectBuilderException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SemanticObjectBuilderException(String message, Throwable cause) {
		super(message, cause);
	}

}
