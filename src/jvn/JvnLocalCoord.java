package jvn;

import java.io.Serializable;

public interface JvnLocalCoord {
	/**
	 * Send an invalidateWriter message to a remote JvnServer about a specific JvnObject	 
	 * @param id_obj : the JVN object identifier
	 * @param id_jvm : the JvnServer identifier
	 * @return the current JVN object state 
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriter(int id_obj, int id_jvm) throws JvnException;
	/**
	 * Send an invalidateReader message to a remote JvnServer about a specific JvnObject	 
	 * @param id_obj : the JVN object identifier
	 * @param id_jvm : the JvnServer identifier
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnInvalidateReader(int id_obj, int id_jvm) throws JvnException;
	/**
	 * Send an invalidateWriterForReader message to a remote JvnServer about a specific JvnObject	 
	 * @param id_obj : the JVN object identifier
	 * @param id_jvm : the JvnServer identifier
	 * @return the current JVN object state 
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader(int id_obj, int id_jvm) throws JvnException;
	/**
	 * Make a backup of the JvnCoord state in a .ser file
	 **/
	public void jvnSaveCoordState();
}
