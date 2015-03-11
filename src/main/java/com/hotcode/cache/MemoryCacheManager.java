package com.hotcode.cache;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.hotcode.cache.MemoryCache.CacheEntry;

public class MemoryCacheManager {

	/**
	 * 
	 */
	private static ConcurrentMap<Object, CacheHanlder<?,?>> handles = new ConcurrentHashMap<>() ;
	
	/**
	 * 
	 */
	private static ConcurrentMap<Object, MemoryCache<?, ?>> caches = new ConcurrentHashMap<>() ;
	
	
	private static Thread clear ;
	
	static{ start() ; }
	
	
	private MemoryCacheManager(){}
	
	private static MemoryCacheManager instance = new MemoryCacheManager() ;
	
	
	/**
	 * 
	 * @return
	 */
	public static MemoryCacheManager getInstance() {
		return instance ;
	}
	
	
	
	/**
	 * 
	 */
	public static synchronized void start() {
		if(clear==null) {
			Runnable r = new Runnable() {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public void run() {
					try{Thread.sleep(5*1000);}catch(Exception ignore){}
					for(Object key : caches.keySet()) {
						MemoryCache cache = (MemoryCache<Object, Object>) caches.get(key) ; 
						CacheHanlder handler = (CacheHanlder<Object, Object>) handles.get(key) ;
						Collection<CacheEntry<Object, Object>> c = cache.entrySet() ;
						for(CacheEntry<Object, Object> e : c) {
							if(handler.isExpire(e.k, e.v, e.borntime)) {
								cache.remove(e.k) ;
							}
						}
					}
				}
			};
			
			clear = new Thread(r, "memory-cache-clear") ;
			clear.start(); 
		}
		
	}
	
	
	
	/**
	 * 
	 */
	public synchronized void stop() {
		caches.clear();
		handles.clear(); 
		if(!clear.isInterrupted()) clear.interrupt(); 
	}
	
	
	
	/**
	 * 
	 * @param handle
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized MemoryCacheManager register(CacheHanlder<?, ?> handle) {
		if(!handles.containsKey(handle.getName())) {
			handles.put(handle.getName(), handle) ;
			caches.put(handle.getName(), new MemoryCache(handle)) ;
		}
		return this ;
	}
	
	/**
	 * 
	 * @param cache
	 * @return
	 */
	public synchronized MemoryCacheManager register(MemoryCache<?, ?> cache) {
		if(!caches.containsKey(cache.getName())) {
			caches.put(cache.getName(), cache) ;
			if(cache.getHanlder()!=null) {
				handles.put(cache.getName(), cache.getHanlder()) ;
			}
		}
		return this ;
	}
	
	
	
	/**
	 * 
	 * @param cacheName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <K, V> MemoryCache<K, V> getCache(String cacheName) {
		return (MemoryCache<K, V>) caches.get(cacheName) ;
	}
	
	
	
	/**
	 * 
	 * @param cacheName
	 * @param k
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <K, V> V getCache(String cacheName, K k) {
		return (V)getCache(cacheName).get(k) ;
	}
	
	
	
	
}
