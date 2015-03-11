package com.hotcode.cache;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MemoryCache <K, V> {
	
	/**
	 * 
	 */
	private ConcurrentMap<Object, CacheEntry<K,V>> memories = new ConcurrentHashMap<>() ;
	
	/**
	 * 
	 */
	private CacheHanlder<K, V> handler ;
	
	
	
	/**
	 * 
	 * @param handler
	 */
	public MemoryCache(CacheHanlder<K, V> handler) {
		this.handler = handler ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return handler!=null ? handler.getName() : null ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public CacheHanlder<K, V> getHanlder() {
		return this.handler ;
	}
	

	/**
	 * 
	 * @param k
	 * @return
	 */
	public V get(K k) {
		V v = null ;
		CacheEntry<K,V> stamp = memories.get(k) ;
		//1. is expire 
		if(stamp!=null && handler!=null && handler.isExpire(k, v, stamp.borntime))
		{
			stamp.v = null ;
		}
		
		//2. read v
		v = stamp == null ? null : stamp.v ;
		
		//3. handle v
		if(v == null)
		{
			v = handler.handleGet(k) ;
			if(v != null)
			{
				freshValue(k, v) ;
			}
			else if(stamp!=null)// v==null && stamp!=null
			{
				memories.remove(k) ;
			}
		}
		/**
		if(v == null && handler!=null)
		{
			v = handler.handleGet(k) ;
			if(v != null) freshValue(k, v) ;
		}
		else if(handler.isExpire(k, v, stamp.borntime))
		{
			memories.remove(k) ;
			v = null ;
		}*/
		return v ;
	}
	
	
	
	/**
	 * 
	 * @param k
	 * @param v
	 */
	private void freshValue(K k, V v) {
		if(k!=null && v !=null)
		{
			CacheEntry<K,V> stamp = memories.get(k) ;
			if(stamp==null) {
				stamp = new CacheEntry<K, V>() ;
				memories.put(k, stamp) ;
			}
			stamp.k = k ; stamp.v = v ; stamp.borntime = System.currentTimeMillis() ;
		}
	}
	
	/**
	 * 
	 * @param k
	 * @return
	 */
	public V remove(K k) {
		CacheEntry<K, V> e =  memories.remove(k) ;
		V v = e == null ? null : e.v ;
		handler.handleRemove(k, v);
		return v ;
	}
	
	
	/**
	 * 
	 * @param c
	 */
	public void remove(Collection<K> c) {
		if(c==null || c.isEmpty()) return ;
		for(K k : c) remove(k) ;
	}
	
	
	
	
	/**
	 * 
	 * @param k
	 * @param v
	 * @return
	 */
	public V put(K k, V v) {
		if(k == null || v == null) throw new NullPointerException("k=" + k + " or v=" + v + " is NULL.") ;
		
		V old = null ;
		CacheEntry<K,V> stamp = memories.get(k) ;
		if(stamp==null) {
			stamp = new CacheEntry<K, V>() ;
			memories.put(k, stamp) ;
		} else {
			old = stamp.v ;
		}
		
		stamp.k = k ; stamp.v = v ; stamp.borntime = System.currentTimeMillis() ;
		if(handler!=null) handler.handlePut(k, v);
		return old ;
	}
	
	

	protected V getFromMemory(K k) {
		CacheEntry<K, V> e = memories.get(k) ;
		return e == null ? null : e.v ;
	}
	
	
	public Collection<CacheEntry<K, V>> entrySet(){
		return memories.values() ;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public Set<K> keySet() {
		return (Set<K>) memories.keySet() ;
	}
	
	protected static class CacheEntry<K, V> {
		K k ; V v ; long borntime ;
	}
	
}
