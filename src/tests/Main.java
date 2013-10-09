package tests;

import jvn.JvnException;
import jvn.JvnServerImpl;



public class Main {
	private static JvnServerImpl server = JvnServerImpl.jvnGetServer();
	public static void main(String[] argv){
		try {
			MonObjetInterface mon_objet_partage = new MonObjet("State de creation");
			MonObjetInterface jvnO = (MonObjetInterface)server.jvnCreateObject(mon_objet_partage);
			server.jvnRegisterObject("objet1", jvnO);
			Thread.sleep(5000);
			System.out.println(jvnO.getString());
			jvnO.setString("coucou, main 1 j'ai tout modifié :)");
			Thread.sleep(5000);
			System.out.println(jvnO.getString());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JvnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
}
