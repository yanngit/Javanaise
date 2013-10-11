package tests;

import java.io.Serializable;

import jvn.JavanaiseAnnotationRead;
import jvn.JavanaiseAnnotationWrite;



public interface MonObjetInterface extends Serializable {
	@JavanaiseAnnotationWrite
	public void setString(String s);
	@JavanaiseAnnotationRead
	public String getString();	
}
