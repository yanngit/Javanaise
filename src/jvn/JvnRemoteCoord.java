/***
 * JAVANAISE API
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.*;
import java.util.List;
import java.io.*;


/**
 * Remote Interface of the JVN Coordinator  
 */

public interface JvnRemoteCoord extends Remote {

	/**
	 *  Allocate a NEW JVN object id (usually allocated to a 
	 *  newly created JVN object)
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public int jvnGetObjectId()
			throws java.rmi.RemoteException,jvn.JvnException; 
	
	/**
	 * Allocate a NEW JVN server id (usually allocated to a newly created JVNServerImp)
	 *  
	 * @throws java.rmi.RemoteException
	 *             ,JvnException
	 **/
	public int jvnGetServerId() throws java.rmi.RemoteException, jvn.JvnException;
	
	/**
	 * Associate a symbolic name with a JVN object
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object 
	 * @param js  : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
			throws java.rmi.RemoteException,jvn.JvnException; 

	/**
	 * Get the reference of a JVN object managed by a given JVN server 
	 * @param jon : the JVN object name
	 * @param js : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
			throws java.rmi.RemoteException,jvn.JvnException; 

	/**
	 * Get a Read lock on a JVN object managed by a given JVN server 
	 * @param joi : the JVN object identifier
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public Serializable jvnLockRead(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException;

	/**
	 * Get a Write lock on a JVN object managed by a given JVN server 
	 * @param joi : the JVN object identifier
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException;

	/**
	 * A JVN server terminates
	 * @param js  : the remote reference of the server
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public void jvnTerminate(JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException;
	
	/**
	 * Remove a specific distributed object
	 * @param joi : the JVN object identifier
	 * @throws java.rmi.RemoteException 
	 * @throws JvnException
	 */
	public void jvnRemoveObject(int joi) throws java.rmi.RemoteException, JvnException;
	
	/** 
	 * Get the shared object from the coordinator
	 * 
	 * @param js the server asking the object
	 * @param id the shared object identifier 
	 * @return the shared object state
	 */
	public Serializable jvnGetSharedObject(JvnRemoteServer js, Integer id) throws JvnException,
			RemoteException;
	
	/** Get the Names of all shared objects 
	 *
	 * @return a list of shared object names
	 * 
	 * @throws java.rmi.RemoteException
	 * @throws JvnException
	 * **/
	public List<String> getLookupNames() throws RemoteException, JvnException;

}


