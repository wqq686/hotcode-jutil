package com.hotcode.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生成的线程池工厂的工具类.
 * 
 * @author wuqq
 *
 */
public abstract class SimpleNamedThreadFactory implements ThreadFactory {
	
	/**
	 * 产生名字为threadName的ThreadFactory
	 * 
	 * @param threadName
	 *            线程的名字
	 * @return
	 */
	public static SimpleNamedThreadFactory newSingleThreadFactory(String threadName) {
		return new SingleThreadNamedFactory(threadName) ;
	}
	
	/**
	 * 产生名字为namePrefix-1,2...的ThreadFactory
	 * 
	 * @param namePrefix
	 *            线程池前缀
	 * @return
	 */
	public static SimpleNamedThreadFactory newPoolThreadFactory(String namePrefix) {
		return new ThreadPoolNamedFactory(namePrefix) ;
	}
	
	
	
	private final ThreadGroup group;
	
	protected SimpleNamedThreadFactory() {
		this.group = getThreadGroup();
	}
	
	/**
	 * 获取子类生成的线程名
	 * 
	 * @return
	 */
	protected abstract String getThreadName() ;

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, getThreadName(), 0);
		if (t.isDaemon()) t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
	
	private ThreadGroup getThreadGroup() {
		SecurityManager s = System.getSecurityManager();
		return (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
	}
	
	
	
	/**
	 * 
	 * @author wuqq
	 *
	 */
	private static class SingleThreadNamedFactory extends SimpleNamedThreadFactory {
		private final String name ;
		
		/**
		 * @param name 单线程的名字
		 */
		private SingleThreadNamedFactory(String name) {
			super() ;
			this.name = name;
		}
		
		@Override
		protected String getThreadName() {
			return name ;
		}
	}
	
	
	/**
	 * 
	 * @author wuqq
	 *
	 */
	private static class ThreadPoolNamedFactory extends SimpleNamedThreadFactory {
		private final String namePrefix ;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		
		/**
		 * @param namePrefix 线程池前缀
		 */
		private ThreadPoolNamedFactory(String namePrefix) {
			super() ;
			this.namePrefix = namePrefix;
		}
		
		@Override
		protected String getThreadName() {
			return namePrefix + "-" + threadNumber.getAndIncrement() ;
		}
	}
	
}