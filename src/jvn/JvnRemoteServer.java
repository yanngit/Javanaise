/***
 * JAVANAISE API
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Remote interface of a JVN server (used by a remote JvnCoord)
 */

public interface JvnRemoteServer extends Remote {
	
	public int jvnGetId() throws RemoteException;
	/**
	 * Invalidate the Read lock of a JVN object 
	 * @param joi : the JVN object id
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnInvalidateReader(int joi)
			throws RemoteException,JvnException;

	/**
	 * Invalidate the Write lock of a JVN object 
	 * @param joi : the JVN object id
	 * @return the current JVN object state 
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriter(int joi)
			throws RemoteException,JvnException;

	/**
	 * Reduce the Write lock of a JVN object 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader(int joi)
			throws RemoteException,JvnException;
	
	public void broadcastDeletedObject(Integer integer) throws RemoteException, JvnException;
}


