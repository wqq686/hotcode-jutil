package com.hotcode.cache;

public interface CacheHanlder<K, V> {
	
	/**
	 * 
	 * @return
	 */
	public String getName() ;
	
	
	/**
	 * 是否过期
	 * 
	 * @param k
	 * @param v
	 * @param borntime
	 * @return
	 */
	public boolean isExpire(K k, V v, long borntime) ;

	
	/**
	 * 如果内存中不存在, 则调用这个方法
	 * 
	 * @param k
	 * @return
	 */
	V handleGet(K k) ;
	
	/**
	 * 如果内存中不存在, 则调用这个方法, 没啥用
	 * 
	 * @param k
	 * @param v
	 * @return
	 */
	@Deprecated
	V handleGet(K k, V v) ;
	
	
	
	/**
	 * 主动移除时, 通知接口
	 * @param k
	 */
	void handleRemove(K k) ;

	
	/**
	 * 主动移除时, 通知接口
	 * @param k
	 * @param v
	 */
	void handleRemove(K k, V v) ;
	
	
	/**
	 * 主动添加到cache中时, 通知机制
	 * 
	 * @param k
	 * @param v
	 */
	void handlePut(K k, V v) ;
}
