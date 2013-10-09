package tests;


public class MonObjet implements MonObjetInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8827971146941007191L;
	/**
	 * 
	 */
	private String s;
	
	
	public MonObjet(String s){
		this.s = s;
	}
	
	public void setString(String s){
		this.s = s;
	}
	
	public String getString(){
		return s;
	}
}
