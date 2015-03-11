package com.hotcode.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * 
 * @author wuqq
 *
 */
public class JdkSerializer implements ObjectSerializer {
	

	@Override
	public byte[] serialize(Object object) {
		if (!(object instanceof Serializable)) throw new IllegalArgumentException("object=["+object.getClass().getName()+"] unimplements Serializable");
		
		ObjectOutputStream oos = null ;
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos) ;
			oos.writeObject(object);
			return bos.toByteArray();
		}
		catch (IOException e) { throw new IllegalStateException(e); }
		finally { if(oos!=null) {try { oos.close(); } catch (IOException igore) {}} }
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] bytes) {
		ObjectInputStream ois = null ;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
			return (T)ois.readObject();
		}
		catch (Exception e) { throw new IllegalStateException(e); }
		finally { if(ois!=null) {try { ois.close(); } catch (IOException igore) {}} }
	}

}
