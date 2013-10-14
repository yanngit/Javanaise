/***
 * JAVANAISE API
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.io.*;

/**
 * Interface of a JVN object. 
 * The serializable property is required in order to be able to transfer 
 * a reference to a JVN object remotely
 */

public interface JvnObject extends Serializable {

	/**
	 * Get a Read lock on the object 
	 * @throws JvnException
	 **/
	public Serializable jvnLockRead()
			throws jvn.JvnException; 

	/**
	 * Get a Write lock on the object 
	 * @throws JvnException
	 **/
	public Serializable jvnLockWrite()
			throws jvn.JvnException; 

	/**
	 * Unlock  the object 
	 * @throws JvnException
	 **/
	public void jvnUnLock()
			throws jvn.JvnException; 


	/**
	 * Get the object identifier
	 * @throws JvnException
	 **/
	public int jvnGetObjectId()
			throws jvn.JvnException; 

	/** Update the status of a given object
	 * 
	 * @param ser
	 * 		  The new state of a shared object
	 * 
	 * @throws JvnException
	 */
	public void jvnUpdateObjectState(Serializable ser) throws jvn.JvnException;
	
	/**
	 * Get the object state
	 * @throws JvnException
	 **/
	public Serializable jvnGetObjectState()
			throws jvn.JvnException; 


	/**
	 * Invalidate the Read lock of the JVN object 
	 * @throws JvnException
	 **/
	public void jvnInvalidateReader()
			throws jvn.JvnException;

	/**
	 * Invalidate the Write lock of the JVN object  
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnInvalidateWriter()
			throws jvn.JvnException;

	/**
	 * Reduce the Write lock of the JVN object 
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader()
			throws jvn.JvnException;
	
	/** 
	 * Unloading a shared object (to liberate a place in the cache)
	 * 
	 */
	public void jvnUnloadObject();
	
	/**
	 * Test if the shared object is cached
	 * 
	 * @return true if the shared object is cached, false otherwise
	 */
	public boolean jvnIsCached();

	
}
