package com.hotcode.common;

import java.util.HashMap;
import java.util.Map;


/**
 * 1. 借助ThreadLocal进行对资源的管理
 * 2. 使用时, 必须要将这个类定义为静态变量(类变量), 将这个类的生命周期绑定到Class
 * 
 * @author wuqq
 *
 * @param <T>
 */
public class ResourcesManager<T> {

	
	/**
	 * 
	 */
	private ThreadLocal<Map<String,T>> __resources__context__ = new ThreadLocal<>() ;
	
	
	
	/**
	 * 
	 */
	private ResourceFactory<T> __resource__factory__ ;
	
	
	
	
	/**
	 * 
	 * @param resourceFactory
	 */
	public ResourcesManager(ResourceFactory<T> resourceFactory) {
		this.__resource__factory__ = resourceFactory;
	}

	
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public T get(String name) {
		Map<String, T> container = __resources__context__.get() ;
		if(container==null)
		{
			container = new HashMap<String, T>(8, 1.0f) ;
			__resources__context__.set(container) ;
		}
		
		T resource = container.get(name) ;
		if(resource==null)
		{
			resource = __resource__factory__.getResource(name) ;
			if(resource!=null) container.put(name, resource) ;
		}
		return resource ;
	}
	
	
	
	
	/**
	 * 
	 */
	public void release() {
		Map<String, T> container = __resources__context__.get() ;
		if(container!=null)
		{
			for(String name : container.keySet())
			{
				T resource = container.get(name) ;
				if(resource!=null)
				{
					try
					{
						__resource__factory__.returnResource(name, resource) ;
					}catch(Exception e){e.printStackTrace();} ;
				}
			}
			container.clear() ;
			__resources__context__.remove() ;
		}
	}
	
	
	
	public static interface ResourceFactory<T> {
		
		public T getResource(String name) ;
		
		public void returnResource(String name, T resource) ;
		
	}
	
}
