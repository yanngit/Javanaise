package tests;

import jvn.JvnException;
import jvn.JvnServerImpl;

public class Main2 {
	private static JvnServerImpl server = JvnServerImpl.jvnGetServer();
	public static void main(String[] argv){
		try {
			MonObjetInterface jvnO = (MonObjetInterface)server.jvnLookupObject("objet1");
			System.out.println(jvnO.getString());
			Thread.sleep(5000);
			System.out.println(jvnO.getString());
			Thread.sleep(5000);
			System.out.println(jvnO.getString());
			jvnO.setString("changed by 2");
			System.out.println(jvnO.getString());
		} catch (JvnException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
