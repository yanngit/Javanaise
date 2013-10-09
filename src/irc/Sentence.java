/***
 * Sentence class : used for representing the text exchanged between users
 * during a chat application
 * Contact: 
 *
 * Authors: 
 */

package irc;

public class Sentence implements SentenceInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1569328181317050007L;
	String 		data;
  
	public Sentence() {
		data = new String("");
	}
	
	public void write(String text) {
		data = text;
	}
	public String read() {
		return data;	
	}
	
}