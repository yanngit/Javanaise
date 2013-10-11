/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JvnCoordImpl extends UnicastRemoteObject implements
JvnRemoteCoord, JvnLocalCoord {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1164487591124280323L;
	private Map<Integer, JvnRemoteServer> id_server = new HashMap<Integer, JvnRemoteServer>();
	private Map<String, Integer> name_objectid = new HashMap<String, Integer>();
	private List<JvnSharedObjectStructure> struct_list = new ArrayList<JvnSharedObjectStructure>();
	private Map<Integer, JvnObject> id_object = new HashMap<Integer, JvnObject>();
	private int NEXT_ID = 0;
	private int NEXT_ID_JVM = 0;

	/**
	 * Default constructor
	 * 
	 * @throws JvnException
	 **/
	private JvnCoordImpl() throws Exception {
		LocateRegistry.createRegistry(5555);
		Naming.rebind("//localhost:5555/coordinator", this);
		System.out
		.println("Coordinateur enregistré dans rmiregistry, pret à fonctionner");
		jvnRestoreCoordState();
	}

	/**
	 * Allocate a NEW JVN object id (usually allocated to a newly created JVN
	 * object)
	 * 
	 * @throws java.rmi.RemoteException
	 *             ,JvnException
	 **/
	public synchronized int jvnGetObjectId() throws java.rmi.RemoteException,
	jvn.JvnException {
		NEXT_ID++;
		return NEXT_ID;
	}

	public synchronized int jvnGetServerId() throws RemoteException,
	JvnException {
		NEXT_ID_JVM++;
		return NEXT_ID_JVM;
	}

	/**
	 * Associate a symbolic name with a JVN object
	 * 
	 * @param jon
	 *            : the JVN object name
	 * @param jo
	 *            : the JVN object
	 * @param joi
	 *            : the JVN object identification
	 * @param js
	 *            : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException
	 *             ,JvnException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
			throws java.rmi.RemoteException, jvn.JvnException {
		/* Si le nom symbolique est déjà utilisé, cela pose problème! */
		if (name_objectid.containsKey(jon)) {
			throw new JvnException(
					"Un objet existe déjà avec ce nom symbolique, impossible d'en créer un autre");
		}
		/* Sinon on l'enregistre */
		else {
			/* Récupération du serveur pour appels distants si pas déjà présent */
			if (!id_server.containsKey(new Integer(js.jvnGetId()))) {
				id_server.put(js.jvnGetId(), js);
			}
			/*
			 * Si l'objet n'est pas connu du coordinateur on va créer une
			 * structure de controle d'acces
			 */
			if (!name_objectid.containsValue(new Integer(jo.jvnGetObjectId()))) {
				struct_list.add(new JvnSharedObjectStructure(jo, js.jvnGetId(),
						this));
				id_object.put(new Integer(jo.jvnGetObjectId()), jo);
			}
			/* Mapping nom symbolique / identifiant objet */
			name_objectid.put(jon, new Integer(jo.jvnGetObjectId()));
		}
	}

	/*
	 * Supprimer un client, donc tous ses locks dans la structure ainsi que
	 * toutes les références de lui dans le coord
	 */
	private void deleteClient(int id_jvm) {
		for (JvnSharedObjectStructure str : struct_list) {
			str.removeOwner(id_jvm);
		}
		id_server.remove(new Integer(id_jvm));
		jvnSaveCoordState();
	}

	public synchronized void jvnSaveCoordState() {
		try {
			FileOutputStream fichier = new FileOutputStream("coord.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			oos.writeObject(this);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void jvnRestoreCoordState() {
		File f = new File("coord.ser");
		if (f.exists()) {
			System.out.println("Restoring an old instance of coord");
			try {
				FileInputStream fichier = new FileInputStream("coord.ser");
				ObjectInputStream ois = new ObjectInputStream(fichier);
				JvnCoordImpl restored = (JvnCoordImpl) ois.readObject();
				this.id_object = restored.id_object;
				this.id_server = restored.id_server;
				this.name_objectid = restored.name_objectid;
				this.NEXT_ID = restored.NEXT_ID;
				this.NEXT_ID_JVM = restored.NEXT_ID_JVM;
				this.struct_list = restored.struct_list;
				ois.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * Sending a jvnInvalidateWriter message to a remote jvm for the id_obj
	 * object
	 */
	public Serializable jvnInvalidateWriter(int id_obj, int id_jvm)
			throws JvnException {
		Serializable ser = null;
		try {
			ser = id_server.get(id_jvm).jvnInvalidateWriter(id_obj);
		} catch (RemoteException e) {
			/*
			 * Le client distant n'est plus disponible, on le supprime et on
			 * renvoie la dernière valeur connu pour l'objet
			 */
			deleteClient(id_jvm);
			ser = id_object.get(new Integer(id_obj)).jvnGetObjectState();
		}
		return ser;
	}

	/*
	 * Sending a jvnInvalidateReader message to a remote jvm for the id_obj
	 * object
	 */
	public void jvnInvalidateReader(int id_obj, int id_jvm) throws JvnException {
		try {
			id_server.get(id_jvm).jvnInvalidateReader(id_obj);
		} catch (RemoteException e) {
			/*
			 * Le client distant n'est plus disponible, on le supprime et on
			 * renvoie la dernière valeur connu pour l'objet
			 */
			deleteClient(id_jvm);
		}
	}

	/*
	 * Sending a jvnInvalidateWriterForReader message to a remote jvm for the
	 * id_obj object
	 */
	public Serializable jvnInvalidateWriterForReader(int id_obj, int id_jvm)
			throws JvnException {
		Serializable ser = null;
		try {
			ser = id_server.get(id_jvm).jvnInvalidateWriterForReader(id_obj);
		} catch (RemoteException e) {
			/*
			 * Le client distant n'est plus disponible, on le supprime et on
			 * renvoie la dernière valeur connu pour l'objet
			 */
			deleteClient(id_jvm);
			ser = id_object.get(new Integer(id_obj)).jvnGetObjectState();
		}
		return ser;
	}

	private JvnSharedObjectStructure getStruct(Integer id) throws JvnException {
		for (JvnSharedObjectStructure str : struct_list) {
			if (str.getObjectId() == id.intValue()) {
				return str;
			}
		}
		/*123*/
		throw new JvnException("Aucune structure pour l'objet [id=" + id
				+ "] n'a été trouvé.");
	}

	/**
	 * Get the reference of a JVN object managed by a given JVN server
	 * 
	 * @param jon
	 *            : the JVN object name
	 * @param js
	 *            : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException
	 *             ,JvnException
	 **/
	public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
			throws java.rmi.RemoteException, jvn.JvnException {
		/* Récupération du serveur pour appels distants si pas déjà présent */
		if (!id_server.containsKey(new Integer(js.jvnGetId()))) {
			id_server.put(new Integer(js.jvnGetId()), js);
		}
		Integer id = name_objectid.get(jon);
		/* Si l'objet existe , on ajoute une duplication et retourne l'objet */
		if (id != null) {
			JvnSharedObjectStructure o = getStruct(id);
			o.addOwner(js.jvnGetId());
			return new JvnObjectImpl(id.intValue(), id_object.get(id)
					.jvnGetObjectState());
		}
		return null;
	}

	@Override
	public Serializable recharge(JvnRemoteServer js, Integer id)
			throws JvnException, RemoteException {
		if (id_object.containsKey(id)) {
			JvnSharedObjectStructure o = getStruct(id);
			o.addOwner(js.jvnGetId());
			return id_object.get(id).jvnGetObjectState();
		}
		return null;
	}

	/**
	 * Get a Read lock on a JVN object managed by a given JVN server
	 * 
	 * @param joi
	 *            : the JVN object identification
	 * @param js
	 *            : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException
	 *             , JvnException
	 **/
	public Serializable jvnLockRead(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
		JvnSharedObjectStructure str = getStruct(joi);
		str.getLock();
		if (str.hasWriter()) {
			Serializable ser = str.invalidateWriterForReader(js.jvnGetId());
			updateObject(joi, ser);
			str.releaseLock();
			return ser;
		} else {
			str.addReader(js.jvnGetId());
			str.releaseLock();
			return id_object.get(str.getObjectId()).jvnGetObjectState();
		}
	}

	private void updateObject(int id, Serializable ser) throws JvnException {
		id_object.get(new Integer(id)).jvnUpdateObjectState(ser);
	}

	/**
	 * Get a Write lock on a JVN object managed by a given JVN server
	 * 
	 * @param joi
	 *            : the JVN object identification
	 * @param js
	 *            : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException
	 *             , JvnException
	 **/
	public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException {
		System.out.println("Coord dit : lock write demandé par "
				+ js.jvnGetId() + " sur l'objet " + joi + "     "
				+ System.currentTimeMillis());
		JvnSharedObjectStructure str = getStruct(joi);
		str.getLock();
		if (str.hasWriter()) {
			System.out.println("has writer par " + js.jvnGetId()
					+ " sur l'objet " + joi + "     "
					+ System.currentTimeMillis());
			Serializable ser = str.invalidateWriter(js.jvnGetId());
			updateObject(joi, ser);
			System.out.println("Coord dit : lock write demandé par "
					+ js.jvnGetId() + " sur l'objet " + joi
					+ " est TERMINEEEEEEEEEEEEE" + "     "
					+ System.currentTimeMillis());
			str.releaseLock();
			return ser;
		} else if (str.hasReader()) {
			System.out.println("has reader par " + js.jvnGetId()
					+ " sur l'objet " + joi + "     "
					+ System.currentTimeMillis());
			str.invalidateReader(js.jvnGetId());
		} else {
			System.out.println("nobody par " + js.jvnGetId() + " sur l'objet "
					+ joi + "     " + System.currentTimeMillis());
			str.setWriter(new Integer(js.jvnGetId()));
		}
		System.out.println("Coord dit : lock write demandé par "
				+ js.jvnGetId() + " sur l'objet " + joi
				+ " est TERMINEEEEEEEEEEEEE" + "     "
				+ System.currentTimeMillis());
		str.releaseLock();
		return id_object.get(str.getObjectId()).jvnGetObjectState();
	}

	/**
	 * A JVN server terminates
	 * 
	 * @param js
	 *            : the remote reference of the server
	 * @throws java.rmi.RemoteException
	 *             , JvnException
	 **/
	public void jvnTerminate(JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException {
		deleteClient(js.jvnGetId());
	}

	public static void main(String[] args) {
		try {
			new JvnCoordImpl();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Remove a distributed object
	 * 
	 * @param joi
	 *            : the JVN object identification
	 * @param js
	 *            : the remote reference of the server
	 * @throws java.rmi.RemoteException
	 * @throws JvnException
	 */
	public void jvnRemoveObject(int joi)
			throws RemoteException, JvnException {
		/*Suppression des référence vers l'objet à supprimer*/
		JvnSharedObjectStructure struct = getStruct(joi);
		struct.getLock();
		id_object.remove(new Integer(joi));
		String name = null;
		for(Entry<String,Integer> entry : name_objectid.entrySet()){
			if(entry.getValue().intValue() == joi){
				name = entry.getKey();
				break;
			}
		}
		name_objectid.remove(name);
		/*Broadcast d'un message de suppression d'objet*/
		for(Entry<Integer,JvnRemoteServer> entry : id_server.entrySet()){
			entry.getValue().broadcastDeletedObject(new Integer(joi));
		}
		struct_list.remove(getStruct(joi));

	}
}
