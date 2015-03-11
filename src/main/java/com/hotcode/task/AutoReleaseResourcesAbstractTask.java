package com.hotcode.task;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.hotcode.common.CommonUtils;
import com.hotcode.common.ReflectUtils;
import com.hotcode.logger.Logger;


/**
 * 1. 执行结束时释放dbclient, redis连接池资源;
 * 2. 执行异常时 rollback dbclient.
 * @author wuqq
 *
 */
public abstract class AutoReleaseResourcesAbstractTask implements ITask {
	
	/**
	 * 
	 */
	private static Class<?> dbclientFactoryClass ;
	
	/**
	 * 
	 */
	private static Method releaseDBClientMethod ;
	
	/**
	 * 
	 */
	private static Method rollbackDBClientMethod ;
	
	/**
	 * 
	 */
	private static Class<?> redisProxyFactoryClass ;

	/**
	 * 
	 */
	private static Method releaseRedisProxyMethod ;
	
	/**
	 * 
	 */
	private String name ;
	
	
	
	static {touchResources();}
	
	
	protected abstract Object execute() throws Exception ;
	
	/**
	 * 
	 * @return
	 */
	protected Logger getLogger() {
		return TaskContainer.getLogger() ;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTaskName() {
		if(name==null) name = this.getClass().getSimpleName() ;
		return name ;
	}
	
	
	
	@Override
	public void run() {
		long start = System.currentTimeMillis() ;
		Object result = null ;
		getLogger().info("task=["+getTaskName()+"] excute started.") ;
		try
		{
			result = execute() ;
		}
		catch(Throwable t)
		{
			rollbackResources() ;
			getLogger().info("task=["+getTaskName()+"] execute with:"+CommonUtils.formatThrowable(t)) ;
		}
		finally
		{
			releaseResources();
			long cost = TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS) ;
			getLogger().info("task=["+getTaskName()+"] excute end. cost="+cost+"s. result=" + result+"\n") ;
		}
	}
	
	
	
	
	
	
	/**
	 * 
	 */
	protected void rollbackResources() {
		if(dbclientFactoryClass!=null && rollbackDBClientMethod!=null) {
			try {
				rollbackDBClientMethod.invoke(dbclientFactoryClass) ;
			}catch(Exception e){e.printStackTrace();}
		}
	}
	
	
	
	/**
	 * 
	 */
	protected void releaseResources() {
		if(dbclientFactoryClass!=null && releaseDBClientMethod!=null) {
			try {
				releaseDBClientMethod.invoke(dbclientFactoryClass) ;
			}catch(Exception e){e.printStackTrace();}
		}
		
		if(redisProxyFactoryClass!=null && releaseRedisProxyMethod!=null) {
			try {
				releaseRedisProxyMethod.invoke(redisProxyFactoryClass) ;
			}catch(Exception e){e.printStackTrace();}
		}
	}
	
	
	/**
	 * 
	 */
	private static void touchResources() {
		try
		{
			dbclientFactoryClass = CommonUtils.classForName("com.touna.dbclient.DBClientFactory") ;
			if(dbclientFactoryClass!=null) {
				releaseDBClientMethod = ReflectUtils.foundMethod(dbclientFactoryClass, "releaseClient") ;
				rollbackDBClientMethod = ReflectUtils.foundMethod(dbclientFactoryClass, "rollbackClient") ;
			}
		}catch(Exception ignore){}
		
		
		try
		{
			redisProxyFactoryClass = CommonUtils.classForName("com.rxdai.jedis.proxy.RedisProxyFactory") ;
			if(redisProxyFactoryClass!=null) {
				releaseRedisProxyMethod = ReflectUtils.foundMethod(redisProxyFactoryClass, "releaseProxy") ;
			}
		}catch(Exception ignore){}
		TaskContainer.getLogger().info("dbclientFactoryClass=" + dbclientFactoryClass 
				+ ", releaseDBClientMethod="+releaseDBClientMethod
				+ ", rollbackDBClientMethod=" + rollbackDBClientMethod 
				+ ", redisProxyFactoryClass=" + redisProxyFactoryClass
				+ ", releaseRedisProxyMethod=" + releaseRedisProxyMethod
				); 
	}
	
	
	
	/**
	 * 
	 * @param success
	 * @param failed
	 * @return
	 */
	protected Map<String, Object> wapperResult(Object success, Object failed) {
		Map<String, Object> result = CommonUtils.stableMap(2) ;
		if(success!=null) result.put("success", success) ;
		if(failed!=null) result.put("failed", failed) ;
		return result ;
	}
}
