package jvn;

import java.io.Serializable;

public interface JvnLocalCoord {
	public Serializable jvnInvalidateWriter(int id_obj, int id_jvm) throws JvnException;
	public void jvnInvalidateReader(int id_obj, int id_jvm) throws JvnException;
	public Serializable jvnInvalidateWriterForReader(int id_obj, int id_jvm) throws JvnException;
	public void jvnSaveCoordState();
}
