package com.hotcode.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hotcode.common.CommonUtils;


/**
 * 
 * @author wuqq
 *
 */
public abstract class AbstractTask implements ITask {
	
	protected static Logger logger = LoggerFactory.getLogger(AbstractTask.class) ;
	
	
	static 
	{
		System.out.println("===>task is classload==" + AbstractTask.class.getClassLoader().getClass().getName());
	}
	
	
	private String name ;
	
	public String getName() {
		if(name==null) name = this.getClass().getSimpleName() ;
		return name ;
	}
	
	
	
	/**
	 * 
	 */
	public void start(boolean doNext) {
		if(doNext) 
		logger.info("{} doNext={} started.", this.getClass().getName(), doNext) ;
	}
	
	/**
	 * 
	 * @return
	 */
	protected boolean isLogger() {
		return false ;
	}
	
	
	@Override
	public void run() {
		long start = System.currentTimeMillis() ;
		Object result = null ;
		if(isLogger()) logger.info("task=[{}] excute started.", getName()) ;
		try
		{
			result = execute() ;
		}
		catch(Exception e)
		{
			logger.error("task:{} execute with:{}", new Object[]{getName(), CommonUtils.formatThrowable(e)}) ;
		}
		finally
		{
			long cost = System.currentTimeMillis() - start ;
			if(isLogger()) logger.info("task=[{}] excute end. cost={} seconds. result={}", new Object[]{getName(), cost, result}) ;
		}	
	}

	
	protected abstract Object execute() throws Exception ;
	
}
