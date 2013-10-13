package jvn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class JvnSharedObjectStructure implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3186748225270818874L;
	private int id_jvn_object;
	private CopyOnWriteArrayList<Integer> reader_list = new CopyOnWriteArrayList<Integer>();
	private ArrayList<Integer> owner = new ArrayList<Integer>();
	private Integer writer = new Integer(-1);
	private JvnLocalCoord coord;
	private boolean locked = false;

	/**
	 * The class constructor
	 * @param obj a jvnObject
	 * @param id_jvm the id of the server
	 * @param coord the coordinator
	 */
	public JvnSharedObjectStructure(JvnObject obj, int id_jvm, JvnLocalCoord coord){
		try {
			id_jvn_object = obj.jvnGetObjectId();
			addOwner(new Integer(id_jvm));
			this.coord = coord;
		} catch (JvnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get a lock on the structure
	 */
	public synchronized void getLock(){
		while(locked){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		locked = true;
	}

	/**
	 * Release a lock on the structure
	 */
	public synchronized void releaseLock(){
		locked = false;
		coord.jvnSaveCoordState();
		this.notify();
	}

	/*Va invalider le(s) reader(s) pour un writer, sauf si le reader est le demandeur*/
	/**
	 * Invalidate all the reader of a shared object
	 * 
	 * @param id_jvm the id of the server
	 * @throws JvnException
	 */
	public synchronized void invalidateReader(int id_jvm) throws JvnException{
		if(reader_list.size() > 0){
			for(Integer i : reader_list){
				if(i.intValue() != id_jvm){
					coord.jvnInvalidateReader(id_jvn_object, i.intValue());
				}
			}
			reader_list.clear();
			writer = id_jvm;	
		}
	}

	/*Va invalider le writer pour un reader*/
	/**
	 * Invalidate the writer of a shared object to give the object to the readers
	 * 
	 * @param id_jvm the id of the server
	 * @return the shared object
	 * @throws JvnException
	 */
	public synchronized Serializable invalidateWriterForReader(int id_jvm) throws JvnException{
		Serializable ser = null;
		if(writer > 0){
			ser = coord.jvnInvalidateWriterForReader(id_jvn_object, writer);
			reader_list.add(new Integer(id_jvm));
			writer = -1;
			return ser;
		}
		throw new JvnException("Impossible d'invalider writerForReader");
	}

	/*Va invalider le writer pour un writer*/

	/**
	 * Invalidate the writer on the shared object 
	 * 
	 * @param id_jvm the id of the server
	 * @throws JvnException
	 */
	public synchronized Serializable invalidateWriter(int id_jvm) throws JvnException{
		if(writer > 0){
			Serializable ser = coord.jvnInvalidateWriter(id_jvn_object, writer);
			writer = id_jvm;
			return ser;
		}
		throw new JvnException("Impossible d'invalider writer");
	}
	/**
	 * Add a new owner to the list of the shared objects owners
	 * @param id the identifier of the new owner
	 */
	public synchronized void addOwner(int id){
		owner.add(new Integer(id));
	}

	/**
	 * Delete  an owner the list of the shared objects owners
	 * @param id the identifier of the new owner
	 */
	public synchronized void removeOwner(int id){
		boolean exists = false;
		for(Integer i : owner){
			if(i.intValue() == id){
				owner.remove(i);
				exists = true;
				break;
			}
		}
		if(exists){
			if(writer == id){
				writer = -1;
			}
			for(Integer i : reader_list){
				if(i.intValue() == id){
					reader_list.remove(i);
					break;
				}
			}
		}
	}

	/**
	 * Set the identifier of the writer
	 * 
	 * @param id the new identifier
	 */
	public synchronized void setWriter(Integer id){
		writer = id;
	}

	/**
	 * Test if there is a writer on the shared object
	 *
	 * @return true if there is a writer, false otherwise
	 */
	public synchronized boolean hasWriter(){
		return writer.intValue() != -1;
	}

	/**
	 * Add a reader to the list of readers
	 * @param id the identifier of the new reader
	 */
	public synchronized void addReader(Integer id){
		reader_list.add(id);
	}

	/**
	 * Test if there is a reader on the shared object
	 *
	 * @return true if there is a reader, false otherwise
	 */
	public synchronized boolean hasReader(){
		return !reader_list.isEmpty();
	}

	/**
	 * Return the identifier of the shared object
	 * @return identifier of the shared object
	 */
	public int getObjectId(){
		return id_jvn_object;
	}

}
