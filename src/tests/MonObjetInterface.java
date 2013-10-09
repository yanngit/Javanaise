package tests;

import java.io.Serializable;

import jvn.JavanaiseAnnotation;



public interface MonObjetInterface extends Serializable {
	@JavanaiseAnnotation (lockType="write")
	public void setString(String s);
	@JavanaiseAnnotation (lockType="read")
	public String getString();	
}
