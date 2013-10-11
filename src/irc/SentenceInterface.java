package irc;

import jvn.JavanaiseAnnotationRead;
import jvn.JavanaiseAnnotationWrite;

public interface SentenceInterface extends java.io.Serializable {
	@JavanaiseAnnotationWrite
	public void write(String text);
	@JavanaiseAnnotationRead
	public String read();
}
