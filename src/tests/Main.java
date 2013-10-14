package tests;

import java.util.List;

import jvn.JvnException;
import jvn.JvnServerImpl;



public class Main {
	private static JvnServerImpl server = JvnServerImpl.jvnGetServer();
	public static void main(String[] argv){
		try {
			MonObjetInterface mon_objet_partage = new MonObjet("State de creation");
			MonObjetInterface jvnO = (MonObjetInterface)server.jvnCreateObject(mon_objet_partage);
			server.jvnRegisterObject("objet1", jvnO);
			Thread.sleep(8000);
			System.out.println(jvnO.getString());
			jvnO.setString("coucou, main 1 j'ai tout modifié :)");
			System.out.println(jvnO.getString());
			Thread.sleep(1000);
			List<String> list = server.getLookupNames();
			for(String s : list){
				System.out.println(s);
			}
			MonObjetInterface objet1 = (MonObjetInterface) JvnServerImpl.jvnGetServer().jvnLookupObject("objet1");
			System.out.println("lookup ok");
			server.jvnRemoveObject(objet1);
			 list = server.getLookupNames();
			for(String s : list){
				System.out.println(s);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JvnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
}
