package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3351923401318854408L;
	private int joi = 0;
	private Serializable obj = null;
	public enum STATE_ENUM {NL,R,W,RC,WC,RWC};
	private STATE_ENUM STATE = STATE_ENUM.NL; 
	private boolean wait_for_read = false;
	private boolean wait_for_write = false;

	public JvnObjectImpl(int id, Serializable obj){
		this.joi = id;
		this.obj = obj;
	}

	public Serializable jvnLockRead() throws JvnException {
		boolean ask = false;
		synchronized(this){
			System.out.println("lock read Etat courant :"+STATE+" sur l'objet "+joi+"     "+System.currentTimeMillis());
			if(STATE != STATE_ENUM.RC && STATE != STATE_ENUM.WC){
				ask = true;
			}
		}

		if(ask){
			obj = JvnServerImpl.jvnGetServer().jvnLockRead(joi);
		}
		synchronized(this){
			if(ask){
				STATE = STATE_ENUM.R;
			}
			else if(STATE == STATE_ENUM.RC){
				STATE = STATE_ENUM.R;
			}
			else if(STATE == STATE_ENUM.WC){
				STATE = STATE_ENUM.RWC;
			}
			else{
				throw new JvnException("Impossible de vérouiller l'objet"+joi+" en lecture, l'etat courant est le suivant : "+STATE);
			}
		}
		System.out.println("lock read Etat courant :"+STATE+" sur l'objet "+joi+" terminéééééééé"+"     "+System.currentTimeMillis());
		return obj;
	}

	public Serializable jvnLockWrite() throws JvnException {
		boolean ask = false;
		synchronized(this){
			System.out.println("lock write Etat courant :"+STATE+" sur l'objet "+joi+"     "+System.currentTimeMillis());
			if(STATE != STATE_ENUM.WC){
				ask = true;
			}
		}
		
		if(ask){
			obj = JvnServerImpl.jvnGetServer().jvnLockWrite(joi);
		}
		
		synchronized(this){
			if(ask){
				STATE = STATE_ENUM.W;
			}
			else if(STATE == STATE_ENUM.WC){
				STATE = STATE_ENUM.W;
			}
			else{
				throw new JvnException("Impossible de vérouiller l'objet"+joi+" en écriture, l'etat courant est le suivant : "+STATE+"     "+System.currentTimeMillis());
			}
		}		
		System.out.println("lock write Etat courant :"+STATE+" sur l'objet "+joi+" terminéééééééé"+"     "+System.currentTimeMillis());
		return obj;
	}

	public synchronized void jvnUnLock() throws JvnException {
		System.out.println("unlock STATE : "+STATE+" waitforread : "+wait_for_read+" waitforwrite : "+wait_for_write+"     "+System.currentTimeMillis());
		if(STATE == STATE_ENUM.R){
			if(wait_for_write){
				wait_for_write = false;
				STATE = STATE_ENUM.NL;
				this.notify();
			}
			else{
				STATE = STATE_ENUM.RC;
			}
		}
		else if(STATE == STATE_ENUM.W || STATE == STATE_ENUM.RWC){
			if(wait_for_write || wait_for_read){
				wait_for_write = false;
				wait_for_read = false;
				STATE = STATE_ENUM.NL;
				this.notify();
			}
			else{
				STATE = STATE_ENUM.WC;
			}
		}
		else{
			throw new JvnException("Execution de jvnUnLock() sur un jnvObject id ="+joi+" avec l'état "+STATE);
		}
		System.out.println("unlock STATE : "+STATE+" waitforread : "+wait_for_read+" waitforwrite : "+wait_for_write+" finishhhhhhhh"+"     "+System.currentTimeMillis());
	}

	public synchronized int jvnGetObjectId() throws JvnException {
		return joi;
	}

	public synchronized Serializable jvnGetObjectState() throws JvnException {
		return obj;
	}

	public synchronized void jvnInvalidateReader() throws JvnException {
		System.out.println("invalidate reader with state : "+STATE+" sur l'objet "+joi+"         "+System.currentTimeMillis());
		if(STATE != STATE_ENUM.R && STATE != STATE_ENUM.RWC){
			STATE = STATE_ENUM.NL;
		}
		else{
			wait_for_write = true;
			while(STATE == STATE_ENUM.R || STATE == STATE_ENUM.W || STATE == STATE_ENUM.RWC){
				try {
					System.out.println("waiting reader");
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized Serializable jvnInvalidateWriter() throws JvnException {
		System.out.println("invalidate writer with state : "+STATE+" sur l'objet "+joi+"         "+System.currentTimeMillis());

		if(STATE != STATE_ENUM.RWC && STATE != STATE_ENUM.W){
			STATE = STATE_ENUM.NL;
		}
		else{
			wait_for_write = true;
			while(STATE == STATE_ENUM.W || STATE == STATE_ENUM.RWC){
				try {
					System.out.println("waiting writer"+"     "+System.currentTimeMillis());
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return obj;
	}

	public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
		System.out.println("invalidate writerforreader with state : "+STATE+" sur l'objet "+joi+"         "+System.currentTimeMillis());

		if(STATE != STATE_ENUM.RWC && STATE != STATE_ENUM.W){
			STATE = STATE_ENUM.NL;
		}
		else{
			wait_for_read = true;	
			if(STATE == STATE_ENUM.W || STATE == STATE_ENUM.RWC){
				try {
					System.out.println("waiting writerforreader"+"     "+System.currentTimeMillis());
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return obj;
	}

	public void jvnUpdateObjectState(Serializable ser) throws JvnException {
		this.obj = ser;
	}
	
	public void jvnRemoveSer(){
		this.obj = null;
	}

}
