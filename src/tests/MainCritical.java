package tests;

import java.util.ArrayList;

import jvn.JvnException;
import jvn.JvnServerImpl;

public class MainCritical {
	public static void main(String[] args){
		try {
			String name = "main";
			int num_rand;
			ArrayList<MonObjetInterface> array = new ArrayList<MonObjetInterface>();
			MonObjet o1 = new MonObjet("objet1");
			MonObjetInterface shared_object_1 = (MonObjetInterface) JvnServerImpl.jvnGetServer().jvnCreateObject(o1);
			JvnServerImpl.jvnGetServer().jvnRegisterObject("objet1", shared_object_1);
			Thread.sleep(5000);
			//JvnObjectImpl shared_object_1 = (JvnObjectImpl) JvnServerImpl.jvnGetServer().jvnLookupObject("objet1");
			MonObjetInterface shared_object_2 = (MonObjetInterface) JvnServerImpl.jvnGetServer().jvnLookupObject("objet2");
			MonObjetInterface shared_object_3 = (MonObjetInterface) JvnServerImpl.jvnGetServer().jvnLookupObject("objet3");
			array.add(shared_object_1);
			array.add(shared_object_2);
			array.add(shared_object_3);
			for(int i = 0; i < 10; i++){
				for(MonObjetInterface obj : array){
					num_rand = (int) Math.round(Math.random());
					switch(num_rand){
					case 0 :
						System.out.println("Writing operation");
						obj.setString("lock by"+name);
						System.out.println("Writing done");
						break;
					case 1 : 
						System.out.println("Reading operation");
						System.out.println("Actual value : "+obj.getString());
						System.out.println("Read done");
						break; 
					}		
				}
			}
			JvnServerImpl.jvnGetServer().jvnTerminate();
			System.out.println("YES fin du test pour "+name);
			System.exit(0);
		} catch (JvnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
