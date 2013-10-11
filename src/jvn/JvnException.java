/***
 * JAVANAISE API
 * Contact: 
 *
 * Authors: 
 */

package jvn;

/**
 * Interface of a JVN Exception. 
 */

public class JvnException extends Exception {

	private static final long serialVersionUID = 5040142103568889147L;
	/**
	 * The message displayed when the exception occurs
	 */
	String message;

	public JvnException() {
	}

	/**
	 * Create a JvnException with a given message
	 * @param message : the displayed message
	 */
	public JvnException(String message) {
		this.message = message;
	}	

	/**
	 * Get the message string of the exception
	 * @return the message of the exception
	 */
	public String getMessage(){
		return message;
	}
}
