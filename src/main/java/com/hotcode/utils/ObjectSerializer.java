package com.hotcode.utils;


/**
 * 对象序列化接口, 用于取代坑爹的JDK Serializable
 * @author wuqq
 *
 */
public interface ObjectSerializer {

	
	/**
	 * 
	 * @param object
	 * @return
	 */
	public byte[] serialize(Object object) ;
	
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public <T> T deserialize(byte[] bytes) ;
	
}
