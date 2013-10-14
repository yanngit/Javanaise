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
		if(m.isAnnotationPresent(JavanaiseAnnotationWrite.class)) {
			ser = obj.jvnLockWrite();
			locked = true;
		}
		else if(m.isAnnotationPresent(JavanaiseAnnotationRead.class)) {
			ser = obj.jvnLockRead();
			locked = true;
		}

		result = m.invoke(ser, args);
		if(locked){
			Thread.sleep(1000);
			obj.jvnUnLock();
		}
		return result;
	}
}
