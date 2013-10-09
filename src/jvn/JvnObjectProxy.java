package jvn;

import java.lang.reflect.Method;
import java.rmi.RemoteException;

public class JvnObjectProxy implements java.lang.reflect.InvocationHandler {
	/**
	 * 
	 */
	private JvnObject obj;
	private Object ser;
	public static Object newInstance(JvnObject jvno, Object ser) throws IllegalArgumentException, RemoteException{
		return java.lang.reflect.Proxy.newProxyInstance(ser.getClass().getClassLoader(),
				ser.getClass().getInterfaces(), new JvnObjectProxy(jvno,ser));
	}

	private JvnObjectProxy (JvnObject obj,Object ser) {
		this.ser = ser;
		this.obj = obj;
	}

	public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
		Object result = null;
		boolean locked = false;
		/*Verifier la présence d'annotation et faire le lock en fonction de l'annotation*/
		if(m.isAnnotationPresent(JavanaiseAnnotation.class)){
			JavanaiseAnnotation annot = m.getAnnotation(JavanaiseAnnotation.class);
			if(annot.lockType().equals("write")){
				ser = obj.jvnLockWrite();
				locked = true;
			}
			else if(annot.lockType().equals("read")){
				ser = obj.jvnLockRead();
				locked = true;
			}
			else{
				throw new JvnException("Problème d'annotation, valeur différente de read/write trouvée");
			}
			result = m.invoke(ser, args);
			if(locked){
				obj.jvnUnLock();
			}
		}
		return result;
	}
}
