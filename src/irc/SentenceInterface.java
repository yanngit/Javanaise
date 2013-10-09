package irc;

import jvn.JavanaiseAnnotation;

public interface SentenceInterface extends java.io.Serializable {
	@JavanaiseAnnotation (lockType="write")
	public void write(String text);
	@JavanaiseAnnotation (lockType="read")
	public String read();
}
