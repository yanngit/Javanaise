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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JvnServerImpl extends UnicastRemoteObject implements
JvnLocalServer, JvnRemoteServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1834717182805714127L;
	private int id;
	// A JVN server is managed as a singleton
	private static JvnServerImpl js = null;
	private JvnRemoteCoord coordinator = null;
	private Map<Integer, JvnObject> idobj_intercepteur = Collections
			.synchronizedMap(new LruLinkedMap<Integer, JvnObject>(2));
	/*Dans cet ordre car null pointeur lors du add sinon (car il fais un hash pour la clef qui est ici un proxy)*/
	private HashMap <JvnObject, Object> proxy_intercepteur = new HashMap<JvnObject, Object>();

	private String lookup_name;

	/**
	 * Default constructor
	 * 
	 * @throws JvnException
	 **/
	private JvnServerImpl() throws Exception {
		super();
		coordinator = (JvnRemoteCoord) Naming
				.lookup("//localhost:5555/coordinator");
		System.out.println("Coordinateur bien récupéré !");
		this.id = coordinator.jvnGetServerId();
		lookup_name = new String("//localhost:5555/server_" + id);
		Naming.bind(lookup_name, this);
	}

	/**
	 * Static method allowing an application to get a reference to a JVN server
	 * instance
	 * 
	 * @throws JvnException
	 **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null) {
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
	 * 
	 * @throws JvnException
	 **/
	public synchronized void jvnTerminate() throws jvn.JvnException {
		try {
			coordinator.jvnTerminate(this);
		} catch (RemoteException e) {
			throw new JvnException("Problème dans le jvnTerminate : "+e.getMessage());
		}
	}

	/**
	 * creation of a JVN object
	 * 
	 * @param o
	 *            : the JVN object state
	 * @throws JvnException
	 **/
	public Object jvnCreateObject(Serializable o) throws jvn.JvnException {
		try {
			JvnObject obj = new JvnObjectImpl(coordinator.jvnGetObjectId(), o);
			idobj_intercepteur.put(new Integer(obj.jvnGetObjectId()), obj);
			System.out.println(idobj_intercepteur);
			Object proxy = JvnObjectProxy.newInstance(obj, o);
			proxy_intercepteur.put(obj,proxy);
			return proxy;
		} catch (RemoteException e) {
			throw new JvnException("Problème dans le CreateObject : "+e.getMessage());
		}
	}

	/**
	 * Associate a symbolic name with a JVN object
	 * 
	 * @param jon
	 *            : the JVN object name
	 * @param jo
	 *            : the JVN object
	 * @throws JvnException
	 **/
	public void jvnRegisterObject(String jon, Object jo)
			throws jvn.JvnException {
		try {
			boolean done = false;
			for(Entry<JvnObject,Object> entry : proxy_intercepteur.entrySet()){
				if(entry.getValue() == jo){
					coordinator.jvnRegisterObject(jon, entry.getKey(),this);
					done = true;
				}
			}
			if(!done){
				throw new JvnException("L'objet ne peut pas etre enregistré car il n'est pas connu du serveur local ...");
			}
		} catch (RemoteException e) {
			throw new JvnException("Problème dans le RegisterObject : "+e.getMessage());
		}
	}

	/**
	 * Provide the reference of a JVN object beeing given its symbolic name
	 * 
	 * @param jon
	 *            : the JVN object name
	 * @return the JVN object
	 * @throws JvnException
	 **/
	public Object jvnLookupObject(String jon) throws jvn.JvnException {
		try {
			JvnObject obj = coordinator.jvnLookupObject(jon, this);
			if (obj != null) {
				idobj_intercepteur.put(new Integer(obj.jvnGetObjectId()), obj);
				Object proxy = JvnObjectProxy.newInstance(obj,
						obj.jvnGetObjectState());
				proxy_intercepteur.put(obj, proxy);
				return proxy;
			}
			return null;//new JvnException("Aucun objet ne correspond à ce nom symbolique "+jon);
		} catch (RemoteException e) {
			throw new JvnException("Problème dans le LookupObject : "+e.getMessage());
		}
	}

	/**
	 * Get a Read lock on a JVN object
	 * 
	 * @param joi
	 *            : the JVN object identifier
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockRead(int joi) throws JvnException {

		try {
			if(idobj_intercepteur.containsKey(new Integer(joi))){
				return coordinator.jvnLockRead(joi, this);
			}
			else{
				throw new JvnException("Impossible de verouiller cet objet, il n'est pas connu du serveur");
			}
		} catch (RemoteException e) {
			throw new JvnException("Problème dans le LockRead : "+e.getMessage());
		}

	}

	/**
	 * Get a Write lock on a JVN object
	 * 
	 * @param joi
	 *            : the JVN object identifier
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockWrite(int joi) throws JvnException {
		try {
			if(idobj_intercepteur.containsKey(new Integer(joi))){
				return coordinator.jvnLockWrite(joi, this);
			}
			else{
				throw new JvnException("Impossible de verouiller cet objet, il n'est pas connu du serveur");
			}	
		} catch (RemoteException e) {
			throw new JvnException("Problème dans le LockWrite : "+e.getMessage());
		}
	}

	/**
	 * Invalidate the Read lock of the JVN object identified by id called by the
	 * JvnCoord
	 * 
	 * @param joi
	 *            : the JVN object id
	 * @return void
	 * @throws java.rmi.RemoteException
	 *             ,JvnException
	 **/
	public void jvnInvalidateReader(int joi) throws java.rmi.RemoteException,
	jvn.JvnException {
		idobj_intercepteur.get(joi).jvnInvalidateReader();
	};

	/**
	 * Invalidate the Write lock of the JVN object identified by id
	 * 
	 * @param joi
	 *            : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException
	 *             ,JvnException
	 **/
	public Serializable jvnInvalidateWriter(int joi)
			throws java.rmi.RemoteException, jvn.JvnException {
		return idobj_intercepteur.get(joi).jvnInvalidateWriter();
	};

	/**
	 * Reduce the Write lock of the JVN object identified by id
	 * 
	 * @param joi
	 *            : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException
	 *             ,JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader(int joi)
			throws java.rmi.RemoteException, jvn.JvnException {
		return idobj_intercepteur.get(joi).jvnInvalidateWriterForReader();
	}

	/**
	 * Get the identifier of the server
	 * @return the server identifier
	 */
	public int jvnGetId() {
		return id;
	}

	/**
	 * 
	 * @param joi
	 * @return
	 * @throws JvnException
	 */
	public Serializable jvnRecharge(int joi) throws JvnException{
		try {
			this.jvnRefresh(idobj_intercepteur.get(joi));
			return coordinator.jvnGetSharedObject(this, joi);
		} catch (RemoteException e) {
			throw new JvnException("Problème dans le recharge : "+e.getMessage());
		}
	}

	/**
	 * 
	 * @param jo
	 * @throws JvnException
	 */
	public void jvnRefresh(JvnObject jo) throws JvnException {
		this.idobj_intercepteur.get(jo.jvnGetObjectId());
	}

	/**
	 * Remove an object from the Jvn system (created and registered before)
	 * @param oj : the object to remove
	 * @throws JvnException
	 */
	public void jvnRemoveObject(Object jo) throws JvnException{
		/*Quand on demande une suppression on doit avoir un lock write*/
		try {
			int id = -1;
			for(Entry<JvnObject,Object> entry : proxy_intercepteur.entrySet()){
				if(entry.getValue() == jo){
					id =  entry.getKey().jvnGetObjectId();
				}
			}
			if(id > 0){
				coordinator.jvnRemoveObject(id);
			}
			else {
				throw new JvnException("Impossible de supprimer cet objet, il est inconnu du serveur local");
			}
		} catch (RemoteException e) {
			throw new JvnException("Problème dans le RemoveObject : "+e.getMessage());
		}
	}

	@Override
	/**
	 * Informing the server about a deleted object
	 * @param integer the object identifier
	 * @throws RemoteException
	 * @throws JvnException
	 */
	public void jvnDeletedObjectInformation(Integer integer) {
		JvnObject obj = idobj_intercepteur.get(integer);
		/*Si l'objet était connu du serveur on le supprime sinon rien*/
		if(obj != null){
			proxy_intercepteur.remove(obj);
			idobj_intercepteur.remove(integer);
		}
	}

	/** Get the Names of all shared objects 
	 *
	 * @return a list of shared object names
	 * 
	 * @throws java.rmi.RemoteException
	 * @throws JvnException
	 * **/
	public List<String> getLookupNames() throws JvnException {
		try {
			return coordinator.getLookupNames();
		} catch (RemoteException e) {
			throw new JvnException("Problème dans le LookupNames : "+e.getMessage());
		}
	}

}
