/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map.Entry;



public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1834717182805714127L;
	private int id;
	// A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
	private JvnRemoteCoord coordinator = null;
	private HashMap<Integer, JvnObject> idobj_intercepteur = new HashMap<Integer, JvnObject>();
	private HashMap<JvnObject,Object> proxy_intercepteur = new HashMap<JvnObject,Object>();
	private String lookup_name;

	/**
	 * Default constructor
	 * @throws JvnException
	 **/
	private JvnServerImpl() throws Exception {
		super();
		coordinator = (JvnRemoteCoord)Naming.lookup("//localhost:5555/coordinator");
		System.out.println("Coordinateur bien récupéré !");
		this.id = coordinator.jvnGetServerId();
		lookup_name = new String("//localhost:5555/server_"+id);
		Naming.bind(lookup_name,  this);
	}

	/**
	 * Static method allowing an application to get a reference to 
	 * a JVN server instance
	 * @throws JvnException
	 **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null){
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				return null;
			}
		}
		return js;
	}

	/**
	 * The JVN service is not used anymore
	 * @throws JvnException
	 **/
	public synchronized void jvnTerminate()
			throws jvn.JvnException {
		// to be completed 
	} 

	/**
	 * creation of a JVN object
	 * @param o : the JVN object state
	 * @throws JvnException
	 **/
	public Object jvnCreateObject(Serializable o)
			throws jvn.JvnException { 
		try {
			JvnObject obj = new JvnObjectImpl(coordinator.jvnGetObjectId(), o);
			idobj_intercepteur.put(new Integer(obj.jvnGetObjectId()), obj);
			Object proxy = JvnObjectProxy.newInstance(obj, o);
			proxy_intercepteur.put(obj,proxy);
			return proxy;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *  Associate a symbolic name with a JVN object
	 * @param jon : the JVN object name
	 * @param jo : the JVN object 
	 * @throws JvnException
	 **/
	public void jvnRegisterObject(String jon, Object jo)
			throws jvn.JvnException {
		try {
			for(Entry<JvnObject, Object> o : proxy_intercepteur.entrySet()){
				if (o.getValue() == jo){
					coordinator.jvnRegisterObject(jon, (JvnObject)o.getKey(), this);

				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Provide the reference of a JVN object beeing given its symbolic name
	 * @param jon : the JVN object name
	 * @return the JVN object 
	 * @throws JvnException
	 **/
	public Object jvnLookupObject(String jon)
			throws jvn.JvnException {
		try {
			JvnObject obj = coordinator.jvnLookupObject(jon, this);
			if(obj != null){
				idobj_intercepteur.put(new Integer(obj.jvnGetObjectId()), obj);
				Object proxy = JvnObjectProxy.newInstance(obj, obj.jvnGetObjectState());
				proxy_intercepteur.put(obj,proxy);
				return proxy;
			}
			return null;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	

	/**
	 * Get a Read lock on a JVN object 
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws  JvnException
	 **/
	public Serializable jvnLockRead(int joi)
			throws JvnException {
		try {
			return coordinator.jvnLockRead(joi, this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}	
	/**
	 * Get a Write lock on a JVN object 
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws  JvnException
	 **/
	public Serializable jvnLockWrite(int joi)
			throws JvnException {
		try {
			return coordinator.jvnLockWrite(joi, this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	


	/**
	 * Invalidate the Read lock of the JVN object identified by id 
	 * called by the JvnCoord
	 * @param joi : the JVN object id
	 * @return void
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnInvalidateReader(int joi)
			throws java.rmi.RemoteException,jvn.JvnException {
		idobj_intercepteur.get(joi).jvnInvalidateReader();
	};

	/**
	 * Invalidate the Write lock of the JVN object identified by id 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriter(int joi)
			throws java.rmi.RemoteException,jvn.JvnException { 
		return idobj_intercepteur.get(joi).jvnInvalidateWriter();
	};

	/**
	 * Reduce the Write lock of the JVN object identified by id 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader(int joi)
			throws java.rmi.RemoteException,jvn.JvnException { 
		return idobj_intercepteur.get(joi).jvnInvalidateWriterForReader();
	}

	public int jvnGetId() throws RemoteException {
		return id;
	};

}


